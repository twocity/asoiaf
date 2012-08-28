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

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.twocity.asoiaf.R;

public class ImageAdapter extends BaseAdapter{
	
	private Context mContext;
	private ArrayList<ImageUrl> lists;
	private String[] imageUrls;
	private LayoutInflater mInflater;
	private final ImageDownloader imageDownloader = new ImageDownloader(); 
	
	public ImageAdapter(Context context,ArrayList<ImageUrl> list){
		this.mContext = context;
		this.lists = list;
	}
	
	public int getCount(){
		return lists.size();
	}
	
	public Object getItem(int position){
		return lists.get(position);
	}
	
	public long getItemId(int position){
		return position;
	}
	
	public View getView(int position,View convertView,ViewGroup group){
		ViewHolder holder = new ViewHolder();

		if(convertView == null){
			mInflater = LayoutInflater.from(mContext);					  
			convertView = mInflater.inflate(R.layout.grid_view_item, null);
            
            holder.imageView = (ImageView)convertView.findViewById(R.id.imageview_item);
            convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		convertView.setLayoutParams(new GridView.LayoutParams(200,180));  
		holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        //holder.imageView.setPadding(8, 8, 8, 8);
		String url = lists.get(position).getThumbUrl();//imageUrls[position];
		imageDownloader.download(url, (ImageView)holder.imageView);
		return convertView;
	}
	
	private class ViewHolder{
		ImageView imageView;
	}
}