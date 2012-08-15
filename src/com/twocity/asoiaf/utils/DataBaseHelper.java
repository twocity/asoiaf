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
    //The Android's default system path of your application database.
    private static String DB_PATH = "/data/data/com.twocity.asoiaf/databases/";
 
    private static String DB_NAME = "asoiaf_db";
    private static final int DB_VERSION = 1;
    private static final String _ID = "_id";
    private static final String PICTURE_URLS_TABLENAME = "picture_urls_table";
    private static final String THUMB = "thumb";
    private static final String ORIGINAL = "original";
 
    private SQLiteDatabase myDataBase; 
 
    private final Context myContext;
 
    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context
     */
    public DataBaseHelper(Context context) {
 
    	super(context, DB_NAME, null, 1);
        this.myContext = context;
    }	
 
  /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
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
 
    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
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
 
    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
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
 
        // Add your public helper methods to access and get content from the database.
       // You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
       // to you to create adapters for your views.
 
}