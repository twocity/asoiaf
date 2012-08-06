package com.twocity.asoiaf.ui;



import com.twocity.asoiaf.R;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class WebViewer extends BaseActivity{
    private final static String TAG = "BookDetail";
    private final static String SEARCH_URL = "search_url";
    private final static String ACTIVITY_TITLE = "activity_title";
    
    private WebView webview;
    private TextView titleText;
    private ProgressBar mProgressBar;
    
    protected void onCreate(Bundle savedInstanceSaved) {
        super.onCreate(savedInstanceSaved);
        setContentView(R.layout.webviewer_layout);
        
        titleText = (TextView)findViewById(R.id.title_text);
        String url = null;
        url = this.getIntent().getStringExtra(SEARCH_URL);
        titleText.setText(this.getIntent().getStringExtra(ACTIVITY_TITLE));
        
        setWebView(url);
    }
    
    private void setWebView(String url){
        webview = (WebView)findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setSupportZoom(true);

        final Activity activity = this;
        webview.setWebChromeClient(new WebChromeClient() {
          public void onProgressChanged(WebView view, int progress) {
            activity.setProgress(progress * 100);
          }
        });
        webview.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {  
                view.loadUrl(url);  
                return true;  
            }
            public void onPageStarted(WebView view, String url, Bitmap favicon){
                //mProgressBar.setVisibility(View.VISIBLE);
            }
            public void onPageFinished(WebView view, String url) {
                //mProgressBar.setVisibility(View.INVISIBLE);
            }
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }
        });

        webview.loadUrl(url);
    }
    
    final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
        }
    };
}