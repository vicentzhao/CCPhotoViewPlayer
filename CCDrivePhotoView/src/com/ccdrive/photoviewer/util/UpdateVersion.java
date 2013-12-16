package com.ccdrive.photoviewer.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ccdrive.photoviewer.R;
import com.ccdrive.photoviewer.content.Constant;

public class UpdateVersion implements Runnable {

	public static final String TAG = "version";
	private static Context mContext;
	private static UpdateVersion version;
	private static Handler mHandler;
	private String mUrl;
	private static boolean isApk = true;
	private static String mName;
	private String fileName;
	private static String mAppID;
	private static boolean mflag = true;
	private ProgressBar mybar;

	public static UpdateVersion instance(Context context, Handler handler,
			boolean boo) {
		mContext = context;
		mHandler = handler;
		isApk = boo;
		if (version == null) {
			version = new UpdateVersion();
		}
		return version;
	}

	public static UpdateVersion instance(Context context, Handler handler,
			boolean boo, boolean flag) {
		mContext = context;
		mHandler = handler;
		mflag = flag;
		isApk = boo;
		if (version == null) {
			version = new UpdateVersion();
		}
		return version;
	}

	public void setUpdateUrl(String verUrl) {
		this.mUrl = verUrl;
	}

	public void setLoadApkName(String name ) {
		this.mName = name;

	}

	public void setAppID(String appID) {
		this.mAppID = appID;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			fileName = Environment.getExternalStorageDirectory().toString();
			fileName = fileName + "/ccdrive/app/CCnews/downloader";
			fileIsExist(fileName);
			downloader(mUrl);

		} else {
			if (Build.MODEL.equals("M3 media box board")) { // M3 media box
															// board
				fileName = "/mnt/sda/sda1";
				fileName = fileName + "/tvMarket";
				fileIsExist(fileName);
				downloader(mUrl);
			} else {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						Toast.makeText(
								mContext,
								mContext.getResources().getString(
										R.string.nosdcard), Toast.LENGTH_LONG)
								.show();
					}
				});
			}
		}
	}

	ProgressDialog progressDialog = null;

