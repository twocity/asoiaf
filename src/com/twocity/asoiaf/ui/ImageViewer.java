package com.twocity.asoiaf.ui;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.twocity.asoiaf.R;
import com.twocity.asoiaf.utils.CustomHttpClient;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class ImageViewer extends BaseActivity {
	private final static String TAG = "ImageViewer";
	private static final String WALLPAPER_BUCKET_NAME =
	        Environment.getExternalStorageDirectory().toString() + "/DCIM/asoiaf";
	
	private static final int REQUEST_CROP_IMAGE = 1;
	private static final int SAVE_PIC_OK = 2;
	private static final int SD_NOT_MOUNT = 3;

    private ImageView imageView;
    private ProgressBar mProgressBar;
    private TextView mTextViewTip;
    private String mPath = "";

    private DisplayImageOptions options;
    protected ImageLoader imageLoader = ImageLoader.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageview);
        
        
        mTextViewTip = (TextView)findViewById(R.id.loading_tip);
        mProgressBar = (ProgressBar)findViewById(R.id.progressbar);
        mProgressBar.setVisibility(View.VISIBLE);
        mProgressBar.setProgress(0);
        
        imageView = (ImageView) findViewById(R.id.imageviewer);
        options = new DisplayImageOptions.Builder()
        .showImageForEmptyUri(R.drawable.frame)
        .cacheInMemory()
        .cacheOnDisc()
        .imageScaleType(ImageScaleType.EXACT)
        .build();


        setImageView();

    }
    
    private void setImageView(){
    	String url = "";
    	try{
            url = this.getIntent().getStringExtra("imageurl");
            if(url != null){
            	String name = url.substring(url.lastIndexOf("/")+1,url.lastIndexOf("."));
            	mPath = String.format("%s/asoiaf-%s.png", WALLPAPER_BUCKET_NAME,name);
            	Log.d(TAG,mPath);
            }
    	}catch(Exception e){
    		Log.d(TAG,e.getMessage());
    		return;
    	}
    	
    	imageLoader.displayImage(url, imageView, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted() {
                mTextViewTip.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onProgressChanged(int progress){
                mProgressBar.setProgress(progress);
            }
            @Override
            public void onLoadingFailed(FailReason failReason) {
                String message = null;
                switch (failReason) {
                    case IO_ERROR:
                        message = "Input/Output error";
                        break;
                    case OUT_OF_MEMORY:
                        message = "Out Of Memory error";
                        break;
                    case UNKNOWN:
                        message = "Unknown error";
                        break;
                }
                Toast.makeText(ImageViewer.this, message, Toast.LENGTH_SHORT).show();

                imageView.setImageResource(android.R.drawable.ic_delete);
            }

            @Override
            public void onLoadingComplete(Bitmap loadedImage) {
                mTextViewTip.setVisibility(View.INVISIBLE);
                mProgressBar.setVisibility(View.INVISIBLE);
                imageView.setVisibility(View.VISIBLE);
                Animation anim = AnimationUtils.loadAnimation(ImageViewer.this, R.anim.fade_in);
                imageView.setAnimation(anim);
                anim.start();
            }

            @Override
            public void onLoadingCancelled() {
                // Do nothing
            }
        });
    }


    
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
    	MenuInflater inflater = this.getMenuInflater();
    	inflater.inflate(R.menu.imageview_menu, menu);
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	
    	/*if(item.getItemId() == R.id.set_as_wallpaper){
    		setWallpaper();

    	}else */if(item.getItemId() == R.id.save2sd){
    		//save2SD();
    	    mHandler.post(SaveBitmap2SdRunnable);
    	}
    	return true;
    }
    
    final Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch(msg.what){
                case SAVE_PIC_OK:
                    Toast.makeText(ImageViewer.this, R.string.save2sd_success, Toast.LENGTH_SHORT).show();
                    break;
                case SD_NOT_MOUNT:
                    Toast.makeText(ImageViewer.this,R.string.not_mounted, Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };
    
    @SuppressWarnings("unused")
	private void setWallpaper(){
    	Log.d(TAG,"== set wallpaper ===");
    	try{
        	if(save2SD()) {
            	File mFile = new File(mPath);
                int width = getWallpaperDesiredMinimumWidth();
                int height = getWallpaperDesiredMinimumHeight();
                Display display = getWindowManager().getDefaultDisplay();
//                float spotlightX = (float) display.getWidth() / width;
//                float spotlightY = (float) display.getHeight() / height;
                
        		Intent intent = new Intent("com.android.camera.action.CROP"); 
        		intent.setDataAndType(Uri.fromFile(mFile), "image/*");
        		intent.putExtra("outputX", width); 
        		intent.putExtra("outputY", height); 
        		intent.putExtra("aspectX", width); 
        		intent.putExtra("aspectY", height); 
        		intent.putExtra("crop", true); 
        		intent.putExtra("noFaceDetection", true); 
        		intent.putExtra("return-data", true);
        		startActivityForResult(intent, REQUEST_CROP_IMAGE); 
        	}
    	}catch(Exception e){
    		e.printStackTrace();
    		Toast.makeText(this, R.string.set_wallpaper_toast_failed, Toast.LENGTH_SHORT).show();
    	}

    }
    
    final Runnable SaveBitmap2SdRunnable = new Runnable(){
        @Override
        public void run(){
            Log.d(TAG,"=== save2SD ===");

            if(!(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))) {
                 mHandler.sendEmptyMessage(SD_NOT_MOUNT);
                 return;
            }
            try {
                File dir = new File(WALLPAPER_BUCKET_NAME);
                if (!dir.exists()) dir.mkdirs();
                
                if(!new File(mPath).exists()){
                    FileOutputStream fout = new FileOutputStream(mPath);
                    BitmapDrawable bd = (BitmapDrawable)imageView.getDrawable();
                    if(bd != null){
                        bd.getBitmap().compress(CompressFormat.PNG, 100, fout);
                        fout.close();
                        mHandler.sendEmptyMessage(SAVE_PIC_OK);
                    }else{
                        return;
                    }

                }
                return;
                
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    };
    
    private boolean save2SD(){
    	Log.d(TAG,"=== save2SD ===");

    	if(!(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))) {
             Toast.makeText(this,R.string.not_mounted, Toast.LENGTH_LONG).show();
             
             return false;
        }
    	try {
            File dir = new File(WALLPAPER_BUCKET_NAME);
            if (!dir.exists()) dir.mkdirs();
            
            if(!new File(mPath).exists()){
                FileOutputStream fout = new FileOutputStream(mPath);
                BitmapDrawable bd = (BitmapDrawable)imageView.getDrawable();
                if(bd != null){
                    bd.getBitmap().compress(CompressFormat.PNG, 100, fout);
                    fout.close();
                }else{
                    return false;
                }

            }
            Toast.makeText(this, R.string.save2sd_success, Toast.LENGTH_SHORT).show();
            return true;
            
        }catch (Exception ex){
        	ex.printStackTrace();
        	return false;
        }
    }
    
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		Log.d(TAG,"=== onActivityResult ===");
//		if(resultCode != RESULT_OK){
//			return;
//		}
//		if(requestCode == REQUEST_CROP_IMAGE){
//			Log.d(TAG,"=== set wallpaper in onActivityResult ===");
//			Bitmap bitmap = (Bitmap)data.getExtras().getParcelable("data");
//			WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
//			try{
//				wallpaperManager.setBitmap(bitmap);
//				Toast.makeText(this, R.string.set_wallpaper_toast_success, Toast.LENGTH_SHORT).show();
//			}catch(IOException e){
//				e.printStackTrace();
//			
//			}catch(Exception e){
//				e.printStackTrace();
//			}
//		}
//	}
	
	@Override
	public void onStop(){
	    imageLoader.stop();
	    super.onStop();
	}
	
    
    @SuppressWarnings("unused")
    private class DownloadImageTask extends AsyncTask<ImageView, Integer, Bitmap> {
    	private ImageView imageView = null;
    	
    	@Override
        protected Bitmap doInBackground(ImageView... views) {
        	this.imageView = views[0];
        	String url = (String)imageView.getTag();
        	return downloadImage(url);
        }
    	@Override
        protected void onProgressUpdate(Integer... progress) {
    		mProgressBar.setProgress(progress[0]);
        }
    	@Override
        protected void onPostExecute(Bitmap result) {
    		if(result != null){
    		    Log.d(TAG,"=== imageview set bitmap");
    			//mScaledBitmap = result;
    			mTextViewTip.setVisibility(View.INVISIBLE);
    			imageView.setVisibility(View.VISIBLE);
        		imageView.setImageBitmap(result);
                mProgressBar.setVisibility(View.INVISIBLE);
    		}

        }
    	
        private Bitmap downloadImage(String url)
        {
          HttpClient httpClient = CustomHttpClient.getHttpClient();
          try {
        	HttpGet request = new HttpGet(url);
        	HttpParams params = new BasicHttpParams();
            HttpConnectionParams.setSoTimeout(params, 60000);   // 1 minute
            request.setParams(params);

            publishProgress(25);

            HttpResponse response = httpClient.execute(request);

            publishProgress(50);

            byte[] image = EntityUtils.toByteArray(response.getEntity());

            publishProgress(75);
            BitmapFactory.Options option = new BitmapFactory.Options();
//            option.inJustDecodeBounds = true;
//            BitmapFactory.decodeByteArray(image, 0, image.length, option);
//            option.inJustDecodeBounds = false;
            option.inSampleSize = 2;
            publishProgress(80);
            //Bitmap mBitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
            Bitmap mBitmap = BitmapFactory.decodeByteArray(image, 0, image.length, option);
            
            publishProgress(100);

            return mBitmap;
    	  } catch (IOException e) {
            e.printStackTrace();
          } catch(Exception e){
        	  e.printStackTrace();
          }
          return null;
        }
    }

}