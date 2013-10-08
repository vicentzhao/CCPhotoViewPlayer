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
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.ccdrive.photoviewer.ImageAsyncLoader.ImageCallback;

public class MainActivityOld extends Activity {
	private ImageView imageSwitcher;
	private HashMap<String, Bitmap> bitMapHashMap;
	private ArrayList<String> emptyPathList;
	private LinearLayout lay_list;
	// private Deque<String> stack ;
	private boolean isShow = false;
	private Animation menu_in_Animation;
	private Animation menu_out_Animation;
	private AQuery aQuery;
	private RelativeLayout loadingBar;
	private int count = 1; // 判断是否到最后
	private ArrayList<String> photoList;
	private ArrayList<String> idList;

	private View oldview;
	private TextView movie_count;
	private TextView movie_allcount;
	private RelativeLayout movie_imageview_re;
	private ImageAsyncLoader imageLoader;
	private int width;
	private int height;
	private int endCount; // 判断当前页是否到最后，是否需要翻页
	
	/* String path ="http://192.168.1.3:2014/html/workplay/workplay_10_137932008781600622_1.txt"; */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		imageSwitcher = (ImageView) findViewById(R.id.imageswitcher);
		lay_list = (LinearLayout) findViewById(R.id.lay_list);
		aQuery = new AQuery(MainActivityOld.this);
		movie_count = (TextView) findViewById(R.id.movie_count);
		movie_allcount = (TextView) findViewById(R.id.movie_allcount);
		checkVersion();
		RelativeLayout mainRelativeLayout = (RelativeLayout) findViewById(R.id.admain_view);
		movie_imageview_re = (RelativeLayout) findViewById(R.id.movie_imageview_re);
		mainRelativeLayout.setBackgroundResource(R.drawable.menu_bg);
		imageLoader = new ImageAsyncLoader();
		Intent i = getIntent();
		menu_in_Animation = AnimationUtils.loadAnimation(MainActivityOld.this,
				R.anim.buttom_menu_push_in);
		menu_out_Animation = AnimationUtils.loadAnimation(MainActivityOld.this,
				R.anim.buttom_menu_push_out);
		loadingBar = (RelativeLayout) findViewById(R.id.re_movie_loading);
		ImageView frame_image05 = (ImageView) findViewById(R.id.frame_image05);
		frame_image05.startAnimation(AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.logoanmi));
		loadingBar.setVisibility(View.VISIBLE);
		WindowManager windowManager = getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		width = display.getWidth();
		height = display.getHeight();
		// loadingBar.setVisibility(View.VISIBLE);
		Intent intent = getIntent();
		String type = i.getStringExtra("type");
		String id = i.getStringExtra("id");
		String token = i.getStringExtra("token");
		System.out.println("收到的type为" + type);
		System.out.println("收到的id为" + id);
		System.out.println("收到的token为" + token);
//		HttpRequest.getInstance().setId(id);
		HttpRequest.getInstance().setId("137465074908240001");
//		HttpRequest.getInstance().setId(id);
		HttpRequest.getInstance().setType("3");
//		HttpRequest.getInstance().setType(type);
//		HttpRequest.getInstance().setMytoken(token);
		HttpRequest.getInstance().setMytoken("5AC3DF9A-2EB1-3E78-39F2-54F1EC89C494");
		idList = new ArrayList<String>();
		photoList=new ArrayList<String>();
