package com.icreate.projectx.datamodel;

import java.util.List;

public class Project {
	private int project_id;
	private String module_code;
	private String project_name;
	private String due_date;
	private int leader_id;
	private String leader_name;
	private String project_desc;
	private List<ProjectMembers> members;

	public String getLeader_name() {
		return leader_name;
	}

	public void setLeader_name(String leader_name) {
		this.leader_name = leader_name;
	}

	public int getLeader_id() {
		return leader_id;
	}

	public void setLeader_id(int leader_id) {
		this.leader_id = leader_id;
	}

	public String getDue_date() {
		return due_date;
	}

	public void setDue_date(String due_date) {
		this.due_date = due_date;
	}

	public String getProject_name() {
		return project_name;
	}

	public void setProject_name(String project_name) {
		this.project_name = project_name;
	}

	public String getModule_code() {
		return module_code;
	}

	public void setModule_code(String module_code) {
		this.module_code = module_code;
	}

	public int getProject_id() {
		return project_id;
	}

	public void setProject_id(int project_id) {
		this.project_id = project_id;
	}

	public String getProject_desc() {
		return project_desc;
	}

	public void setProject_desc(String project_desc) {
		this.project_desc = project_desc;
	}

	public List<ProjectMembers> getMembers() {
		return members;
	}

	public void setMembers(List<ProjectMembers> members) {
		this.members = members;
	}
}