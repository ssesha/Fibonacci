package com.icreate.projectx.task;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.icreate.projectx.R;
import com.icreate.projectx.datamodel.Task;

public class myTasksBaseAdapter extends BaseAdapter {

	private static ArrayList<Task> taskList;

	private final LayoutInflater mInflater;

	public myTasksBaseAdapter(Context context, ArrayList<Task> taskList) {
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
			convertView = mInflater.inflate(R.layout.mytaskitem, null);
			holder = new ViewHolder();
			holder.txtName = (TextView) convertView.findViewById(R.id.mytasklistname);
			holder.txtProjectName = (TextView) convertView.findViewById(R.id.mytaskProjectName);
			holder.txtdate = (TextView) convertView.findViewById(R.id.mytasklistduedate);
			holder.txtstatus = (TextView) convertView.findViewById(R.id.mytaskliststatus);
			holder.TaskProgress = (ProgressBar) convertView.findViewById(R.id.mytaskProgress);
			holder.priority = (ImageView) convertView.findViewById(R.id.myimageViewPriority);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.TaskProgress.setVisibility(View.GONE);
		holder.txtName.setText(taskList.get(position).getTask_name());

		holder.txtProjectName.setText(taskList.get(position).getProject_name());

		holder.txtdate.setText(taskList.get(position).getDue_date());

		holder.txtstatus.setText(taskList.get(position).getTask_status());

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

		if (taskList.get(position).getTask_status().equalsIgnoreCase("COMPLETE")) {
			holder.txtName.setTextColor(Color.parseColor("#AAB3B6"));
			holder.txtProjectName.setTextColor(Color.parseColor("#AAB3B6"));
			holder.txtName.setPaintFlags(holder.txtdate.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
		} else {
			holder.txtName.setTextColor(Color.parseColor("#FFFF00"));
			holder.txtProjectName.setTextColor(Color.parseColor("#FFFF00"));
			holder.txtName.setPaintFlags(holder.txtName.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
		}

		return convertView;
	}

	static class ViewHolder {
		TextView txtName;
		TextView txtProjectName;
		TextView txtdate;
		TextView txtstatus;
		ProgressBar TaskProgress;
		ImageView priority;
	}

}
