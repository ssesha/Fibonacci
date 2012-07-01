package com.icreate.projectx.datamodel;

import java.util.ArrayList;
import java.util.List;

public class Task {
	private int task_id;
	private int projectId;
	private String project_name;
	private int parentId;
	private String task_name;
	private String due_date;
	private int assignee;
	private String assignee_name;
	private int createdBy;
	private String creator_name;
	private String description;
	private String task_status;
	private String task_priority;
	private int totalTasks;
	private int tasksAssigned;
	private int tasksCompleted;
	private int tasksInProgress;
	private int tasksOpen;
	private double progress;
	private List<Integer> subTasks = new ArrayList<Integer>();
	private List<Integer> topSubTasks = new ArrayList<Integer>();

	public Task(int x) {
		task_id = x;
	}

	public int getTask_id() {
		return task_id;
	}

	public void setTask_id(int task_id) {
		this.task_id = task_id;
	}

	public String getAssignee_name() {
		return assignee_name;
	}

	public void setAssignee_name(String assignee_name) {
		this.assignee_name = assignee_name;
	}

	public String getCreator_name() {
		return creator_name;
	}

	public void setCreator_name(String creator_name) {
		this.creator_name = creator_name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public int getAssignee() {
		return assignee;
	}

	public void setAssignee(int assignee) {
		this.assignee = assignee;
	}

	public int getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public String getProject_name() {
		return project_name;
	}

	public void setProject_name(String project_name) {
		this.project_name = project_name;
	}

	public String getTask_name() {
		return task_name;
	}

	public void setTask_name(String task_name) {
		this.task_name = task_name;
	}

	public String getDue_date() {
		return due_date;
	}

	public void setDue_date(String due_date) {
		this.due_date = due_date;
	}

	public String getTask_status() {
		return task_status;
	}

	public void setTask_status(String task_status) {
		this.task_status = task_status;
	}

	public String getTask_priority() {
		return task_priority;
	}

	public void setTask_priority(String task_priority) {
		this.task_priority = task_priority;
	}

	public void setProgress(double progress) {
		this.progress = progress;
	}

	public double getProgress() {
		return progress;
	}

	public void setTasksOpen(int tasksOpen) {
		this.tasksOpen = tasksOpen;
	}

	public int getTasksOpen() {
		return tasksOpen;
	}

	public void setTasksInProgress(int tasksInProgress) {
		this.tasksInProgress = tasksInProgress;
	}

	public int getTasksInProgress() {
		return tasksInProgress;
	}

	public void setTasksCompleted(int tasksCompleted) {
		this.tasksCompleted = tasksCompleted;
	}

	public int getTasksCompleted() {
		return tasksCompleted;
	}

	public void setTasksAssigned(int tasksAssigned) {
		this.tasksAssigned = tasksAssigned;
	}

	public int getTasksAssigned() {
		return tasksAssigned;
	}

	public void setTotalTasks(int totalTasks) {
		this.totalTasks = totalTasks;
	}

	public int getTotalTasks() {
		return totalTasks;
	}

	public void setSubTasks(List<Integer> subTasks) {
		this.subTasks = subTasks;
	}

	public List<Integer> getSubTasks() {
		return subTasks;
	}

	public List<Integer> getTopSubTasks() {
		return topSubTasks;
	}

	public void setTopSubTasks(List<Integer> topSubTasks) {
		this.topSubTasks = topSubTasks;
	}
}
