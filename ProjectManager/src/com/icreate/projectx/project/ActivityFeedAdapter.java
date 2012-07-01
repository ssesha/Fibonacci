package com.icreate.projectx.project;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.icreate.projectx.R;
import com.icreate.projectx.datamodel.Notification;

public class ActivityFeedAdapter extends BaseAdapter {
	private static ArrayList<Notification> projActivities;

	private final LayoutInflater mInflater;

	public ActivityFeedAdapter(Context context,
			ArrayList<Notification> projActivities) {
		ActivityFeedAdapter.projActivities = projActivities;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return projActivities.size();
	}

	@Override
	public Object getItem(int position) {
		return projActivities.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		Log.d("activity feed","inside base adapter");
		Log.d("activity feed", projActivities.toString());
		Log.d("activity feed", "position "+ position);

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.activityitem, null);
			holder = new ViewHolder();
			holder.message = (TextView) convertView
					.findViewById(R.id.notification_msg);
			holder.sentTime = (TextView) convertView
					.findViewById(R.id.notification_time);

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.message.setText(projActivities.get(position).getMessage());
		System.out.println(projActivities.get(position).getSentTime());
		//holder.sentTime.setText("today");
		holder.sentTime.setText(projActivities.get(position).getSentTime());
		System.out.println("out of getView");
		return convertView;
	}

	private static class ViewHolder {
		TextView message;
		TextView sentTime;
	}

}
