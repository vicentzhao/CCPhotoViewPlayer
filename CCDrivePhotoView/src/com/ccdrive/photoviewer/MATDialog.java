package com.ccdrive.photoviewer;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class MATDialog extends Dialog {

		TextView msgText, progressText;
		ImageView logo;
		Context  mContext;
	

		public MATDialog(Context context,int them) {
			super(context,them);
			mContext =context;
			initContentView();
		}

		private View initContentView() {
			View v = LayoutInflater.from(getContext()).inflate(R.layout.promptdialog, null);
//			msgText = (TextView) v.findViewById(R.id.tv_loading);
//			progressText = (TextView) v.findViewById(R.id.tv_processbar);
			logo = (ImageView) v.findViewById(R.id.iv_loading);
			logo.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.logoanmi));
			setContentView(v);
			return v;
		}

		@Override
		public void hide() {
			if (isShowing()) {
				dismiss();
			}
		}
		public void setAnimation(){
			logo.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.logoanmi));
		}
		void setMessage(String msg) {
			if (msg == null)
				return;
			msgText.setText(msg);
		}

		void setProgress(int progress) {
			if (progress < 0)
				return;
			progressText.setText(progress + "%");
		}

		void setLogo(int resId) {
			logo.setImageResource(resId);
		}

		void setLogo(Bitmap bm) {
			if (bm == null)
				return;
			logo.setImageBitmap(bm);
		}

		void setLogo(Drawable drawable) {
			if (drawable == null)
				return;
			logo.setImageDrawable(drawable);
		}

		public void showMessage(String msg) {
			setMessage(msg);
			show();
		}

	}