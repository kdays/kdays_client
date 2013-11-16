package com.kdclient.ui;

import java.util.HashMap;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.kdclient.R;
import com.kdclient.SaveData;
import com.kdclient.Utils;
import com.kdclient.api.kdApi;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class TopicListActivity extends BaseActivity{
	
	public int nSinceId = 0;
	public int nMaxId = 0;
	private kdApi api = new kdApi();
	
	private ProgressDialog dialog;
	private TopicListFrag topicFragment;
	
	private String fid;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent intent = getIntent(); 
        JSONObject content = (JSONObject)JSONValue.parse(intent.getStringExtra("topic").toString()); 
        
        fid = content.get("fid").toString();
        
        getSupportActionBar().setTitle(content.get("name").toString());
        
        setContentView(R.layout.topic_list);
        dialog = new ProgressDialog(this);

        getNowData("" + nSinceId);
        //((TextView)findViewById(R.id.text)).setText("TEST");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Used to put dark icons on light action bar
        boolean isLight = true;

        menu.add("Post")
            .setIcon(isLight ? R.drawable.ic_compose_inverse : R.drawable.ic_compose)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        menu.add("Refresh")
            .setIcon(isLight ? R.drawable.ic_refresh_inverse : R.drawable.ic_refresh)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	String sKey = item.getTitle().toString();
    	if(sKey == "Post"){
    		getNextPage();
    	}else if(sKey == "Refresh"){
    		getNowData("" + nSinceId);
    	}
    	
    	return true;
    }
    
    private void SetData(String ret){
		topicFragment = new TopicListFrag();
		
    	Bundle args = new Bundle();  
		args.putString("data", ret);
		topicFragment.setArguments(args);

    	getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.tab_list, topicFragment)
		.commit();
	}
    
    public void getNextPage(){
    	Log.i("kdapi", "page: " + nSinceId + "/" + nMaxId);
    	
    	if(nMaxId < nSinceId){
    		displayToast("已经最后一页");
    		//Utils.showAlert(TopicListActivity.this, "失败", "已经到最后一页");
    	}else{
    		nSinceId += 20;
    		getNowData("" + nSinceId);
    	}
    }
    
    public void getPrevPage(){
    	if(nSinceId < 1)	nSinceId = 0;
    	if(nSinceId == 0){
    		displayToast("已经第一页");
    		//Utils.showAlert(TopicListActivity.this, "失败", "已经到第一页");
    	}else{
    		nSinceId -= 20;
    		getNowData("" + nSinceId);
    	}
    }
    
    public void getNowData(String Start){
    	getListData(Start);
    }
    
    public void getListData(String start){
    	HashMap<String, String> HMap = new HashMap<String, String>();
    	HMap.put("token", SaveData.GetToken(TopicListActivity.this));
        HMap.put("fid", fid);
        HMap.put("start", start);
    	
        api.ASyncAccess("get_topic_list", HMap, new AsyncHttpResponseHandler () {
        	@Override
            public void onStart() {
        		dialog.setTitle("正在联网下载数据...");
        		dialog.setMessage("请稍后...");
        		dialog.show();
            }
        	
        	@Override
            public void onFailure(Throwable e, String response) {
                Utils.showAlert(TopicListActivity.this, R.string.error_title, R.string.network_too_busy);
            }
        	
        	@Override
        	public void onFinish(){
        		dialog.cancel();
        	}
        	
        	@Override
            public void onSuccess(String response) {
        		JSONObject result = api.Parse(response);
        		nMaxId = Integer.parseInt(result.get("count").toString());
            	
            	SetData(result.get("result").toString());
            }
        });
    }
    
}
