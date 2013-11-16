package com.kdclient.ui;

import net.minidev.json.JSONValue;

import com.kdclient.SaveData;
import com.kdclient.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class UserInfoFrag extends ListFragment{
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.rightside_user, null);
	}
	

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		//SharedPreferences userinfo = getActivity().getSharedPreferences("USER", Activity.MODE_PRIVATE);
		userInfoAdapter adapter = new userInfoAdapter(getActivity());

		adapter.add(new userItem("uid", SaveData.Get(getActivity(), "uid", "1")));
		adapter.add(new userItem("username", SaveData.Get(getActivity(), "username", "test")));
		setListAdapter(adapter);
	}
	
	@Override  
    public void onListItemClick(ListView l, View v, int position, long id) {  
		Log.i("kdays", "click: " + JSONValue.toJSONString(l.getItemAtPosition(position)));
	}  
	
	private class userItem{
		public String key;
		public String value;
		public userItem(String key, String value){
			this.key = key;
			this.value = value;
		}
	}
	
	public class userInfoAdapter extends ArrayAdapter<userItem> {
		public userInfoAdapter(Context context) {
			super(context, 0);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.row, null);
			}
			//ImageView icon = (ImageView) convertView.findViewById(R.id.row_icon);
			//icon.setImageResource(getItem(position).iconRes);
			
			ImageView icon = (ImageView) convertView.findViewById(R.id.row_icon);
			
			String vKey = getItem(position).key.toString();
			TextView title = (TextView) convertView.findViewById(R.id.row_title);
			
			if(vKey == "uid"){
				title.setText(getItem(1).value);
				
				icon.setVisibility(View.VISIBLE);
				ImageLoader.getInstance().displayImage("http://f.kdays.cn/1.jpg!s", icon); 
			}else{
				title.setText(getItem(1).key + "=" + getItem(1).value);
				icon.setVisibility(View.GONE);
			}
			

			return convertView;
		}
	}
}