package com.ccdrive.photoviewer.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;

import com.ccdrive.photoviewer.util.ImageAsyncLoaderOld.ImageCallback;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

public class AsyncImageLoader {
    
    private static final String ImageFolder = "CCDrive/ImageCache";
    //ָ�������·��
    private String myPath;


    private static AsyncImageLoader loader;
        private AsyncImageLoader(String path) {
                this.myPath = path;
        }
       

//*************************************************************���ⲿ���õķ���
        /**
         * ��ȡ�ӿ�
         * @param defaultPath
         * @return AsyncImageLoader
         */
        public static AsyncImageLoader getInstance(String defaultPath) {
                if(loader==null){
                        loader=new AsyncImageLoader(defaultPath);
                }
                return loader;
        }


         /**
         * ����������������ͼƬ
         * ����ʱ�����������̣߳�
         * @param imageUrl
         * @param isZip
         * @return Bitmap
         */
        public Bitmap load(String imageUrl, boolean isZip){
                Bitmap bitmap=null;
                bitmap=loadImageFromSD(imageUrl, isZip);
                if(bitmap==null){
                        bitmap=loadImgFromUrl(imageUrl, isZip);
                }
                return bitmap;
        }

        /**
         * �������������ͼ
         * @param imageUrl
         * @param imageView
         * @param isZip
         * @return
         */
        public Bitmap loadThumb(final String imageUrl,final ImageView imageView, boolean isZip){
                return loadImageThumb(imageUrl, imageView, isZip);
        }
       
//*************************************************************
       /**
         * ���SDCard�Ƿ����
         * @return
         */
        public static final boolean isSDCardAvailable(){
                return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        }

        //����������
        private HashMap<String, SoftReference<Bitmap>> imageCache;


//--------------------------------------------------------------------------------------��ȡsd�����ڴ滺��
        /**
         * �ӻ���Ӷ�ȡͼƬ��������ڴ�������⣩
         *
         * @param imageUrl
         * @return Bitmap
         */
        public Bitmap loadImageFromSD( String imageUrl) {
                return loadImageFromSD(imageUrl, false);
        }


        /**
         * �ӻ���Ӷ�ȡͼƬ��������ڴ�������⣩
         *
         * @param imageUrl
         * @param isZip
         *            �Ƿ񽵵Ͳ�����
         * @return Bitmap
         */
        public Bitmap loadImageFromSD( String imageUrl, boolean isZip) {
                String key = imageUrl;//��urlΪ����
                Bitmap bmp = null;
               
                if (isSDCardAvailable()) {
                        File file = new File(Environment.getExternalStorageDirectory(),this.myPath);
                        if (!file.exists()) {
                                file.mkdirs();
                        }
                        file = new File(file,imageUrl.substring(imageUrl.lastIndexOf('/') + 1));

                        if (file.exists()) {
                                bmp = getFromCard(file.getAbsolutePath(), isZip);
                                if (null != bmp) {
                                        System.out.println("���ڶ�ȡ���ص��ļ��� " + file.getAbsolutePath());
                                        return bmp;
                                }
                        } else {
                                System.out.println("-------------------���ص�ͼƬ������-------------------------");                       
                                if (imageCache == null) {
                                        imageCache = new HashMap<String, SoftReference<Bitmap>>();
                                }
                                if (imageCache.containsKey(key)) {
                                        SoftReference<Bitmap> softReference = imageCache.get(key);
                                        if (softReference == null) {
                                                return null;
                                        }
                                         bmp = softReference.get();
                                        if (bmp != null) {                                               
                                                return bmp;
                                        }
                                } else {                                       
                                        imageCache.put(key, null);
                                }
                        }
                     } else {                                               
                        if (imageCache == null) {
                                imageCache = new HashMap<String, SoftReference<Bitmap>>();
                        }
                        if (imageCache.containsKey(key)) {
                                SoftReference<Bitmap> softReference = imageCache.get(key);
                                if (softReference == null) {
                                        return null;
                                }
                                 bmp = softReference.get();
                                if (bmp != null) {
                                       
                                        return bmp;
                                }
                        } else {
                               
                                imageCache.put(key, null);
                        }
                }

                return null;
        }


       // -------------------------------------------------------------------------------------����������
        /**
         * �ӷ�������ȡͼƬ��������ڴ�������⣩
         *
         * @param imageUrl
         * @param folderPath
         * @return Bitmap
         */
        public Bitmap loadImgFromUrl(String imageUrl) {
                return loadImgFromUrl(imageUrl, false);
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
                                                Environment.getExternalStorageDirectory(), myPath);
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
                                bmp = getFromCard(basePathFile.getAbsolutePath(), isZip);// ��ȡ������ͼƬ����
                        } 
