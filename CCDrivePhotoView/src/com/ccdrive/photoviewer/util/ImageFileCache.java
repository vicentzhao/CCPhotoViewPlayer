package com.ccdrive.photoviewer.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Comparator;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

public class ImageFileCache {
	private static ImageFileCache imageCash =  null;
	private static final String CACHDIR = "CCdrive/ccphotoviewer/imagecache";
	private static final String WHOLESALE_CONV = ".cach";
	/** 过期时间3�?**/
	private static final long mTimeDiff = 3 * 24 * 60 * 60 * 1000;
  //单例模式
	public synchronized static ImageFileCache getCashInstance(){
		if(imageCash==null){
			imageCash = new ImageFileCache();
		}
		return imageCash;
	}
	//私有化构造函�?
	private ImageFileCache(){}
//	public ImageFileCache() {
//		// 清理文件缓存
//		removeCache(getDirectory());
//	}

	public Bitmap getImage(final String url) {
		final String path = getDirectory() + "/" + convertUrlToFileName(url);
		File file = new File(path);
		if (file.exists()) {
			Bitmap bmp = BitmapFactory.decodeFile(path);
			if (bmp == null) {
				file.delete();
			} else {
				updateFileTime(path);
				return bmp;
			}
		}
		return null;
	}
	/*** 缓存空间大小 ****/
	private static final int FREE_SD_SPACE_NEEDED_TO_CACHE = 10;
	public void saveBmpToSd(Bitmap bm, String url) {
//		if (bm == null) {
//			// �?��保存的是�?��空�?
//			return;
//		}
		// 判断sdcard上的空间
		if (FREE_SD_SPACE_NEEDED_TO_CACHE > freeSpaceOnSd()) {
			// SD空间不足
			return;
		}
		String filename = convertUrlToFileName(url);
		String dir = getDirectory();
		File dirMk=new File(dir);
		if(!dirMk.exists()){
			dirMk.mkdirs();
		}
		File file = new File(dir + "/" + filename);
		try {
			file.createNewFile();
			OutputStream outStream = new FileOutputStream(file);
			bm.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
			outStream.flush();
			outStream.close();

		} catch (FileNotFoundException e) {
			Log.w("ImageFileCache", "FileNotFoundException");
		} catch (IOException e) {
			Log.w("ImageFileCache", "IOException");
		}
	}
	private static final int CACHE_SIZE = 10;
	// 清理缓存
	/**
	 * 计算存储目录下的文件大小�?
	 * 当文件�?大小大于规定的CACHE_SIZE或�?sdcard剩余空间小于FREE_SD_SPACE_NEEDED_TO_CACHE的规�?
	 * 那么删除40%�?��没有被使用的文件
	 * 
	 * @param dirPath
	 * @param filename
	 */
	private boolean removeCache(String dirPath) {
		File dir = new File(dirPath);
		File[] files = dir.listFiles();
		if (files == null) {
			return true;
		}
		if (!android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			return false;
		}
		int dirSize = 0;
		for (int i = 0; i < files.length; i++) {
			if (files[i].getName().contains(WHOLESALE_CONV)) {
				dirSize += files.length;
			}
		}
		if (dirSize > CACHE_SIZE * MB
				|| FREE_SD_SPACE_NEEDED_TO_CACHE > freeSpaceOnSd()) {
			int removeFactor = (int) ((0.4 * files.length) + 1);
			Arrays.sort(files, new FileLastModifSort());
			Log.i("ImageFileCache", "清理缓存文件");
			for (int i = 0; i < removeFactor; i++) {
				if (files[i].getName().contains(WHOLESALE_CONV)) {
					files[i].delete();
				}
			}
		}
		if (freeSpaceOnSd() <= CACHE_SIZE) {
			return false;
		}
		return true;
	}
	/**
	 * TODO 根据文件的最后修改时间进行排�?*
	 */
	private class FileLastModifSort implements Comparator<File> {
		public int compare(File arg0, File arg1) {
			if (arg0.lastModified() > arg1.lastModified()) {
				return 1;
			} else if (arg0.lastModified() == arg1.lastModified()) {
				return 0;
			} else {
				return -1;
			}
		}
	}
	/**
	 * 删除过期文件
	 * 
	 * @param dirPath
	 * @param filename
	 */
	public void removeExpiredCache(String dirPath, String filename) {

		File file = new File(dirPath, filename);

		if (System.currentTimeMillis() - file.lastModified() > mTimeDiff) {

			Log.i("ImageFileCache", "Clear some expiredcache files ");

			file.delete();
		}
	}
	/**
	 * 修改文件的最后修改时�?这里�?��考虑,是否将使用的图片日期改为当前日期
	 * 
	 * @param path
	 */
	public void updateFileTime(String path) {
		File file = new File(path);
		long newModifiedTime = System.currentTimeMillis();
		file.setLastModified(newModifiedTime);
	}
	/**
	 * 计算sdcard上的剩余空间
	 * 
	 * @return
	 */
	private int MB = 1024 * 1024;

	private int freeSpaceOnSd() {
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory()
				.getPath());
		double sdFreeMB = ((double) stat.getAvailableBlocks() * (double) stat
				.getBlockSize()) / MB;
		return (int) sdFreeMB;
	}
	/** 将url转成文件�?**/
	private String convertUrlToFileName(String url) {
		int start = url.lastIndexOf('/');
		int end =url.lastIndexOf(".");
		if(start>=end){
		String 	extension="unkownedpath";
			return extension+WHOLESALE_CONV;
		}else{
		String extension = url.substring(start,end);
		String fileName = extension.replace("/", "");
		return fileName+WHOLESALE_CONV;
		}
		
	}
	/** 获得缓存目录 **/
	private String getDirectory() {
		String dir = getSDPath() + "/" + CACHDIR;
		String substr = dir.substring(0, 4);
		if (substr.equals("/mnt")) {
			dir = dir.replace("/mnt", "");
		}
		return dir;
	}
	/**** 取SD卡路径不�? ****/
	public String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存�?
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();// 获取跟目�?
		}
		if (sdDir != null) {
			return sdDir.toString();
		} else {
			return "";
		}
	}
}