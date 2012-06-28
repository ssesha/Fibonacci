package com.icreate.projectx.datamodel;

public class ProjectMembers {
	private int member_id;
	private String user_name;
	private String user_id;

	public ProjectMembers(int x) {
		member_id = x;
		user_id = "";
	}

	public int getMember_id() {
		return member_id;
	}

	public void setMember_id(int member_id) {
		this.member_id = member_id;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
}
