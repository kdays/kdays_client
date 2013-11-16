package com.kdclient;

import com.kdclient.api.Preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SaveData {
	public static void SaveToken(Context context, String token) {
        Save(context, Preferences.ACCESS_TOKEN, token);
        Save(context, Preferences.EXPIRES_IN, "" + Utils.timestamp());
    }
	
	public static void Save(Context context, String key, String value) {
		SharedPreferences pref = Preferences.get(context);
        Editor editor = pref.edit();
        
        editor.putString(key, value);
        editor.commit();
	}
	
    public static void Clear(Context context) {
        SharedPreferences pref = Preferences.get(context);
        Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }
    
    public static String Get(Context context, String key, String defaultValue){
    	SharedPreferences pref = Preferences.get(context);

        return pref.getString(key, defaultValue);
    }
    
    public static String GetToken(Context context) {
    	return Get(context, Preferences.ACCESS_TOKEN, "");
    }
}