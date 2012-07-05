package com.icreate.projectx.project;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.icreate.projectx.R;
import com.icreate.projectx.datamodel.Project;

public class ProjectListBaseAdapter extends BaseAdapter {
	private static ArrayList<Project> projectList;
	private final Context context;
	private final Typeface font;

	private final LayoutInflater mInflater;

	public ProjectListBaseAdapter(Context context, ArrayList<Project> projectList) {
		this.projectList = projectList;
		this.context = context;
		font = Typeface.createFromAsset(context.getAssets(), "EraserDust.ttf");
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return projectList.size();
	}

	@Override
	public Object getItem(int position) {
		return projectList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void removeItem(int position) {
		projectList.remove(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.projectlistitem, null);
			holder = new ViewHolder();
			holder.txtName = (TextView) convertView.findViewById(R.id.projectname);
			holder.txtModuleCode = (TextView) convertView.findViewById(R.id.projectmoduleCode);
			holder.txtLeader = (TextView) convertView.findViewById(R.id.projectleader);
			holder.txtDeadline = (TextView) convertView.findViewById(R.id.projectdeadline);
			holder.txtName.setTypeface(font);
			holder.txtModuleCode.setTypeface(font);
			holder.txtLeader.setTypeface(font);
			holder.txtDeadline.setTypeface(font);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.txtName.setText(projectList.get(position).getProject_name());
		holder.txtModuleCode.setText(projectList.get(position).getModule_code());
		holder.txtLeader.setText(projectList.get(position).getLeader_name());

		Calendar now = Calendar.getInstance();

		String[] temp = projectList.get(position).getDue_date().split("-");
		int dueYear = Integer.parseInt(temp[0]);
		int dueMonth = Integer.parseInt(temp[1]);
		int dueDate = Integer.parseInt(temp[2]);
		Date duedateDate = new Date(dueYear, dueMonth, dueDate);
		Date nowdateDate = new Date(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DATE));

		int diffinDays = (int) (duedateDate.getTime() - nowdateDate.getTime()) / (1000 * 60 * 60 * 24 * 7);

		projectList.get(position).getDue_date();

		if (diffinDays >= 0) {
			holder.txtDeadline.setText("Due in " + diffinDays + "weeks..");
		} else {
			holder.txtDeadline.setText("Overdue..");
		}

		return convertView;
	}

	static class ViewHolder {
		TextView txtName;
		TextView txtModuleCode;
		TextView txtLeader;
		TextView txtDeadline;
	}

}
