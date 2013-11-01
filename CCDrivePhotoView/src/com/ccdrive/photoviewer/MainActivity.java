package com.ccdrive.photoviewer;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.ccdrive.photoviewer.ImageAsyncLoader.ImageCallback;

public class MainActivity extends Activity implements OnClickListener {

	private AQuery aQuery;
	private ImageView image_main;
	private TextView movie_count;
	private TextView movie_allcount;
	private TextView photo_content;
	private Button btn_pageUp;
	private Button btn_pageDown;
	private ArrayList<String> photoList;
	private ArrayList<String> contentList;
	private ArrayList<String> idList;
	private int currentCount = 1; // 当前计数
	private ImageAsyncLoader imageLoader;
	private MATDialog mDialog;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.main_mini);
		image_main = (ImageView) findViewById(R.id.image_main);
		aQuery = new AQuery(MainActivity.this);
		movie_count = (TextView) findViewById(R.id.movie_count);
		movie_allcount = (TextView) findViewById(R.id.movie_allcount);
		btn_pageUp = (Button) findViewById(R.id.btn_app_pageup);
		btn_pageDown = (Button) findViewById(R.id.btn_app_pagedown);
		photo_content=(TextView) findViewById(R.id.photo_content);
		btn_pageUp.setOnClickListener(this);
		btn_pageDown.setOnClickListener(this);
		btn_pageUp.setVisibility(View.GONE);
		btn_pageDown.setSelected(true);
		imageLoader =new ImageAsyncLoader();
		checkVersion();
		initDialog();
		RelativeLayout mainRelativeLayout = (RelativeLayout) findViewById(R.id.admain_view);
//		mainRelativeLayout.setBackgroundResource(R.drawable.menu_bg);
		Intent i = getIntent();
		// menu_in_Animation =
		// AnimationUtils.loadAnimation(MainActivityOld.this,
		// R.anim.buttom_menu_push_in);
		// menu_out_Animation =
		// AnimationUtils.loadAnimation(MainActivityOld.this,
		// R.anim.buttom_menu_push_out);
		// loadingBar = (RelativeLayout) findViewById(R.id.re_movie_loading);
		// ImageView frame_image05 = (ImageView)
		// findViewById(R.id.frame_image05);
		// frame_image05.startAnimation(AnimationUtils.loadAnimation(
		// getApplicationContext(), R.anim.logoanmi));
		// loadingBar.setVisibility(View.VISIBLE);
		// WindowManager windowManager = getWindowManager();
		// Display display = windowManager.getDefaultDisplay();
		// width = display.getWidth();
		// height = display.getHeight();
		// loadingBar.setVisibility(View.VISIBLE);
		String type = i.getStringExtra("type");
		String id = i.getStringExtra("id");
		String token = i.getStringExtra("token");
		String webroot=i.getStringExtra("webRoot");
		String sid = i.getStringExtra("sid");
		int position =i.getIntExtra("position", -1);
		 String currentPage = i.getStringExtra("currentPage");
		 if(position!=-1){
			 currentCount =12*Integer.parseInt(currentPage)+position;
		 }
		  
		 
//		
		System.out.println("收到的type为" + type);
		System.out.println("收到的id为" + id);
		System.out.println("收到的token为" + token);
		// HttpRequest.getInstance().setId(id);
//		HttpRequest.getInstance().setId("137465074908240001");
		 HttpRequest.getInstance().setId(id);
		 HttpRequest.getInstance().setId(sid);
		 HttpRequest.getInstance().setSTATIC_WEB_ROOT(webroot);
		 if(!"".equals(webroot)&&null!=webroot){
			 if(webroot.contains("192")){
				 HttpRequest.getInstance().setSTATIC_WEB_ROOT("http://192.168.1.3:2014/");
			 }else{
				 HttpRequest.getInstance().setSTATIC_WEB_ROOT("http://html.vocy.com/"); 
			 }
		 }
//		HttpRequest.getInstance().setType("3");
		 HttpRequest.getInstance().setType(type);
		 HttpRequest.getInstance().setMytoken(token);
