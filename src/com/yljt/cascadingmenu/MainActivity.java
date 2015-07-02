package com.yljt.cascadingmenu;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.yljt.cascadingmenu.interfaces.CascadingMenuViewOnSelectListener;
import com.yljt.model.Area;

public class MainActivity extends FragmentActivity implements OnClickListener {

	ArrayList<Area> provinceList;
	// 两级联动菜单数据
	private CascadingMenuFragment cascadingMenuFragment = null;
	private CascadingMenuPopWindow cascadingMenuPopWindow = null;

	private Button menuViewPopWindow;
	private Button menuViewFragment;
	private DBhelper dBhelper;

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
		provinceList = dBhelper.getProvince();

		menuViewPopWindow.setOnClickListener(this);
		menuViewFragment.setOnClickListener(this);

	}

	public void showFragmentMenu() {
		FragmentTransaction fragmentTransaction = getSupportFragmentManager()
				.beginTransaction();
		fragmentTransaction.setCustomAnimations(R.anim.short_menu_pop_in,
				R.anim.short_menu_pop_out);

		if (cascadingMenuFragment == null) {
			cascadingMenuFragment = CascadingMenuFragment.getInstance();
			cascadingMenuFragment.setMenuItems(provinceList);
			cascadingMenuFragment
					.setMenuViewOnSelectListener(new NMCascadingMenuViewOnSelectListener());
			fragmentTransaction.replace(R.id.liner, cascadingMenuFragment);
		} else {
			fragmentTransaction.remove(cascadingMenuFragment);
			cascadingMenuFragment = null;
		}
		fragmentTransaction.commit();
	}

	private void showPopMenu() {
		if (cascadingMenuPopWindow == null) {
			cascadingMenuPopWindow = new CascadingMenuPopWindow(
					getApplicationContext(), provinceList);
			cascadingMenuPopWindow
					.setMenuViewOnSelectListener(new NMCascadingMenuViewOnSelectListener());
			cascadingMenuPopWindow.showAsDropDown(menuViewPopWindow, 5, 5);
		} else if (cascadingMenuPopWindow != null
				&& cascadingMenuPopWindow.isShowing()) {
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
		public void getValue(Area area) {
			cascadingMenuFragment = null;
			Toast.makeText(getApplicationContext(), "" + area.getName(), 1000)
					.show();
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
