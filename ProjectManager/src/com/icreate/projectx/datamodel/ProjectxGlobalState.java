package com.icreate.projectx.datamodel;

import android.app.Application;

public class ProjectxGlobalState extends Application {
	private String authToken;
	private String apiKey;
	private String userid;
	private ProjectList projectList;
	private TaskList taskList;

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
		if (userid.indexOf("@") > 0) {
			userid = userid.substring(0, userid.indexOf("@"));
		}
		this.userid = userid;
	}

	public ProjectList getProjectList() {
		return projectList;
	}

	public void setProjectList(ProjectList projectList) {
		this.projectList = projectList;
	}

	public TaskList getTaskList() {
		return taskList;
	}

	public void setTaskList(TaskList taskList) {
		this.taskList = taskList;
	}

}
