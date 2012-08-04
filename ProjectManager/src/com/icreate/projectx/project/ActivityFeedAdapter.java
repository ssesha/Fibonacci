package com.icreate.projectx.project;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
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
	private final Context cont;
	private final LayoutInflater mInflater;

	public ActivityFeedAdapter(Context context, ArrayList<Notification> projActivities) {
		ActivityFeedAdapter.projActivities = projActivities;
		cont = context;
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
		Log.d("activity feed", "inside base adapter");
		Log.d("activity feed", projActivities.toString());
		Log.d("activity feed", "position " + position);

		if (convertView == null) {
			Typeface font = Typeface.createFromAsset(cont.getAssets(), "EraserDust.ttf");
			convertView = mInflater.inflate(R.layout.activityitem, null);
			holder = new ViewHolder();
			holder.message = (TextView) convertView.findViewById(R.id.notification_msg);
			holder.sentTime = (TextView) convertView.findViewById(R.id.notification_time);
			holder.message.setTypeface(font);
			holder.sentTime.setTypeface(font);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.message.setText(projActivities.get(position).getMessage());
		// holder.sentTime.setText("today");
		holder.sentTime.setText(projActivities.get(position).getSentTime());
		return convertView;
	}

	private static class ViewHolder {
		TextView message;
		TextView sentTime;
	}

}
