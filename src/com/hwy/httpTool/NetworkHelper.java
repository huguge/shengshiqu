package com.hwy.httpTool;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class NetworkHelper {

	public final static int NONE = 0; // 无网络
	public final static int MOBILE = 1; // 手机网络
	public final static int WIFI = 2; // Wi-Fi

	/**
	 * 获取当前网络状态
	 * 
	 * @param context
	 * @return
	 */
	public static int getNetworkType(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		State state=null;
		
		if(!isPad(context)){
			// 是否手机网络
			state = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
			if (state == State.CONNECTED || state == State.CONNECTING) {
				return MOBILE;
			}
			
		}
		
		// Wifi网络判断
		state = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		if (state == State.CONNECTED || state == State.CONNECTING) {
			return WIFI;
		}
		return NONE;
	}
	
	public static boolean isNetworkConnected(Context context) {  
	    if (context != null) {  
	        ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);  
	        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();  
	        if (mNetworkInfo != null) {  
	            return mNetworkInfo.isAvailable();  
	        }  
	    }  
	    return false;  
	} 

	public static String getMobileNetworkType(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		String str = "";
		if (info != null && info.getType() == ConnectivityManager.TYPE_MOBILE) {
			switch (info.getSubtype()) {
			case 1:
				str = "联通2G";
				break;
			case 2:
				str = "移动2G";
				break;
			case 3:
				str = "联通3G";
				break;
			case 4:
				str = "电信2G";
				break;
			case 5:
				str = "电信3G";
				break;
			case 6:
				str = "电信3G";
				break;
			case 8:
				str = "联通3G";
				break;
			case 12:
				str = "电信3G";
				break;
			}
		}
		return str;
	}
	
    /** 
     * 判断是否为平板 
     *  
     * @return 
     */  
    private static boolean isPad(Context context) {  
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);  
        Display display = wm.getDefaultDisplay();  
        // 屏幕宽度  
        float screenWidth = display.getWidth();  
        // 屏幕高度  
        float screenHeight = display.getHeight();  
        DisplayMetrics dm = new DisplayMetrics();  
        display.getMetrics(dm);  
        double x = Math.pow(dm.widthPixels / dm.xdpi, 2);  
        double y = Math.pow(dm.heightPixels / dm.ydpi, 2);  
        // 屏幕尺寸  
        double screenInches = Math.sqrt(x + y);  
        // 大于6尺寸则为Pad  
        if (screenInches >= 7.0) {  
            return true;  
        }  
        return false;  
    }  

}
