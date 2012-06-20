package com.icreate.projectx;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.icreate.projectx.datamodel.Project;
import com.icreate.projectx.datamodel.ProjectMembers;

public class ProjectListBaseAdapter extends BaseAdapter {
	private static ArrayList<Project> projectList;

	private final LayoutInflater mInflater;

	public ProjectListBaseAdapter(Context context,
			ArrayList<Project> projectList) {
		this.projectList = projectList;
		mInflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return projectList.size();
	}

	public Object getItem(int position) {
		return projectList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public void removeItem(int position) {
		projectList.remove(position);
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.projectlistitem, null);
			holder = new ViewHolder();
			holder.txtName = (TextView) convertView
					.findViewById(R.id.projectname);
			holder.txtModuleCode = (TextView) convertView
					.findViewById(R.id.projectmoduleCode);
			holder.txtLeader = (TextView) convertView
					.findViewById(R.id.projectleader);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.txtName.setText(projectList.get(position).getProject_name());
		holder.txtModuleCode
				.setText(projectList.get(position).getModule_code());
		holder.txtLeader.setText(projectList.get(position).getLeader_name());
		return convertView;
	}

	static class ViewHolder {
		TextView txtName;
		TextView txtModuleCode;
		TextView txtLeader;
	}

}
