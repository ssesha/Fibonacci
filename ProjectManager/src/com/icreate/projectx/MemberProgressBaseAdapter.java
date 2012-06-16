package com.icreate.projectx;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.icreate.projectx.datamodel.ProjectMembers;
import com.icreate.projectx.datamodel.Task;
import com.icreate.projectx.datamodel.TaskList;
import com.icreate.projectx.datamodel.Project;

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

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.MemberName.setText(memberList.get(position).getUser_name());
		holder.TotalTask.setText("Total Tasks: " + GetTotalTasks(memberList.get(position).getMember_id()));
		holder.CompletedTask.setText("No of completed Tasks: " + GetCompletedTasks(memberList.get(position).getMember_id()));
		return convertView;
	}
	
	public String GetTotalTasks(int userId){
		int totalTasks = 0;
		
		for(int i = 0; i < tasks.size(); i++)
		{
			Log.d("Assignee", new Integer(tasks.get(i).getAssignee()).toString());
			if(tasks.get(i).getAssignee() == userId)
				totalTasks++;
		}
		return new Integer(totalTasks).toString();		
	}
	
	public String GetCompletedTasks(int userId){
		int totalTasks = 0;
		for(int i = 0; i < tasks.size(); i++)
		{
			Log.d("Status", tasks.get(i).getTask_status());
			if(tasks.get(i).getAssignee() == userId && tasks.get(i).getTask_status().equals("COMPLETE"))
				totalTasks++;
		}
		return new Integer(totalTasks).toString();		
	}	
	
	static class ViewHolder {
		TextView MemberName;
		TextView TotalTask;
		TextView CompletedTask;
	}
	
	
}
