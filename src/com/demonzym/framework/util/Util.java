package com.demonzym.framework.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.zip.GZIPInputStream;

import com.demonzym.smartbox.data.Foobar;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

public class Util {
	/**
	 * ��ѹ
	 * @param data
	 * @return
	 * @throws IOException
	 */
	public static byte[] gzipDecompress(byte[] data) throws IOException {
		GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(data));
		ByteArrayOutputStream o = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int iRead = 0;
		while(0 < (iRead = gzip.read(buffer))){
			o.write(buffer, 0, iRead);
		}
		return o.toByteArray();
	}
	
	/**
	 * ��ѹ
	 * @param InputStream
	 * @return
	 * @throws IOException
	 */
	public static InputStream gzipDecompress(InputStream in) throws IOException {
		GZIPInputStream gzip = new GZIPInputStream(in);
		Log.i("PalUtil", "gzipDecompress InputStream");
		return gzip;
	}
	
	/**
	 * ��ȡ���ÿռ�
	 * @param root
	 * @return
	 */
	public static long getAvailableBytes(File root) {
        StatFs stat = new StatFs(root.getPath());
        // put a bit of margin (in case creating the file grows the system by a few blocks)
        long availableBlocks = (long) stat.getAvailableBlocks() - 4;
        return stat.getBlockSize() * availableBlocks;
    }
	public static long getAvailableBytes(String dir){
		return getAvailableBytes(new File(dir));
	}
	

	public static String inputStream2String(InputStream is) throws IOException{
	    BufferedReader in = new BufferedReader(new InputStreamReader(is));
	    StringBuffer buffer = new StringBuffer();
	    String line = "";
	    while ((line = in.readLine()) != null){
	      buffer.append(line);
	    }
	    return buffer.toString();
	}
	
	public static InputStream String2InputStream(String str){
	    ByteArrayInputStream stream = new ByteArrayInputStream(str.getBytes());
	    return stream;
	}
	
	/**
	 * �ж�string�Ƿ�Ϊ��
	 * @param v
	 * @return
	 */
	public static boolean isStringEmpty(String v)
	{
		if(v == null || v.length() == 0 ){
			return true;
		}else{
			return false;
		}
	}

	public static boolean isContainFoobar(List<Foobar> list, Foobar foobar) {		
		if(list == null || foobar == null)
			return false;
		Iterator<Foobar> iterator = list.iterator();
		while(iterator.hasNext()){
			Foobar fb = iterator.next();
			if(fb.getName().equals(foobar.getName()) ||
					fb.getId().equals(foobar.getId())){
				
				return true;
			}
//		    Iterator<String> sListIterator = list.iterator();  
//		    while(sListIterator.hasNext()){  
//		        String e = sListIterator.next();  
//		        if(e.equals("3")){  
//		        sListIterator.remove();  
//		        }  
//		    } 
		}
		return false;
	}
	
	
/*	public static int isContainFoobar(List<Foobar> list, Foobar foobar) {
		int index=0;
		Log.w("index1", index+"");
		if(list == null || foobar == null)
			return 0;
		Iterator<Foobar> iterator = list.iterator();
		while(iterator.hasNext()){			
			Foobar fb = iterator.next();
			if(fb.getName().equals(foobar.getName()) ||
					fb.getId().equals(foobar.getId())){
				Log.w("index2", index+"");
				return index;
			}
			index++;
		}
		return 0;
	}*/
	
	public static boolean hasSD() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}
	
	private static final String DATE_FORMAT_OUT = "yyyy-MM-dd";
	private static SimpleDateFormat mOutDateFormat =
			new SimpleDateFormat(DATE_FORMAT_OUT, Locale.CHINA);
	
	
	public static String getCurrentTime(){
		return mOutDateFormat.format(System.currentTimeMillis());
	}
}
