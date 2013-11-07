package com.ccdrive.photoviewer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

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
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.ccdrive.photoviewer.bean.ArtsBean;
import com.ccdrive.photoviewer.util.HttpRequest;
import com.ccdrive.photoviewer.util.ImageAsyncLoader;
import com.ccdrive.photoviewer.util.ImageAsyncLoader.ImageCallback;
import com.ccdrive.photoviewer.util.JSONUtil;
import com.ccdrive.photoviewer.util.PagenationBean;
import com.ccdrive.photoviewer.util.ToastUtil;
import com.ccdrive.photoviewer.util.UpdateApk;
import com.ccdrive.photoviewer.util.UpdateVersion;

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
	private int listcount = 0;
	private PagenationBean pagenationBean = new PagenationBean();
	private boolean isLoading ; //判断是否正在加载
    private ArrayList<ArtsBean> arts;  //加载过来艺术集合
    private int currentPage =1;//  当前页
    private boolean  isFlag = true;
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
		imageLoader =ImageAsyncLoader.getInstance();
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
		String token= i.getStringExtra("mytoken");
		System.out.println("传过来的sid的============>"+token);
		String webroot=i.getStringExtra("webRoot");
		String sid = i.getStringExtra("sid");
//		intent.putExtra("artFlag", "27_1");
		String artFlag =i.getStringExtra("artFlag");
		System.out.println("传过来的sid的============>"+artFlag);
		if(!"".equals(artFlag)&&null!=artFlag){
			isFlag =false;
		}
		System.out.println("传过来的sid的============>"+sid);
		int position =i.getIntExtra("position", -1);
		System.out.println("传过来的position的============>"+position);
		
		int currentPage = i.getIntExtra("currentPage",-1);
		 System.out.println("传过来的currentpage"+currentPage);
		 if(position!=-1&&currentCount!=-1){
			 currentCount =12*(currentPage-1)+position+1;
			 currentPage=(currentCount+49)/50;
			 HttpRequest.getInstance().setCurrentPage(currentPage);
			 listcount=currentCount%50-1;
				System.out.println("传过来的count的============>"+listcount);
		 }
		System.out.println("收到的type为" + type);
		System.out.println("收到的id为" + id);
		System.out.println("收到的token为" + token);
		// HttpRequest.getInstance().setId(id);
