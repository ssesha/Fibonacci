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

package com.icreate.projectx.meeting;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.accounts.Account;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.google.api.client.http.HttpResponseException;
import com.icreate.projectx.R;
import com.icreate.projectx.R.id;
import com.icreate.projectx.R.layout;
import com.icreate.projectx.R.string;
import com.icreate.projectx.datamodel.AvailableMeetingTime;
import com.icreate.projectx.datamodel.Constants;
import com.icreate.projectx.meetingscheduler.adapter.EventExpandableListAdapter;
import com.icreate.projectx.meetingscheduler.util.DateUtils;
import com.icreate.projectx.meetingscheduler.util.EventTimesRetriever;
import com.icreate.projectx.meetingscheduler.util.FreeBusyTimesRetriever;
import com.icreate.projectx.meetingscheduler.util.OAuthManager;

/**
 * Activity Screen where the user selects the meeting time between the meeting
 * times proposed.
 * 
 * @author Nicolas Garnier
 */
public class SelectMeetingTimeActivity extends Activity {

	/** The constant to store the selectedAttendees list in an intent */
	private static final String SELECTED_ATTENDEES = "SELECTED_ATTENDEES";

	/** The constant to store an error message on result. */
	public static final String MESSAGE = "MESSAGE";

	/** UI attributes. */
	private final Handler handler = new Handler();
	private ProgressDialog progressBar = null;

	/** The date from which to start to look for available meeting times */
	private Calendar startDate;
	private int timeSpan;
	private int meetingLength;

	private List<String> selectedAttendees;
	private List<AvailableMeetingTime> availableMeetingTimes;

	/**
	 * Called when the activity is first created.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final boolean customTitleSupported = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

		// Creating main layout
		setContentView(R.layout.select_meeting_time);

		// Custom title bar
		if (customTitleSupported) {
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.app_title_select_time);
		}

		// Getting the selectedAttendees list from the intent
		Intent intent = getIntent();
		selectedAttendees = (List<String>) intent.getSerializableExtra(SELECTED_ATTENDEES);

		availableMeetingTimes = new ArrayList<AvailableMeetingTime>();

		// Today at midnight.
		startDate = Calendar.getInstance();
		startDate.set(Calendar.HOUR_OF_DAY, 0);
		startDate.clear(java.util.Calendar.HOUR);
		startDate.clear(java.util.Calendar.MINUTE);
		startDate.clear(java.util.Calendar.SECOND);
		startDate.clear(java.util.Calendar.MILLISECOND);

		initializeFindMoreButton();
		getAuthToken();
	}

	/**
	 * Returns an Intent that will display this Activity.
	 * 
	 * @param context
	 *            The application Context
	 * @param selectedAttendees
	 *            The list of selected Attendees. Should be of a Serializable
	 *            List type
	 * @return An intent that will display this Activity
	 * @throws NotSerializableException
	 *             If the {@code selectedAttendees} is not serializable
	 */
	public static Intent createViewIntent(Context context, List<String> selectedAttendees) throws NotSerializableException {
		Intent intent = new Intent(context, SelectMeetingTimeActivity.class);
		if (!(selectedAttendees instanceof Serializable)) {
			Log.e(Constants.TAG, "List<Attendee> selectedAttendees is not serializable");
			throw new NotSerializableException();
		}
		intent.putExtra(SELECTED_ATTENDEES, (Serializable) selectedAttendees);
		Log.e(Constants.TAG, "Successfully serialized List<Attendee> selectedAttendees in the intent");
		intent.setClass(context, SelectMeetingTimeActivity.class);
		return intent;
	}

