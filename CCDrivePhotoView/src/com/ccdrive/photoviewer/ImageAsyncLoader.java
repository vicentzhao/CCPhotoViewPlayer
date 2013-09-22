package com.ccdrive.photoviewer;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ImageAsyncLoader {
	private HashMap<String, SoftReference<Bitmap>> imgeCache;
	ImageFileCache cache ;
	public ImageAsyncLoader() {
		imgeCache = new HashMap<String, SoftReference<Bitmap>>();
		cache = ImageFileCache.getCashInstance();
	}

	public Bitmap loadDrawable(final String imageURL,
			final ImageCallback imageCallback) {
		if (imgeCache.containsKey(imageURL)) {
			SoftReference<Bitmap> softReference = imgeCache.get(imageURL);
			Bitmap drawable = softReference.get();
			if (drawable != null) {
				return drawable;
			}
			Bitmap image = cache.getImage(imageURL);
			if(image!=null){
				return image;
			}
		}

		final Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				imageCallback.imgeLoader((Bitmap) msg.obj, imageURL);
			}

		};

		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				Bitmap drawable = getImage(imageURL);
				imgeCache.put(imageURL, new SoftReference<Bitmap>(drawable));
				if(drawable!=null){
				cache.saveBmpToSd(drawable, imageURL);
				}
				Message msg = handler.obtainMessage(0, drawable);
				handler.sendMessage(msg);
			}

		}.start();

		return null;
	}

	public Bitmap getImage(String urlPath) {
		URL url;
		Bitmap map = null;
		try {
			url = new URL(urlPath);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(10 * 1000);
			conn.setRequestMethod("GET");
			conn.connect();
			if (conn.getResponseCode() == 200) {
				map = BitmapFactory.decodeStream(conn.getInputStream());
				return map;
			}
		} catch (MalformedURLException e) {
			Log.e("url-->", "url error");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	public interface ImageCallback {
		public void imgeLoader(Bitmap draw, String imgeURL);
	}

}
