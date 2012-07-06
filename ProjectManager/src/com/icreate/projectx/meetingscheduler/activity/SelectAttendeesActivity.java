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

import java.io.NotSerializableException;
import java.util.ArrayList;
import java.util.List;

import android.accounts.Account;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter.FilterListener;
import android.widget.ListView;

import com.icreate.projectx.R;
import com.icreate.projectx.meetingscheduler.adapter.SelectableAttendeeAdapter;
import com.icreate.projectx.meetingscheduler.model.Attendee;
import com.icreate.projectx.meetingscheduler.model.Constants;
import com.icreate.projectx.meetingscheduler.util.AttendeeRetriever;
import com.icreate.projectx.meetingscheduler.util.OAuthManager;

/**
 * Activity Screen where the user selects the meeting attendees.
 * 
 * @author Alain Vongsouvanh (alainv@google.com)
 */
public class SelectAttendeesActivity extends Activity {

	/** List of attendees that can be selected. */
	private final List<Attendee> attendees = new ArrayList<Attendee>();

	/** ArrayAdapter for the attendees. */
	private SelectableAttendeeAdapter attendeeAdapter;

	/** UI Attributes. */
	private final Handler handler = new Handler();
	private ProgressDialog progressBar;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final boolean customTitleSupported = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

		// Creating main layout
		setContentView(R.layout.select_attendees);

		// Custom title bar
		if (customTitleSupported) {
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.app_title_select_attendees);
		}

		// Adding action to the button
		addFindMeetingButtonListener();
		setAttendeeListView();
	}

	/**
	 * Add the OnClickListner to the findMeetingButton.
	 */
	private void addFindMeetingButtonListener() {
		Button findMeetingButton = (Button) findViewById(R.id.find_time_button);
		findMeetingButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				List<String> selectedAttendees = getSelectedAttendees();
				if (selectedAttendees.size() > 0) {
					Log.i(Constants.TAG, "Find meeting button pressed - about to launch SelectMeeting activity");

					// the results are called on widgetActivityCallback
					try {

						startActivity(SelectMeetingTimeActivity.createViewIntent(getApplicationContext(), selectedAttendees));
					} catch (NotSerializableException e) {
						Log.e(Constants.TAG, "Intent is not run because of a NotSerializableException. " + "Probably the selectedAttendees list which is not serializable.");
					}
					Log.i(Constants.TAG, "Find meeting button pressed - successfully launched SelectMeeting activity");
				} else {

				}
			}
		});
	}

	/**
	 * Populate the list of attendees into the activity's ListView.
	 */
	private void setAttendeeListView() {
		final ListView attendeeListView = (ListView) findViewById(R.id.attendee_list);

		initializeTextFilter(attendeeListView);

		attendeeAdapter = new SelectableAttendeeAdapter(this, attendees);
		attendeeAdapter.sort();

		attendeeListView.setAdapter(attendeeAdapter);

		// Adding click event to attendees Widgets
		attendeeListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// We use position -1 to ignore the header.
				Attendee attendee = (Attendee) attendeeListView.getItemAtPosition(position);
				attendee.selected = !attendee.selected;
				attendeeAdapter.sort();
			}
		});
	}

	/**
	 * Retrieve the list of attendees from the phone's Contacts database.
	 */
	private void retrieveAttendees() {
		// Retrieves the attendees on a separate thread.
		new Thread(new Runnable() {
			@Override
			public void run() {
				AttendeeRetriever attendeeRetriever = new AttendeeRetriever(SelectAttendeesActivity.this, OAuthManager.getInstance().getAccount());
				final List<Attendee> newAttendees = attendeeRetriever.getAttendees();

				// Update the progress bar
				handler.post(new Runnable() {
					@Override
					public void run() {
						if (newAttendees != null) {
							attendees.clear();
							attendees.addAll(newAttendees);

							attendeeAdapter.sort();
							attendeeAdapter.notifyDataSetChanged();
						}

						Log.d(Constants.TAG, "Got attendees, dismissing progress bar");
						if (progressBar != null) {
							progressBar.dismiss();
							Log.d(Constants.TAG, "Progress bar should have been dismissed");
						}
					}
				});
			}
		}).start();
		// Show a progress bar while the attendees are retrieved from the
		// phone's
		// database.
		progressBar = ProgressDialog.show(this, null, getString(R.string.retrieve_contacts_wait_text), true);
	}

	/**
	 * Add on text changed listener to filter the attendee list view.
	 * 
	 * @param view
	 *            ListView to add the edit text to.
	 */
	private void initializeTextFilter(ListView view) {
		EditText editText = (EditText) getLayoutInflater().inflate(R.layout.attendees_text_filter, null);

		editText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (attendeeAdapter != null) {
					attendeeAdapter.getFilter().filter(s, new FilterListener() {
						@Override
						/**
						 * Sort the array once the filter has been completed.
						 */
						public void onFilterComplete(int count) {
							attendeeAdapter.sort();
						}
					});
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		view.addHeaderView(editText);
	}

	/**
	 * Returns the list of currently selected attendees.
	 * 
	 * @return the list of currently selected attendees
	 */
	private List<String> getSelectedAttendees() {
		List<String> selectedAttendees = new ArrayList<String>();

		if (attendees != null) {
			for (Attendee attendee : attendees) {
				if (attendee.selected) {
					selectedAttendees.add(attendee.email);
				}
			}
		}

		return selectedAttendees;
	}

	/**
	 * Initialize the contents of the Activity's options menu.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.settings, menu);
		return true;
	}

	/**
	 * Called whenever an item in the options menu is selected.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.menu.settings:
			startActivity(PreferencesActivity.createViewIntent(getApplicationContext()));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Update the settings text whenever this activity resumes
	 */
	@Override
	protected void onResume() {
		super.onResume();
		getAccount();
	}

	/**
	 * Prompt user to choose an account and retrieve attendees from phone's
	 * database.
	 */
	private void getAccount() {
		OAuthManager.getInstance().doLogin(false, this, new OAuthManager.AuthHandler() {
			@Override
			public void handleAuth(Account account, String authToken) {
				if (account != null) {
					retrieveAttendees();
				}
			}
		});
	}
}