	/**
	 * Called when sub activity returns.
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, final Intent results) {
		super.onActivityResult(requestCode, resultCode, results);
		switch (requestCode) {
		case Constants.CREATE_EVENT:
			if (resultCode == RESULT_OK) {
				Toast.makeText(this, getString(R.string.event_creation_success), Toast.LENGTH_SHORT).show();
			} else if (resultCode == RESULT_FIRST_USER && results != null) {
				Toast.makeText(this, getString(R.string.event_creation_failure) + ": " + results.getStringExtra(CreateEventActivity.MESSAGE), Toast.LENGTH_LONG).show();
			}
			break;
		}
	}

	/**
	 * Set the "Find More" button onClick's action.
	 */
	private void initializeFindMoreButton() {
		Button findMore = (Button) findViewById(R.id.find_more_meeting_time_button);

		findMore.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (OAuthManager.getInstance().getAuthToken() != null) {
					findMeetingTimes(5);
				} else
					getAuthToken();
			}
		});
	}

	/**
	 * Authenticates into the Calendar API using the selected account.
	 */
	private void getAuthToken() {
		OAuthManager authManager = OAuthManager.getInstance();

		if (authManager.getAuthToken() != null) {
			findMeetingTimes(5);
		} else {
			authManager.doLogin(false, this, new OAuthManager.AuthHandler() {
				@Override
				public void handleAuth(Account account, String authToken) {
					onActivityResult(Constants.AUTHENTICATED, RESULT_OK, null);
				}
			});
		}
	}

	/**
	 * Set preferences for EventTimesRetriever.
	 */
	private void setPreferences(EventTimesRetriever eventTimeRetriever) {
		if (eventTimeRetriever != null) {
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

			eventTimeRetriever.setSkipWeekEnds(preferences.getBoolean(Constants.SKIP_WEEKENDS_PREFERENCE, true));
			eventTimeRetriever.setUseWorkingHours(preferences.getBoolean(Constants.USE_WORKING_HOURS_PREFERENCE, true));
			eventTimeRetriever.setWorkingHoursStart(DateUtils.getCalendar(preferences.getString(Constants.WORKING_HOURS_START_PREFERENCE, getString(R.string.working_hours_start_default_value))));
			eventTimeRetriever.setWorkingHoursEnd(DateUtils.getCalendar(preferences.getString(Constants.WORKING_HOURS_END_PREFERENCE, getString(R.string.working_hours_end_default_value))));

			timeSpan = Integer.parseInt(preferences.getString(Constants.TIME_SPAN_PREFERENCE, getString(R.string.time_span_default_value)));
			meetingLength = Integer.parseInt(preferences.getString(Constants.MEETING_LENGTH_PREFERENCE, getString(R.string.meeting_length_default_value)));
		}
	}

	/**
	 * Find available meeting times.
	 * 
	 * @param tryNumber
	 *            Number of try.
	 */
	private void findMeetingTimes(final int tryNumber) {
		if (progressBar == null) {
			// Show a progress bar while the meeting times are computed.
			progressBar = ProgressDialog.show(this, null, getString(R.string.find_meeting_time_wait_text), true);
		}

		// Retrieves the common free time on a separate thread.
		new Thread(new Runnable() {
			@Override
			public void run() {
				boolean done = true;

				try {
					// Calculating the available meeting times from the
					// selectedAttendees
					// and the settings
					final EventTimesRetriever eventTimeRetriever = new EventTimesRetriever(new FreeBusyTimesRetriever(OAuthManager.getInstance().getAuthToken()));
					setPreferences(eventTimeRetriever);
					final List<AvailableMeetingTime> newTimes = eventTimeRetriever.getAvailableMeetingTime(selectedAttendees, startDate.getTime(), timeSpan, meetingLength);
					// Update the list of available meeting times.
					handler.post(new Runnable() {
						@Override
						public void run() {
							populateMeetingTimes(newTimes);
							// Set "find more" action to retrieve next meeting
							// times.
							startDate.add(Calendar.DAY_OF_YEAR, timeSpan);
						}
					});
				} catch (final IOException e) {
					if (e instanceof HttpResponseException) {
						HttpResponseException exceptionResponse = (HttpResponseException) e;
						String response = exceptionResponse.getMessage();
						int statusCode = exceptionResponse.getStatusCode();
						/*
						 * try { response.ignore(); } catch (IOException e1) {
						 * e1.printStackTrace(); }
						 */
						if (statusCode == 401 && (tryNumber - 1) > 0) {
							done = false;
							Log.d(Constants.TAG, "Got 401, refreshing token.");
							OAuthManager.getInstance().doLogin(true, SelectMeetingTimeActivity.this, new OAuthManager.AuthHandler() {

								@Override
								public void handleAuth(Account account, String authToken) {
									findMeetingTimes(tryNumber - 1);
								}
							});
						}
					}
					if (done) {
						handler.post(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(SelectMeetingTimeActivity.this, getString(R.string.available_time_retrieval_failure) + ": " + e.getMessage(), Toast.LENGTH_LONG).show();
							}
						});
					}
				}
				if (done) {
					// Update the progress bar
					handler.post(new Runnable() {
						@Override
						public void run() {
							if (progressBar != null) {
								progressBar.dismiss();
								progressBar = null;
							}
						}
					});
				}
			}
		}).start();
	}

	/**
	 * Displays the available meeting times on the screen.
	 * 
	 * @param newTimes
	 *            The meeting times to display.
	 */
	private void populateMeetingTimes(List<AvailableMeetingTime> newTimes) {
		availableMeetingTimes.addAll(newTimes);
		// Adding the available meeting times to the UI
		ExpandableListView meetingListContainer = (ExpandableListView) findViewById(R.id.meeting_list);
		meetingListContainer.setAdapter(new EventExpandableListAdapter(this, availableMeetingTimes, meetingLength, new EventExpandableListAdapter.EventHandler() {
			@Override
			public void handleEventSelected(long startTime, long endTime) {
				try {
					startActivityForResult(CreateEventActivity.createViewIntent(SelectMeetingTimeActivity.this, selectedAttendees, startTime, endTime), Constants.CREATE_EVENT);
				} catch (NotSerializableException e) {
					Log.e(Constants.TAG, "Intent is not run because of a NotSerializableException. " + "Probably the selectedAttendees list which is not serializable.");
				}
			}
		}));
	}
}
