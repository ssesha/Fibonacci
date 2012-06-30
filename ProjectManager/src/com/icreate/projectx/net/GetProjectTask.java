package com.icreate.projectx.net;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.icreate.projectx.R;
import com.icreate.projectx.project.projectViewActivity;
import com.icreate.projectx.task.TaskViewActivity;

public class GetProjectTask extends AsyncTask<String, Void, String> {

	private final Context context;
	private final Activity callingActivity;
	private final ProgressDialog dialog;
	private final int task_id;

	public GetProjectTask(Context context, Activity callingActivity, ProgressDialog dialog, int task_id) {
		this.context = context;
		this.callingActivity = callingActivity;
		this.dialog = dialog;
		this.task_id = task_id;
	}

	@Override
	protected void onPreExecute() {
		if (!this.dialog.isShowing()) {
			this.dialog.setMessage("Getting Project Info...");
			this.dialog.show();
		}
	}

	@Override
	protected String doInBackground(String... urls) {
		String response = "";
		for (String url : urls) {
			DefaultHttpClient client = new DefaultHttpClient();
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
			System.out.println(resultJson.toString());
			System.out.println("task id is" + task_id);
			if (resultJson.getString("msg").equals("success")) {
				if (task_id == 0) {
					Intent projectViewIntent = new Intent(context, projectViewActivity.class);
					projectViewIntent.putExtra("projectJson", resultJson.getString("project"));
					Log.d("project json", projectViewIntent.getStringExtra("projectJson"));
					callingActivity.startActivity(projectViewIntent);
				} else {
					Intent TaskViewIntent = new Intent(context, TaskViewActivity.class);
					TaskViewIntent.putExtra("project", resultJson.getString("project"));
					TaskViewIntent.putExtra("task_id", task_id);
					Log.d("project in getProject", TaskViewIntent.getStringExtra("project"));
					callingActivity.startActivity(TaskViewIntent);
				}
			} else {
				Toast.makeText(context, "Unable to get Project", Toast.LENGTH_LONG).show();
			}
		} catch (JSONException e) {
			Toast.makeText(context, R.string.server_error, Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}
}
