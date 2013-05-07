package com.demonzym.smartbox.update;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public abstract class GetDataFromServer {

	public final static InputStream getData(String  url)
	{		
		try{
			URL uri = new URL(url);
			URLConnection connection=uri.openConnection();
			connection.connect();
			InputStream inputStream=connection.getInputStream();
			
			return inputStream;
		}catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
		return null;
	}
}
