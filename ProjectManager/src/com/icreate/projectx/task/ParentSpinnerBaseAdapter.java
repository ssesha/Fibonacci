package com.icreate.projectx.task;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.icreate.projectx.R;
import com.icreate.projectx.datamodel.Task;

public class ParentSpinnerBaseAdapter extends BaseAdapter {
	private final ArrayList<Task> taskList;
	private final ArrayList<Task> allTaskList;
	private final Context context;

	private final LayoutInflater mInflater;

	public ParentSpinnerBaseAdapter(Context context, ArrayList<Task> taskList, ArrayList<Task> allTaskList) {
		this.taskList = taskList;
		this.allTaskList = allTaskList;
		mInflater = LayoutInflater.from(context);
		this.context = context;
	}

	public ParentSpinnerBaseAdapter(Context context, ArrayList<Task> taskList) {
		this.taskList = taskList;
		this.allTaskList = taskList;
		this.context = context;
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
		Typeface font = Typeface.createFromAsset(context.getAssets(), "EraserDust.ttf");
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.tasklistitem, null);
			holder = new ViewHolder();
			holder.txtName = (TextView) convertView.findViewById(R.id.tasklistname);
			holder.txtParentName = (TextView) convertView.findViewById(R.id.taskParentName);
			holder.txtdate = (TextView) convertView.findViewById(R.id.tasklistduedate);
			holder.TaskProgress = (ProgressBar) convertView.findViewById(R.id.taskProgress);
			holder.arrow = (ImageView) convertView.findViewById(R.id.imageViewarrow);
			holder.priority = (ImageView) convertView.findViewById(R.id.imageViewPriority);
			holder.txtassignee = (TextView) convertView.findViewById(R.id.tasklistassignee);
			holder.txtName.setTypeface(font);
			holder.txtParentName.setTypeface(font);
			holder.txtassignee.setTypeface(font);
			holder.txtdate.setTypeface(font);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (taskList.get(position).getTask_id() == 0) {
			holder.txtName.setText("Choose Parent Task");
			holder.txtParentName.setVisibility(View.GONE);
			holder.txtdate.setVisibility(View.GONE);
			holder.TaskProgress.setVisibility(View.GONE);
			holder.arrow.setVisibility(View.GONE);
			holder.priority.setVisibility(View.GONE);
			holder.txtassignee.setVisibility(View.GONE);
			holder.txtName.setTextColor(Color.parseColor("#FFFF00"));
			holder.txtParentName.setTextColor(Color.parseColor("#FFFF00"));
			holder.txtName.setPaintFlags(holder.txtName.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
		} else {

			holder.txtParentName.setVisibility(View.VISIBLE);
			holder.txtdate.setVisibility(View.VISIBLE);
			holder.TaskProgress.setVisibility(View.VISIBLE);
			holder.arrow.setVisibility(View.VISIBLE);
			holder.priority.setVisibility(View.VISIBLE);
			holder.txtassignee.setVisibility(View.VISIBLE);

			holder.txtName.setText(taskList.get(position).getTask_name());

			if (taskList.get(position).getAssignee_name() == null)
				holder.txtassignee.setVisibility(View.GONE);
			else
				holder.txtassignee.setText(taskList.get(position).getAssignee_name());

			int parent_id = taskList.get(position).getParentId();
			int flag = 0;
			for (int i = 0; i < taskList.size(); i++) {
				if (parent_id == taskList.get(i).getTask_id() && parent_id != 0) {
					holder.txtParentName.setText(taskList.get(i).getTask_name());
					flag = 1;
					break;
				} else
					flag = 0;

			}

			if (flag == 0) {
				holder.arrow.setVisibility(View.GONE);
				holder.txtParentName.setVisibility(View.GONE);
			} else if (flag == 1) {
				holder.arrow.setVisibility(View.VISIBLE);
				holder.txtParentName.setVisibility(View.VISIBLE);
			}
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

			holder.txtdate.setText(taskList.get(position).getDue_date());
			double progress = taskList.get(position).getProgress() * 100.0;
			holder.TaskProgress.setProgress((int) progress);
			if (taskList.get(position).getTask_status().equalsIgnoreCase("COMPLETE")) {
				holder.txtName.setTextColor(Color.parseColor("#AAB3B6"));
				holder.txtParentName.setTextColor(Color.parseColor("#AAB3B6"));
				holder.txtName.setPaintFlags(holder.txtdate.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
			} else {
				holder.txtName.setTextColor(Color.parseColor("#FFFF00"));
				holder.txtParentName.setTextColor(Color.parseColor("#FFFF00"));
				holder.txtName.setPaintFlags(holder.txtName.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
			}

		}

		return convertView;
	}

	static class ViewHolder {
		TextView txtName;
		TextView txtParentName;
		ImageView arrow;
		TextView txtdate;
		TextView txtassignee;
		ProgressBar TaskProgress;
		ImageView priority;
	}

}