//		HttpRequest.getInstance().setId("137465074908240001");
		 HttpRequest.getInstance().setId(id);
		 HttpRequest.getInstance().setSid(sid);
		 HttpRequest.getInstance().setWEB_ROOT(webroot);
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
			photo_content.setVisibility(View.GONE);
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
			photo_content.setVisibility(View.GONE);
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
			String url;
			System.out.println(isFlag+"isflage========");
			if(isFlag){
			 url= HttpRequest.getInstance().getArtsAllPhotos();
			}else{
				url =HttpRequest.getInstance().getArtsPeoPleAllPhotos();
			}
			System.out.println("要寻找的地址为+========》"+url);
			if(currentCount!=1){
				btn_pageDown.setVisibility(View.VISIBLE);
				btn_pageUp.setVisibility(View.VISIBLE);
			}
			getAllArtsInfo(url, false,false);
			
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
		 if(!mDialog.isShowing()){
		switch (v.getId()) {
	
		case R.id.btn_app_pagedown:
			if(!mDialog.isShowing()&&photoList.size()!=1&&!"27".equals(HttpRequest.getInstance().getType())){
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
			if(contentList.size()!=0){
				if(null!=contentList.get(currentCount-1)&&!"".equals(contentList.get(currentCount-1))){
					photo_content.setText(contentList.get(currentCount-1));
				}
				}
			imageLoader.loadDrawable(photoList.get(currentCount - 1), mDialog,new ImageCallback() {
				@Override
				public void imgeLoader(Bitmap draw, String imgeURL) {
					image_main.setImageBitmap(draw);
				}
			});
			movie_count.setText(currentCount + "");
			}	 else if("27".equals(HttpRequest.getInstance().getType())){
					 getNextPage();
			 }
			break;

		case R.id.btn_app_pageup:
			if(!mDialog.isShowing()&&photoList.size()!=1&&!"27".equals(HttpRequest.getInstance().getType())){
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
			if(contentList.size()!=0){
				if(null!=contentList.get(currentCount-1)&&!"".equals(contentList.get(currentCount-1))){
					photo_content.setText(contentList.get(currentCount-1));
				}
				}
//			aQuery.find(R.id.image_main).image(photoList.get(count - 1));
			imageLoader.loadDrawable(photoList.get(currentCount - 1),mDialog, new ImageCallback() {
				@Override
				public void imgeLoader(Bitmap draw, String imgeURL) {
					image_main.setImageBitmap(draw);
				}
			});
			movie_count.setText(currentCount + "");
			} else if("27".equals(HttpRequest.getInstance().getType())){
				 getPrePage();
		 }
			break;
		}
		 }
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		  
		 if(!mDialog.isShowing()){
		
		 if(!mDialog.isShowing()&&photoList.size()!=1&&!"27".equals(HttpRequest.getInstance().getType())){
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
				if(contentList.size()!=0){
				if(null!=contentList.get(currentCount-1)&&!"".equals(contentList.get(currentCount-1))){
					photo_content.setText(contentList.get(currentCount-1));
				}
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
				if(contentList.size()!=0){
				if(null!=contentList.get(currentCount-1)&&!"".equals(contentList.get(currentCount-1))){
					photo_content.setText(contentList.get(currentCount-1));
				}
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
		 else if("27".equals(HttpRequest.getInstance().getType())){
			 if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
				 getPrePage();
			 }
			 if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
				 getNextPage();
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
	
	private boolean  getNextPage(){
		if (isLoading)
			return true;
		if (listcount == arts.size() - 1 && pagenationBean.isLastPage()) {
			btn_pageDown.setVisibility(View.INVISIBLE);
			btn_pageUp.setSelected(true);
			btn_pageDown.setSelected(false);
			return true;
		}
		listcount++;
		if (listcount > pagenationBean.getPageSize() - 1) {
			currentPage = currentPage + 1;
			currentCount = currentCount + 1;
			HttpRequest.getInstance().setCurrentPage(currentPage);
			if(isFlag){
				getAllArtsInfo(HttpRequest.getInstance().getArtsAllPhotos(),true ,false);
				}else{
					getAllArtsInfo(HttpRequest.getInstance().getArtsPeoPleAllPhotos(),true ,false);
				}
		} else {
			currentCount = currentCount + 1;
			setPage();	
			btn_pageUp.setVisibility(View.VISIBLE);
			btn_pageDown.setVisibility(View.VISIBLE);
			btn_pageDown.setSelected(true);
			btn_pageUp.setSelected(false);
//			iv_img.requestFocus();
//			iv_img.setFocusable(true);
		}
		return true;
		
	}
	
	 private boolean getPrePage(){
			System.out.println("listcount==========>"+listcount);
		 
//			if (isLoading)
//				return true;
//			if (count == arts.size() - 1 && pagenationBean.isLastPage()) {
//				btn_pageDown.setVisibility(View.INVISIBLE);
//				btn_pageUp.setSelected(true);
//				btn_pageDown.setSelected(false);
//				return true;
//			}
//			count--;
//			if (count > pagenationBean.getPageSize() - 1) {
//				currentPage = currentPage - 1;
//				currentCount = currentCount - 1;
//				HttpRequest.getInstance().setCurrentPage(currentPage);
//				getAllArtsInfo(HttpRequest.getInstance().getArtsAllPhotos(),false);
//				return true;
//			} else {
//				currentCount = currentCount - 1;
//				setPage();
//				btn_pageUp.setVisibility(View.VISIBLE);
//				btn_pageDown.setVisibility(View.VISIBLE);
//				btn_pageDown.setSelected(true);
//				btn_pageUp.setSelected(false);
////				iv_img.requestFocus();
////				iv_img.setFocusable(true);
//			}
//			
//			return true;

			if (isLoading)
				return true;
			if (pagenationBean.isFirstPage() && currentCount == 1) {
				btn_pageUp.setVisibility(View.INVISIBLE);
				btn_pageDown.setSelected(true);
				btn_pageUp.setSelected(false);
				return true;

			} else {
				if (listcount == 0 && currentPage > 1) {
					currentPage = currentPage - 1;
					currentCount = currentCount - 1;
					HttpRequest.getInstance().setCurrentPage(currentPage);
					if(isFlag){
					getAllArtsInfo(HttpRequest.getInstance().getArtsAllPhotos(),false ,true);
					}else{
						getAllArtsInfo(HttpRequest.getInstance().getArtsPeoPleAllPhotos(),false ,true);
					}
					return true;
				} else {
					System.out.println("listcount==========>>>"+listcount);
					listcount--;
					currentCount = currentCount - 1;
					setPage();
					btn_pageDown.setVisibility(View.VISIBLE);
					btn_pageUp.setVisibility(View.VISIBLE);
					btn_pageUp.setSelected(true);
					btn_pageDown.setSelected(false);
					if (currentCount == 1) {
						btn_pageUp.setVisibility(View.INVISIBLE);
						btn_pageDown.setSelected(true);
						btn_pageUp.setSelected(false);
					}
					return true;
				}
			}
	 };
	
	
	private void setPage(){
		movie_count.setText(currentCount + "");
		photo_content.setText(arts.get(listcount).getContent());
		imageLoader.loadDrawable(arts.get(listcount).getVideoPath(), mDialog, new ImageCallback() {
			
			@Override
			public void imgeLoader(Bitmap draw, String imgeURL) {
				 image_main.setImageBitmap(draw);
			}
		});
		
		
	}
	private void  getAllArtsInfo(final String url,final boolean isnofirst,final boolean isleft){
		   new AsyncTask<Void, Void, String>(){
			   private String stream2String;
			@Override
			protected void onPreExecute() {
				mDialog.show();
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(String result) {
				mDialog.dismiss();
				isLoading=false;
				if(isnofirst){
					listcount =0;
				}else if(isleft){
					listcount=49;
				}
				System.out.println("在postexexute的测试中=========="+listcount);
				if (null != result && !"".equals(result)) {
					try {
						JSONObject  js=new JSONObject(result);
						JSONObject jopage=js.getJSONObject("page");
						int totalpage=Integer.parseInt(jopage.getString("totalRows"));
						movie_allcount.setText(totalpage+"");
						movie_count.setText(currentCount+"");
						pagenationBean.init(jopage.getString("currentPage"), jopage.getString("pageSize"), Integer.parseInt(jopage.getString("totalRows")));
						arts= JSONUtil.getArts(result);
					
							imageLoader.loadDrawable(arts.get(listcount).getVideoPath(), mDialog, new ImageCallback() {
								
								@Override
								public void imgeLoader(Bitmap draw, String imgeURL) {
									image_main.setImageBitmap(draw);
								}
							});
							
							photo_content.setText(arts.get(listcount).getContent());
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			@Override
			protected String doInBackground(Void... params) {
				try {
					isLoading = true;
					System.out.println("下载的地址为===========" + url);
					URL urls = new URL(url);
					URLConnection conn = urls.openConnection();
					InputStream stream = conn.getInputStream();
					stream2String = Stream2String(stream);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return stream2String;
			
			}
			   
		   }.execute();
	}
	
	private String Stream2String(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is),
				16 * 1024); 
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) { 
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
	

}
