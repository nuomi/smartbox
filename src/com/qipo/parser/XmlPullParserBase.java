package com.qipo.parser;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.net.http.AndroidHttpClient;
import android.util.Log;

public class XmlPullParserBase {
	
 private static final String TAG="XmlPullParserBase";
	
	 public static XmlPullParser getUriXmlPullParser(String url,AndroidHttpClient mHttpClient) {
		 
	        XmlPullParser parser = null;
	        try {
	            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	            factory.setNamespaceAware(true);
	            parser = factory.newPullParser();
	        } catch (XmlPullParserException e) {
	            Log.e(TAG, "Unable to create XmlPullParser", e);
	            return null;
	        }

	        InputStream inputStream = null;
	        try {
	            final HttpGet get = new HttpGet(url);
	            HttpResponse response = mHttpClient.execute(get);
	            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
	                final HttpEntity entity = response.getEntity();
	                if (entity != null) {
	                    inputStream = entity.getContent();
	                }
	            }
	        } catch (IOException e) {
	            Log.w(TAG, "Error while retrieving XML file " + url, e);
	            return null;
	        }

	        try {
	        	if(inputStream!=null)
	            parser.setInput(inputStream, null);
	        } catch (XmlPullParserException e) {
	            Log.w(TAG, "Error while reading XML file from " + url, e);
	            return null;
	        }
	        
	        return parser;
	    }
}