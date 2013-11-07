package com.ccdrive.photoviewer.util;

import com.ccdrive.photoviewer.R;
import com.ccdrive.photoviewer.R.id;
import com.ccdrive.photoviewer.R.layout;
import com.ccdrive.photoviewer.R.style;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class UpdateApk {
	 public  static void setInstall(final Context context,final String packName, final String downPath) {
			final Handler hd = new Handler();			
			final Dialog dialog = new Dialog(context,	R.style.Theme_Dialog);
			dialog.setContentView(R.layout.exitdialog);
			Button btn_yes = (Button) dialog.findViewById(R.id.btn_yes);
			Button btn_no = (Button) dialog.findViewById(R.id.btn_no);
			TextView tishi = (TextView)dialog.findViewById(R.id.textView1);
			tishi.setTextSize(25);
			tishi.setText("有新版本,是否更新?");
			btn_yes.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					final Dialog dialogbar = new Dialog(context,
							R.style.Theme_Dialog);
					dialogbar.setContentView(R.layout.activity_progressbar);
					final ProgressBar	pb_bar = (ProgressBar) dialogbar.findViewById(R.id.pb_bar);
					final TextView	tv_progressbar = (TextView) dialogbar
							.findViewById(R.id.tv_progressbar);
					dialogbar.show();
					new AsyncTask<Void, Void, Void>() {
						@Override
						protected Void doInBackground(Void... params) {
							UpdateVersion uv = UpdateVersion
									.instance(context,
											hd, true, false);
							uv.setUpdateUrl(downPath);
							uv.setProgressBar(pb_bar, dialogbar, tv_progressbar);
							uv.setLoadApkName(packName);
							uv.run();
							return null;
						}
					}.execute();
					dialog.dismiss();
				}
				
			});
			btn_no.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {						
					dialog.dismiss();
				}
			});
			dialog.show();	
}

}
