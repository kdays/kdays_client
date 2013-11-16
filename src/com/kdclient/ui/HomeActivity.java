package com.kdclient.ui;

import java.util.ArrayList;
import java.util.HashMap;

import net.minidev.json.JSONObject;
import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.kdclient.R;
import com.kdclient.SaveData;
import com.kdclient.Utils;
import com.kdclient.api.kdApi;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;


public class HomeActivity extends BaseActivity implements ActionBar.TabListener {
	
	private kdApi api = new kdApi();
	private ProgressDialog dialog;
	
	private TopicListFrag topicFragment;
	private ForumListFrag forumFragment;
	
	//private static final String STATE_MENUDRAWER = HomeActivity.class.getName() + ".menuDrawer";
	private MenuDrawer mMenuDrawer;
    
    protected ListFragment mFrag;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mMenuDrawer = MenuDrawer.attach(this, MenuDrawer.Type.BEHIND, Position.LEFT, MenuDrawer.MENU_DRAG_WINDOW);
        
		mMenuDrawer.setContentView(R.layout.tab_navigation);
		mMenuDrawer.setMenuView(R.layout.menu_frame);

        if (savedInstanceState == null) {
			FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
			mFrag = new UserInfoFrag();
			t.replace(R.id.menu_frame, mFrag);
			t.commit();
		} else {
			mFrag = (ListFragment)this.getSupportFragmentManager().findFragmentById(R.id.menu_frame);
		}
		
		dialog = new ProgressDialog(this);
		// set the Above View
		getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
		
		//setContentView(R.layout.content_frame);
		

        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
        ArrayList<String> map = new ArrayList<String>();  
    	map.add("最新回复");
    	map.add("最新帖子");
    	map.add("板块列表");
    	
    	for(int i = 0; i < map.size(); i++){
    		ActionBar.Tab tab = getSupportActionBar().newTab();
            tab.setText(map.get(i).toString());
            tab.setTabListener(this);
            getSupportActionBar().addTab(tab);
    	}
    	
		//setSlidingActionBarEnabled(false);
	}
	
	private void SetData(String ret){
		topicFragment = new TopicListFrag();
		
    	Bundle args = new Bundle();  
		args.putString("data", ret);
		topicFragment.setArguments(args);
        
		Utils.debug("frag_pack send ok");

    	getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.tab_list, topicFragment)
		.commit();
	}
	
	private void SetForumData(String ret){
		forumFragment = new ForumListFrag();
		
		Bundle args = new Bundle();  
		args.putString("data", ret);
		forumFragment.setArguments(args);
		
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.tab_list, forumFragment)
		.commit();
	}
	
    @Override
    public void onTabReselected(Tab tab, FragmentTransaction transaction) {
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction transaction) {
    	if(tab.getText() == "最新回复"){
    		getListData("reply");
    	}else if(tab.getText() == "最新帖子"){
    		getListData("subject");
    	}else{
    		getForumList();
    	}
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction transaction) {
    }
    
    public void getForumList(){
    	HashMap<String, String> HMap = new HashMap<String, String>();
    	HMap.put("child", "0");
        HMap.put("cate", "0");
    	
        api.ASyncAccess("get_forum_list", HMap, new AsyncHttpResponseHandler () {
        	@Override
            public void onStart() {
        		dialog.setTitle("正在联网下载数据...");
        		dialog.setMessage("请稍后...");
        		dialog.show();
            }
        	
        	@Override
            public void onFailure(Throwable e, String response) {
                Utils.showAlert(HomeActivity.this, R.string.error_title, R.string.network_too_busy);
            }
        	
        	@Override
        	public void onFinish(){
        		dialog.cancel();
        	}
        	
        	@Override
            public void onSuccess(String response) {
        		JSONObject result = api.Parse(response);
            	
        		SetForumData(result.get("result").toString());
            }
        });
    }
    
    public void getListData(String type){
    	HashMap<String, String> HMap = new HashMap<String, String>();
    	HMap.put("token", SaveData.GetToken(HomeActivity.this));
        HMap.put("get_type", type);
    	
        api.ASyncAccess("get_hot_topic", HMap, new AsyncHttpResponseHandler () {
        	@Override
            public void onStart() {
        		dialog.setTitle("正在联网下载数据...");
        		dialog.setMessage("请稍后...");
        		dialog.show();
            }
        	
        	@Override
            public void onFailure(Throwable e, String response) {
                Utils.showAlert(HomeActivity.this, R.string.error_title, R.string.network_too_busy);
            }
        	
        	@Override
        	public void onFinish(){
        		dialog.cancel();
        	}
        	
        	@Override
            public void onSuccess(String response) {
        		JSONObject result = api.Parse(response);
            	
            	SetData(result.get("result").toString());
            }
        });
    }
}
