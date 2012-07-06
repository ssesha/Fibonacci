package com.icreate.projectx.meetingscheduler.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.NotSerializableException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.api.client.http.HttpResponseException;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.gson.Gson;
import com.icreate.projectx.R;
import com.icreate.projectx.homeActivity;
import com.icreate.projectx.datamodel.Event;
import com.icreate.projectx.datamodel.EventList_IVLE;
import com.icreate.projectx.datamodel.ProjectMembers;
import com.icreate.projectx.datamodel.ProjectxGlobalState;
import com.icreate.projectx.meetingscheduler.adapter.SelectableAttendeeAdapter;
import com.icreate.projectx.meetingscheduler.model.Attendee;
import com.icreate.projectx.meetingscheduler.model.Constants;
import com.icreate.projectx.meetingscheduler.util.AttendeeRetriever;
import com.icreate.projectx.meetingscheduler.util.CalendarServiceBuilder;
import com.icreate.projectx.meetingscheduler.util.OAuthManager;

public class SelectAttendeesActivity extends Activity {

	/** List of attendees that can be selected. */
	private final List<Attendee> attendees = new ArrayList<Attendee>();
	private ProjectxGlobalState global;
	/** ArrayAdapter for the attendees. */
	private SelectableAttendeeAdapter attendeeAdapter;
	private final List<String> memberGmails = new ArrayList<String>();
	private String myGmail;
	private static boolean Sync;
	private Activity callingActivity;
	private Context context;
	/** UI Attributes. */
	private final Handler handler = new Handler();
	private ProgressDialog progressBar;
	private Button findMeetingButton;
	private TextView logoText;
	private Context cont;
	private Activity currentActivity;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final boolean customTitleSupported = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		Sync = false;
		currentActivity = this;
		cont = this;
		callingActivity = this;
		context = this;
		global = (ProjectxGlobalState) getApplication();
		List<ProjectMembers> memberList = global.getProject().getMembers();
		for (ProjectMembers member : memberList) {
			memberGmails.add(member.getGmail());
			if (member.getUser_id().equals(global.getUserid())) {
				myGmail = member.getGmail();
				if (member.getIsSynced().equals("N"))
					Sync = true;
			}
		}
		// Creating main layout
		setContentView(R.layout.select_attendees);

