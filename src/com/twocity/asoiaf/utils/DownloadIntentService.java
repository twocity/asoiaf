package com.twocity.asoiaf.utils;

import java.io.IOException;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;


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
    		inserURLs2DB();
    		sendBroadcast();
    	}else if(intent.getAction().equals(ACTION_CREATE_DATABASE)){
    		Log.d(TAG,"=== create database ===");
    		// copy database
    		DataBaseHelper dbHelper = new DataBaseHelper(this);
    		try{
    			dbHelper.createDataBase();
    		}catch(IOException e){
    			Log.d(TAG,e.getMessage());
    		}
    	}
    }
    
    private void sendBroadcast(){
		Intent resultIntent = new Intent();
		resultIntent.setAction(ACTION_FETCH_URLS_COMPLETE);
		this.sendBroadcast(resultIntent);
    }
    
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
}