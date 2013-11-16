package com.kdclient;
import com.kdclient.ui.ReadTopicActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import cn.jpush.android.api.JPushInterface;

public class KdBroadcastReceiver extends BroadcastReceiver { 
	public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Utils.debug("onReceive - " + intent.getAction());
         
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
             
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
        	//System.out.println("收到了自定义消息。消息内容是：" + bundle.getString(JPushInterface.EXTRA_MESSAGE));
            // 自定义消息不会展示在通知栏，完全要开发者写代码去处理
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            //System.out.println("收到了通知");
        	//Log.i("kdapi.share", "get message");
            // 在这里可以做些统计，或者做些其他工作
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            // 在这里可以自己写代码去定义用户点击后的行为
        	//Log.i("kdapi.share", bundle.getString(JPushInterface.EXTRA_EXTRA));
        	Utils.debug(bundle.getString(JPushInterface.EXTRA_EXTRA));
        	
            Intent i = new Intent(context, ReadTopicActivity.class);  //自定义打开的界面
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("topic", bundle.getString(JPushInterface.EXTRA_EXTRA));
            context.startActivity(i);
        } else {
           // Log.i("kdapi.share", "Unhandled intent - " + intent.getAction());
        }
	}
}