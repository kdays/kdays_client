package com.kdclient.ui;


import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import com.actionbarsherlock.internal.widget.IcsAdapterView.OnItemSelectedListener;
import com.kdclient.R;
import com.kdclient.api.kdApi;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class TopicListFrag extends ListFragment{
	OnItemSelectedListener mListener;  
	private kdApi api = new kdApi();
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.list, null);
	}
	
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		topicListAdapter adapter = new topicListAdapter(getActivity());
		
		String nowData = getArguments().getString("data");
		JSONArray rets = (JSONArray) JSONValue.parse(nowData);
		
		for(int i = 0; i < rets.size(); i++){
        	JSONObject tmp = api.Parse(rets.get(i).toString());
        	
        	adapter.add(new topicItem(tmp.get("tid").toString(), tmp.get("subject").toString(), "test"));
        }
		
		setListAdapter(adapter);
	}
	
	@Override  
    public void onListItemClick(ListView l, View v, int position, long id) {  
		Log.i("kdays", "click: " + JSONValue.toJSONString(l.getItemAtPosition(position)));
		Intent mainIntent = new Intent(getActivity(),
				ReadTopicActivity.class);
		
		mainIntent.putExtra("topic", JSONValue.toJSONString(l.getItemAtPosition(position)));
		startActivity(mainIntent);
	}  
	
	private class topicItem{
		public String subject;
		public String tid;
		@SuppressWarnings("unused")
		public String author;
		public topicItem(String tid, String subject, String author){
			this.subject = subject;
			this.tid = tid;
			this.author = author;
		}
	}
	
	public class topicListAdapter extends ArrayAdapter<topicItem> {
		public topicListAdapter(Context context) {
			super(context, 0);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.ex_row, null);
			}
			
			TextView title = (TextView) convertView.findViewById(R.id.ItemTitle);
			title.setText(getItem(position).subject);
			
			TextView txt = (TextView) convertView.findViewById(R.id.ItemText);
			txt.setText(getItem(position).tid);
			
			return convertView;
		}
	}
}