//                        else {
//                                bmp = getFromUrl(imageUrl, isZip);// �����������
//                        }
                } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                }
                System.out.println("������ɣ�");
                return bmp;

        }

        /**
         * ��ȡSDcard�е�ͼƬ��Դ������ڴ�������⣩
         *
         * @param pathName
         *            �ļ�·����
         * @param isZip
         *            �Ƿ񽵵Ͳ�����
         * @return Bitmap
         */                       

        public Bitmap getFromCard(String pathName, boolean isZip) {
                Bitmap bitmap = null;
                if (isZip == true) {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 2;// ͼƬ��߶�Ϊԭ���Ķ���֮һ����ͼƬΪԭ�����ķ�֮һ
                        bitmap = BitmapFactory.decodeFile(pathName, options);
                } else {
                        bitmap = BitmapFactory.decodeFile(pathName);
                }

                return bitmap;
        }

        /**
         * �������ж�ȡ������ڴ�������⣩
         *
         * @param imageUrl
         *             ͼƬURL
         * @return Bitmap
         */
        public Bitmap getFromUrl(String imageUrl, boolean isZip) {
                URL mUrl;
                InputStream is = null;
                Bitmap bitmap = null;
                try {
                        mUrl = new URL(imageUrl);
                        is = (InputStream) mUrl.getContent();
                } catch (Exception e) {
                        e.printStackTrace();
                }
                if (isZip == true) {                       
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 2;// ͼƬ��߶�Ϊԭ���Ķ���֮һ����ͼƬΪԭ�����ķ�֮һ
                        bitmap = BitmapFactory.decodeStream(is, null, options);
                } else {
                        bitmap = BitmapFactory.decodeStream(is);
                }
                return bitmap;
        }


//--------------------------------------------------------------------------
       
   /**
     * һ�����ڻ�ȡ����ͼ
     * @param imageUrl
     * @param imageView
     * @param imageCallback
     * @return Bitmap
     */
        public Bitmap loadImageThumb(final String imageUrl,final ImageView imageView, boolean isZip) {
            final String key = imageUrl;
            Bitmap bmp;
                if(isSDCardAvailable()){
                        File file = new File(Environment.getExternalStorageDirectory(), this.myPath);
                        if(!file.exists()){
                                file.mkdirs();
                        }
                        file = new File(file, imageUrl.substring(imageUrl.lastIndexOf('/')+1));

                        if(file.exists()){
                                bmp = getFromCard(file.getAbsolutePath(), isZip);
                                if (null != bmp) {
                                        System.out.println("���ڶ�ȡ���ص��ļ��� " + file.getAbsolutePath());
                                        return bmp;
                                }
                        }
                        if (imageCache == null) {
                                imageCache = new HashMap<String, SoftReference<Bitmap>>();
                        }
                        if (imageCache.containsKey(key)) {
                                SoftReference<Bitmap> softReference = imageCache.get(key);
                                if (softReference == null) {
                                        return null;
                                }
                                bmp = softReference.get();
                                if (bmp != null) {                                       
                                        return bmp;
                                }
                        } else {
                               
                                imageCache.put(key, null);
                        }
                       final Handler handler = new Handler() {
                                @Override
                                public void handleMessage(Message msg) {                                    
                                    imageView.setImageBitmap((Bitmap)msg.obj);
                                }

                        };
                        new Thread() {
                                @Override
                                public void run() {
                                        // TODO Auto-generated method stub
                                        Bitmap bmp = loadImgFromUrl(imageUrl);
                                        Message message = handler.obtainMessage(0, bmp);
                                        handler.sendMessage(message);
                                }
                        }.start();                       
                        System.out.println("-----------------------����ͼƬ�߳̿���");
                }else{
                        if (imageCache == null) {
                                imageCache = new HashMap<String, SoftReference<Bitmap>>();
                        }
                        if (imageCache.containsKey(key)) {
                                SoftReference<Bitmap> softReference = imageCache.get(key);
                                if (softReference == null) {
                                        return null;
                                }
                                 bmp = softReference.get();
                                if (bmp != null) {
                                        return bmp;
                                }
                        } else {
                                imageCache.put(key, null);
                        }
                       final Handler handler = new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                        super.handleMessage(msg);
                                         imageView.setImageBitmap((Bitmap)msg.obj);
                                }
                        };
                        new Thread() {
                                @Override
                                public void run() {
                                        Bitmap bmp  = loadImgFromUrl(imageUrl);
                                        imageCache.put(key, new SoftReference<Bitmap>(bmp));
                                        Message message = handler.obtainMessage(0, bmp);
                                        handler.sendMessage(message);
                                }

                        }.start();
                        System.out.println("-----------------------����ͼƬ�߳̿���");
                }
                return null;
        }
        
        /**
         * �ص����������˼���ͼƬ
         * @author CCDrive.ZhaoYiqun
         *
         */
        
    	public interface ImageCallback {
    		public void imgeLoader(Bitmap draw, String imgeURL);
    	}
    	
    	/**
    	 * ������Ӧ��ͼƬ
    	 * @param imageURL
    	 * @param imageCallback
    	 * @return
    	 */
    	
//    	public Bitmap loadDrawable(final String imageURL,
//    			final ImageCallback imageCallback,boolean isZIP) {
//    			Bitmap image = loader.getImage(imageURL);
//    			if(image!=null){
//    				return image;
//    			}
////    		}
//    		final Handler handler = new Handler() {
//    			@Override
//    			public void handleMessage(Message msg) {
//    				// TODO Auto-generated method stub
//    				super.handleMessage(msg);
//    				imageCallback.imgeLoader((Bitmap) msg.obj, imageURL);
//    			}
//
//    		};
//    		new Thread() {
//    			@Override
//    			public void run() {
//    				// TODO Auto-generated method stub
//    				super.run();
//    				Bitmap drawable = getImage(imageURL);
//    				imgeCache.put(imageURL, new SoftReference<Bitmap>(drawable));
//    				if(drawable!=null){
//    				cache.saveBmpToSd(drawable, imageURL);
//    				}
//    				Message msg = handler.obtainMessage(0, drawable);
//    				handler.sendMessage(msg);
//    			}
//
//    		}.start();
//
//    		return null;
//    	}
}       

