package com.icreate.projectx.task;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.icreate.projectx.R;
import com.icreate.projectx.datamodel.Task;

public class TaskViewBaseAdapter extends BaseAdapter {

	private static ArrayList<Task> taskList;

	private final LayoutInflater mInflater;

	public TaskViewBaseAdapter(Context context, ArrayList<Task> taskList) {
		this.taskList = taskList;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return taskList.size();
	}

	@Override
	public Object getItem(int position) {
		return taskList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.tasklistitem, null);
			holder = new ViewHolder();
			holder.txtName = (TextView) convertView.findViewById(R.id.tasklistname);
			holder.txtParentName = (TextView) convertView.findViewById(R.id.taskParentName);
			holder.txtdate = (TextView) convertView.findViewById(R.id.tasklistduedate);
			holder.TaskProgress = (ProgressBar) convertView.findViewById(R.id.taskProgress);
			holder.arrow = (ImageView) convertView.findViewById(R.id.imageViewarrow);
			holder.priority = (ImageView) convertView.findViewById(R.id.imageViewPriority);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.txtName.setText(taskList.get(position).getTask_name());

		String priority = null;

		priority = taskList.get(position).getTask_priority();
		if (priority.equals("LOW"))
			holder.priority.setImageResource(R.drawable.icon_priority_low);
		else if (priority.equals("MEDIUM"))
			holder.priority.setImageResource(R.drawable.icon_priority_medium);
		else if (priority.equals("HIGH"))
			holder.priority.setImageResource(R.drawable.icon_priority_high);
		else if (priority.equals("CRITICAL"))
			holder.priority.setImageResource(R.drawable.icon_priority_critical);

		holder.txtParentName.setVisibility(View.GONE);
		holder.arrow.setVisibility(View.GONE);
		holder.txtdate.setText(taskList.get(position).getDue_date());

		holder.TaskProgress.setProgress((int) taskList.get(position).getProgress());

		return convertView;
	}

	static class ViewHolder {
		TextView txtName;
		TextView txtParentName;
		TextView txtdate;
		ImageView arrow;
		ProgressBar TaskProgress;
		ImageView priority;
	}

}