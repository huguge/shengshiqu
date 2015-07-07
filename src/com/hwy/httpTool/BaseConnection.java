package com.hwy.httpTool;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.util.Log;

public class BaseConnection {

	private HttpClient httpClient;
	private Object request;
	
	private HttpResponse response;
	private String mUrl;
	private Context mContext;

	private List<NameValuePair> nameValuePairs;
	private List<File> mFiles;
	private List<String> mFileKeys;

	/**
	 * 构造方法
	 * @param sb 建立字符串
	 * @param httpClient 可以把HttpClient想象成一个浏览器，通过它的API我们可以很方便的发出GET，POST请求
	 * @param request 通过post请求进行数据发送
	 * @param response 返回的结果
	 * @param nameValuePair 保存键值对
	 */
	public BaseConnection(HttpClient httpClient, 
			HttpResponse response, String resultStr,
			List<NameValuePair> nameValuePairs) {
		super();
		this.httpClient = httpClient;
		this.response = response;
		this.nameValuePairs = nameValuePairs;
	}
	
	
	/**
	 * 构造方法
	 */
	public BaseConnection(Context context) {
		super();
		mContext = context;
		nameValuePairs = new ArrayList<NameValuePair>();
		mFiles = new ArrayList<File>();
		mFileKeys = new ArrayList<String>();
	}


	/**
	 * 发送到服务器的表单数据，要在getConnection()方法之前执行
	 * 
	 * @paramkey
	 * @paramvalue
	 */
	public void addParameter(String key, String value) {
		nameValuePairs.add(new BasicNameValuePair(key, value));
	}
	
	/**
	 * 发送到服务器的表单数据，通过这个方法设置文件
	 * @author huweiyang
	 * @date 2015年2月11日
	 * @time 下午12:08:20
	 * @param FileKey  后台接口获取文件的key
	 * @param file  表单文件
	 */
	public void addFile(String FileKey,File file){
		mFileKeys.add(FileKey);
		mFiles.add(file);
	}

	/**
	 * 客户端开始向服务器端发送请求，在新线程中进行连接
	 * 
	 * @throwsException
	 */
	public void openConnection(String url,final String method,final com.hwy.httpTool.CallBack callBack) {
		mUrl=url;
		new Thread(new Runnable() {
			public void run() {
				String result = buildConnection(method);
				callBack.executeResult(result); ///进行回调操作  
			}
		}).start(); 
		
	}
	
	/**
	 * 客户端开始向服务器端发送请求，在新线程中进行连接
	 * 
	 * @throwsException
	 */
	public void openConnectionWithFiles(String url,final CallBack callBack) {
		mUrl=url;
		new Thread(new Runnable() {
			public void run() {
				String result = buildConnection();
				callBack.executeResult(result); ///进行回调操作  
			}
		}).start(); 
		
	}
	
	/**
	 * 建立连接，并且返回数据
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	private String buildConnection(String method){
		if(NetworkHelper.getNetworkType(mContext)==NetworkHelper.NONE){
			return "networkNone";
		}else{
			httpClient = new DefaultHttpClient();
			String resultStr="";
			
			httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 8000);  //请求超时
			httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 8000);  //读取超时
			
			if(method.trim().equals("post")){
				request = new HttpPost(mUrl);
				try {
					((HttpPost) request).setEntity(new UrlEncodedFormEntity(nameValuePairs,HTTP.UTF_8));
					response = httpClient.execute((HttpPost)request);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}else if(method.trim().equals("get")){
				request = new HttpGet(mUrl);
				try {
					response = httpClient.execute((HttpGet)request);
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if(response!=null){
				if (response.getStatusLine().getStatusCode() == 200) {
					HttpEntity entity = response.getEntity();
					if (entity != null) {
						try {
							resultStr = EntityUtils.toString(entity, "UTF-8");
						} catch (ParseException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						return resultStr;
					}else{
						 return "networkTimeout";
					}
				}else {  
					Log.e("web error:",response.getStatusLine().getStatusCode()+"");
					return "networkError";
		        } 
			}else{
				return "networkError";
			}
		}
	}	
	
	/**
	 * 建立连接，并且返回数据
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	private String buildConnection(){
		if(NetworkHelper.getNetworkType(mContext)==NetworkHelper.NONE){
			return "networkNone";
		}else{
			String resultStr="";
			
			httpClient = new DefaultHttpClient();
			
			httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);  //请求超时
			httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);  //读取超时
			
			request = new HttpPost(mUrl);
			try {
				MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
				multipartEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);//设置浏览器兼容模式
				StringBody mStringBody=null;
				File mFile = null;
				FileBody mFileBody = null;
				BasicNameValuePair mNameValuePair = null;
				
//				for(File file:mFiles) {
//					FileBody fileBody = new FileBody(file);//把文件转换成流对象FileBody
//					multipartEntityBuilder.addBinaryBody(file.getName(), file);
//				}	
				
				/*设置文件参数*/
				for(int i=0;i<mFiles.size();i++){
					mFile = (File)mFiles.get(i);
					mFileBody = new FileBody(mFile);
					multipartEntityBuilder.addPart(mFileKeys.get(i), mFileBody);//参数1为文件后台接收的文件key,与接口中的一致
				}
				
				/*设置普通参数*/
				for(int j=0;j<nameValuePairs.size();j++){
					mNameValuePair = (BasicNameValuePair)nameValuePairs.get(j);
					mStringBody = new StringBody(mNameValuePair.getValue(), Charset.forName("UTF-8"));//需设置编码，否则为乱码
					multipartEntityBuilder.addPart(mNameValuePair.getName(), mStringBody);//filename1为请求后台的普通参数;属性 
//					multipartEntityBuilder.addTextBody(mNameValuePair.getName(), mNameValuePair.getValue());
				}
				 
				HttpEntity reqEntity = multipartEntityBuilder.build();
				
				((HttpPost) request).setEntity(reqEntity);
				response = httpClient.execute((HttpPost)request);
			} catch (Exception e) {
				e.printStackTrace();
			}
				
			
			if(response!=null){
				if (response.getStatusLine().getStatusCode() == 200) {
					HttpEntity entity = response.getEntity();
					if (entity != null) {
						try {
							resultStr = EntityUtils.toString(entity, "UTF-8");
						} catch (ParseException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						return resultStr;
					}else{
						 return "networkTimeout";
					}
				}else {  
					Log.e("web error:",response.getStatusLine().getStatusCode()+"");
					return "networkError";
		        } 
			}else{
				return "networkError";
			}
		}
	}	
	
}
