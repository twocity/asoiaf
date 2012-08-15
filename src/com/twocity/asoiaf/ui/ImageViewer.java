package com.twocity.asoiaf.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.twocity.asoiaf.R;
import com.twocity.asoiaf.utils.CustomHttpClient;


public class ImageViewer extends BaseActivity implements OnTouchListener {
	private final static String TAG = "ImageViewer";
	private static final String WALLPAPER_BUCKET_NAME =
	        Environment.getExternalStorageDirectory().toString() + "/DCIM/asoiaf";
	
	private static final int REQUEST_CROP_IMAGE = 1;
//	private static final int SAVE_PIC_OK = 1;
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
    private DisplayMetrics dm;
    private ImageView imageView;
    private ProgressBar mProgressBar;
    private TextView mTextViewTip;
    private Bitmap mBitmap;
    private String mPath = "";
    private float minScaleR;// 最小缩放比例
    private static final float MAX_SCALE = 4f;// 最大缩放比例

    private static final int NONE = 0;// 初始状态
    private static final int DRAG = 1;// 拖动
    private static final int ZOOM = 2;// 缩放
    private int mode = NONE;

    private PointF prev = new PointF();
    private PointF mid = new PointF();
    private float dist = 1f;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageview);
        
        dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        
        mTextViewTip = (TextView)findViewById(R.id.loading_tip);
        mProgressBar = (ProgressBar)findViewById(R.id.progressbar);
        mProgressBar.setVisibility(View.VISIBLE);
        mProgressBar.setProgress(0);
        imageView = (ImageView) findViewById(R.id.imageviewer);
        imageView.setOnTouchListener(this);

        mBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.main_test_bg);
        
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
            
    		File tmp = new File(mPath);
    		if(tmp.exists()){
    			Log.d(TAG,"=== set bitmap from sdcard ===");
            	mBitmap = BitmapFactory.decodeFile(mPath);
            	if(mBitmap != null){
                	imageView.setImageBitmap(mBitmap);
                	mTextViewTip.setVisibility(View.INVISIBLE);
        			imageView.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.INVISIBLE);
                    
                    minZoom();
                    center();
                    imageView.setImageMatrix(matrix);
            	}
    		}else{
    			Log.d(TAG,"=== download bitmap ===");
                imageView.setTag(url);
                new DownloadImageTask().execute(imageView);
    		}

    	}catch(Exception e){
    		Log.d(TAG,e.getMessage());
            imageView.setTag(url);
            new DownloadImageTask().execute(imageView);
    	}
    }

//    final Handler mHandler = new Handler(){
//    	@Override
//    	public void handleMessage(Message msg){
//    		if(msg.what == SAVE_PIC_OK){
//    			
//    		}
//    	}
//    };

    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
        case MotionEvent.ACTION_DOWN:
            savedMatrix.set(matrix);
            prev.set(event.getX(), event.getY());
            mode = DRAG;
            break;
        case MotionEvent.ACTION_POINTER_DOWN:
            dist = spacing(event);
            if (spacing(event) > 10f) {
                savedMatrix.set(matrix);
                midPoint(mid, event);
                mode = ZOOM;
            }
            break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_POINTER_UP:
            mode = NONE;
            break;
        case MotionEvent.ACTION_MOVE:
            if (mode == DRAG) {
                matrix.set(savedMatrix);
                matrix.postTranslate(event.getX() - prev.x, event.getY()
                        - prev.y);
            } else if (mode == ZOOM) {
                float newDist = spacing(event);
                if (newDist > 10f) {
                    matrix.set(savedMatrix);
                    float tScale = newDist / dist;
                    matrix.postScale(tScale, tScale, mid.x, mid.y);
                }
            }
            break;
        }
        imageView.setImageMatrix(matrix);
        CheckView();
        return true;
    }

    private void CheckView() {
        float p[] = new float[9];
        matrix.getValues(p);
        if (mode == ZOOM) {
            if (p[0] < minScaleR) {
                matrix.setScale(minScaleR, minScaleR);
            }
            if (p[0] > MAX_SCALE) {
                matrix.set(savedMatrix);
            }
        }
        center();
    }

    private void minZoom() {
        minScaleR = Math.min(
                (float) dm.widthPixels / (float) mBitmap.getWidth(),
                (float) dm.heightPixels / (float) mBitmap.getHeight());
        if (minScaleR < 1.0) {
            matrix.postScale(minScaleR, minScaleR);
        }
    }

    private void center() {
        center(true, true);
    }

    protected void center(boolean horizontal, boolean vertical) {

        Matrix m = new Matrix();
        m.set(matrix);
        RectF rect = new RectF(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
        m.mapRect(rect);

        float height = rect.height();
        float width = rect.width();

        float deltaX = 0, deltaY = 0;

        if (vertical) {
            int screenHeight = dm.heightPixels;
            if (height < screenHeight) {
                deltaY = (screenHeight - height) / 2 - rect.top;
            } else if (rect.top > 0) {
                deltaY = -rect.top;
            } else if (rect.bottom < screenHeight) {
                deltaY = imageView.getHeight() - rect.bottom;
            }
        }

        if (horizontal) {
            int screenWidth = dm.widthPixels;
            if (width < screenWidth) {
                deltaX = (screenWidth - width) / 2 - rect.left;
            } else if (rect.left > 0) {
                deltaX = -rect.left;
            } else if (rect.right < screenWidth) {
                deltaX = screenWidth - rect.right;
            }
        }
        matrix.postTranslate(deltaX, deltaY);
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
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
    		save2SD();
    	}
    	return true;
    }
    
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
                mBitmap.compress(CompressFormat.PNG, 100, fout);
                fout.close();
            }
            //path = String.format("%s/asoiaf-photo-%d.png", WALLPAPER_BUCKET_NAME, System.currentTimeMillis());
            Toast.makeText(this, R.string.save2sd_success, Toast.LENGTH_SHORT).show();
            return true;
            
        }catch (Exception ex){
        	ex.printStackTrace();
        	return false;
        }
    }
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG,"=== onActivityResult ===");
		if(resultCode != RESULT_OK){
			return;
		}
		if(requestCode == REQUEST_CROP_IMAGE){
			Log.d(TAG,"=== set wallpaper in onActivityResult ===");
			Bitmap bitmap = (Bitmap)data.getExtras().getParcelable("data");
			WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
			try{
				wallpaperManager.setBitmap(bitmap);
				Toast.makeText(this, R.string.set_wallpaper_toast_success, Toast.LENGTH_SHORT).show();
			}catch(IOException e){
				e.printStackTrace();
			
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
    
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
            //showDialog("Downloaded " + result + " bytes");
    		if(result != null){
    			mBitmap = result;
    			mTextViewTip.setVisibility(View.INVISIBLE);
    			imageView.setVisibility(View.VISIBLE);
        		imageView.setImageBitmap(result);
                minZoom();
                center();
                imageView.setImageMatrix(matrix);
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

            Bitmap mBitmap = BitmapFactory.decodeByteArray(image, 0, image.length);

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