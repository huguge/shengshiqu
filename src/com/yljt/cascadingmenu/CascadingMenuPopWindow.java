package com.yljt.cascadingmenu;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

import com.yljt.cascadingmenu.interfaces.CascadingMenuViewOnSelectListener;
import com.yljt.model.Category;

/**
 * @author LILIN 下午1:48:27 提供PopWindow调用方法
 */
public class CascadingMenuPopWindow extends PopupWindow {

	private Context context;
	private CascadingMenuView cascadingMenuView ;
	private List<Category> categories = new ArrayList<Category>();
	// 提供给外的接口
	private CascadingMenuViewOnSelectListener menuViewOnSelectListener;

	public void setMenuItems(List<Category> categories) {
		this.categories = categories;
	}

	public void setMenuViewOnSelectListener(
			CascadingMenuViewOnSelectListener menuViewOnSelectListener) {
		this.menuViewOnSelectListener = menuViewOnSelectListener;
	}

	public CascadingMenuPopWindow(Context context, List<Category> list) {
		super(context);
		this.context = context;
		this.categories = list;
		init();
	}

	public void init() {
		// 实例化级联菜单
		cascadingMenuView = new CascadingMenuView(context, categories);
		setContentView(cascadingMenuView);
		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.MATCH_PARENT);
		// 设置回调接口
		cascadingMenuView.setCascadingMenuViewOnSelectListener(new MCascadingMenuViewOnSelectListener());
	}

	// 级联菜单选择回调接口
	class MCascadingMenuViewOnSelectListener implements
			CascadingMenuViewOnSelectListener {

		@Override
		public void getValue(Category menuItem) {
			if (menuViewOnSelectListener != null) {
				menuViewOnSelectListener.getValue(menuItem);
				dismiss();
			}
		}

	}
}
