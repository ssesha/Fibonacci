package com.icreate.projectx;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.icreate.projectx.datamodel.TaskList;
import com.icreate.projectx.datamodel.Project;

public class MemberProgressBaseAdapter extends BaseAdapter {
	private static ArrayList<String> memberList;
	private final LayoutInflater mInflater;

	public MemberProgressBaseAdapter(Context context,
			ArrayList<String> memberList) {
		this.memberList = memberList;
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
		holder.MemberName.setText(memberList.get(position));
		holder.TotalTask.setText("Total Tasks: 4");
		holder.CompletedTask.setText("No of completed Tasks: 0");
		return convertView;
	}
	
	static class ViewHolder {
		TextView MemberName;
		TextView TotalTask;
		TextView CompletedTask;
	}
}
