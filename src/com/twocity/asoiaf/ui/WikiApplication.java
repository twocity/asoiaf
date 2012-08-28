/*
 * Copyright (C) 2011 twocity
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
            .threadPoolSize(3)
            .threadPriority(Thread.NORM_PRIORITY - 2)
            .memoryCacheSize(1500000*2) // 1.5 Mb
            .denyCacheImageMultipleSizesInMemory()
            .discCacheFileNameGenerator(new Md5FileNameGenerator())
            //.enableLogging() // Not necessary in common
            .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }
}