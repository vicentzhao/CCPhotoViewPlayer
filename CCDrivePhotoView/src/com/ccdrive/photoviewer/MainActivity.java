package com.ccdrive.photoviewer;

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

public class MainActivity extends Activity implements OnClickListener {

	private AQuery aQuery;
	private ImageView image_main;
	private TextView movie_count;
	private TextView movie_allcount;
	private Button btn_pageUp;
	private Button btn_pageDown;
	private ArrayList<String> photoList;
	private ArrayList<String> idList;
	private int count = 1; // 当前页

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
		btn_pageUp.setOnClickListener(this);
		btn_pageDown.setOnClickListener(this);
		checkVersion();
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
		Intent intent = getIntent();
		String type = i.getStringExtra("type");
		String id = i.getStringExtra("id");
		String token = i.getStringExtra("token");
		System.out.println("收到的type为" + type);
		System.out.println("收到的id为" + id);
		System.out.println("收到的token为" + token);
		// HttpRequest.getInstance().setId(id);
//		HttpRequest.getInstance().setId("137465074908240001");
		 HttpRequest.getInstance().setId(id);
//		HttpRequest.getInstance().setType("3");
		 HttpRequest.getInstance().setType(type);
		 HttpRequest.getInstance().setMytoken(token);
//		HttpRequest.getInstance().setMytoken(
//				"5AC3DF9A-2EB1-3E78-39F2-54F1EC89C494");
		idList = new ArrayList<String>();
		photoList = new ArrayList<String>();
		// setTestData();
		if (!"3".equals(HttpRequest.getInstance().getType())) {
			String path = HttpRequest.getInstance().getURL_DETAIL_INFO();
			// String path
			// ="http://192.168.1.3:2014/html/workplay/workplay_10_137932008781600622_1.txt";
			System.out.println("要下载的组图地址为" + path + "====");
			aQuery.ajax(path, String.class, new AjaxCallback<String>() {
				@Override
				public void callback(String url, String object,
						AjaxStatus status) {
					// loadingBar.setVisibility(View.GONE);
					if (null != object && !"{}".equals(object)) {
						try {
							System.out.println("下载下来的组图信息" + object);
							JSONArray jaArray = new JSONArray(object);
							if (null != jaArray && jaArray.length() != 0) {
								for (int j = 0; j < jaArray.length(); j++) {
								   	JSONObject jo = jaArray.getJSONObject(j);
									if (!jo.isNull("VIDEOPATH")
											&& !"".equals(jo
													.getString("VIDEOPATH"))) {
										photoList.add(jo.getString("VIDEOPATH"));
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
	}

	private void setPhotoView() {
		// TODO Auto-generated method stub
		btn_pageUp.setVisibility(View.GONE);
		movie_allcount.setText(photoList.size() + "");
		movie_count.setText(count + "");
//		aQuery.find(R.id.image_main).image(photoList.get(count - 1));
		aQuery.find(R.id.image_main).image(photoList.get(count - 1), true, true);
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
							HttpRequest.getInstance().setApkuuid(apkEntity[3]);
							String path = HttpRequest.getInstance()
									.getURL_DOWN_UPDATE_APK();
							setUpdateDiago(path, apkEntity[1]);
							System.out.println("更新的地址为" + path);
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

		switch (v.getId()) {
		case R.id.btn_app_pagedown:
			if (count == photoList.size()) {
				ToastUtil.showToast(aQuery.getContext(), "已经是最后一页了");
				return;
			}
			if (count == photoList.size() - 1) {
				btn_pageDown.setVisibility(View.GONE);
			} else {
				btn_pageDown.setVisibility(View.VISIBLE);
				btn_pageUp.setVisibility(View.VISIBLE);
			}
			aQuery.find(R.id.image_main).image(photoList.get(count - 1));
			count++;
			movie_count.setText(count + "");
			break;

		case R.id.btn_app_pageup:
			if (count == 1) {
				ToastUtil.showToast(aQuery.getContext(), "已经是第一页");
				return;
			}
			if (count == 2) {
				btn_pageUp.setVisibility(View.GONE);
			} else {
				btn_pageDown.setVisibility(View.VISIBLE);
				btn_pageUp.setVisibility(View.VISIBLE);
			}
			aQuery.find(R.id.image_main).image(photoList.get(count - 1));
			count--;
			movie_count.setText(count + "");
			break;
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			if (count == photoList.size()) {
				ToastUtil.showToast(aQuery.getContext(), "已经是最后一页了");

			} else {
				if (count == photoList.size() - 1) {
					btn_pageDown.setVisibility(View.GONE);
				} else {
					btn_pageDown.setVisibility(View.VISIBLE);
					btn_pageUp.setVisibility(View.VISIBLE);
				}
				aQuery.find(R.id.image_main).image(photoList.get(count - 1));
				count++;
				movie_count.setText(count + "");
			}
		}
		if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
			if (count == 1) {
				ToastUtil.showToast(aQuery.getContext(), "已经是第一页");
			} else {
				if (count == 2) {
					btn_pageUp.setVisibility(View.GONE);
				} else {
					btn_pageDown.setVisibility(View.VISIBLE);
					btn_pageUp.setVisibility(View.VISIBLE);
				}
				aQuery.find(R.id.image_main).image(photoList.get(count - 1));
				count--;
				movie_count.setText(count + "");
			}
		}
		return super.onKeyDown(keyCode, event);
	}

}
