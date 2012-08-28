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


import android.content.Intent;
import android.os.Bundle;

import com.twocity.asoiaf.R;
import com.twocity.asoiaf.utils.DownloadIntentService;


public class HomeActivity extends BaseActivity {
    
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Intent i = new Intent(this,DownloadIntentService.class);
        i.setAction("action_create_database");
        this.startService(i);
    }

    
    protected void onDestroy (){
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
    
    } // end class
