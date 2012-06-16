package com.icreate.projectx;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.icreate.projectx.datamodel.Task;

public class TaskListBaseAdapter extends BaseAdapter {
	private static ArrayList<Task> taskList;

	private final LayoutInflater mInflater;

	public TaskListBaseAdapter(Context context,
			ArrayList<Task> taskList) {
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
			//holder.txtModuleCode = (TextView) convertView
				//	.findViewById(R.id.projectmoduleCode);
			holder.txtdate = (TextView) convertView
					.findViewById(R.id.tasklistduedate);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.txtName.setText(taskList.get(position).getTask_name());
		//holder.txtModuleCode
				//.setText(projectList.get(position).getModule_code());
		holder.txtdate.setText(taskList.get(position).getDue_date());

		return convertView;
	}

	static class ViewHolder {
		TextView txtName;
		//TextView txtModuleCode;
		TextView txtdate;
	}

}

