package com.icreate.projectx;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.icreate.projectx.datamodel.Project;

public class ProfileProjListBaseAdapter extends BaseAdapter {
	private static List<Project> projectList;
	private final LayoutInflater mInflater;
	
	public ProfileProjListBaseAdapter(Context context, List<Project> projectList){
		this.projectList = projectList;
		mInflater = LayoutInflater.from(context);
	}
	@Override
	public int getCount() {
		return projectList.size();
	}

	@Override
	public Object getItem(int index) {
		return projectList.get(index);
	}

	@Override
	public long getItemId(int index) {
		return index;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		System.out.println("inside base adapter");
		System.out.println(projectList.toString());
		System.out.println("position");
		System.out.println(position);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.profileprojectitem, null);
			holder = new ViewHolder();
			holder.ProjectName = (TextView) convertView.findViewById(R.id.prof_projectName);
			holder.TotalTask = (TextView) convertView.findViewById(R.id.prof_totalTask);
			holder.CompletedTask = (TextView) convertView.findViewById(R.id.prof_completedTask);
			holder.ProjectProgress = (ProgressBar) convertView.findViewById(R.id.prof_projectProgress);
	
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.ProjectName.setVisibility(View.VISIBLE);
		holder.TotalTask.setVisibility(View.VISIBLE);
		holder.CompletedTask.setVisibility(View.VISIBLE);
		holder.ProjectProgress.setVisibility(View.VISIBLE);
		double totalTasks = new Double(projectList.get(position).getTotalTasks());
		double completedTasks = new Double(projectList.get(position).getTasksCompleted());
		double progress = 0;
		if (totalTasks != 0) {
			progress = (completedTasks / totalTasks) * 100.0;
		}
		System.out.println("total tasks: "+totalTasks);
		System.out.println("completed tasks:" +completedTasks);
		System.out.println("progress: "+progress);
		convertView.setTag(R.id.prof_totalTask, totalTasks);
		convertView.setTag(R.id.prof_completedTask, completedTasks);
		convertView.setTag(R.id.prof_projectProgress, progress);
		holder.ProjectName.setText(projectList.get(position).getProject_name());
		holder.TotalTask.setText("Total Tasks: " + (int) totalTasks);
		holder.CompletedTask.setText("Completed Tasks: " + (int) completedTasks);
		holder.ProjectProgress.setProgress((int) progress);			
		return convertView;
	}
	
	static class ViewHolder {
		TextView ProjectName;
		TextView TotalTask;
		TextView CompletedTask;
		ProgressBar ProjectProgress;
	}

}