		// Custom title bar
		if (customTitleSupported) {
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.logo1);
		}
		Typeface font = Typeface.createFromAsset(getAssets(), "EraserDust.ttf");
		logoText = (TextView) findViewById(R.id.logoText);
		logoText.setTypeface(font);
		logoText.setText("New Meeting");
		ImageButton homeButton = (ImageButton) findViewById(R.id.logoImageButton);
		homeButton.setBackgroundResource(R.drawable.home_button);

		homeButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent HomeIntent = new Intent(cont, homeActivity.class);
				HomeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(HomeIntent);
				currentActivity.finish();
			}
		});
		findMeetingButton = (Button) findViewById(R.id.find_time_button);
		// Adding action to the button
		addFindMeetingButtonListener();
		setAttendeeListView();
	}

	/**
	 * Add the OnClickListner to the findMeetingButton.
	 */
	private void addFindMeetingButtonListener() {
		findMeetingButton.setText("Find meetings");
		findMeetingButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				List<String> selectedAttendees = getSelectedAttendees();
				if (selectedAttendees.size() > 0) {
					Log.i(Constants.TAG, "Find meeting button pressed - about to launch SelectMeeting activity");

					// the results are called on widgetActivityCallback
					try {

						if (Sync) {
							AlertDialog.Builder builder = new AlertDialog.Builder(callingActivity);
							builder.setCancelable(true);
							builder.setTitle("Sync Calendar");
							builder.setMessage("Do you want to sync your IVLE and Google Calendars?");
							builder.setInverseBackgroundForced(true);
							builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {

									String url = "https://ivle.nus.edu.sg/api/Lapi.svc/MyOrganizer_Events?APIKey=tlXXFhEsNoTIVTJQruS2o" + "&AuthToken=" + global.getAuthToken()
											+ "&StartDate=23/11/2011&EndDate=23/11/2012";
									Log.d("events url", url);
									ProgressDialog dialog1 = new ProgressDialog(context);
									GetEventsTask task = new GetEventsTask(context, callingActivity, dialog1);
									task.execute(url);
								}
							});
							builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
									Sync = false;
								}
							});
							AlertDialog alert = builder.create();
							alert.show();
						} else {
							startActivity(SelectMeetingTimeActivity.createViewIntent(getApplicationContext(), selectedAttendees));
						}

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
				final List<Attendee> newAttendees = attendeeRetriever.getAttendees(memberGmails);

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

	private void AddEventToCalendar(EventList_IVLE events) {
		final ArrayList<Event> eventList = events.getEvents();
		new Thread(new Runnable() {
			@Override
			public void run() {
				Calendar service = CalendarServiceBuilder.build(OAuthManager.getInstance().getAuthToken());
				for (Event iEvent : eventList) {
					com.google.api.services.calendar.model.Event newEvent = new com.google.api.services.calendar.model.Event();
					newEvent.setSummary(iEvent.getTitle());
					newEvent.setLocation(iEvent.getLocation());
					newEvent.setDescription(iEvent.getDescription());

					String hour = iEvent.getDate_js().substring(11, 13);
					Integer endHr = Integer.parseInt(hour);
					endHr = endHr + 1;
					String endHrString = (endHr <= 9) ? "0" + endHr.toString() : endHr.toString();
					String endTime = iEvent.getDate_js().substring(0, 11) + endHrString + iEvent.getDate_js().substring(13);
					DateTime startTime;
					DateTime endTimeDate;
					try {
						startTime = new DateTime((new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss")).parse(iEvent.getDate_js()), TimeZone.getDefault());

						Log.d("startTime", startTime.toString());
						endTimeDate = new DateTime((new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss")).parse(endTime), TimeZone.getDefault());
						newEvent.setStart(new EventDateTime().setDateTime(startTime));
						newEvent.setEnd(new EventDateTime().setDateTime(endTimeDate));
						Log.d("endTime", endTimeDate.toString());
					} catch (ParseException e1) {
						e1.printStackTrace();
					}
					List<EventAttendee> attendees = new ArrayList<EventAttendee>();
					attendees.add(new EventAttendee().setEmail(myGmail));
					newEvent.setAttendees(attendees);
					try {
						Log.d("Sync Calendar", newEvent.getSummary());
						Log.d("Sync Calendar Start", newEvent.getStart().toString());
						Log.d("Sync Calendar End", newEvent.getEnd().toString());
						service.events().insert("primary", newEvent).setSendNotifications(true).execute();
					} catch (IOException e) {
						if (e instanceof HttpResponseException) {
							HttpResponseException exceptionResponse = (HttpResponseException) e;
							String response = exceptionResponse.getMessage();
							int statusCode = exceptionResponse.getStatusCode();
							if (statusCode == 401) {
								e.printStackTrace();
							}
							e.printStackTrace();
						}
						System.out.println(e.getStackTrace());
					}
					setResult(RESULT_OK);
				}
				Sync = false;
			}
		}).start();
	}

	private class GetEventsTask extends AsyncTask<String, Void, String> {
		private final Context context;
		private final Activity callingActivity;
		private final ProgressDialog dialog;

		public GetEventsTask(Context context, Activity callingActivity, ProgressDialog dialog) {
			this.context = context;
			this.callingActivity = callingActivity;
			this.dialog = dialog;
		}

		@Override
		protected void onPreExecute() {
			if (!this.dialog.isShowing()) {
				this.dialog.setMessage("Syncing Calendar to IVLE...");
				this.dialog.show();
				this.dialog.setCanceledOnTouchOutside(false);
				this.dialog.setCancelable(false);
			}
		}

		@Override
		protected String doInBackground(String... urls) {
			String content = "";
			for (String url : urls) {
				try {
					HttpClient client = new DefaultHttpClient();
					HttpGet get = new HttpGet(url);
					HttpResponse responseGet = client.execute(get);
					HttpEntity mResEntityGet = responseGet.getEntity();
					if (mResEntityGet != null) {
						content = EntityUtils.toString(mResEntityGet);
						Log.d("response", content);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return content;
		}

		@Override
		protected void onPostExecute(String result) {
			if (this.dialog.isShowing()) {
				this.dialog.dismiss();
			}
			System.out.println(result);
			try {
				JSONObject resultJson = new JSONObject(result);
				System.out.println(resultJson.toString());
				Gson gson = new Gson();
				EventList_IVLE events = gson.fromJson(result, EventList_IVLE.class);
				AddEventToCalendar(events);
				String url = ProjectxGlobalState.urlPrefix + "updateSync.php";
				List<NameValuePair> params = new LinkedList<NameValuePair>();
				params.add(new BasicNameValuePair("user_id", global.getUserid()));
				String paramString = URLEncodedUtils.format(params, "utf-8");
				url += "?" + paramString;
				UpdateUserSync task = new UpdateUserSync(context, callingActivity);
				task.execute(url);
			} catch (JSONException e) {

				e.printStackTrace();
			}
		}
	}

	private class UpdateUserSync extends AsyncTask<String, Void, String> {
		private final Context context;
		private final Activity callingActivity;

		public UpdateUserSync(Context context, Activity callingActivity) {
			this.context = context;
			this.callingActivity = callingActivity;
		}

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected String doInBackground(String... urls) {
			String response = "";
			for (String url : urls) {
				HttpClient client = new DefaultHttpClient();
				HttpPut httpPut = new HttpPut(url);
				try {
					HttpResponse execute = client.execute(httpPut);
					InputStream content = execute.getEntity().getContent();
					BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
					String s = "";
					while ((s = buffer.readLine()) != null) {
						response += s;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			System.out.println(result);
			try {
				JSONObject resultJson = new JSONObject(result);
				Log.d("PostComment", resultJson.toString());
				if (resultJson.getString("msg").equals("success")) {
					findMeetingButton.setText("Find meetings");
					Sync = false;
				} else {

				}
			} catch (JSONException e) {

				e.printStackTrace();
			}
		}
	}
}
