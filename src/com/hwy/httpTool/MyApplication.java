package com.hwy.httpTool;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.app.Application;


public class MyApplication extends Application{

	private Map<String, Object> mData;

	public Map<String, Object> getmData() {
		return mData;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		mData = new HashMap<String, Object>();
		// synchronized the map，使mData支持多线程数据同步,虽然网上说是个老方法，但这里暂时先这样用，因为数据不多毕竟
		mData = Collections.synchronizedMap(mData);
		// then restore your map
		
	}
	
	public void onTerminate() {
		super.onTerminate();
		
		//save data of the map
	}
	
}
