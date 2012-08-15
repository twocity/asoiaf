package com.twocity.asoiaf.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.twocity.asoiaf.R;
import com.twocity.asoiaf.utils.DataBaseHelper;
import com.twocity.asoiaf.utils.DownloadIntentService;
import com.twocity.asoiaf.utils.ImageCursorAdapter;


public class GridViewActivity extends BaseActivity implements OnScrollListener{
	private static final String TAG = "ImageViewActivity";
	
	private static int QUERY_SUCCESS_MSG = 0;
	private static int QUERY_FAILED_MSG = 1;
	private String ACTION_FETCH_URLS = "action.fetch_urls";
	private GridView gridView;
	private ProgressBar mProgressBar;
	private ImageCursorAdapter cursorAdapter = null;
	private Cursor mCursor = null;
	private boolean loadMore = false;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gridview);
		
		mProgressBar = (ProgressBar)findViewById(R.id.progressbar);
		
		setGridView();
		

//		SharedPreferences sp = this.getPreferences(MODE_PRIVATE);
//		boolean isfirst = sp.getBoolean("first", true);
//		if(isfirst){
//			Toast.makeText(this, R.string.first_load_data, Toast.LENGTH_LONG).show();
//			updateData();
//			SharedPreferences.Editor editor = sp.edit();
//			editor.putBoolean("first", false);
//			editor.commit();
//		}
		
	}
	
	private void setGridView(){
		gridView = (GridView)findViewById(R.id.gridview);
		
		gridView.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent,View v,int position,long id){
				Cursor c = (Cursor)parent.getAdapter().getItem(position);
				Intent intent = new Intent(GridViewActivity.this,ImageViewer.class);
				intent.putExtra("imageurl", c.getString(c.getColumnIndex("original")));
				GridViewActivity.this.startActivity(intent);
			}
		});
		//gridView.setOnScrollListener(this);
        int[] toViews = {R.id.imageview_item}; 
        String[] from = {"thumb"};
        cursorAdapter = new ImageCursorAdapter(GridViewActivity.this, 
              R.layout.grid_view_item, mCursor,
              from, toViews);
        gridView.setAdapter(cursorAdapter);
        
		mHandler.post(fetchCursorRunnable);
	}
	
	
	@SuppressWarnings("unused")
	private void updateData(){
		Intent intent = new Intent(GridViewActivity.this,DownloadIntentService.class);
		intent.setAction(ACTION_FETCH_URLS);
		GridViewActivity.this.startService(intent);
		mProgressBar.setVisibility(View.VISIBLE);
	}
	
	private BroadcastReceiver broadcastreceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals("action_fetch_urls_complete")){
				Log.d(TAG,"=== received imageurl list ===");
				mHandler.post(fetchCursorRunnable);
			}
		}
	};
	
	final Runnable fetchCursorRunnable = new Runnable(){
		@Override
		public void run(){
//			DBHandler dbHandler = new DBHandler(GridViewActivity.this);
//			mCursor = dbHandler.queryURLs();
//			if(mCursor != null){
//				mHandler.sendEmptyMessage(0);
//			}
			DataBaseHelper dbHelper = new DataBaseHelper(GridViewActivity.this);
			try{
				if(dbHelper.checkDataBase()){
					dbHelper.openDataBase();
					mCursor = dbHelper.queryCursor();
					dbHelper.close();
				}
			}catch(SQLException e){
				Log.d(TAG,e.getMessage());
			}
			if(mCursor != null){
				mHandler.sendEmptyMessage(QUERY_SUCCESS_MSG);
			}else{
				mHandler.sendEmptyMessage(QUERY_FAILED_MSG);
			}
		}
	};
	
	final Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			if(msg.what == QUERY_SUCCESS_MSG){
                Log.d(TAG,"=== update listview ===");
                int[] toViews = {R.id.imageview_item}; 
                String[] from = {"thumb"};
                cursorAdapter = new ImageCursorAdapter(GridViewActivity.this, 
                      R.layout.grid_view_item, mCursor,
                      from, toViews);
                gridView.setAdapter(cursorAdapter);
                mProgressBar.setVisibility(View.INVISIBLE);
			}else if(msg.what == QUERY_FAILED_MSG){
				Log.d(TAG,"=== query cursor from database failed ===");
			}
		}
	};
	
    @Override
    public void onScroll(AbsListView view,int firstVisible, int visibleCount, int totalCount) {
        loadMore = firstVisible + visibleCount >= totalCount;
    }
    
    @Override
    public void onScrollStateChanged(AbsListView v, int s){
        if(s == OnScrollListener.SCROLL_STATE_IDLE && loadMore){
            Log.d(TAG,"=== load more ===");
            //mHandler.post(fetchCursorRunnable);
        }
    }
	
//	public void onClickRefresh(View v){
//		updateData();
//	}
	
	@Override
	protected void onStart(){
		super.onStart();
		IntentFilter filter = new IntentFilter();
		filter.addAction("action_fetch_urls_complete");
		this.registerReceiver(broadcastreceiver, filter);
	}
	
	@Override
	protected void onStop(){
		super.onStop();
		this.unregisterReceiver(broadcastreceiver);
	}
}