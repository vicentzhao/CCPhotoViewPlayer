package com.ccdrive.photoviewer;


public class HttpRequest {
	private String WEB_ROOT = "http://apk.vocy.com/";
	private String STATIC_WEB_ROOT="http://html.vocy.com/";

	public String getSTATIC_WEB_ROOT() {
		return STATIC_WEB_ROOT;
	}
	public void setSTATIC_WEB_ROOT(String sTATIC_WEB_ROOT) {
		STATIC_WEB_ROOT = sTATIC_WEB_ROOT;
	}
	private static HttpRequest request;
	private String apkuuid;
	private String mac;
	
	private String type;  //频道类型
	
	private String id;   //传过来的id

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMytoken() {
		return mytoken;
	}
	// 获取下载的uuid
	public String getURL_UPDATE_APK() {
		return "http://api.vocy.com/android!getFunction.action?arg0=droidpc_app_getversion&arg1=";
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getApkuuid() {
		return apkuuid;
	}
	public void setApkuuid(String apkuuid) {
		this.apkuuid = apkuuid;
	}
	private String packName;
	public String getPackName() {
		return packName;
	}
	public void setPackName(String packName) {
		this.packName = packName;
	}
	public void setMytoken(String mytoken) {
		this.mytoken = mytoken;
	}
	private HttpRequest() {
	}
	public static HttpRequest getInstance() {
		if (request == null) {
			request = new HttpRequest();
		}
		return request;
	}
	private String mytoken = "";
	public String getWEB_ROOT() {
		return WEB_ROOT;
	}
	public static HttpRequest getRequest() {
		return request;
	}
	public String getURL_DOWN_UPDATE_APK() {
		return "http://api.vocy.com/apk_file/" + apkuuid + ".apk";
	}
	
	 public String getURL_DETAIL_INFO(){
		 return STATIC_WEB_ROOT+"html/workplay/workplay_"+type+"_"+id+"_1.txt";
	 }
	public String getNEWSPICTURES() {
		// TODO Auto-generated method stub
		return WEB_ROOT+"todayrec!querypic.action?jsonType=json&tid="+id;
	}
	
	public String getIMAGEDOWNDOLADER(){
		
		return WEB_ROOT + "index/download.action?token=" + mytoken
				+ "&inputPath=";
	}
}
