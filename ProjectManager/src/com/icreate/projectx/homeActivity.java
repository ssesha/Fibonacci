package com.icreate.projectx;

import java.io.IOException;
import java.util.ArrayList;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.http.HttpResponseException;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.gson.Gson;
import com.icreate.projectx.datamodel.Event;
import com.icreate.projectx.datamodel.EventList_IVLE;
import com.icreate.projectx.datamodel.ProjectxGlobalState;
import com.icreate.projectx.meetingscheduler.activity.CreateEventActivity;
import com.icreate.projectx.meetingscheduler.activity.SelectAttendeesActivity;
import com.icreate.projectx.meetingscheduler.model.Constants;
import com.icreate.projectx.meetingscheduler.util.CalendarServiceBuilder;
import com.icreate.projectx.meetingscheduler.util.OAuthManager;
import com.icreate.projectx.project.ProjectListActivity;
import com.icreate.projectx.project.newProjectActivity;
import com.icreate.projectx.task.TaskListActivity;

public class homeActivity extends Activity {
	private ProjectxGlobalState globalData;
	private TextView logoText;
	private ImageButton logoButton, myProjectButton, myTaskButton,
			logoutButton, profileButton;
	private Context context;
	private Activity callingActivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.home);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.logo1);
		context = this;
		callingActivity = this;

		globalData = (ProjectxGlobalState) getApplication();
		final Context cont = this;
		Typeface font = Typeface.createFromAsset(getAssets(), "EraserDust.ttf");

		logoButton = (ImageButton) findViewById(R.id.logoImageButton);
		logoText = (TextView) findViewById(R.id.logoText);

		profileButton = (ImageButton) findViewById(R.id.profileButton);
		myProjectButton = (ImageButton) findViewById(R.id.myProjectButton);
		myTaskButton = (ImageButton) findViewById(R.id.myTaskButton);
		logoutButton = (ImageButton) findViewById(R.id.logoutButton);

		logoText.setTypeface(font);
		logoText.setText("Project-X");
		logoButton.setBackgroundResource(R.drawable.newprojectbutton);

		logoButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(cont, newProjectActivity.class));
			}
		});

		profileButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(cont, ProfileActivity.class));
			}
		});

		myProjectButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent projectListIntent = new Intent(cont,
						ProjectListActivity.class);
				String currentUserId = globalData.getUserid();
				if (!(currentUserId.isEmpty())) {
					projectListIntent.putExtra("requiredId", currentUserId);
					startActivity(projectListIntent);
				}
			}
		});

		myTaskButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent TaskListIntent = new Intent(cont, TaskListActivity.class);
				String currentUserId = globalData.getUserid();
				if (!(currentUserId.isEmpty())) {
					TaskListIntent.putExtra("requiredId", currentUserId);
					startActivity(TaskListIntent);
				}
			}
		});

		/*calendarButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ProjectxGlobalState global = (ProjectxGlobalState) getApplication();
				String url = "https://ivle.nus.edu.sg/api/Lapi.svc/MyOrganizer_Events?APIKey=tlXXFhEsNoTIVTJQruS2o"
						+ "&AuthToken="
						+ global.getAuthToken()
						+ "&StartDate=23/11/2011&EndDate=23/11/2012";
				Log.d("events url", url);
				ProgressDialog dialog = new ProgressDialog(context);
				GetEventsTask task = new GetEventsTask(context,
						callingActivity, dialog);
				task.execute(url);
				// startActivity(new Intent(cont,
				// SelectAttendeesActivity.class));
			}
		});*/


		logoutButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ProjectXPreferences.getEditor(cont).clear().commit();
				Intent HomeIntent = new Intent(cont, ProjectManagerActivity.class);
				HomeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(HomeIntent);
				finish();
			}
		});
	}

	private class GetEventsTask extends AsyncTask<String, Void, String> {
		private final Context context;
		private final Activity callingActivity;
		private final ProgressDialog dialog;

		public GetEventsTask(Context context, Activity callingActivity,
				ProgressDialog dialog) {
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
				EventList_IVLE events = gson.fromJson(result,
						EventList_IVLE.class);
				AddEventToCalendar(events);
			} catch (JSONException e) {
				Toast.makeText(context, R.string.server_error,
						Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
		}
	}

	private void AddEventToCalendar(EventList_IVLE events) {
		final ArrayList<Event> eventList = events.getEvents();
		getAuthToken();
		new Thread(new Runnable() {
			@Override
			public void run() {
				Calendar service = CalendarServiceBuilder.build(OAuthManager
						.getInstance().getAuthToken());
				for (Event iEvent : eventList) {
					com.google.api.services.calendar.model.Event newEvent = new com.google.api.services.calendar.model.Event();
					newEvent.setSummary(iEvent.getTitle());
					newEvent.setLocation(iEvent.getLocation());
					newEvent.setDescription(iEvent.getDescription());
					newEvent.setStart(new EventDateTime().setDate(iEvent
							.getDate_js()));
					newEvent.setEnd(new EventDateTime().setDate(iEvent
							.getDate_js()));
					// newEvent.setEnd(newEvent.getStart().)
					newEvent.setAttendees(null);
					try {
						Log.d("Sync Calendar", newEvent.getSummary());
						Log.d("Sync Calendar Start", newEvent.getStart()
								.toString());
						Log.d("Sync Calendar End", newEvent.getEnd().toString());
						service.events().insert("primary", newEvent)
								.setSendNotifications(false).execute();
					} catch (IOException e) {
						if (e instanceof HttpResponseException) {
							HttpResponseException exceptionResponse = (HttpResponseException) e;
							String response = exceptionResponse.getMessage();
							int statusCode = exceptionResponse.getStatusCode();
							if (statusCode == 401) {
								getAuthToken();
							}
						}
					}
					setResult(RESULT_OK);
				}
			}
		}).start();
	}

	private void getAuthToken() {
		OAuthManager authManager = OAuthManager.getInstance();
		
		if (authManager.getAuthToken() == null) {
			authManager.doLogin(false, this, new OAuthManager.AuthHandler() {
				@Override
				public void handleAuth(Account account, String authToken) {
					onActivityResult(Constants.AUTHENTICATED, RESULT_OK, null);
				}
			});
		}
		Log.d("auth token", authManager.getAuthToken());
	}

}
