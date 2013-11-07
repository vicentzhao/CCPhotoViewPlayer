package com.ccdrive.photoviewer.util;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ccdrive.photoviewer.bean.ArtsBean;



public class JSONUtil {
	
	
	 public static  ArrayList<ArtsBean> getArts(String info){
		 
		 ArrayList<ArtsBean> artsBeans = new ArrayList<ArtsBean>();
		 JSONObject jo;
		try {
			jo = new JSONObject(info);
			JSONArray artsArray = jo.getJSONArray("data");
			for (int i = 0; i < artsArray.length(); i++) {
		         ArtsBean  artsBean = new ArtsBean();
				String videopath =artsArray.getJSONObject(i).getString("VIDEOPATH");
				String content =artsArray.getJSONObject(i).getString("CONTENTS");
				artsBean.setContent(content);
				artsBean.setVideoPath(videopath);
				artsBeans.add(artsBean);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return artsBeans;
		 
		 
		 
	 }
	


}
