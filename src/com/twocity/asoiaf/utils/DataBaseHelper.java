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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DataBaseHelper extends SQLiteOpenHelper{
	private static final String TAG = "DataBaseHelper";
    private static String DB_PATH = "/data/data/com.twocity.asoiaf/databases/";
 
    private static String DB_NAME = "asoiaf_db";
//    private static final int DB_VERSION = 1;
    private static final String _ID = "_id";
    private static final String PICTURE_URLS_TABLENAME = "picture_urls_table";
    private static final String THUMB = "thumb";
    private static final String ORIGINAL = "original";
 
    private SQLiteDatabase myDataBase; 
 
    private final Context myContext;
 

    public DataBaseHelper(Context context) {
 
    	super(context, DB_NAME, null, 1);
        this.myContext = context;
    }	

    public void createDataBase() throws IOException{
 
    	boolean dbExist = checkDataBase();
 
    	if(dbExist){
    		//do nothing - database already exist
    		Log.d(TAG,"=== database exists,no need to create ===");
    	}else{
 
    		//By calling this method and empty database will be created into the default system path
               //of your application so we are gonna be able to overwrite that database with our database.
        	this.getReadableDatabase();
 
        	try {
        		this.close();
    			copyDataBase();
 
    		} catch (IOException e) {
 
        		throw new Error("Error copying database");
 
        	}
    	}
 
    }
 
    public boolean checkDataBase(){
    	
/*    	SQLiteDatabase checkDB = null;
 
    	try{
    		String myPath = DB_PATH + DB_NAME;
    		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
 
    	}catch(SQLiteException e){
 
    		//database does't exist yet.
 
    	}
 
    	if(checkDB != null){
 
    		checkDB.close();
 
    	}
 
    	return checkDB != null ? true : false;*/
    	File dbfile = new File(DB_PATH + DB_NAME);
    	return dbfile.exists();
    }
 
    
    private void copyDataBase() throws IOException{
 
    	//Open your local db as the input stream
    	InputStream myInput = myContext.getAssets().open(DB_NAME);
 
    	// Path to the just created empty db
    	String outFileName = DB_PATH + DB_NAME;
 
    	//Open the empty db as the output stream
    	OutputStream myOutput = new FileOutputStream(outFileName);
 
    	//transfer bytes from the inputfile to the outputfile
    	byte[] buffer = new byte[1024];
    	int length;
    	while ((length = myInput.read(buffer)) != -1){
    		myOutput.write(buffer, 0, length);
    	}
 
    	//Close the streams
    	myOutput.flush();
    	myOutput.close();
    	myInput.close();
 
    }
 
    public void openDataBase() throws SQLException{
 
    	//Open the database
        String myPath = DB_PATH + DB_NAME;
        this.close();
    	myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    	Log.d(TAG,"=== myDataBase open ===");
    }
 
    @Override
	public synchronized void close() {
 
    	    if(myDataBase != null){
    	    	Log.d(TAG,"=== myDataBase closed ===");
    	    	myDataBase.close();
    	    }
    		    
 
    	    super.close();
 
	}
    
    public void insert(ImageUrl imageurl){
    	if(myDataBase != null){
    		
        	ContentValues values = new ContentValues();
        	values.put(THUMB, imageurl.getThumbUrl());
        	values.put(ORIGINAL, imageurl.getOriginUrl());
        	values.put(_ID, imageurl.getId());
        	Log.d(TAG,"=== insert ImageUrl Object " + + imageurl.getId()+" ===");
        	
        	myDataBase.delete(PICTURE_URLS_TABLENAME, _ID + " = ?", new String[]{ String.valueOf(imageurl.getId())});
        	myDataBase.insert(PICTURE_URLS_TABLENAME,null,values);
    	}

	    //this.close();
    }
    
    public Cursor queryCursor() throws SQLException{
    	Cursor c = null;
    	if(myDataBase != null){
    		c = myDataBase.query(PICTURE_URLS_TABLENAME,null,null , null, null, null, null);
    		c.moveToFirst();
    	}

		return c;
    }
 
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(TAG,"=== onCreate ===");
	}
 
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
 
}