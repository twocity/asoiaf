package com.twocity.asoiaf.ui;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.twocity.asoiaf.R;
import com.twocity.asoiaf.utils.DataBaseHelper;
import com.twocity.asoiaf.utils.DownloadIntentService;
import com.twocity.asoiaf.utils.ImageDownloader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;


public class GridViewActivity extends BaseActivity{
	private static final String TAG = "ImageViewActivity";
	
	private static int QUERY_SUCCESS_MSG = 0;
	private static int QUERY_FAILED_MSG = 1;
	private String ACTION_FETCH_URLS = "action.fetch_urls";
	private GridView gridView;
	private ProgressBar mProgressBar;
	private ImageCursorAdapter2 cursorAdapter = null;
	private Cursor mCursor = null;
	
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gridview);
		
		mProgressBar = (ProgressBar)findViewById(R.id.progressbar);
		
	    options = new DisplayImageOptions.Builder()
	       .showStubImage(R.drawable.frame)
	       .showImageForEmptyUri(R.drawable.frame)
           .cacheInMemory()
           .cacheOnDisc()
           .build();
	    
	    setGridView();
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

//        int[] toViews = {R.id.imageview_item}; 
//        String[] from = {"thumb"};
//        cursorAdapter = new ImageCursorAdapter2(GridViewActivity.this, 
//              R.layout.grid_view_item, mCursor,
//              from, toViews);
//        gridView.setAdapter(cursorAdapter);
        
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
                cursorAdapter = new ImageCursorAdapter2(GridViewActivity.this, 
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
	protected void onStart(){
		
		IntentFilter filter = new IntentFilter();
		filter.addAction("action_fetch_urls_complete");
		this.registerReceiver(broadcastreceiver, filter);
		super.onStart();
	}
	
	@Override
	protected void onStop(){
		imageLoader.stop();
		this.unregisterReceiver(broadcastreceiver);
		super.onStop();
	}
	
	public class ImageCursorAdapter2 extends SimpleCursorAdapter {
        private ImageView imageview;
        private LayoutInflater mInflater;
        private final ImageDownloader imageDownloader = new ImageDownloader();

        public ImageCursorAdapter2 (Context context, int layout, Cursor c, String[] from, int[] to) {
            super(context, layout, c, from, to);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            LinearLayout ll = null;
            if (view == null) {
                ll = (LinearLayout) mInflater.inflate(R.layout.grid_view_item,null);
            } else {
                ll = (LinearLayout)view;
            }

            int thumbIndex = cursor.getColumnIndex("thumb");
            imageview = (ImageView)ll.findViewById(R.id.imageview_item);
            imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
            String imagelink = cursor.getString(thumbIndex);
            //imageDownloader.download(imagelink, (ImageView) imageview);
            
            imageLoader.displayImage(imagelink, imageview, options, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(Bitmap loadedImage) {
                    Animation anim = AnimationUtils.loadAnimation(GridViewActivity.this, R.anim.fade_in);
                    imageview.setAnimation(anim);
                    anim.start();
                }
            });
            
        }
        
    }
}