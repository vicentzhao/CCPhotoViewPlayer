package com.ccdrive.photoviewer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;



public class HttpUtil {
	public static final String TAG = "HttpUtil";
	private HttpResponse mHttpResponse ;
	private static HttpUtil httpClient = null ;
	static Map<String,String> urlMap = null ;
	private HttpUtil() {
	}
	
	public static HttpUtil getInstance() {
		if(httpClient==null) {
			httpClient = new HttpUtil() ;
		}
		return httpClient ;
	}
	
	private static int NETWORK_CONNECT_TIMEOUT = 500000;
	private static int NETWORK_SO_TIMEOUT = 500000;
	public static boolean network_enable = false;
	/**
	 * 获取网络连接 200
	 * @param url
	 * @return
	 */
	public boolean connectServerByURL(String url) {
		HttpGet httpRequest = new HttpGet(url) ;
		try {
			HttpParams p = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(p, NETWORK_CONNECT_TIMEOUT);
			HttpConnectionParams.setSoTimeout(p, NETWORK_SO_TIMEOUT);
			HttpClient httpClient = new DefaultHttpClient(p) ;
			
			HttpResponse httpResponse= httpClient.execute(httpRequest) ;
			if(httpResponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK) {
				mHttpResponse = httpResponse ;
				return true ;
			}
		} catch (ClientProtocolException e) {
			httpRequest.abort() ;
			e.printStackTrace();
		} catch (IOException e) {
			httpRequest.abort() ;
			e.printStackTrace();
		}
		return false;
	}

	
	
	/**
	 * 下载相应的信�?
	 * getInputStream
	 * @param url
	 * @return
	 */
	public InputStream getInputStreamFromUrl(String url) {
		InputStream inputStream = null ;
		try {
			if(connectServerByURL(url)) {
				HttpEntity entity = mHttpResponse.getEntity() ;
				inputStream = entity.getContent() ;
				apkLength = entity.getContentLength();
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return inputStream;
	}
	/**
	 *  下载更新的apk
	 * @param url
	 * @return
	 */
	public InputStream getApkInputStream(String url) {
		InputStream inputStream = null ;
		try {
			if(connectServerByURL(url)) {
				HttpEntity entity = mHttpResponse.getEntity() ;
				apkLength = entity.getContentLength();
				inputStream = entity.getContent() ;
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return inputStream;
	}
	
	private long apkLength = -1;
	
	public long getApkLength() {
		return apkLength;
	}

	/**
	 * �?��网络连接是否可用
	 * @param context
	 * @return
	 */
	public static boolean checkNetworkEnabled(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo nwi = cm.getActiveNetworkInfo();
		if(nwi!=null){
			network_enable = nwi.isAvailable(); 
		}
		return network_enable;
	}
}
