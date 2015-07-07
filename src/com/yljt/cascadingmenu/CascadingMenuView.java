package com.yljt.cascadingmenu;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.hwy.httpTool.BaseConnection;
import com.hwy.httpTool.CallBack;
import com.yljt.cascadingmenu.adpater.MenuItemAdapter;
import com.yljt.cascadingmenu.interfaces.CascadingMenuViewOnSelectListener;
import com.yljt.model.Category;

/**
 * 三级级联动ListView
 * 
 */
public class CascadingMenuView extends LinearLayout {
	private static final String TAG = CascadingMenuView.class.getSimpleName();
	// 三级菜单选择后触发的接口，即最终选择的内容
	private CascadingMenuViewOnSelectListener mOnSelectListener;
	private ListView firstMenuListView;
	private ListView secondMenuListView;
	private ListView thirdMenuListView;

	// 每次选择的子菜单内容
	private List<Category> thirdItem = new ArrayList<Category>();
	private List<Category> secondItem = new ArrayList<Category>();
	private List<Category> menuItem = new ArrayList<Category>();

	private MenuItemAdapter firstMenuListViewAdapter;

	private MenuItemAdapter secondMenuListViewAdapter;

	private MenuItemAdapter thirdMenuListViewAdapter;
	
	private ArrayList<Category> mCategoryList = new ArrayList<Category>();

	private int firstPosition = 0;
	private int secondPosition = 0;
	private int thirdPosition = 0;

	private DBhelper dBhelper;

	private Context context;

	private static final int SECOND_SUCCESS = 1001;// 加载成功
	private static final int THIRD_SUCCESS = 1002;// 加载成功
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SECOND_SUCCESS:
				secondItem.addAll(mCategoryList);
				mCategoryList.clear();
				if (secondItem.size() > 0 && !secondItem.isEmpty()) {
					thirdItem.clear();
					mCategoryList = new ArrayList<Category>();
					selectCategory(secondItem.get(secondPosition).getId());
					thirdItem.addAll(mCategoryList);
					mCategoryList.clear();
				}
				
				secondMenuListViewAdapter = new MenuItemAdapter(context, secondItem,R.drawable.choose_item_selected,R.drawable.choose_eara_item_selector);
				secondMenuListViewAdapter.setTextSize(15);
				secondMenuListViewAdapter.setSelectedPositionNoNotify(secondPosition,secondItem);
				secondMenuListView.setAdapter(secondMenuListViewAdapter);
				secondMenuListViewAdapter.setOnItemClickListener(new MenuItemAdapter.OnItemClickListener() {

					@Override
					public void onItemClick(View view, final int position) {
						// 选择主菜单，清空原本子菜单内容，增加新内容
						mCategoryList.clear();
						thirdItem.clear();
//						thirdItem = getThirdItem(secondItem.get(position).getId());
						selectCategory(secondItem.get(position).getId());
						thirdItem.addAll(mCategoryList);
						// 通知适配器刷新
						thirdMenuListViewAdapter.notifyDataSetChanged();
						thirdMenuListViewAdapter.setSelectedPositionNoNotify(0,thirdItem);
					}
				});
				