//		HttpRequest.getInstance().setMytoken(
//				"5AC3DF9A-2EB1-3E78-39F2-54F1EC89C494");
		idList = new ArrayList<String>();
		photoList = new ArrayList<String>();
		contentList = new ArrayList<String>();
		// setTestData();
		if (!"3".equals(HttpRequest.getInstance().getType())&&(!"27".equals(HttpRequest.getInstance().getType()))) {
			String path = HttpRequest.getInstance().getURL_DETAIL_INFO();
//			String path =HttpRequest.getInstance().getURL_DETAIL_PHOTOPATH();
			// String path
			// ="http://192.168.1.3:2014/html/workplay/workplay_10_137932008781600622_1.txt";
//			System.out.println("要下载的组图地址为" + path + "====");
//			String path ="http://192.168.1.3:2014/html/workplay/workplay_27_138242209322440622_1.txt";
			aQuery.ajax(path, String.class, new AjaxCallback<String>() {
				@Override
				public void callback(String url, String object,
						AjaxStatus status) {
					// loadingBar.setVisibility(View.GONE);
					if (null != object && !"{}".equals(object)) {
						try {
							//--------------------------------------
//							  JSONObject  json =new JSONObject(object);
//							  JSONArray jaArray =json.getJSONArray("data");
							
							//----------------------------------------
							System.out.println("下载下来的组图信息"+ object);
							JSONArray jaArray = new JSONArray(object);
							if (null != jaArray && jaArray.length() != 0) {
								for (int j = 0; j < jaArray.length(); j++) {
								   	JSONObject jo = jaArray.getJSONObject(j);
									if (!jo.isNull("VIDEOPATH")
											&& !"".equals(jo
													.getString("VIDEOPATH"))) {
										photoList.add(jo.getString("VIDEOPATH"));
										if(!jo.isNull("SYNOPSIS")){
										contentList.add(jo.getString("SYNOPSIS"));
										}
									}
									idList.add(jo.getString("VIDEOID"));
								}
							}
							setPhotoView();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						ToastUtil.showToast(MainActivity.this,
								"没有相关的组图信息,正在退出..");
						try {
							Thread.sleep(1500);
							finish();
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					super.callback(url, object, status);
				}
			});
		} else if ("3".equals(HttpRequest.getInstance().getType())) {
			// loadingBar.setVisibility(View.GONE);
			// setPhotoView();
			String url = HttpRequest.getInstance().getNEWSPICTURES();
			System.out.println("要下载组图的地址为" + url);
			aQuery.ajax(url, String.class, new AjaxCallback<String>() {
				@Override
				public void callback(String url, String object,
						AjaxStatus status) {
					try {
						// loadingBar.setVisibility(View.GONE);
						JSONObject jo = new JSONObject(object);
						JSONArray ja = jo.getJSONArray("data");
						for (int j = 0; j < ja.length(); j++) {
							photoList.add(HttpRequest.getInstance()
									.getIMAGEDOWNDOLADER()
									+ ja.getJSONObject(j).getString("PICPATH"));
							System.out.println("要下载的组图的地址为====+"
									+ HttpRequest.getInstance()
											.getIMAGEDOWNDOLADER()
									+ ja.getJSONObject(j).getString("PICPATH"));
							idList.add(ja.getJSONObject(j).getString("ID"));
						}
						setPhotoView();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			});
		}
		else if("27".equals(HttpRequest.getInstance().getType())){
			String url = HttpRequest.getInstance().getNEWSPICTURES();
		}
		
	}    
//    ArrayList list = new ArrayList();
//    int index=0;
//    int count;//总页数
//    int pagesize=50;
//    int curentpage=1;
//	public HashMap getNextDate(String url){
//		HashMap hs = new HashMap ();		
//		index++;		
//		if(pagesize>index){
//			list.clear();
//			aQuery.ajax(url, String.class, new AjaxCallback<String>(){
//				@Override
//				public void callback(String url, String object,
//						AjaxStatus status) {
//					// TODO Auto-generated method stub
//					super.callback(url, object, status);
//				}
//			});
//			index=1;
//			curentpage++;
//		}
//		return getDate(url);
//	}
//	public HashMap getDate(String url){
//		HashMap hs = new HashMap ();
//		return (HashMap)list.get(index%pagesize);
//	}
//	public HashMap getPreDate(String url){
//		HashMap hs = new HashMap ();
//		index--;
//		if(0>index){
//			list.clear();
//			//请求上一页数据
//			index=pagesize;
//		}
//		return getDate(url);
//	}
	
	 private void getData(String path){
		  //算page=50时，应该显示第几页
		
		 aQuery.ajax(path, String.class, new AjaxCallback<String>(){
			 @Override
			public void callback(String url, String object, AjaxStatus status) {
				 
				  
			}
		 });		 
	 }
	private void setPhotoView() {
		// TODO Auto-generated method stub
		if(photoList.size()==1){
		btn_pageUp.setVisibility(View.GONE);
		btn_pageDown.setVisibility(View.GONE);
		}
		movie_allcount.setText(photoList.size() + "");
		movie_count.setText(currentCount + "");
		if(contentList.size()!=0){
		if(null!=contentList.get(currentCount-1)&&!"".equals(contentList.get(currentCount-1))){
			photo_content.setText(contentList.get(currentCount-1));
		}
		}
//		aQuery.find(R.id.image_main).image(photoList.get(count - 1));
//		aQuery.find(R.id.image_main).image(photoList.get(count - 1), true, true);
//		aQuery.im
		System.out.println("要加载的图片的数据为===========>"+photoList.get(currentCount - 1));
		imageLoader.loadDrawable(photoList.get(currentCount - 1), mDialog,new ImageCallback() {
			@Override
			public void imgeLoader(Bitmap draw, String imgeURL) {
				image_main.setImageBitmap(draw);
			}
		});
	}

	/**
	 * check version
	 */
	private void checkVersion() {
		try {
			PackageManager packageManager = getPackageManager();
			// getPackageName()是你当前类的包名，0代表是获取版本信息
			PackageInfo packInfos = packageManager.getPackageInfo(
					getPackageName(), 0);
			final String version = packInfos.versionName;
			System.out.println("version"+version);
			String packageName = packInfos.packageName;
			String apkPath = HttpRequest.getInstance().getURL_UPDATE_APK()
					+ packageName;
			aQuery.ajax(apkPath, String.class, new AjaxCallback<String>() {
				@Override
				public void callback(String url, String apkStr,
						AjaxStatus status) {
					if (apkStr != null) {
						// TODO Auto-generated method stub
						super.callback(url, apkStr, status);
						// 动漫[TAB]com.ccdrive.comic[TAB]1.1.2.9[TAB]137048843775650001[CR]
						/**
						 * apkEntity[0]=动漫 apkEntity[1]=com.ccdrive.comic
						 * apkEntity[2]=1.1.2.9 apkEntity[3]=137048843775650001
						 */
						String[] apkEntity = apkStr.split("\\[TAB\\]|\\[CR\\]");
						if (!version.equals(apkEntity[2])) {
							System.out.println(apkEntity[2]);
							HttpRequest.getInstance().setApkuuid(apkEntity[3]);
							String path = HttpRequest.getInstance()
									.getURL_DOWN_UPDATE_APK();
//							setUpdateDiago(path, apkEntity[1]);
//							System.out.println("更新的地址为" + path);
							UpdateApk.setInstall(aQuery.getContext(), apkEntity[1], path);
						}
					}
				}
			});
		} catch (NameNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	/**
	 * appearUpdateDialog
	 * 
	 * @param path
	 *            Update url path @ param desc Update content
	 */
	void setUpdateDiago(final String path, final String apkName) {
		final Handler hd = new Handler();
		// TODO Auto-generated method stub
		Dialog dialog = new AlertDialog.Builder(aQuery.getContext())
				.setTitle("发现新版本")
				.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						new AsyncTask<Void, Void, Void>() {
							@Override
							protected Void doInBackground(Void... params) {
								UpdateVersion uv = UpdateVersion.instance(
										aQuery.getContext(), hd, true, false);
								uv.setUpdateUrl(path);
								uv.setLoadApkName(apkName);
								uv.run();
								return null;
							}
						}.execute();
					}
				})
				.setNegativeButton(getResources().getString(R.string.cancle),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								dialog.dismiss();
							}
						}).create();
		dialog.show();
		dialog.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					return true;
				}
				return false;
			}
		});
	}

	@Override
	public void onClick(View v) {
		 if(mDialog.isShowing()){
		switch (v.getId()) {
	
		case R.id.btn_app_pagedown:
			if (currentCount == photoList.size()) {
				ToastUtil.showToast(aQuery.getContext(), "已经是最后一页了");
				return;
			}
			if (currentCount == photoList.size() - 1) {
				btn_pageDown.setVisibility(View.GONE);
			} else {
				btn_pageDown.setVisibility(View.VISIBLE);
				btn_pageUp.setVisibility(View.VISIBLE);
			}
//			aQuery.find(R.id.image_main).image(photoList.get(count - 1));
			currentCount++;
			if(null!=contentList.get(currentCount-1)&&!"".equals(contentList.get(currentCount-1))){
				photo_content.setText(contentList.get(currentCount-1));
			}
			imageLoader.loadDrawable(photoList.get(currentCount - 1), mDialog,new ImageCallback() {
				@Override
				public void imgeLoader(Bitmap draw, String imgeURL) {
					image_main.setImageBitmap(draw);
				}
			});
			movie_count.setText(currentCount + "");
			break;

		case R.id.btn_app_pageup:
			if (currentCount == 1) {
				ToastUtil.showToast(aQuery.getContext(), "已经是第一页");
				return;
			}
			if (currentCount == 2) {
				btn_pageUp.setVisibility(View.GONE);
			} else {
				btn_pageDown.setVisibility(View.VISIBLE);
				btn_pageUp.setVisibility(View.VISIBLE);
			}
			currentCount--;
			if(null!=contentList.get(currentCount-1)&&!"".equals(contentList.get(currentCount-1))){
				photo_content.setText(contentList.get(currentCount-1));
			}
//			aQuery.find(R.id.image_main).image(photoList.get(count - 1));
			imageLoader.loadDrawable(photoList.get(currentCount - 1),mDialog, new ImageCallback() {
				@Override
				public void imgeLoader(Bitmap draw, String imgeURL) {
					image_main.setImageBitmap(draw);
				}
			});
			movie_count.setText(currentCount + "");
			break;
		}
		 }
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		 if(!mDialog.isShowing()&&photoList.size()!=1){

		if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			if (currentCount == photoList.size()) {
				ToastUtil.showToast(aQuery.getContext(), "已经是最后一页了");
				btn_pageDown.setVisibility(View.GONE);
				btn_pageUp.setVisibility(View.VISIBLE);

			} else {
//				if (count == photoList.size() - 1) {
//					btn_pageDown.setVisibility(View.GONE);
//						btn_pageUp.setVisibility(View.VISIBLE);
//				} else {
//					btn_pageDown.setVisibility(View.VISIBLE);
//					btn_pageUp.setVisibility(View.VISIBLE);
//				}
				btn_pageDown.setVisibility(View.VISIBLE);
				btn_pageDown.setSelected(true);
				btn_pageUp.setSelected(false);
				btn_pageUp.setVisibility(View.VISIBLE);
				currentCount++;
//				aQuery.find(R.id.image_main).image(photoList.get(count - 1));
				if(null!=contentList.get(currentCount-1)&&!"".equals(contentList.get(currentCount-1))){
					photo_content.setText(contentList.get(currentCount-1));
				}
				imageLoader.loadDrawable(photoList.get(currentCount - 1),mDialog, new ImageCallback() {
					@Override
					public void imgeLoader(Bitmap draw, String imgeURL) {
						image_main.setImageBitmap(draw);
					}
				});
				movie_count.setText(currentCount + "");
			}
		}
		if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
			
			if (currentCount == 1) {
				ToastUtil.showToast(aQuery.getContext(), "已经是第一页");
				btn_pageUp.setVisibility(View.GONE);
				btn_pageDown.setVisibility(View.VISIBLE);
			} else {
				btn_pageUp.setSelected(true);
				btn_pageDown.setSelected(false);
				btn_pageDown.setVisibility(View.VISIBLE);
				btn_pageUp.setVisibility(View.VISIBLE);
//				if (count == 2) {
//					btn_pageUp.setVisibility(View.GONE);
//					btn_pageDown.setVisibility(View.VISIBLE);
//				} else {
//					btn_pageDown.setVisibility(View.VISIBLE);
//					btn_pageUp.setVisibility(View.VISIBLE);
//				}
				currentCount--;
				if(null!=contentList.get(currentCount-1)&&!"".equals(contentList.get(currentCount-1))){
					photo_content.setText(contentList.get(currentCount-1));
				}
//				aQuery.find(R.id.image_main).image(photoList.get(count - 1));
				imageLoader.loadDrawable(photoList.get(currentCount - 1),mDialog, new ImageCallback() {
					@Override
					public void imgeLoader(Bitmap draw, String imgeURL) {
						image_main.setImageBitmap(draw);
					}
				});
				movie_count.setText(currentCount + "");
			}
		}
		 }
		return super.onKeyDown(keyCode, event);
	}
	
	private MATDialog initDialog() {
	mDialog = new MATDialog(aQuery.getContext(),R.style.dialog);
	mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
//	mDialog.showMessage("正在加载");
	return mDialog;
}

}
