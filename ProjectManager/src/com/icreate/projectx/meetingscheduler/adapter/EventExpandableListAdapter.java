/*
 * Copyright (c) 2010 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.icreate.projectx.meetingscheduler.adapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.icreate.projectx.R;
import com.icreate.projectx.datamodel.AvailableMeetingTime;

/**
 * Adapts the Meeting data to the ExpendableListView.
 * 
 * @author Nicolas Garnier
 */
public class EventExpandableListAdapter extends BaseExpandableListAdapter {

	/**
	 * An interface for receiving updates once the user has selected the event
	 * times.
	 */
	public interface EventHandler {
		/**
		 * Handle the event being selected.
		 * 
		 * @param startTime
		 *            Start time of the event.
		 * @param endTime
		 *            End time of the event.
		 */
		public void handleEventSelected(long startTime, long endTime);
	}

	/** The Application Context */
	private final Activity activity;

	/** The lit of AvailableMeetingTime mapped by Days */
	private final Map<Date, List<AvailableMeetingTime>> sortedEventsByDays;

	/** The sorted list of Days with AvailableMeetingTime in them */
	private final List<Date> sortedDays;

	/** Inflater used to create Views from layouts */
	private final LayoutInflater inflater;

	/** The length of the meeting */
	private final int meetingLength;

	private final EventHandler handler;

	/**
	 * Constructs a new EventExpandableListAdapter given the List of Dates
	 * 
	 * @param activity
	 *            The activity of the application
	 * @param availableMeetingTimes
	 *            All the times for which a meeting is possible for the
	 *            attendees
	 */
	public EventExpandableListAdapter(Activity activity, List<AvailableMeetingTime> availableMeetingTimes, int meetingLength, EventHandler handler) {
		this.activity = activity;

		sortedEventsByDays = sortEventsByDay(availableMeetingTimes);
		sortedDays = asSortedList(sortedEventsByDays.keySet());

		inflater = LayoutInflater.from(activity);

		this.meetingLength = meetingLength;
		this.handler = handler;
	}