				mCategoryList = new ArrayList<Category>();
				selectCategory(secondItem.get(secondPosition).getId());
				break;
			case THIRD_SUCCESS:
				thirdItem.clear();
				thirdItem.addAll(mCategoryList);
				mCategoryList.clear();
				thirdMenuListViewAdapter = new MenuItemAdapter(context, thirdItem,R.drawable.choose_item_right,R.drawable.choose_plate_item_selector);
				thirdMenuListViewAdapter.setTextSize(13);
				thirdMenuListViewAdapter.setSelectedPositionNoNotify(thirdPosition,thirdItem);
				thirdMenuListView.setAdapter(thirdMenuListViewAdapter);
				thirdMenuListViewAdapter.setOnItemClickListener(new MenuItemAdapter.OnItemClickListener() {

					@Override
					public void onItemClick(View view, final int position) {
						Category menuItem = thirdItem.get(position);
						if (mOnSelectListener != null) {
							mOnSelectListener.getValue(menuItem);
						}
						Log.e(TAG, menuItem.toString());
					}

				});
				break;
			default:
				break;
			}
		}
	};
	
	/**
	 * @param context
	 *            上下文
	 */
	public CascadingMenuView(Context context, List<Category> menuList) {
		super(context);
		this.menuItem = menuList;
		this.context = context;
		init(context);
	}

	public CascadingMenuView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init(context);
	}

	private void init(final Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_region, this, true);
		firstMenuListView = (ListView) findViewById(R.id.listView);
		secondMenuListView = (ListView) findViewById(R.id.listView2);
		thirdMenuListView = (ListView) findViewById(R.id.listView3);

		// 初始化一级主菜单
		firstMenuListViewAdapter = new MenuItemAdapter(context, menuItem,R.drawable.choose_item_selected,R.drawable.choose_eara_item_selector);
		firstMenuListViewAdapter.setTextSize(17);
		if (menuItem.size() > 0 && !menuItem.isEmpty()) {
			firstMenuListViewAdapter.setSelectedPositionNoNotify(firstPosition,menuItem);
		}
		firstMenuListView.setAdapter(firstMenuListViewAdapter);
		firstMenuListViewAdapter.setOnItemClickListener(new MenuItemAdapter.OnItemClickListener() {

			@Override
			public void onItemClick(View view, int position) {
				// 选择主菜单，清空原本子菜单内容，增加新内容
				secondItem.clear();
//				secondItem = getSecondItem(menuItem.get(position).getId());
				mCategoryList = new ArrayList<Category>();
				selectCategory(menuItem.get(position).getId());
//				secondItem.addAll(mCategoryList);
				 
				// 通知适配器刷新
//				secondMenuListViewAdapter.notifyDataSetChanged();
//				secondMenuListViewAdapter.setSelectedPositionNoNotify(0, secondItem);

//				thirdItem.clear();
//				thirdItem = getThirdItem(secondItem.get(0).getId());
//				selectCategory(secondItem.get(0).getId());
//				thirdItem.addAll(mCategoryList);
				// 通知适配器刷新
//				thirdMenuListViewAdapter.notifyDataSetChanged();
//				thirdMenuListViewAdapter.setSelectedPositionNoNotify(0,thirdItem);
			}
		});
	
		
		// 初始化二级主菜单
		mCategoryList = new ArrayList<Category>();
		selectCategory(menuItem.get(firstPosition).getId());// 根据一级选中查询二级
		

		// 初始化三级主菜单
		
		// 设置默认选择
		setDefaultSelect();
	}


	public void setDefaultSelect() {
		firstMenuListView.setSelection(firstPosition);
		secondMenuListView.setSelection(secondPosition);
		thirdMenuListView.setSelection(thirdPosition);
	}

	public void setCascadingMenuViewOnSelectListener(
			CascadingMenuViewOnSelectListener onSelectListener) {
		mOnSelectListener = onSelectListener;
	}
	
	// 查询类别菜单
	public void selectCategory(final String upId){
		Log.i(TAG, "upId==========>"+upId);
		BaseConnection conn = new BaseConnection(context.getApplicationContext());
		conn.addParameter("upId", upId);
		conn.openConnection(com.hwy.httpTool.Constants.GET_SELECT_CATEGORY, "post",
				new CallBack() {
				@Override
				public void executeResult(String result) {
					if(result != null){
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
									mCategoryList.add(category);
								}
								if (upId.length() == 6) {
									handler.sendEmptyMessage(SECOND_SUCCESS);
								} else if (upId.length() == 9) {
									Log.i(TAG, "第三级要开始查询咯----");
									handler.sendEmptyMessage(THIRD_SUCCESS);
								}
							} 
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			});
	}
}