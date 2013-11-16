package com.kdclient.api;

import java.util.HashMap;
import java.util.Iterator;

import com.kdclient.Config;
import com.kdclient.Utils;
import com.loopj.android.http.AsyncHttpResponseHandler;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

public class kdApi extends http{
	public String PUBLIC_KEY = Config.PUBLIC_KEY;
	public String PRIVATE_KEY = Config.PRIVATE_KEY;
	
	public JSONObject Access(String action, HashMap<String, String> requests){
		String urlParams = GetURLParam(action, requests);
		
		String result = null;
		result = this.Run(urlParams);
		
		return this.Parse(result);
	}
	
	public String GetURLParam(String action, HashMap<String, String> requests){
		String urlParams = "?action=" + action;
		
		requests.put("apikey", PUBLIC_KEY);
		if(requests != null){
			Iterator<String> iterator = requests.keySet().iterator(); 
			while (iterator.hasNext()) {
				Object key = iterator.next();
				urlParams += "&" + key + "=" + requests.get(key);
			}
		}
		
		return urlParams;
	}
	
	public JSONObject Parse(String json){
		Utils.debug("JSON:" + json);
		if(json != null){
			JSONObject JObj = (JSONObject)JSONValue.parse(json);
		
			return JObj;
		}
		
		return null;
	}
	
	public void ASyncAccess(String action, HashMap<String, String> requests, AsyncHttpResponseHandler responseHandler){
		String RequestURL = "http://kdays.cn/api/" + GetURLParam(action, requests);
		Utils.debug("ASync URL:" + RequestURL);
		
		ASyncGet(RequestURL, null, responseHandler);
	}
	
	private String Run(String request_url){
		System.setProperty("http.keepAlive", "false");  
			
		String RequestURL = "http://kdays.cn/api/" + request_url;
		Utils.debug("Sync URL: " + RequestURL);
		
		String json = null;
		try {
			json = this.GET(RequestURL);
		} catch (Exception e) {
			
		}
		
		return json;
	}
}