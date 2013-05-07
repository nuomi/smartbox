package com.demonzym.framework.imagemanager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.WeakHashMap;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.demonzym.framework.net.http.IHttp;
import com.demonzym.framework.net.http.download.FileDownLoader;
import com.demonzym.framework.net.http.download.FileDownloadListener.IDownLoadListener;
import com.demonzym.framework.util.Util;

/**
 * 使用软应用的一个image管理类
 * @author Administrator
 *
 */
public class ImageManager extends WeakHashMap<String, Bitmap> {
	
	private static ImageManager mInstance;
	private static final String CACHE_FILE = "/Cache/";
	
	private ImageManager() {
		
	}

	public static ImageManager getInstance() {
		if(mInstance == null)
			mInstance = new ImageManager();
		return mInstance;
	}

	/**
	 * 判断图片是否存在首先判断内存中是否存在然后判断本地是否存在
	 * @param url
	 * @return
	 */
	public boolean isBitmapExit(String url) {
		boolean isExit = containsKey(url);
		if (false == isExit) {
			isExit = isLocalHasBmp(url);
		}
		return isExit;
	}
	
	/*
	 * 判断本地有没有
	 */
	private boolean isLocalHasBmp(String url) {
		boolean isExit = true;
		
		String name = changeUrlToName(url);
		String filePath = isCacheFileIsExit();
		
		File file = new File(filePath, name);
		
		if (file.exists()) {
			isExit = cacheBmpToMemory(file, url);
		} else {
			isExit = false;
		}
		return isExit;
	}
	
	/*
	 * 将本地图片缓存到内存中
	 */
	private boolean cacheBmpToMemory(File file, String url) {
		boolean sucessed = true;
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			sucessed = false;
			return sucessed;
		}
		byte[] bs = getBytesFromStream(inputStream);
		Bitmap bitmap = null;
		try{
		bitmap = BitmapFactory.decodeByteArray(bs, 0, bs.length);
		}catch (OutOfMemoryError e) {
			// TODO: handle exception
		}
		if (bitmap == null) {
			return false;
		}
		this.put(url, bitmap, false);
		return sucessed;
	}
	
	private byte[] getBytesFromStream(InputStream inputStream) {
		if(inputStream == null)
			return null;
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] b = new byte[1024];
		int len = 0;
		while (len != -1) {
			try {
				len = inputStream.read(b);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (len != -1) {
				baos.write(b, 0, len);
			}
		}

		if (inputStream != null) {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return baos.toByteArray();
	}
	
	/*
	 * 判断缓存文件夹是否存在如果存在怎返回文件夹路径，如果不存在新建文件夹并返回文件夹路径
	 */
	private String mRootPath = "";
	private String isCacheFileIsExit() {
		String filePath="";
		
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			mRootPath = Environment.getExternalStorageDirectory().toString();
		}
		filePath = mRootPath + CACHE_FILE;
		File file = new File(filePath);
		if (!file.exists()) {
			file.mkdirs();
		}
		return filePath;
	}
	
	/*
	 * 将url变成图片的地址
	 */
	private String changeUrlToName(String url) {
		if(Util.isStringEmpty(url))
			return "";
		String name = url.replaceAll(":", "_");
		name = name.replaceAll("//", "_");
		name = name.replaceAll("/", "_");
		name = name.replaceAll("=", "_");
		name = name.replaceAll(",", "_");
		name = name.replaceAll("&", "_");
		return name;
	}
	
	public String getImagePath(String url){
		return mRootPath + CACHE_FILE + changeUrlToName(url);
	}
	
	public Bitmap put(String key, Bitmap value) {
		String filePath = isCacheFileIsExit();
		String name = changeUrlToName(key);
		File file = new File(filePath, name);
		OutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		value.compress(CompressFormat.JPEG, 100, outputStream);
		try {
			outputStream.flush();
		} catch (IOException e1) {
			e1.printStackTrace();;
		}
		try {
			outputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();;
		}
		if (null != outputStream) {
			try {
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();;
			}
			outputStream = null;
		}
		
		return super.put(key, value);
	}

	/**
	 * 
	 * @param key
	 * @param value
	 * @param isCacheToLocal  是否缓存到本地
	 * @return
	 */
	public Bitmap put(String key, Bitmap value, boolean isCacheToLocal) {
		if (isCacheToLocal) {
			return this.put(key, value);
		} else {
			return super.put(key, value);
		}
	}
	
	public void getImage(String url, final IImageDownloadListener listener) {
		if (isBitmapExit(url)) {
			Bitmap bitmap = get(url);
			listener.onGetImage(bitmap, getImagePath(url));
		}
		else
		{
			FileDownLoader.getInstance().downloadFile(url, getImagePath(url), new IDownLoadListener() {
				
				public void onProcess(int cur, int max) {
					if(listener != null)
						listener.onDownloading(cur, max);
				}				

				public void onError(IHttp http) {
					listener.onGetImageError();
				}

				public void onSuccess(IHttp http, String path) {
					try{
						Bitmap bmp = BitmapFactory.decodeFile(path);
						listener.onGetImage(bmp, path);
					}catch(OutOfMemoryError e){
						Log.e("ImageManager", "out of memory");
					}
				}
			});
		}
	}
	
	public interface IImageDownloadListener {
		public void onGetImage(Bitmap bitmap, String localPath);
		public void onGetImageError();
		public void onDownloading(int cur, int max);
	}
}
