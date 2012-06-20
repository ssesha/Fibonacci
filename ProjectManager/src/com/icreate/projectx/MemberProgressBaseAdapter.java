package com.icreate.projectx;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.icreate.projectx.datamodel.ProjectMembers;
import com.icreate.projectx.datamodel.Task;

public class MemberProgressBaseAdapter extends BaseAdapter {
	private static List<ProjectMembers> memberList;
	private final LayoutInflater mInflater;
	private static ArrayList<Task> tasks;

	public MemberProgressBaseAdapter(Context context,
			List<ProjectMembers> memberList, ArrayList<Task> tasks) {
		this.memberList = memberList;
		this.tasks = tasks;
		mInflater = LayoutInflater.from(context);
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return memberList.size();
	}

	public Object getItem(int index) {
		// TODO Auto-generated method stub
		return memberList.get(index);
	}

	public long getItemId(int index) {
		// TODO Auto-generated method stub
		return index;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		System.out.println("inside base adapter");
		System.out.println(memberList.toString());
		System.out.println("position");
		System.out.println(position);
		System.out.println("tasklist");
		System.out.println(tasks.toString());
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.memberprogressitem, null);
			holder = new ViewHolder();
			holder.MemberName = (TextView) convertView
					.findViewById(R.id.memberName);
			holder.TotalTask = (TextView) convertView
					.findViewById(R.id.totalTask);
			holder.CompletedTask = (TextView) convertView
					.findViewById(R.id.completedTask);
			holder.MemberProgress = (ProgressBar) convertView
					.findViewById(R.id.memberProgress);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		double totalTasks = GetTotalTasks(memberList.get(position)
				.getMember_id());
		double completedTasks = GetCompletedTasks(memberList.get(position)
				.getMember_id());
		double progress = 0;
		if (totalTasks != 0) {
			progress = (completedTasks / totalTasks) * 100.0;
		}
		holder.MemberName.setText(memberList.get(position).getUser_name());
		holder.TotalTask.setText("Total Tasks: " + (int) totalTasks);
		holder.CompletedTask
				.setText("Completed Tasks: " + (int) completedTasks);
		holder.MemberProgress.setProgress((int) progress);
		return convertView;
	}

	public int GetTotalTasks(int userId) {
		int totalTasks = 0;

		for (int i = 0; i < tasks.size(); i++) {
			Log.d("Assignee",
					new Integer(tasks.get(i).getAssignee()).toString());
			if (tasks.get(i).getAssignee() == userId)
				totalTasks++;
		}
		return totalTasks;
	}

	public int GetCompletedTasks(int userId) {
		int completedTasks = 0;
		for (int i = 0; i < tasks.size(); i++) {
			Log.d("Status", tasks.get(i).getTask_status());
			if (tasks.get(i).getAssignee() == userId
					&& tasks.get(i).getTask_status().equals("COMPLETE"))
				completedTasks++;
		}
		return completedTasks;
	}

	static class ViewHolder {
		TextView MemberName;
		TextView TotalTask;
		TextView CompletedTask;
		ProgressBar MemberProgress;
	}

}
