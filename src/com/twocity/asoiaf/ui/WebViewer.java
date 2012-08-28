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

package com.twocity.asoiaf.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.twocity.asoiaf.R;


public class WebViewer extends BaseActivity{
    private final static String TAG = "BookDetail";
    private final static String SEARCH_URL = "search_url";
    private final static String ACTIVITY_TITLE = "activity_title";
    
    private WebView webview;
    private TextView titleText;
    private ProgressBar mProgressBar;
    private TextView mTextViewTip;
    
    protected void onCreate(Bundle savedInstanceSaved) {
        super.onCreate(savedInstanceSaved);
        
        
        setContentView(R.layout.activity_webview);
        
        
        titleText = (TextView)findViewById(R.id.title_text);
        String url = null;
        url = this.getIntent().getStringExtra(SEARCH_URL);
        titleText.setText(this.getIntent().getStringExtra(ACTIVITY_TITLE));
        mProgressBar = (ProgressBar)findViewById(R.id.progressbar);
        mTextViewTip = (TextView)findViewById(R.id.loading_tip);
        mTextViewTip.setText(getTipString());
        
        setWebView(url);
    }
    
    private void setWebView(String url){
        webview = (WebView)findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setSupportZoom(true);

        final Activity activity = this;
        webview.setWebChromeClient(new WebChromeClient() {
          public void onProgressChanged(WebView view, int progress) {
        	  mProgressBar.setProgress(progress);
            if(progress == 100) {
            	mProgressBar.setProgress(0);
            }
          }
        });
        webview.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {  
                view.loadUrl(url);  
                return true;  
            }
            public void onPageStarted(WebView view, String url, Bitmap favicon){
            	mProgressBar.setProgress(0);
                mProgressBar.setVisibility(View.VISIBLE);
            }
            public void onPageFinished(WebView view, String url) {
                mProgressBar.setVisibility(View.INVISIBLE);
                mTextViewTip.setVisibility(View.INVISIBLE);
                webview.setVisibility(View.VISIBLE);
            }
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }
        });

        webview.loadUrl(url);
    }
    
    public void onClickRefresh(View v){
    	webview.reload();
    }
    
    final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
        }
    };
}