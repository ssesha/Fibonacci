package com.icreate.projectx;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.icreate.projectx.datamodel.Task;

public class TaskListBaseAdapter extends BaseAdapter {
	private static ArrayList<Task> taskList;

	private final LayoutInflater mInflater;

	public TaskListBaseAdapter(Context context, ArrayList<Task> taskList) {
		this.taskList = taskList;
		mInflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return taskList.size();
	}

	public Object getItem(int position) {
		return taskList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.tasklistitem, null);
			holder = new ViewHolder();
			holder.txtName = (TextView) convertView
					.findViewById(R.id.tasklistname);
			holder.txtParentName = (TextView) convertView
					.findViewById(R.id.taskParentName);
			holder.txtdate = (TextView) convertView
					.findViewById(R.id.tasklistduedate);
			holder.TaskProgress = (ProgressBar) convertView
					.findViewById(R.id.taskProgress);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.txtName.setText(taskList.get(position).getTask_name());

		int parent_id = taskList.get(position).getParentId();
		holder.txtParentName.setText("" + parent_id);

		holder.txtdate.setText(taskList.get(position).getDue_date());

		holder.TaskProgress.setProgress((int) taskList.get(position)
				.getProgress());

		return convertView;
	}

	static class ViewHolder {
		TextView txtName;
		TextView txtParentName;
		TextView txtdate;
		ProgressBar TaskProgress;
	}

}
