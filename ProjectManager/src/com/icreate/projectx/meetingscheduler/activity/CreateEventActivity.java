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

package com.icreate.projectx.meetingscheduler.activity;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.accounts.Account;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.api.client.http.HttpResponseException;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.icreate.projectx.R;
import com.icreate.projectx.R.id;
import com.icreate.projectx.R.layout;
import com.icreate.projectx.R.string;
import com.icreate.projectx.datamodel.Constants;
import com.icreate.projectx.meetingscheduler.util.CalendarServiceBuilder;
import com.icreate.projectx.meetingscheduler.util.OAuthManager;

/**
 * Activity displaying a UI to edit event's Title, Description and Location.
 * Clicking on "Create event" send a request to the API to create the events
 * with the specified attendees on the user's primary calendar.
 * 
 * @since 2.2
 * @author Alain Vongsouvanh (alainv@google.com)
 */
public class CreateEventActivity extends Activity {

	/** The constant to store the selectedAttendees list in an intent */
	private static final String SELECTED_ATTENDEES = "SELECTED_ATTENDEES";
	private static final String START_DATE = "START_DATE";
	private static final String END_DATE = "END_DATE";

	/** The constant to store an error message on result. */
	public static final String MESSAGE = "MESSAGE";

	/** UI attributes. */
	private final Handler handler = new Handler();
	private ProgressDialog progressBar = null;

	/** Class attributes retrieved from the intent. */
	List<String> selectedAttendees;
	private DateTime startDate;
	private DateTime endDate;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final boolean customTitleSupported = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

		// Creating main layout
		setContentView(R.layout.set_event_details);

		// Custom title bar
		if (customTitleSupported) {
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.app_title_set_event_details);
		}

		getParameters();
		setSaveButtonAction();
	}

	/**
	 * Set action when the Save Button is clicked.
	 */
	private void setSaveButtonAction() {
		Button saveButton = (Button) findViewById(R.id.save_event_button);
		saveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String title = getString(R.id.event_title_text);
				final String where = getString(R.id.event_where_text);
				final String description = getString(R.id.event_description_text);
				final boolean sendEventNotifications = getBoolean(R.id.send_event_notifications_checkbox);

				if (OAuthManager.getInstance().getAuthToken() == null) {
					OAuthManager.getInstance().doLogin(false, CreateEventActivity.this, new OAuthManager.AuthHandler() {
						@Override
						public void handleAuth(Account account, String authToken) {
							createEvent(title, where, description, sendEventNotifications, 5);
						}
					});
				} else {
					createEvent(title, where, description, sendEventNotifications, 5);
				}
			}

			private String getString(int viewId) {
				EditText view = (EditText) findViewById(viewId);

				if (view != null)
					return view.getText().toString();
				else
					return null;
			}

			private boolean getBoolean(int viewId) {
				CheckBox view = (CheckBox) findViewById(viewId);

				if (view != null)
					return view.isChecked();
				else
					return false;
			}
		});
	}

	/**
	 * Send a request to the Calendar API to create an event wit the specified
	 * parameters.
	 * 
	 * @param title
	 *            Event's title.
	 * @param where
	 *            Event's location.
	 * @param description
	 *            Event's description
	 * @param sendEventNotifications
	 *            Whether or not to send a notification to attendees.
	 */
	private void createEvent(final String title, final String where, final String description, final boolean sendEventNotifications, final int tryNumber) {
		// Show a progress bar while the common free times are computed.
		if (progressBar == null) {
			progressBar = ProgressDialog.show(CreateEventActivity.this, null, CreateEventActivity.this.getString(R.string.create_event_wait_text), true);
		}

		// Try creating the event on a separate thread.
		new Thread(new Runnable() {
			@Override
			public void run() {
				boolean done = true;

				try {
					Calendar service = CalendarServiceBuilder.build(OAuthManager.getInstance().getAuthToken());
					Event newEvent = new Event();
					List<EventAttendee> attendees = new ArrayList<EventAttendee>();

					newEvent.setSummary(title);
					newEvent.setLocation(where);
					newEvent.setDescription(description);
					newEvent.setStart(new EventDateTime().setDateTime(startDate));
					newEvent.setEnd(new EventDateTime().setDateTime(endDate));

					for (String attendee : selectedAttendees) {
						attendees.add(new EventAttendee().setEmail(attendee));
					}
					newEvent.setAttendees(attendees);

					service.events().insert("primary", newEvent).setSendNotifications(sendEventNotifications).execute();
					setResult(RESULT_OK);
				} catch (IOException e) {
					if (e instanceof HttpResponseException) {
						HttpResponseException exceptionResponse = (HttpResponseException) e;
						String response = exceptionResponse.getMessage();
						int statusCode = exceptionResponse.getStatusCode();
						if (statusCode == 401 && (tryNumber - 1) > 0) {
							done = false;
							Log.d(Constants.TAG, "Got 401, refreshing token.");
							OAuthManager.getInstance().doLogin(true, CreateEventActivity.this, new OAuthManager.AuthHandler() {

								@Override
								public void handleAuth(Account account, String authToken) {
									createEvent(title, where, description, sendEventNotifications, tryNumber - 1);
								}
							});
						}
					}
					if (done) {
						Intent data = new Intent();

						data.putExtra(MESSAGE, e.getMessage());
						setResult(RESULT_FIRST_USER, data);
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
							CreateEventActivity.this.finish();
						}
					});

				}
			}
		}).start();
	}

	/**
	 * Get the parameters passed into this activity.
	 */
	@SuppressWarnings("unchecked")
	private void getParameters() {
		// Getting the selectedAttendees list from the intent
		final Intent intent = getIntent();
		selectedAttendees = (List<String>) intent.getSerializableExtra(SELECTED_ATTENDEES);
		// Default values are "now" and "now + 1hour".
		startDate = new DateTime(intent.getLongExtra(START_DATE, java.util.Calendar.getInstance().getTimeInMillis()), 0);
		endDate = new DateTime(intent.getLongExtra(END_DATE, java.util.Calendar.getInstance().getTimeInMillis() + 3600000), 0);
	}

	/**
	 * Returns an Intent that will display this Activity.
	 * 
	 * @param context
	 *            The application Context
	 * @param selectedAttendees
	 *            The list of selected Attendees. Should be of a Serializable
	 *            List type
	 * @param startDate
	 *            The start date of the event to create
	 * @param endDate
	 *            The end date of the event to create
	 * @return An intent that will display this Activity
	 * @throws NotSerializableException
	 *             If the {@code selectedAttendees} is not serializable
	 */
	public static Intent createViewIntent(Context context, List<String> selectedAttendees, long startDate, long endDate) throws NotSerializableException {
		Intent intent = new Intent(context, SelectMeetingTimeActivity.class);
		if (!(selectedAttendees instanceof Serializable)) {
			Log.e(Constants.TAG, "List<Attendee> selectedAttendees is not serializable");
			throw new NotSerializableException();
		}
		intent.putExtra(SELECTED_ATTENDEES, (Serializable) selectedAttendees);
		intent.putExtra(START_DATE, startDate);
		intent.putExtra(END_DATE, endDate);

		intent.setClass(context, CreateEventActivity.class);
		return intent;
	}

}