	/**
	 * Sorts a Collection and returns it as a Sorted List.
	 * 
	 * @param c
	 *            the collection to sort
	 * @return The sorted collection as a List
	 */
	public static <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
		List<T> list = new ArrayList<T>(c);
		java.util.Collections.sort(list);
		return list;
	}

	/**
	 * Return a map of sorted meeting times key'ed by date.
	 * 
	 * @param availableMeetingTimes
	 *            The list of AvailableMeetingTime
	 * @return A map of sorted meeting times key'ed by date.
	 */
	private Map<Date, List<AvailableMeetingTime>> sortEventsByDay(List<AvailableMeetingTime> availableMeetingTimes) {
		Map<Date, List<AvailableMeetingTime>> sortedEventsByDays = new HashMap<Date, List<AvailableMeetingTime>>();

		for (AvailableMeetingTime availableMeetingTime : availableMeetingTimes) {
			GregorianCalendar calendar = new GregorianCalendar(TimeZone.getDefault());
			calendar.setTime(availableMeetingTime.start);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.clear(Calendar.HOUR);
			calendar.clear(Calendar.MINUTE);
			calendar.clear(Calendar.SECOND);
			calendar.clear(Calendar.MILLISECOND);
			Date day = calendar.getTime();

			List<AvailableMeetingTime> meetingTimes = sortedEventsByDays.get(day);
			if (meetingTimes == null) {
				meetingTimes = new ArrayList<AvailableMeetingTime>();
				sortedEventsByDays.put(day, meetingTimes);
			}
			meetingTimes.add(availableMeetingTime);
		}
		return sortedEventsByDays;
	}

	@Override
	public AvailableMeetingTime getChild(int groupPosition, int childPosition) {
		return sortedEventsByDays.get(sortedDays.get(groupPosition)).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		try {
			return sortedEventsByDays.get(sortedDays.get(groupPosition)).size();
		} catch (Exception e) {
		}
		return 0;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		// Creating the Widget from layout
		View view = inflater.inflate(R.layout.meeting_time_result_entry, null);
		// Setting time of meeting
		final TextView text = (TextView) view.findViewById(R.id.meeting_time_item_text);
		final Date startTime = getChild(groupPosition, childPosition).start;
		final Date endTime = getChild(groupPosition, childPosition).end;
		text.setText(getMeetingDisplayString(startTime, endTime));
		// Adding Action to button
		Button button = (Button) view.findViewById(R.id.meeting_time_create_button);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final List<Pair<Date, Date>> calendars = getPossibleMeetingTime(startTime, endTime);

				if (calendars.size() > 1) {
					final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
					builder.setTitle(R.string.choose_meeting_time);
					builder.setCancelable(true);
					builder.setNegativeButton(R.string.cancel, null);
					builder.setItems(getPossibleMeetingTimeDisplay(calendars), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (handler != null) {
								Pair<Date, Date> meeting = calendars.get(which);
								handler.handleEventSelected(meeting.first.getTime(), meeting.second.getTime());
							}
						}
					});
					builder.show();
				} else {
					if (handler != null) {
						handler.handleEventSelected(startTime.getTime(), endTime.getTime());
					}
				}
			}

			/**
			 * Get the list of possible meeting time from startDate to endDate
			 * for the preferred meeting length.
			 * 
			 * @param startDate
			 *            Start date to retrieve possible meeting times from.
			 * @param endDate
			 *            End date to retrieve possible meeting times to.
			 * @return Possible meeting times.
			 */
			private List<Pair<Date, Date>> getPossibleMeetingTime(Date startDate, Date endDate) {
				List<Pair<Date, Date>> result = new ArrayList<Pair<Date, Date>>();
				Calendar currentStart = new GregorianCalendar(TimeZone.getDefault());
				Calendar currentEnd = new GregorianCalendar(TimeZone.getDefault());

				currentStart.setTime(startDate);
				currentEnd.setTime(startDate);
				currentEnd.add(Calendar.MINUTE, meetingLength);
				while (!currentEnd.getTime().after(endDate)) {
					result.add(new Pair<Date, Date>(currentStart.getTime(), currentEnd.getTime()));
					currentStart.add(Calendar.MINUTE, 15);
					currentEnd.add(Calendar.MINUTE, 15);
				}

				return result;
			}

			/**
			 * Get the list of meeting time display texts for the dialog.
			 * 
			 * @param meetings
			 *            List of meetings to get display for.
			 * @return List of meeting time display text.
			 */
			private String[] getPossibleMeetingTimeDisplay(List<Pair<Date, Date>> meetings) {
				String[] result = new String[meetings.size()];
				int i = 0;

				for (Pair<Date, Date> meeting : meetings) {
					result[i++] = getMeetingDisplayString(meeting.first, meeting.second);
				}

				return result;
			}
		});
		return view;
	}

	@Override
	public Date getGroup(int groupPosition) {
		return sortedDays.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return sortedDays.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		View view = inflater.inflate(R.layout.meeting_time_result_group_title, null);
		TextView title = (TextView) view.findViewById(R.id.meeting_time_group_title);
		String date = DateUtils.formatDateTime(activity, getGroup(groupPosition).getTime(), DateUtils.FORMAT_SHOW_DATE + DateUtils.FORMAT_SHOW_WEEKDAY + DateUtils.FORMAT_SHOW_YEAR);
		title.setText(date + " (" + getChildrenCount(groupPosition) + ")");
		return view;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	private String getMeetingDisplayString(Date startDate, Date endDate) {
		java.text.DateFormat format = DateFormat.getTimeFormat(activity);

		format.setTimeZone(TimeZone.getDefault());

		String dateStart = format.format(startDate);
		String dateEnd = format.format(endDate);

		return dateStart + " - " + dateEnd;
	}
}