//	public void showprogressDialog() {
//		progressDialog = new ProgressDialog(mContext);
//		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//		progressDialog.setTitle(mContext.getResources().getString(
//				R.string.start_download));
//		progressDialog.setIndeterminate(false);
//		progressDialog.setMax(100);
//		progressDialog.setCancelable(true);
//		progressDialog.show();
//		progressDialog.setOnKeyListener(new OnKeyListener() {
//			@Override
//			public boolean onKey(DialogInterface dialog, int keyCode,
//					KeyEvent event) {
//				if (keyCode == KeyEvent.KEYCODE_BACK) {
//					return true;
//				}
//				return false;
//			}
//		});
//	}

	private void disMissProgressBar() {
		if (dialogbar.isShowing())
			dialogbar.dismiss();
	}

	private void downloader(String path) {

		long apkSize = HttpUtil.getInstance().getApkLength();
		File file1 = new File(fileName, mName + ".apk");
		File file = null;
		if (file1.exists() && mflag) {
			if (apkSize == file1.length()) {
				install();
				return;
			} else {
				file1.delete();
				file = new File(fileName, mName + ".apk");
				mHandler.post(new Runnable() {

					@Override
					public void run() {
//						showprogressDialog();
						
					}
				});
				downLoad(path, file);
			}

		} else {
			file = new File(fileName, mName + ".apk");
			mHandler.post(new Runnable() {

				@Override
				public void run() {
//					showprogressDialog();
				}
			});
			downLoad(path, file);
		}
	}

	private long loadSize;
	private int percent;
	private Dialog dialogbar ;
	private TextView tv_progressbar;
	private void downLoad(String url, File file) {

		InputStream is = null;
		FileOutputStream fileOutputStream = null;

		try {
			is = HttpUtil.getInstance().getInputStreamFromUrl(url);
			fileOutputStream = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			int ch = -1;
			int count = 0;
			while ((ch = is.read(buf)) != -1) {
				fileOutputStream.write(buf, 0, ch);
				count += ch;
				loadSize = count;
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						long apkSize = HttpUtil.getInstance().getApkLength();
						if (dialogbar != null
								&& dialogbar.isShowing()
								&& loadSize <= apkSize) {
							double apkSizeD = (double) apkSize;
							double loadSizeD = (double) loadSize;
							// Log.i(TAG, "load apkSize:"+apkSize) ;
							percent = (int) Math
									.ceil((loadSizeD / apkSizeD) * 100);
							mybar.setProgress(percent);
							tv_progressbar.setText(percent+"%");
							
						}
					}
				});
			}

			fileOutputStream.flush();
			if (fileOutputStream != null) {
				fileOutputStream.close();
			}
			complete();
		} catch (Exception e) {
			e.printStackTrace();
			showToast(mContext.getResources().getString(R.string.downErro));
		}
	}

	private void fileIsExist(String path) {
		File file = new File(path);
		file.mkdirs();
	}

	// private void makeException() {
	// try {
	// int i = 1/0 ;
	// } catch (Exception e) {
	// showToast("没锟斤拷锟揭碉拷锟�?备锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟截★�?) ;
	// }
	// }

	private void showToast(final String str) {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Toast.makeText(mContext, str, Toast.LENGTH_LONG).show();
				disMissProgressBar();
			}
		});
	}

	private void complete() {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				disMissProgressBar();
				showToast(mContext.getResources().getString(
						R.string.downComplete));
				if (isApk) {
					install();
				}
			}
		});
	}
	private void install() {
		mflag=true;
		if(Constant.isSlientInstall ){		
		try {
			boolean isSuccess =slientInstall(fileName);
			if(isSuccess){
				Toast.makeText(mContext, "安装成功", 1).show();
			}else{
				Toast.makeText(mContext, "安装失败", 1).show();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}else{
			normalInstall();
		}
	}
	private void normalInstall(){
		Intent intent = new Intent(Intent.ACTION_VIEW);
		Uri uri = Uri.fromFile(new File(fileName, mName+".apk")) ;
		intent.setDataAndType(uri,"application/vnd.android.package-archive");
		mContext.startActivity(intent);
	}
	/**
	 * 静默安装
	 * 
	 * @param apkPath
	 * @return
	 * @throws IOException
	 */
	public boolean slientInstall(String apkPath) throws IOException {
		Runtime runtime = Runtime.getRuntime();
		String apkPathName = apkPath + "/" + mName + ".apk";
		Process proc = runtime.exec("pm install -r " + apkPathName); // 这句话就是shell与高级语�?��的调�?
		// 如果有参数的话可以用另外�?��被重载的exec方法
		// 实际上这样执行时启动了一个子进程,它没有父进程的控制台
		// 也就看不到输�?�?��我们�?��用输出流来得到shell执行后的输出
		InputStream inputstream = proc.getInputStream();
		InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
		BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
		String line = "";
		StringBuilder sb = new StringBuilder(line);
		while ((line = bufferedreader.readLine()) != null) {
			sb.append(line);
		}
		// 9 使用exec执行不会等执行成功以后才返回,它会立即返回
		// �?��在某些情况下是很要命�?比如复制文件的时�?
		// 使用wairFor()可以等待命令执行完成以后才返�?
		try {
			if (proc.waitFor() != 0) {
				System.err.println("exit value = " + proc.exitValue());
			}
		} catch (Exception e) {
			System.err.println(e);
			// TODO: handle exception
		}
		String result = sb.toString();
		if (result.equals("Success")) {
			return true;
		}
		return false;
	}

	/**
	 * 静默卸载
	 * 
	 * @param path
	 * @return
	 */
	public boolean slientUnInstall(String path) {
		Runtime runtime = Runtime.getRuntime();
		try {
			Process proc = runtime.exec("pm uninstall -r " + path);
			if (proc.waitFor() != 0) {
				System.err.println("exit value = " + proc.exitValue());
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void setProgressBar(ProgressBar pb_bar , Dialog dialogbar , TextView tv_progressbar) {
		this.mybar = pb_bar;
		this.dialogbar = dialogbar;
		this.tv_progressbar = tv_progressbar;
	}

}
