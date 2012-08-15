package com.twocity.asoiaf.ui;

import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.twocity.asoiaf.R;


public abstract class BaseActivity extends Activity {

    private final static String SEARCH_URL = "search_url";
    private final static String ACTIVITY_TITLE = "activity_title";
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_default);
    }
    
    protected void onDestroy ()
    {
        super.onDestroy ();
    }

    protected void onPause (){
        super.onPause ();
    }


    protected void onRestart (){
       super.onRestart ();
    }


    protected void onResume (){
       super.onResume ();
    }

    protected void onStart (){
        super.onStart ();
    }

    protected void onStop (){
        super.onStop ();
    }

    public void onClickHome (View v)
    {
        goHome (this);
    }

    public void onClickSearch (View v)
    {
        startActivity (new Intent(getApplicationContext(), SearchActivity.class));
    }

    
    public void onClickAbout (View v)
    {
        startActivity (new Intent(getApplicationContext(), AboutActivity.class));
    }


    public void onClickFeature (View v)
    {
        Intent intent = new Intent(getApplicationContext(),WebViewer.class);
        int id = v.getId ();
        switch (id) {
          case R.id.home_btn_house :
               intent.putExtra(SEARCH_URL, getResources().getString(R.string.url_house));
               intent.putExtra(ACTIVITY_TITLE, getResources().getString(R.string.title_house));
               startActivity(intent);
               break;
          case R.id.home_btn_people :
              intent.putExtra(SEARCH_URL, getResources().getString(R.string.url_people));
              intent.putExtra(ACTIVITY_TITLE, getResources().getString(R.string.title_people));
              startActivity(intent);
               break;
          case R.id.home_btn_geography :
              intent.putExtra(SEARCH_URL, getResources().getString(R.string.url_geography));
              intent.putExtra(ACTIVITY_TITLE, getResources().getString(R.string.title_geography));
              startActivity(intent);
               break;
          case R.id.home_btn_history :
               //startActivity (new Intent(getApplicationContext(), F4Activity.class));
              intent.putExtra(SEARCH_URL, getResources().getString(R.string.url_history));
              intent.putExtra(ACTIVITY_TITLE, getResources().getString(R.string.title_history));
              startActivity(intent);
               break;
          case R.id.home_btn_culture :
              intent.putExtra(SEARCH_URL, getResources().getString(R.string.url_culture));
              intent.putExtra(ACTIVITY_TITLE, getResources().getString(R.string.title_culture));
              startActivity(intent);
               break;
          case R.id.home_btn_more :
        	  Intent i = new Intent(this,GridViewActivity.class);
        	  startActivity(i);
               break;
          default: 
        	   break;
        }
    }

    public void goHome(Context context) 
    {
        final Intent intent = new Intent(context, HomeActivity.class);
        intent.setFlags (Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity (intent);
    }

    public void setTitleFromActivityLabel (int textViewId)
    {
        TextView tv = (TextView) findViewById (textViewId);
        if (tv != null) tv.setText (getTitle ());
    } // end setTitleText
    
    
    public void toast (String msg)
    {
        Toast.makeText (getApplicationContext(), msg, Toast.LENGTH_SHORT).show ();
    } // end toast
    
    public void trace (String msg) 
    {
        Log.d("Demo", msg);
        toast (msg);
    }
    
    public String getTipString(){
    	String [] tips = this.getResources().getStringArray(R.array.loading_tips);
    	int length = tips.length;
    	Random r = new Random();
    	int index = r.nextInt(length);
    	return tips[index];
    }

} // end class
