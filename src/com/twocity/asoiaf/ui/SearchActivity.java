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
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.twocity.asoiaf.R;


public class SearchActivity extends BaseActivity{
    private static final String SEARCH_URL = 
        "http://zh.asoiaf.wikia.com/wiki/Special:搜索?search=%s&fulltext=Search";
    private EditText mSearchEdit = null;
    private WebView webview;
    private ProgressBar mProgressBar;
    private TextView mTextViewTip;
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView (R.layout.activity_search);
        
        initViews();
    }
        
    
    private void initViews() {
        mSearchEdit = (EditText) findViewById(R.id.edit_search);
        mSearchEdit.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v,int keyCode,KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER && 
                        event.getAction() == KeyEvent.ACTION_DOWN) {
                    search();
                    return true;
                }
                return false;
            }
        });
        mProgressBar = (ProgressBar)findViewById(R.id.progressbar);
        mTextViewTip = (TextView)findViewById(R.id.loading_tip);
        mTextViewTip.setText(getTipString());
        setWebView();
    }
    
    private void setWebView(){
        webview = (WebView)findViewById(R.id.search_webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setSupportZoom(true);

        final Activity activity = this;
        webview.setWebChromeClient(new WebChromeClient() {
          public void onProgressChanged(WebView view, int progress) {
        	  mProgressBar.setProgress(progress);
              if(progress == 100) {
              	mProgressBar.setProgress(0);
              }
//              if(progress >= 90){
//            	  webview.setVisibility(View.VISIBLE);
//            	  mTextViewTip.setVisibility(View.INVISIBLE);
//              }
          }
        });
        webview.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {  
                view.loadUrl(url);  
                return true;  
            }
            public void onPageStarted(WebView view, String url, Bitmap favicon){
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.setProgress(0);
            }
            public void onPageFinished(WebView view, String url) {
                mProgressBar.setVisibility(View.INVISIBLE);
          	    webview.setVisibility(View.VISIBLE);
          	    mTextViewTip.setVisibility(View.INVISIBLE);
            }
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void search(){
        String args = mSearchEdit.getText().toString().trim();
        String url = String.format(SEARCH_URL, args);
        Log.d("","search url: "+url);
        webview.loadUrl(url);
    }
    
    public void onClickSearch(View v){
    	search();
    }
    
} // end class
