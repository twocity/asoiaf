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

package com.twocity.asoiaf.utils;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;


public  class DownloadIntentService extends IntentService{
	private static final String TAG = "DownloadIntentService";
	private String ACTION_FETCH_URLS = "action.fetch_urls";
	private String ACTION_FETCH_URLS_COMPLETE = "action_fetch_urls_complete";
	private String ACTION_CREATE_DATABASE = "action_create_database";

	public DownloadIntentService() {
		super(TAG);
	}
	
    @Override
    protected void onHandleIntent(Intent intent){
    	if(intent.getAction().equals(ACTION_FETCH_URLS)){
    		Log.d(TAG,"action: "+intent.getAction());
//    		inserURLs2DB();
//    		sendBroadcast();
    	}else if(intent.getAction().equals(ACTION_CREATE_DATABASE)){
    		Log.d(TAG,"=== create database ===");
    		// copy database
    		DataBaseHelper dbHelper = new DataBaseHelper(this);
    		try{
    			dbHelper.createDataBase();
    		}catch(IOException e){
    			Log.d(TAG,e.getMessage());
    		}
    		//createDB();
    	}
    }
    
    @SuppressWarnings("unused")
	private void sendBroadcast(){
		Intent resultIntent = new Intent();
		resultIntent.setAction(ACTION_FETCH_URLS_COMPLETE);
		this.sendBroadcast(resultIntent);
    }
    
    @SuppressWarnings("unused")
	private void inserURLs2DB(){
    	DataBaseHelper dbHelper = new DataBaseHelper(this);
		if(dbHelper.checkDataBase()){
			try{
				dbHelper.openDataBase();
	    		for(int i=0;i<60;i++){
	    			ImageUrl item = FetchUrls.FetchImageUrl(String.format(FetchUrls.IMAGE_URL, String.valueOf(i)));
	    			item.setId(i);
	    			if(!item.isEmpty()){
	    				dbHelper.insert(item);
	    			}
	    		}
	    		dbHelper.close();
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
    }
    
    @SuppressWarnings("unused")
    private void createDB(){
        Log.d(TAG,"=== createdb... ====");
        DBHandler dbhandler = new DBHandler(this);
        ArrayList<ImageUrl> lists = FetchUrls.FetchImageUrlList();
        int i = 0;
        for(ImageUrl item:lists){
            if(!item.isEmpty()){
                item.setId(i++);
                dbhandler.insertURLs(item);
            }
            
        }
    }
}