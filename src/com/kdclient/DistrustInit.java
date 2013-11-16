package com.kdclient;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import cn.jpush.android.api.JPushInterface;

import android.app.Application;

public class DistrustInit extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Image
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
        .cacheInMemory()
        .cacheOnDisc()
        .build();
        
        ImageLoaderConfiguration imgConfig = new ImageLoaderConfiguration.Builder(getApplicationContext())
        .defaultDisplayImageOptions(defaultOptions)
        .threadPriority(Thread.NORM_PRIORITY - 2)
        .denyCacheImageMultipleSizesInMemory()
        .discCacheFileNameGenerator(new Md5FileNameGenerator())
        .tasksProcessingOrder(QueueProcessingType.LIFO)
        .enableLogging() // Not necessary in common
        .build();
        
        ImageLoader.getInstance().init(imgConfig);
        
        if(Config.isDEBUG){
        	JPushInterface.setDebugMode(true);
        }else{
        	JPushInterface.setDebugMode(false);
        }
        JPushInterface.init(this);
    }
}