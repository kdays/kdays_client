package com.kdclient.ui;

import java.util.HashMap;

import net.minidev.json.JSONObject;
import cn.jpush.android.api.JPushInterface;

import com.kdclient.SaveData;
import com.kdclient.R;
import com.kdclient.Utils;
import com.kdclient.api.kdApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LoginUser extends Activity{
	public Button loginBtn;
	public EditText username;
	public EditText password;
	public kdApi api = new kdApi();
	
	private loginTask nowTask;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        
		this.loginBtn = (Button)super.findViewById(R.id.loginbtn);
        this.username = (EditText)super.findViewById(R.id.username);
        this.password = (EditText)super.findViewById(R.id.password);
        
        loginBtn.setOnClickListener(new LoginListener());
    }
	
	public void loginComplete(String uid){
		Intent mainIntent = new Intent(LoginUser.this,
				HomeActivity.class);
		
    	startActivity(mainIntent);
    	finish();
	}
	
	private HashMap<String,String> getLoginParams(){
		HashMap<String, String> HMap = new HashMap<String, String>();
		
		String ETime = "" + Utils.timestamp();
		HMap.put("sig_time", ETime);
		HMap.put("sit", "md5");
		
		String User = username.getText().toString();
		String EUser = Utils.base64_encode(User);
		String EPwd = Utils.MD5(password.getText().toString());
		
		HMap.put("username", EUser);
		HMap.put("password", EPwd);
		
		String HSig = Utils.MD5(EUser + "get_access_token" + ETime + api.PRIVATE_KEY);
		HMap.put("sig", HSig);
		
		return HMap;
	}
	
	private class LoginListener implements OnClickListener{
		public void onClick(View v){
			nowTask = new loginTask();  
            nowTask.execute();  
		}
	}
	
	private class loginTask extends AsyncTask<String, String, String> {
		@Override  
        protected void onPreExecute() {
    		loginBtn.setEnabled(false);
    		loginBtn.setText("登录中..");
    	}
		
		@Override  
        protected String doInBackground(String... params) {
    		publishProgress("连接中");
    		JSONObject result = api.Access("get_access_token", getLoginParams());
    		
			if(result != null){
				int code = Integer.parseInt(result.get("code").toString());
				
				if(code == 200){
					SaveData.SaveToken(LoginUser.this, result.get("token").toString());
					SaveData.Save(LoginUser.this, "username", username.getText().toString());
					SaveData.Save(LoginUser.this, "uid", result.get("uid").toString());
					
					publishProgress("信息获得中");
					api.AccessURL("http://kdays.cn/api/push_service.php?action=register&uid=" + result.get("uid").toString() + "&apikey=" + api.PUBLIC_KEY);
					
					publishProgress("注册推送中");
					JPushInterface.setAliasAndTags(getApplicationContext(), result.get("uid").toString(), null);
					
					publishProgress("完成");
					
					return result.get("uid").toString();
				}
			}
			
			return "";
    	}
		
		@Override  
        protected void onProgressUpdate(String... progresses) {  
            loginBtn.setText("登录中.." + progresses[0]);  
        }  
		
		@Override  
        protected void onPostExecute(String result) {  
        	Utils.debug("login:" + result);  
        	
        	if(result != ""){
        		loginComplete(result);
        	}else{
        		Utils.showAlert(LoginUser.this, "出错", "登录失败");
        		
        		loginBtn.setText(getString(R.string.login));
        		loginBtn.setEnabled(true);
        	}
        }
		
		@Override  
        protected void onCancelled() {
            loginBtn.setEnabled(true);
        }  
	}
}