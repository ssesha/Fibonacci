package com.icreate.projectx.datamodel;

import android.app.Application;

public class ProjectxGlobalState extends Application{
	private String authToken;
	private String apiKey;
	private String userid;
	public String getAuthToken() {
		return authToken;
	}
	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}
	public String getApiKey() {
		return apiKey;
	}
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}

}
