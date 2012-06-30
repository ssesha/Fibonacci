package com.icreate.projectx.datamodel;

import java.util.Date;

public class Notification {
	private String message;
	private Date sentTime;
	
	public void setMessage(String message) {
		this.message = message;
	}
	public String getMessage() {
		return message;
	}
	public void setSentTime(Date sentTime) {
		this.sentTime = sentTime;
	}
	public Date getSentTime() {
		return sentTime;
	}
	
}