//		setTestData();
		if(!"3".equals(HttpRequest.getInstance().getType())){
			String path =HttpRequest.getInstance().getURL_DETAIL_INFO();
//			String path ="http://192.168.1.3:2014/html/workplay/workplay_10_137932008781600622_1.txt";
			System.out.println("要下载的组图地址为"+path+"====");
			aQuery.ajax(path, String.class, new AjaxCallback<String>(){
				@Override
				public void callback(String url, String object,
						AjaxStatus status) {
					loadingBar.setVisibility(View.GONE);
					if(null!=object&&!"{}".equals(object)){
					try {
						System.out.println("下载下来的组图信息"+object);
						JSONArray jaArray = new JSONArray(object);
						if(null!=jaArray&&jaArray.length()!=0){
							for (int j = 0; j < jaArray.length(); j++) {
								JSONObject jo  = jaArray.getJSONObject(j);
								if(!jo.isNull("VIDEOPATH")&&!"".equals(jo.getString("VIDEOPATH"))){
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
					}else{
						ToastUtil.showToast(MainActivityOld.this, "没有相关的组图信息,正在退出..");
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
		}else if("3".equals(HttpRequest.getInstance().getType())){
//			loadingBar.setVisibility(View.GONE);
//			setPhotoView();
			String url =HttpRequest.getInstance().getNEWSPICTURES();
			System.out.println("要下载组图的地址为"+url);
			aQuery.ajax(url, String.class, new AjaxCallback<String>(){
				@Override
				public void callback(String url, String object,
						AjaxStatus status) {
					 try {
						 loadingBar.setVisibility(View.GONE);
						JSONObject jo = new JSONObject(object);
						JSONArray ja = jo.getJSONArray("data");
						for (int j = 0; j < ja.length(); j++) {
							photoList.add(HttpRequest.getInstance().getIMAGEDOWNDOLADER()+ja.getJSONObject(j).getString("PICPATH"));
							System.out.println("要下载的组图的地址为====+"+HttpRequest.getInstance().getIMAGEDOWNDOLADER()+ja.getJSONObject(j).getString("PICPATH"));
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
	 private  void setPhotoView(){
			if (null==photoList||photoList.size() == 0) {
				ToastUtil.showToast(MainActivityOld.this, "没有相关的组图信息,正在退出..");
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				finish();
			}
			movie_allcount.setText("" + photoList.size());
			bitMapHashMap = new HashMap<String, Bitmap>();
			movie_count.setText("" + count);
			if (photoList.size() != 0) {
				String imageUrl = photoList.get(0);
				String id = idList.get(0);
				imageLoader.loadDrawable(imageUrl,false,id,new ImageCallback() {
					@Override
					public void imgeLoader(Bitmap draw, String imgeURL) {
						imageSwitcher.setImageBitmap(draw);
					}
				});
				imageSwitcher.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						
					}
				});
//				imageSwitcher.setOnTouchListener(new OnTouchListener() {
//					@Override
//					public boolean onTouch(View v, MotionEvent event) {
//						if (event.getAction() == MotionEvent.ACTION_UP) {
//							float x = event.getX();
//							float y = event.getY();
//							// Toast.makeText(MainActivity.this,
//							// "x的位置"+x+"==y的位置"+y, 1).show();
//							if (x > width / 2 && y < height * 0.8) {
//								if (!isShow) {
//									isShow = true;
//									movie_imageview_re
//											.startAnimation(menu_out_Animation);
//									// movie_imageview_re.setVisibility(View.i);
//								} else {
//									if (count == 0) {
//										ToastUtil.showToast(MainActivity.this, "已经是第一页了");
//									} else {
//										count--;
//										if (bitMapHashMap.containsKey(count + "")) {
//											imageSwitcher
//													.setImageBitmap(bitMapHashMap
//															.get(count + ""));
//										} else {
//											imageSwitcher
//													.setImageResource(R.drawable.grid_item_default);
//										}
//									}
//								}
//							} else if (x < width / 2 && y < height * 0.8) {
//								if (!isShow) {
//									isShow = true;
//									movie_imageview_re
//											.startAnimation(menu_out_Animation);
//									movie_imageview_re.setVisibility(View.GONE);
//								} else {
//									if (count == photoList.size()) {
//										ToastUtil.showToastMaxText(MainActivity.this,
//												"已经是最后一张了");
//										movie_count.setText("" + count);
//										return true;
//									} else {
//										count++;
//										if (bitMapHashMap.containsKey(count + "")) {
//											imageSwitcher
//													.setImageBitmap(bitMapHashMap
//															.get(count + ""));
//										} else {
//											imageSwitcher
//													.setImageResource(R.drawable.grid_item_default);
//										}
//									}
//								}
//							} else {
//								if (isShow) {
//									movie_imageview_re
//											.startAnimation(menu_in_Animation);
//									isShow = false;
//								} else {
//									isShow = true;
//									movie_imageview_re
//											.startAnimation(menu_out_Animation);
//								}
//								Toast.makeText(MainActivity.this, "弹出小图浏览", 0).show();
//							}
//						}
//						return true;
//					}
//				});
				int end = photoList.size() > 10 ? 10 : photoList.size();
				endCount = end;
				setPhotoGroup(0, end);
			} else {
				Dialog dialog = new AlertDialog.Builder(MainActivityOld.this)
						.setTitle("提示")
						.setMessage("无法获取图片!")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										finish();
									}
								}).create();
				dialog.show();

			}
	 }

	/**
	 * 分页加载图片
	 * 
	 * @param currentPhotoList
	 */
	private void setPhotoGroup(int begin, int end) {
		final ImageView[] imageviewList = new ImageButton[end];
		for (int j = begin; j < end; j++) {
			final int mCurrentPos = j;
			imageviewList[j] = new ImageButton(MainActivityOld.this);
			imageviewList[j].setId(2000 * j);
			imageviewList[j].setFocusableInTouchMode(true);
			imageviewList[j].setImageResource(R.drawable.grid_item_default);
			// imageviewList[j].setAlpha(0);
			// imageviewList[j].startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
			// R.anim.logoanmi));
			imageviewList[j].setLayoutParams(new LinearLayout.LayoutParams(200,
					160));
			imageviewList[j]
					.setBackgroundResource(R.drawable.grid_item_selector);
			imageviewList[mCurrentPos].setScaleType(ImageView.ScaleType.FIT_XY);
			String Url = photoList.get(j);
			String id = idList.get(j);
			imageLoader.loadDrawable(Url,true,id,new ImageCallback() {
				@Override
				public void imgeLoader(Bitmap draw, String imgeURL) {
					imageviewList[mCurrentPos].clearAnimation();
					imageviewList[mCurrentPos].setImageBitmap(draw);
					bitMapHashMap.put(mCurrentPos + "", draw);
				}
			});

			if (j == 0) {
				// imageviewList[j].requestFocus();
				// imageviewList[j].setFocusable(true);
				// imageviewList[j].setSelected(true);
				oldview = imageviewList[j];
			}
			imageviewList[j]
					.setOnFocusChangeListener(new View.OnFocusChangeListener() {
						@Override
						public void onFocusChange(View v, boolean hasFocus) {
							if (hasFocus) {
								if (v != oldview) {
									oldview.clearFocus();
									oldview.setSelected(false);
								}
								v.setSelected(true);
								v.requestFocus();
								v.setFocusable(true);
								oldview = v;
								count = mCurrentPos + 1;
								movie_count.setText("" + count);
								imageSwitcher.setImageBitmap(bitMapHashMap
										.get(mCurrentPos + ""));
							}
						}
					});
			imageviewList[j].setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (v != oldview) {
						oldview.clearFocus();
						oldview.setSelected(false);
					}
					v.setSelected(true);
					v.setFocusable(true);
					boolean requestFocus = v.requestFocus();
					System.out.println("requestfouce" + requestFocus);
					System.out.println("v获得焦点=========" + v.isFocused());
					System.out.println("获得焦点的id====" + v.getId());
					imageSwitcher.setImageBitmap(bitMapHashMap.get(mCurrentPos
							+ ""));
					oldview = v;
					int myCount = v.getId() / 2000 + 1;
					movie_count.setText("" + myCount);
					// if (isShow) {
					// movie_imageview_re
					// .startAnimation(menu_in_Animation);
					// isShow = false;
					// } else {
					// isShow = true;
					// //
					// imageSwitcher.setImageBitmap(bitMapList.get(mCurrentPos));
					// movie_imageview_re
					// .startAnimation(menu_out_Animation);
					// }
				}
			});
			// linearLayouts[j].addView(imageviewList[j]);
			// lay_list.addView(linearLayouts[j]);
			lay_list.addView(imageviewList[j]);
		}

	}

	// public void setProg(ProgressDialog progressDialog){
	// progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	// progressDialog.setTitle("提示");
	// progressDialog.setMessage("努力加载中...");
	// progressDialog.setIndeterminate(false);
	// progressDialog.setCancelable(true);
	// progressDialog.show();
	// }
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isShow) {
				movie_imageview_re.startAnimation(menu_in_Animation);
				isShow = false;
				return true;
			}
		}
		if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			if (count == photoList.size()) {
				ToastUtil.showToastMaxText(MainActivityOld.this, "已经是最后一张了");
				movie_count.setText("" + count);
				return true;
			} else if (count == endCount) {
				// 翻页，加载下一页的数据
				Log.i("CCDrivePhoto", "我已经开始执行");
				int begin = endCount;
				int end = photoList.size() > (count + 10) ? (count + 10)
						: photoList.size();
				endCount=end;
				setPhotoGroup(begin, end);
			}

		}
		if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
			if (count == 1) {
				ToastUtil.showToastMaxText(MainActivityOld.this, "已经是第一张了");
				movie_count.setText("" + count);
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if (event.getAction() == MotionEvent.ACTION_UP) {
			float x = event.getX();
			float y = event.getY();
			// Toast.makeText(MainActivity.this, "x的位置"+x+"==y的位置"+y, 1).show();
			if (x > width / 2 && y < height * 0.8) {
				System.out.println("向右翻面");
				if (isShow) {
					movie_imageview_re.startAnimation(menu_in_Animation);
					isShow = false;
				} else {
					isShow = true;
					// imageSwitcher.setImageBitmap(bitMapList.get(mCurrentPos));
					movie_imageview_re.startAnimation(menu_out_Animation);
				}
				Toast.makeText(MainActivityOld.this, "向右翻页", 0).show();
			} else if (x < width / 2 && y < height * 0.8) {
				if (isShow) {
					movie_imageview_re.startAnimation(menu_in_Animation);
					isShow = false;
				} else {
					isShow = true;
					// imageSwitcher.setImageBitmap(bitMapList.get(mCurrentPos));
					movie_imageview_re.startAnimation(menu_out_Animation);
				}
				Toast.makeText(MainActivityOld.this, "向左翻页", 0).show();
			} else {
				if (isShow) {
					movie_imageview_re.startAnimation(menu_in_Animation);
					isShow = false;
				} else {
					isShow = true;
					// imageSwitcher.setImageBitmap(bitMapList.get(mCurrentPos));
					movie_imageview_re.startAnimation(menu_out_Animation);
				}
				Toast.makeText(MainActivityOld.this, "弹出小图浏览", 0).show();
			}
		}
		return false;
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

	public void showMyProgress() {
		// 把进度对话框显示出来
		/*
		 * AlphaAnimation aa = new AlphaAnimation(0, 1); aa.setDuration(500);
		 * loadingBar.setAnimation(aa);
		 * loadingBar.setAnimationCacheEnabled(false);
		 */
		loadingBar.setVisibility(View.VISIBLE);
	}

	// 关闭进度条
	public void hideMyProgress() {
		/*
		 * AlphaAnimation aa = new AlphaAnimation(1, 0); aa.setDuration(500);
		 * loadingBar.setAnimation(aa);
		 * loadingBar.setAnimationCacheEnabled(false);
		 */
		loadingBar.setVisibility(View.GONE);
	}

	private void setTestData() {
		String s1 = "http://www.sinaimg.cn/dy/slidenews/4_img/2013_06/704_879175_598090.jpg";
		String s2 = "http://www.sinaimg.cn/dy/slidenews/4_img/2013_06/704_879175_598090.jpg";
		String s3 = "http://www.sinaimg.cn/dy/slidenews/4_img/2013_06/704_879176_259713.jpg";
		String s4 = "http://www.sinaimg.cn/dy/slidenews/4_img/2013_06/704_879177_159551.jpg";
		String s5 = "http://www.sinaimg.cn/dy/slidenews/4_img/2013_06/704_879178_308595.jpg";
		String s6 = "http://www.sinaimg.cn/dy/slidenews/4_img/2013_06/704_879179_738339.jpg";
		String s7 = "http://www.sinaimg.cn/dy/slidenews/4_img/2013_06/704_879180_932059.jpg";
		String s8 = "http://www.sinaimg.cn/dy/slidenews/4_img/2013_06/704_879181_722112.jpg";
		String s9 = "http://www.sinaimg.cn/dy/slidenews/4_img/2013_06/704_879182_738222.jpg";
		String s10 = "http://www.sinaimg.cn/dy/slidenews/4_img/2013_06/704_879183_627526.jpg";
		String s11 = "http://www.sinaimg.cn/dy/slidenews/4_img/2013_06/704_879184_578398.jpg";
		String s12 = "http://www.sinaimg.cn/dy/slidenews/4_img/2013_06/704_879191_255062.jpg";
		String s13 = "http://www.sinaimg.cn/dy/slidenews/4_img/2013_06/704_879192_570363.jpg";
		String s14 = "http://www.sinaimg.cn/dy/slidenews/4_img/2013_06/704_879193_753319.jpg";
		String s15 = "http://www.sinaimg.cn/dy/slidenews/4_img/2013_06/704_879194_825136.jpg";
		String s16 = "http://www.sinaimg.cn/dy/slidenews/4_img/2013_06/704_879139_647774.jpg";
		String s17 = "http://www.sinaimg.cn/dy/slidenews/4_img/2013_06/704_879140_550018.jpg";
		String s18 = "http://www.sinaimg.cn/dy/slidenews/4_img/2013_06/704_879174_205878.jpg";
		String s19 = "http://www.sinaimg.cn/dy/slidenews/4_img/2013_06/704_879185_889614.jpg";
		String s20 = "http://www.sinaimg.cn/dy/slidenews/4_img/2013_06/704_879186_968920.jpg";
		String s21 = "http://www.sinaimg.cn/dy/slidenews/4_img/2013_06/704_879187_948992.jpg";
		String s22 = "http://www.sinaimg.cn/dy/slidenews/4_img/2013_06/704_879188_373590.jpg";
		String s23 = "http://www.sinaimg.cn/dy/slidenews/4_img/2013_06/704_879189_839062.jpg";
		String s24 = "http://www.sinaimg.cn/dy/slidenews/4_img/2013_06/704_879190_209989.jpg";
		String s25 = "http://www.sinaimg.cn/dy/slidenews/4_img/2013_06/704_879195_556370.jpg";
		String s26 = "http://www.sinaimg.cn/dy/slidenews/4_img/2013_06/704_879196_332739.jpg";
		photoList = new ArrayList<String>();
		photoList.add(s26);
		photoList.add(s25);
		photoList.add(s24);
		photoList.add(s23);
		photoList.add(s22);
		photoList.add(s21);
		photoList.add(s20);
		photoList.add(s19);
		photoList.add(s18);
		photoList.add(s17);
		photoList.add(s16);
		photoList.add(s15);
		photoList.add(s14);
		photoList.add(s13);
		photoList.add(s12);
		photoList.add(s11);
		photoList.add(s10);
		photoList.add(s9);
		photoList.add(s8);
		photoList.add(s7);
		photoList.add(s6);
		photoList.add(s5);
		photoList.add(s4);
		photoList.add(s3);
		photoList.add(s2);
		photoList.add(s1);
	}
}