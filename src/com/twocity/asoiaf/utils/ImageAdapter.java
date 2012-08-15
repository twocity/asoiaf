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