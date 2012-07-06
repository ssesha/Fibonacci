package com.icreate.projectx.project;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.google.gson.Gson;
import com.icreate.projectx.R;
import com.icreate.projectx.datamodel.ActivityFeed;
import com.icreate.projectx.datamodel.ProjectxGlobalState;

public class ProjectFeedActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activityfeed);
		ListView activities = (ListView) findViewById(R.id.activity);
		Context cont = this;
		Bundle extras = getIntent().getExtras();
		String project_id = extras.getString("project_id");
		String url = ProjectxGlobalState.urlPrefix + "getActivityFeed.php";
		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair("project_id", new Integer(project_id).toString()));
		String paramString = URLEncodedUtils.format(params, "utf-8");
		url += "?" + paramString;
		ProgressDialog dialog = new ProgressDialog(cont);
		dialog.setMessage("Loading Activity Feed");
		GetActivityFeed task = new GetActivityFeed(cont, this, dialog, activities);
		System.out.println(url);
		task.execute(url);
	}

	private class GetActivityFeed extends AsyncTask<String, Void, String> {
		private final Context context;
		private final Activity callingActivity;
		private final ProgressDialog dialog;
		private final ListView activityListView;

		public GetActivityFeed(Context context, Activity callingActivity, ProgressDialog dialog, ListView activityListView) {
			this.context = context;
			this.callingActivity = callingActivity;
			this.dialog = dialog;
			this.activityListView = activityListView;
		}

		@Override
		protected void onPreExecute() {
			if (!this.dialog.isShowing()) {
				this.dialog.setMessage("Loading...");
				this.dialog.show();
				this.dialog.setCanceledOnTouchOutside(false);
			}
		}

		@Override
		protected String doInBackground(String... urls) {
			String response = "";
			for (String url : urls) {
				HttpClient client = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(url);
				try {
					HttpResponse execute = client.execute(httpGet);
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
			if (this.dialog.isShowing()) {
				this.dialog.dismiss();
			}
			System.out.println(result);
			try {
				JSONObject resultJson = new JSONObject(result);
				Log.d("ActivityFeed", resultJson.toString());
				if (resultJson.getString("msg").equals("success")) {
					Gson gson = new Gson();
					ActivityFeed feed = gson.fromJson(result, ActivityFeed.class);
					activityListView.setAdapter(new ActivityFeedAdapter(context, feed.getNotifications()));
				} else {

				}
			} catch (JSONException e) {

				e.printStackTrace();
			}
		}
	}

}
