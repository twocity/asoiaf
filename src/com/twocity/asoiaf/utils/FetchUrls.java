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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FetchUrls {
	
	public final static String IMAGE_URL = "http://migs4asoiaf.sinaapp.com/photo.php?id=%s";
	private FetchUrls(){
	}
	
	public static ImageUrl FetchImageUrl(String url){
		ImageUrl iu = new ImageUrl();
		try{
			Document doc = Jsoup.connect(url).timeout(5000).get();
			Elements e = doc.select("li.outlink a");
			
			for(Element item:e){
				if(item.text().equals("200")){
					//Log.d("","200:"+item.select("a[href]").attr("href"));
					iu.setThumbUrl(item.select("a[href]").attr("href"));
				}
				if(item.text().equals("original")){
					//Log.d("","original:"+item.select("a[href]").attr("href"));
					iu.setOringinUrl(item.select("a[href]").attr("href"));
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return iu;
	}
	
	public static ArrayList<ImageUrl> FetchImageUrlList(){
		ArrayList<ImageUrl> list = new ArrayList<ImageUrl>();
		for(int i=1;i<=84;i++){
			String url = String.format(FetchUrls.IMAGE_URL, String.valueOf(i));
			ImageUrl item = FetchImageUrl(url);
			if(!item.isEmpty()){
				list.add(item);
			}
		}
		return list;
	}
}