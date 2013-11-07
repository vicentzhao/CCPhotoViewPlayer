package com.ccdrive.photoviewer.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ImageAsyncLoaderOld {
	private HashMap<String, SoftReference<Bitmap>> imgeCache;
	private ImageFileCacheOld cache;
	private static int NETWORK_CONNECT_TIMEOUT = 500000;
	private static int NETWORK_SO_TIMEOUT = 500000;
	private static boolean network_enable = false;
	private HttpResponse mHttpResponse;

	public ImageAsyncLoaderOld() {
		imgeCache = new HashMap<String, SoftReference<Bitmap>>();
		cache = ImageFileCacheOld.getCashInstance();
	}

	public Bitmap loadDrawable(final String imageURL, final boolean isZip,
			final String id, final ImageCallback imageCallback) {
		
			if (imgeCache.containsKey(imageURL)) {
				SoftReference<Bitmap> softReference = null;
				if(isZip){
					softReference = imgeCache.get(imageURL+"mini");
				}else{
					imgeCache.get(imageURL);
				}
				Bitmap drawable = softReference.get();
				if (drawable != null) {
					return drawable;
				}
			}
			Bitmap image = cache.getImage(imageURL, isZip, id);
			if (image != null) {
				return image;
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
				ArrayList<Bitmap> imageList = getImage(imageURL, isZip, id);
				Message msg = null;
				if(null!=imageList&&imageList.size()!=0){
				imgeCache.put(imageURL+"mini", new SoftReference<Bitmap>(imageList.get(0)));
				imgeCache.put(imageURL, new SoftReference<Bitmap>(imageList.get(1)));
				if (imageList.get(0) != null) {
					cache.saveBmpToSd(imageList.get(0), id);
				}
				if(isZip){
					msg= handler.obtainMessage(0, imageList.get(0));
				}else{
					msg= handler.obtainMessage(0, imageList.get(1));
				}
				handler.sendMessage(msg);
				}
			}

		}.start();

		return null;
	}

	public ArrayList<Bitmap> getImage(String urlPath, boolean isZip, String id) {
		URL url;
		ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
		Bitmap map = null;
		Bitmap miniMap=null;
		try {
			url = new URL(urlPath);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(10 * 1000);
			conn.setRequestMethod("GET");
			conn.connect();
			InputStream inputStream = conn.getInputStream();
			if (conn.getResponseCode() == 200) {
					BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 4;// ͼƬ��߶�Ϊԭ����4��֮һ����ͼƬΪԭ����16��֮һ
//				map = BitmapFactory.decodeStream(conn.getInputStream(),options);
                    miniMap = BitmapFactory.decodeStream(inputStream,null,options);
                    cache.saveBmpToSd(miniMap, id+"mini.cach");
                     map = loadImgFromUrl(inputStream,id);
                     bitmaps.add(miniMap);
                     bitmaps.add(map);
				return bitmaps;
			}
		} catch (MalformedURLException e) {
			Log.e("url-->", "url error");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmaps;
	}

	public interface ImageCallback {
		public void imgeLoader(Bitmap draw, String imgeURL);
	}

	/**
	 * �ӷ�������ȡͼƬ��������ڴ�������⣩ ����SDcard����ʱ�������ر��棬����أ�SDcard������ʱ��ֱ�����أ�
	 * 
	 * @param imageUrl
	 * @param isZip
	 *            �Ƿ񽵵Ͳ�����
	 * @return Bitmap
	 */
	public Bitmap loadImgFromUrl(InputStream is, String fileName) {
		Bitmap bmp = null;
		try {  
			String path =cache.getDirectory();
			File basePathFile =new File(path);
			if(!basePathFile.exists()){
			basePathFile.mkdir();
			}
//			File basePathFile = new File(cache.getDirectory(), fileName);
			
			System.out.println("���ص�·��Ϊ"+cache.getDirectory());
			///storage/sdcard0/CCdrive/ccphotoviewer/imagecache
			File file = new File(basePathFile, fileName + ".cach");
			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream outputStream = new FileOutputStream(file);
			byte[] b = new byte[512];
			int offset;
			while ((offset = is.read(b)) != -1) {
				outputStream.write(b, 0, offset);
			}
			outputStream.flush();
			outputStream.close();
			// is.close();
			// basePathFile = new File(basePathFile, fileName);
			// if (basePathFile.exists()) {
			// basePathFile.delete();
			// }
			// if (file.renameTo(basePathFile)) {
			// is = new FileInputStream(basePathFile);
			// } else {
			// is = new FileInputStream(file);
			// }
			// bmp = getFromCard(basePathFile.getAbsolutePath(), isZip);//
			// ��ȡ������ͼƬ����

			// else {
			bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
			// }
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		System.out.println("������ɣ�");
		return bmp;

	}

	/**
	 * ���ӳɹ� 200
	 * 
	 * @param url
	 * @return
	 */
	public boolean connectServerByURL(String url) {
		HttpGet httpRequest = new HttpGet(url);
		try {
			HttpParams p = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(p,
					NETWORK_CONNECT_TIMEOUT);
			HttpConnectionParams.setSoTimeout(p, NETWORK_SO_TIMEOUT);
			HttpClient httpClient = new DefaultHttpClient(p);

			HttpResponse httpResponse = httpClient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				mHttpResponse = httpResponse;
				return true;
			}
		} catch (ClientProtocolException e) {
			httpRequest.abort();
			e.printStackTrace();
		} catch (IOException e) {
			httpRequest.abort();
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * �����Ӧbitmap
	 * @param url
	 * @return
	 */
	
	public Bitmap getInputStreamFromUrl(String url) {
		InputStream inputStream = null ;
		try {
			if(connectServerByURL(url)) {
				HttpEntity entity = mHttpResponse.getEntity() ;
				inputStream = entity.getContent() ;
				long apkLength = entity.getContentLength();
				
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
