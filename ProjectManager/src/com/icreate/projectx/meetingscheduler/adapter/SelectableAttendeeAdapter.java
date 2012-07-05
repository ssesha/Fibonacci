package com.icreate.projectx.meetingscheduler.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.icreate.projectx.R;
import com.icreate.projectx.meetingscheduler.model.Attendee;
import com.icreate.projectx.meetingscheduler.util.AttendeeComparator;

/**
 * Adapts the Attendee data to the ListView
 * 
 * @author Alain Vongsouvanh (alainv@google.com)
 */
public class SelectableAttendeeAdapter extends ArrayAdapter<Attendee> {

	/** Inflater used to create Views from layouts */
	private final LayoutInflater inflater;

	/**
	 * Constructor.
	 * 
	 * @param context
	 *            Used by super class.
	 * @param items
	 *            Used by super class.
	 */
	public SelectableAttendeeAdapter(Context context, List<Attendee> items) {
		super(context, R.layout.selectable_attendee, items);

		inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	/**
	 * Return the view to be drawn on the ListView for the attendee at position.
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		Attendee item = getItem(position);
		LinearLayout attendeeView = getView(convertView);

		setViews(item, attendeeView);
		return attendeeView;
	}

	/**
	 * Get the current view by inflating it with the selectable_attendee layout
	 * if necessary.
	 * 
	 * @param convertView
	 *            The view to convert or inflate.
	 * @return The AttendeeView layout.
	 */
	private LinearLayout getView(View convertView) {
		LinearLayout attendeeView;
		if (convertView == null) {
			attendeeView = new LinearLayout(getContext());
			inflater.inflate(R.layout.selectable_attendee, attendeeView, true);
		} else {
			attendeeView = (LinearLayout) convertView;
		}
		return attendeeView;
	}

	/**
	 * Set the layout items.
	 * 
	 * @param item
	 *            The attendee from which to read the data.
	 * @param attendeeView
	 *            The view to populate.
	 */
	private void setViews(Attendee item, LinearLayout attendeeView) {
		setNameView(item, attendeeView);
		setPhotoView(item, attendeeView);
		setCheckBoxView(item, attendeeView);
	}

	/**
	 * Set the nameView of the layout item with the current attendee name.
	 * 
	 * @param item
	 *            The attendee from which to read the data.
	 * @param attendeeView
	 *            The view to populate.
	 */
	private void setNameView(Attendee item, LinearLayout attendeeView) {
		TextView nameView = (TextView) attendeeView.findViewById(R.id.attendee_name);

		nameView.setText(item.name);
	}

	/**
	 * Set the photoView of the layout item with the current attendee picture.
	 * 
	 * @param item
	 *            The attendee from which to read the data.
	 * @param attendeeView
	 *            The view to populate.
	 */
	private void setPhotoView(Attendee item, LinearLayout attendeeView) {
		ImageView photoView = (ImageView) attendeeView.findViewById(R.id.attendee_photo);

		if (item.photoUri == null) {
			photoView.setImageResource(R.drawable.attendee_icon);
		} else {
			photoView.setImageURI(Uri.parse(item.photoUri));
		}
	}

	/**
	 * Set the checkBoxView of the layout item with the current attendee
	 * selection state.
	 * 
	 * @param item
	 *            The attendee from which to read the data.
	 * @param attendeeView
	 *            The view to populate.
	 */
	private void setCheckBoxView(Attendee item, LinearLayout attendeeView) {
		CheckBox checkBoxView = (CheckBox) attendeeView.findViewById(R.id.attendee_checkbox);

		checkBoxView.setChecked(item.selected);
		if (checkBoxView.isChecked()) {
			attendeeView.setBackgroundResource(R.color.selected_attendee_background);
		} else {
			attendeeView.setBackgroundColor(Color.TRANSPARENT);
		}
	}

	/**
	 * Sort the array using the AttendeeComparator.
	 */
	public void sort() {
		super.sort(AttendeeComparator.getInstance());
		super.notifyDataSetChanged();
	}

}
