package com.kdclient.ui;

import java.util.ArrayList;
import java.util.HashMap;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.androidquery.AQuery;
import com.kdclient.R;
import com.kdclient.SaveData;
import com.kdclient.Utils;
import com.kdclient.api.kdApi;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ReadTopicActivity extends BaseActivity{
	
	protected int nSinceId = 0;
	protected int nMaxId = 0;
	
	private kdApi api = new kdApi();
	private String tid;
	private AQuery aq;
	private ListView mListView = null;
	
	private View loadMoreView;    
    private Button loadMoreButton;    
    
    private View loadLastView;    
    private Button loadLastButton;    
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent intent = getIntent(); 
        JSONObject content = (JSONObject)JSONValue.parse(intent.getStringExtra("topic").toString()); 
        
        tid = content.get("tid").toString();
        Utils.debug("tid:" + tid);
        
        if(content.get("subject").toString() != null){
        	getSupportActionBar().setTitle(content.get("subject").toString());
        }
        
        setContentView(R.layout.topic_detail);
        
        aq = new AQuery(this);
        mListView = (ListView) findViewById(R.id.msg_list_item);
        
        /*mListView = ((PullAndLoadListView) findViewById(R.id.msg_list_item));
        
        mListView.setOnLoadMoreListener(new PullAndLoadListView.OnLoadMoreListener() {

            public void onLoadMore() {
                loadMoreData();
            }
        });*/
        
        //mListView.setLastUpdated(getLastSyncTime(Preferences.PREF_LAST_SYNC_TIME));
        
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
            	
                Utils.debug("item cliecked:" + JSONValue.toJSONString(parent.getItemAtPosition(position)));
            }
        });
        
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
        
        menu.add("Page")
        	.setIcon(R.drawable.ic_compose_inverse)
        	.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        
        return true;
    }
    
    private void InitNxtPage(){
    	loadMoreView = getLayoutInflater().inflate(R.layout.loadmore, null);  
        loadMoreButton = (Button)loadMoreView.findViewById(R.id.loadMoreButton);
        
        mListView.addFooterView(loadMoreView);
        loadMoreButton.setOnClickListener(new View.OnClickListener() {  
            @Override  
            public void onClick(View v) {  
                loadMoreButton.setText("正在加载中...");   //设置按钮文字  
                nSinceId += 10;
                getNowData("" + nSinceId);
            }  
        });  
    }
    
    private void InitPrevPage(){
    	loadLastView = getLayoutInflater().inflate(R.layout.loadlast, null);  
        loadLastButton = (Button)loadLastView.findViewById(R.id.loadLastButton);
        
        mListView.addHeaderView(loadLastView);
        loadLastButton.setOnClickListener(new View.OnClickListener() {  
            @Override  
            public void onClick(View v) {  
            	loadLastButton.setText("正在加载中...");   //设置按钮文字  
            	nSinceId -= 10;
                getNowData("" + nSinceId);
            }  
        });  
    }
    
    private boolean isDisplayNxt = false;
    private boolean isDisplayPrv = false;
    private void CheckExistPage(){
    	int NowPage = (int)Math.floor((Math.ceil((nSinceId+1) / 10) + 1));
    	
    	if(NowPage > 1 && !isDisplayPrv){
    		isDisplayPrv = true;
    		InitPrevPage();
    	}
    	
    	if(isDisplayPrv){
    		loadLastButton.setText("点击查看上一页");
    	}

    	if(NowPage == 1 && isDisplayPrv){
    		isDisplayPrv = false;
    		mListView.removeHeaderView(loadLastView);
    	}
    	
    	if(nMaxId > nSinceId && nMaxId > 10 && !isDisplayNxt){
    		isDisplayNxt = true;
    		InitNxtPage();
    	}
    	
    	if(nMaxId < nSinceId){
    		isDisplayNxt = false;
    		mListView.removeFooterView(loadMoreView);
    	}
    	
    	if(isDisplayNxt){
    		loadMoreButton.setText("当前第" + NowPage + "页，点击查看下一页");
    	}
    }
    
    public String FormatArticle(String Content){
		return Content.replace("&nbsp;", " ").replace("<br />", "\n");
	}
	
	public void CreateList(ArrayList<HashMap<String, Object>>  Posts){
		SimpleAdapter listItemAdapter = new SimpleAdapter(this, Posts, 
        		R.layout.ex_row,
        		new String[]{"content", "author"},
        		new int[]{R.id.ItemText, R.id.ItemTitle});
        
		CheckExistPage();
		mListView.setAdapter(listItemAdapter);
		
		/*mListView.onRefreshComplete();
		mListView.onLoadMoreComplete();
		mListView.setLastUpdated(getLastSyncTime(
                Preferences.PREF_LAST_SYNC_TIME));
		setLastSyncTime("" + Utils.timestamp());*/
	}
    
	public ArrayList<HashMap<String, Object>> getPosts(String ret){
		
        JSONArray rets = (JSONArray) JSONValue.parse(ret);
        
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();  
        
        
        if(ret != null){
	        for(int i = 0; i < rets.size(); i++){
	        	JSONObject tmp = api.Parse(rets.get(i).toString());
	        	
	        	HashMap<String, Object> map = new HashMap<String, Object>();  
	        	map.put("pid", "" + i);
	        	map.put("content", FormatArticle(tmp.get("content").toString()));
	        	map.put("author", tmp.get("author").toString());
	
	        	listItem.add(map);
	        }
		}
	
        return listItem;
	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	//Log.i("kdapi", "menu clicked!" + item.getTitle());
    	Utils.debug("click:" + item.getTitle());
    	
    	String sKey = item.getTitle().toString();
    	if(sKey == "Post"){
    		Intent mainIntent = new Intent(ReadTopicActivity.this,
    				PostActivity.class);
    		
    		HashMap<String, Object> map = new HashMap<String, Object>();  
    		map.put("tid", tid);
    		mainIntent.putExtra("topic", JSONValue.toJSONString(map));
    		startActivity(mainIntent);
    	}else if(sKey == "Refresh"){
    		
    	}else if(sKey == "Page"){
    		int NowPage = (int)Math.floor((Math.ceil((nSinceId+1) / 10) + 1));
    		int MaxPage = (int)Math.floor((Math.ceil((nMaxId+1) / 10) + 1));
    		
    	    final EditText PageInput = new EditText(this);
    	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	    builder.setTitle("输入跳转的页码 (" + NowPage + "/" + MaxPage + ")").setView(PageInput)
    	                .setNegativeButton("取消", null);
    	        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

    	            public void onClick(DialogInterface dialog, int which) {
    	            	int tmpId = (int) (Integer.parseInt(PageInput.getText().toString()) * 10);
    	            	if(tmpId > nMaxId)	tmpId = nMaxId - 10;
    	            	if(tmpId < 0)	tmpId = 0;
    	            	
    	            	nSinceId = tmpId;
    	            	getNowData("" + nSinceId);
    	             }
    	        });
    	        builder.show();

    	}
    	
    	return true;
    }
    
    public void getNowData(String nowLength){
    	getTopicData(nowLength);
    }
    
    private void showLoadingIndicator() {
        aq.id(R.id.placeholder_loading).visible();
        //setRefreshActionButtonState(true);
    }

    private void hideLoadingIndicator() {
        aq.id(R.id.placeholder_loading).gone();
        //setRefreshActionButtonState(false);
    }
    
    public void showErrorIndicator() {
        aq.id(R.id.placeholder_error).visible();
    }
    
    public void hideErrorIndicator() {
        aq.id(R.id.placeholder_error).gone();
    }
    
    public void getTopicData(String start){
    	HashMap<String, String> HMap = new HashMap<String, String>();
        HMap.put("token", SaveData.GetToken(ReadTopicActivity.this));
        HMap.put("tid", tid);
        HMap.put("length", "12");
        HMap.put("start", start);
    	
        api.ASyncAccess("get_forum_post", HMap, new AsyncHttpResponseHandler () {
        	@Override
            public void onStart() {
        		showLoadingIndicator();
            }
        	
        	@Override
            public void onFailure(Throwable e, String response) {
        		CheckExistPage();
                Utils.showAlert(ReadTopicActivity.this, R.string.error_title, R.string.network_too_busy);
            }
        	
        	@Override
        	public void onFinish(){
        		hideLoadingIndicator();
        	}
        	
        	@Override
            public void onSuccess(String response) {
        		JSONObject result = api.Parse(response);
        		nMaxId = Integer.parseInt(result.get("replies").toString());
            	
            	ArrayList<HashMap<String, Object>> Posts = getPosts(result.get("result").toString());
            	CreateList(Posts);
            }
        });
    }
}
