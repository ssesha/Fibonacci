package com.icreate.projectx.datamodel;

import java.util.ArrayList;

public class ActivityFeed {
	private ArrayList<Notification> notifications = new ArrayList<Notification>();

	public void setNotifications(ArrayList<Notification> notifications) {
		this.notifications = notifications;
	}

	public ArrayList<Notification> getNotifications() {
		return notifications;
	}

}
