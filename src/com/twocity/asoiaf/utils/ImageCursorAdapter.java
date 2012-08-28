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

package com.twocity.asoiaf.utils;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;

import com.twocity.asoiaf.R;

public class ImageCursorAdapter extends SimpleCursorAdapter {
        private ImageView imageview;
        private LayoutInflater mInflater;
        private final ImageDownloader imageDownloader = new ImageDownloader();

        public ImageCursorAdapter (Context context, int layout, Cursor c, String[] from, int[] to) {
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
            //imageview.setPadding(8, 8, 8, 8);
            String imagelink = cursor.getString(thumbIndex);
            imageDownloader.download(imagelink, (ImageView) imageview);
        }
        
    }