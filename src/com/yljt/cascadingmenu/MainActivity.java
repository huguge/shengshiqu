package com.yljt.cascadingmenu;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.hwy.httpTool.BaseConnection;
import com.hwy.httpTool.CallBack;
import com.yljt.cascadingmenu.interfaces.CascadingMenuViewOnSelectListener;
import com.yljt.model.Area;
import com.yljt.model.Category;

public class MainActivity extends FragmentActivity implements OnClickListener {

//	ArrayList<Area> provinceList;
	private List<Category> categoryOneList = new ArrayList<Category>();// 一级菜单集合
	// 两级联动菜单数据
	private CascadingMenuFragment cascadingMenuFragment = null;
	private CascadingMenuPopWindow cascadingMenuPopWindow = null;

	private Button menuViewPopWindow;
	private Button menuViewFragment;
	private DBhelper dBhelper;

	private static final int LOAD_SUCCESS = 1001;// 加载成功
	private static final int LOAD_FAILD = 1002;// 加载失败
	private static final int NO_DATA = 1003;// 无数据
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case LOAD_SUCCESS:
				break;
			case LOAD_FAILD:
				
				break;
			case NO_DATA:
				
				break;
			default:
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// DBManager dbManager = new DBManager(this);
		// dbManager.openDatabase();
		// dbManager.closeDatabase();
		menuViewPopWindow = (Button) findViewById(R.id.menuViewPopWindow);
		menuViewFragment = (Button) findViewById(R.id.menuViewFragment);

		// for (int k = 0; k < 7; k++) {
		// secondItems = new ArrayList<MenuItem>();
		// for (int j = 0; j < 7; j++) {
		// thirdItems = new ArrayList<MenuItem>();
		// for (int i = 0; i < 8; i++) {
		// thirdItems.add(new MenuItem(3, "3级菜单" + k+""+ j + "" + i, null,
		// null));
		// }
		// secondItems.add(new MenuItem(2, "2级菜单" +k+""+ j, null, thirdItems));
		// }
		// firstItems.add(new MenuItem(3,"1级菜单"+k,secondItems,thirdItems));
		// }

		// 向三级menu添加地区数据
		dBhelper = new DBhelper(this);
		// 这里是原来的，我现在要从数据库查询商品类别
//		provinceList = dBhelper.getProvince();
		selectCategory();// 查询列表

		menuViewPopWindow.setOnClickListener(this);
		menuViewFragment.setOnClickListener(this);

	}

	// 查询类别菜单
	public void selectCategory(){
		BaseConnection conn = new BaseConnection(MainActivity.this);
		conn.addParameter("upId", "001");
		conn.openConnection(com.hwy.httpTool.Constants.GET_SELECT_CATEGORY, "post",
				new CallBack() {
				@Override
				public void executeResult(String result) {
					if(result == null){
						handler.sendEmptyMessage(LOAD_FAILD);
					}else {
						JSONObject jsonObject;
						// 返回result
						try {
							jsonObject = new JSONObject(result); 
							String flag = jsonObject.optString("result");
							if ("1".equals(flag)) {// 有数据
								JSONArray jsonArray = jsonObject.getJSONArray("category");
								for (int i = 0; i < jsonArray.length(); i++) {
									JSONObject jsonObject2 = (JSONObject)jsonArray.optJSONObject(i);
									Category category = new Category();
									category.setId(jsonObject2.optString("id"));
									category.setName(jsonObject2.optString("name"));
									categoryOneList.add(category);
								}
								handler.sendEmptyMessage(LOAD_SUCCESS);
							} else {
								handler.sendEmptyMessage(NO_DATA);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			});
	}
	
	public void showFragmentMenu() {
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		fragmentTransaction.setCustomAnimations(R.anim.short_menu_pop_in,R.anim.short_menu_pop_out);
		if (cascadingMenuFragment == null) {
			cascadingMenuFragment = CascadingMenuFragment.getInstance();
			cascadingMenuFragment.setMenuItems(categoryOneList);
			cascadingMenuFragment.setMenuViewOnSelectListener(new NMCascadingMenuViewOnSelectListener());
			fragmentTransaction.replace(R.id.liner, cascadingMenuFragment);
		} else {
			fragmentTransaction.remove(cascadingMenuFragment);
			cascadingMenuFragment = null;
		}
		fragmentTransaction.commit();
	}

	private void showPopMenu() {
		if (cascadingMenuPopWindow == null) {
			cascadingMenuPopWindow = new CascadingMenuPopWindow(getApplicationContext(), categoryOneList);
			cascadingMenuPopWindow.setMenuViewOnSelectListener(new NMCascadingMenuViewOnSelectListener());
			cascadingMenuPopWindow.showAsDropDown(menuViewPopWindow, 5, 5);
		} else if (cascadingMenuPopWindow != null && cascadingMenuPopWindow.isShowing()) {
			cascadingMenuPopWindow.dismiss();
		} else {
			cascadingMenuPopWindow.showAsDropDown(menuViewPopWindow, 5, 5);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// 级联菜单选择回调接口
	class NMCascadingMenuViewOnSelectListener implements
			CascadingMenuViewOnSelectListener {

		@Override
		public void getValue(Category category) {
			cascadingMenuFragment = null;
			Toast.makeText(getApplicationContext(), "" + category.getName(), 1000).show();
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.menuViewPopWindow:
			showPopMenu();
			break;
		case R.id.menuViewFragment:
			showFragmentMenu();
			break;
		}
	}
}
