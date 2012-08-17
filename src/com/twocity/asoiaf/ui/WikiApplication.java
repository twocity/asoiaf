package com.twocity.asoiaf.ui;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.app.Application;
import android.util.Log;


public class WikiApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // This configuration tuning is custom. You can tune every option, you may tune some of them, 
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        Log.d("","==== ImageLoader Configuration ===");
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
            .threadPoolSize(5)
            .threadPriority(Thread.NORM_PRIORITY - 2)
            .memoryCacheSize(1500000*3) // 1.5 Mb
            .denyCacheImageMultipleSizesInMemory()
            .discCacheFileNameGenerator(new Md5FileNameGenerator())
            //.enableLogging() // Not necessary in common
            .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }
}