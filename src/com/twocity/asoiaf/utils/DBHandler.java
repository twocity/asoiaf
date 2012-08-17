package com.twocity.asoiaf.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DBHandler extends SQLiteOpenHelper {
    
    private static final String TAG = "DBHandler";
    private static final String DB_NAME = "asoiaf_db_new";
    private static final int DB_VERSION = 1;
    private static final String _ID = "_id";
    private static final String PICTURE_URLS_TABLENAME = "picture_urls_table";
    private static final String THUMB = "thumb";
    private static final String ORIGINAL = "original";
    
    
    private static final String CREATE_URLS_DB = 
    		"CREATE TABLE " + PICTURE_URLS_TABLENAME + "(" +
    		_ID + " INTEGER PRIMARY KEY," +
    	    THUMB + " TEXT," +
    		ORIGINAL + " TEXT" + ")";
    
    
    public DBHandler(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db){
        Log.d(TAG,"=== DB onCreate ===");
        db.execSQL(CREATE_URLS_DB);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
        Log.d(TAG,"=== onUpgrage ===");
        db.execSQL("DROP TABLE IF EXISTS" + CREATE_URLS_DB);
        onCreate(db);  
    }                
    
    public void insertURLs(ImageUrl imageurl){
    	SQLiteDatabase db = this.getWritableDatabase();
    	ContentValues values = new ContentValues();
    	values.put(THUMB, imageurl.getThumbUrl());
    	values.put(ORIGINAL, imageurl.getOriginUrl());
    	values.put(_ID, imageurl.getId());
    	Log.d(TAG,"=== insert ImageUrl Object " + + imageurl.getId()+" ===");
    	
	    db.delete(PICTURE_URLS_TABLENAME, _ID + " = ?", new String[]{ String.valueOf(imageurl.getId()) });
	    db.insert(PICTURE_URLS_TABLENAME,null,values);
	    db.close();
    }

    public Cursor queryURLs(){
    	Log.d(TAG," === query ===");
    	Cursor c = null;
    	try{
    		SQLiteDatabase db = this.getReadableDatabase();
    		c = db.query(PICTURE_URLS_TABLENAME,null,null , null, null, null, null);
    		c.moveToFirst();
    		db.close();
    	}catch(Exception e){
    		Log.d(TAG,e.getMessage());
    	}
    	return c;
    }
    
}