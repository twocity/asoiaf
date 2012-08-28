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

import android.os.Parcel;
import android.os.Parcelable;

public class ImageUrl implements Parcelable{
	
	private String thumbUrl = null;
	private String originUrl = null;
	private int id;
	
	public ImageUrl(){
	}
	
	public ImageUrl(Parcel in){
		id = in.readInt();
		thumbUrl = in.readString();
		originUrl = in.readString();
	}
	
	public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
    	out.writeInt(id);
        out.writeString(thumbUrl);
        out.writeString(originUrl);
    }
    
    public static final Parcelable.Creator<ImageUrl> CREATOR
		    = new Parcelable.Creator<ImageUrl>() {
		public ImageUrl createFromParcel(Parcel in) {
		    return new ImageUrl(in);
		}
		
		public ImageUrl[] newArray(int size) {
		    return new ImageUrl[size];
		}
    };
	
//	public ImageUrl(String t,String o){
//		this.thumbUrl = t;
//		this.originUrl = o;
//	}
	public void setId(int id){
		this.id = id;
	}
    
	public void setThumbUrl(String t){
		this.thumbUrl = t;
	}
	
	public void setOringinUrl(String o){
		this.originUrl = o;
	}
	
	public int getId(){
		return this.id;
	}
	
	public String getThumbUrl(){
		return this.thumbUrl;
	}
	
	public String getOriginUrl(){
		return this.originUrl;
	}
	
	public boolean isEmpty(){
		if(thumbUrl == null || originUrl == null){
			return true;
		}else if(thumbUrl.equals("") || originUrl.equals("")){
			return true;
		}else{
			return false;
		}
	}
}