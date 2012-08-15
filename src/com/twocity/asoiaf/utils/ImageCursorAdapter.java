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
            String imagelink = cursor.getString(thumbIndex);
            imageDownloader.download(imagelink, (ImageView) imageview);
        }
        
    }