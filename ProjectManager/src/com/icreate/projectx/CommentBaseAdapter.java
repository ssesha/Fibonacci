package com.icreate.projectx;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.icreate.projectx.datamodel.Comment;

public class CommentBaseAdapter extends BaseAdapter {
	private static ArrayList<Comment> CommentList;

	private final LayoutInflater mInflater;

	public CommentBaseAdapter(Context context, ArrayList<Comment> commentList) {
		this.CommentList = commentList;
		mInflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return CommentList.size();
	}

	public Object getItem(int position) {
		return CommentList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.commentitem, null);
			holder = new ViewHolder();
			holder.creatorName = (TextView) convertView.findViewById(R.id.commentCreatorName);
			holder.comment = (TextView) convertView.findViewById(R.id.commentTextView);
			holder.lastUpdateTime = (TextView) convertView.findViewById(R.id.lastUpdateTime);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		//System.out.println("in base adapter" + CommentList.get(position).getCreator_name() + " " + CommentList.get(position).getComment());
		holder.creatorName.setText(CommentList.get(position).getCreator_name());
		holder.comment.setText(CommentList.get(position).getComment());
		holder.lastUpdateTime.setText(CommentList.get(position).getLastUpdateTime());
		return convertView;
	}

	static class ViewHolder {
		TextView creatorName;
		TextView comment;
		TextView lastUpdateTime;
	}

}
