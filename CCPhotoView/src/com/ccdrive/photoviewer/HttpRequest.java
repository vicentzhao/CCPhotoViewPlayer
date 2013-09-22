package com.ccdrive.photoviewer;


public class HttpRequest {
	private String WEB_ROOT = "http://apk.vocy.com/";

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
		return "http://sys.vocy.com/android!getFunction.action?arg0=droidpc_app_getversion&arg1=";
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
		return "http://sys.vocy.com/apk_file/" + apkuuid + ".apk";
	}
	
	 public String getURL_DETAIL_INFO(){
		 return "http://192.168.1.3:2014/html/workplay/workplay_"+type+"_"+id+"_1.txt";
	 }
}
