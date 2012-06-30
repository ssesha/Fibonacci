package com.icreate.projectx.datamodel;

import java.util.ArrayList;

public class ActivityFeed {
	private ArrayList<Notification> notifications = new ArrayList<Notification>();
	private ArrayList<Comment> comments = new ArrayList<Comment>();

	public ArrayList<Comment> getComments() {
		return comments;
	}

	public void setComments(ArrayList<Comment> comments) {
		this.comments = comments;
	}
	
	public void setNotifications(ArrayList<Notification> notifications) {
		this.notifications = notifications;
	}

	public ArrayList<Notification> getNotifications() {
		return notifications;
	}

}
