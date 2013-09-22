package com.ccdrive.photoviewer;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ToastUtil {

	private static Toast toast;

	public static void showToast(Context context, String text) {
		if (toast == null) {
			toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
		} else {
			toast.setText(text);
		}
		toast.show();
	}

	public static void showToastMaxText(Context context, String text) {
		LayoutInflater layoutInflater = LayoutInflater.from(context);

		View toastRoot = layoutInflater.inflate(R.layout.no_collect, null);
		TextView message = (TextView) toastRoot.findViewById(R.id.tv_collect);
		if (toast == null) {
			toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
		}
		message.setText(text);
		toast.setGravity(Gravity.CENTER, 0, 10);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(toastRoot);
		toast.show();
	}

	public static void showToastbyId(Context context, int id) {
		if (toast == null) {
			toast = Toast.makeText(context, context.getResources()
					.getString(id), Toast.LENGTH_SHORT);
		} else {
			toast.setText(context.getResources().getString(id));
		}
		toast.show();
	}
}
