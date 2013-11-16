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
        	//System.out.println("�յ����Զ�����Ϣ����Ϣ�����ǣ�" + bundle.getString(JPushInterface.EXTRA_MESSAGE));
            // �Զ�����Ϣ����չʾ��֪ͨ������ȫҪ������д����ȥ����
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            //System.out.println("�յ���֪ͨ");
        	//Log.i("kdapi.share", "get message");
            // �����������Щͳ�ƣ�������Щ��������
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            // ����������Լ�д����ȥ�����û���������Ϊ
        	//Log.i("kdapi.share", bundle.getString(JPushInterface.EXTRA_EXTRA));
        	Utils.debug(bundle.getString(JPushInterface.EXTRA_EXTRA));
        	
            Intent i = new Intent(context, ReadTopicActivity.class);  //�Զ���򿪵Ľ���
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("topic", bundle.getString(JPushInterface.EXTRA_EXTRA));
            context.startActivity(i);
        } else {
           // Log.i("kdapi.share", "Unhandled intent - " + intent.getAction());
        }
	}
}