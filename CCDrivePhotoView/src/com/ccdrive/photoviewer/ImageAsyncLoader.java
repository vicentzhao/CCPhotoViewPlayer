package com.ccdrive.photoviewer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ImageAsyncLoader {
	private HashMap<String, SoftReference<Bitmap>> imgeCache;
	ImageFileCache cache ;
	private final static  int DIALOGSHOW =10001;
	private final int DIALOGDISSMIS =10002;
	
	  private static final String ImageFolder = "CCDrive/ImageCache";
	public ImageAsyncLoader() {
		imgeCache = new HashMap<String, SoftReference<Bitmap>>();
		cache = ImageFileCache.getCashInstance();
	}

	public Bitmap loadDrawable(final String imageURL,final Dialog mDialog,
			final ImageCallback imageCallback) {
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				imageCallback.imgeLoader((Bitmap) msg.obj, imageURL);
				switch (msg.what) {
				case DIALOGSHOW:
					mDialog.show();
					break;
				case DIALOGDISSMIS:
					mDialog.dismiss();
					break;

				default:
					break;
				}
			}
			
		};
		if (imgeCache.containsKey(imageURL)) {
			SoftReference<Bitmap> softReference = imgeCache.get(imageURL);
			Bitmap drawable = softReference.get();
			if (drawable != null) {
				Message msg = handler.obtainMessage(0, drawable);
				handler.sendMessage(msg);
				return drawable;
			}
			Bitmap image = cache.getImage(imageURL);
			if(image!=null){
				Message msg = handler.obtainMessage(0, image);
				handler.sendMessage(msg);
				return image;
			}
		}
		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
//				Bitmap drawable = loadImgFromUrl(imageURL,false);
				Message ms = new Message();
				ms.what=DIALOGSHOW;
				handler.sendMessage(ms);
				Bitmap drawable = getImage(imageURL);
				Message ms1 = new Message();
				ms1.what=DIALOGDISSMIS;
				handler.sendMessage(ms1);
				imgeCache.put(imageURL, new SoftReference<Bitmap>(drawable));
				if(drawable!=null){
				cache.saveBmpToSd(drawable, imageURL);
				}
				Message msg = handler.obtainMessage(0, drawable);
				handler.sendMessage(msg);
			}

		}.start();

//	}
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
	   /**
     * �ӷ�������ȡͼƬ��������ڴ�������⣩ ����SDcard����ʱ�������ر��棬����أ�SDcard������ʱ��ֱ�����أ�
     *
     * @param imageUrl
     * @param isZip
     *            �Ƿ񽵵Ͳ�����
     * @return Bitmap
      */
     public Bitmap loadImgFromUrl(String imageUrl,boolean isZip) {
            System.out.println("��ʼ����ͼƬ����");
            URL mUrl;
            InputStream is = null;
            Bitmap bmp = null;
            try {
                    mUrl = new URL(imageUrl);
                    is = (InputStream) mUrl.getContent();
                    if (isSDCardAvailable()) {
                            String fileName = imageUrl
                                            .substring(imageUrl.lastIndexOf('/') + 1);
                            File basePathFile = new File(
                                            Environment.getExternalStorageDirectory(), ImageFolder);
                            File file = new File(basePathFile, fileName + ".tmp");
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
                            is.close();
                            basePathFile = new File(basePathFile, fileName);
                            if (basePathFile.exists()) {
                                    basePathFile.delete();
                            }
                            if (file.renameTo(basePathFile)) {
                                    is = new FileInputStream(basePathFile);
                            } else {
                                    is = new FileInputStream(file);
                            }
//                            bmp = getFromCard(basePathFile.getAbsolutePath(), isZip);// ��ȡ������ͼƬ����
                    } 
//                    else {
//                            bmp = getFromUrl(imageUrl, isZip);// �����������
//                    }
            } catch (Exception e) {
                    e.printStackTrace();
                    return null;
            }
            System.out.println("������ɣ�");
            return bmp;

    }

	public interface ImageCallback {
		public void imgeLoader(Bitmap draw, String imgeURL);
	}
	
    /**
     * ���SDCard�Ƿ����
     * @return
     */
    public static final boolean isSDCardAvailable(){
            return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }


}
