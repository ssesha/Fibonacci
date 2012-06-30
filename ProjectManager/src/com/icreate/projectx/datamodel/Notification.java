package com.icreate.projectx.datamodel;


public class Notification {
	private String message;
	private String sentTime;	
	
	public void setMessage(String message) {
		this.message = message;
	}
	public String getMessage() {
		return message;
	}
	public void setSentTime(String sentTime) {
		this.sentTime = sentTime;
	}
	public String getSentTime() {
		return sentTime;
	}	
}
