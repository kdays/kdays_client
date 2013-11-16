package com.kdclient.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class http{
	public String GET(String url) throws Exception{
		URL urlPath = null;
		urlPath = new URL(url);
		
		HttpURLConnection conn = null;
		conn = (HttpURLConnection) urlPath.openConnection();

		String result = null;
		try {
			if (conn.getResponseCode() == 200) {
				InputStream is = conn.getInputStream();
				byte[] data = readStream(is);
				result = new String(data);
			}
		} catch (IOException e) {
			
		}
		
		return result;
	}
	
	public void AccessURL(String URL){
		try {
			this.GET(URL);
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}
	
	private static AsyncHttpClient client = new AsyncHttpClient();
	public static void ASyncGet(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
	      client.get(url, params, responseHandler);
	}
	
	public static void ASyncPost(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
	     client.post(url, params, responseHandler);
	 } 
	
	public static byte[] readStream(InputStream inputStream) throws IOException { 
		ByteArrayOutputStream bout = new ByteArrayOutputStream(); 
		byte[] buffer = new byte[1024]; 
		int len = 0; 
		while ((len = inputStream.read(buffer)) != -1) { 
			bout.write(buffer, 0, len); 
		} 
		
		bout.close(); 
		inputStream.close(); 
		
		return bout.toByteArray(); 
	}
}