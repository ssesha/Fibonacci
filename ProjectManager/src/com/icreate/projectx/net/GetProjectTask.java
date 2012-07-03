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
import android.widget.Toast;

import com.google.gson.Gson;
import com.icreate.projectx.R;
import com.icreate.projectx.datamodel.Project;
import com.icreate.projectx.datamodel.ProjectxGlobalState;
import com.icreate.projectx.project.projectViewActivity;
import com.icreate.projectx.task.TaskViewActivity;

public class GetProjectTask extends AsyncTask<String, Void, String> {

	private final Context context;
	private final Activity callingActivity;
	private final ProgressDialog dialog;
	private final int task_id;
	private final boolean flag;

	public GetProjectTask(Context context, Activity callingActivity, ProgressDialog dialog, int task_id, boolean flag) {
		this.context = context;
		this.callingActivity = callingActivity;
		this.dialog = dialog;
		this.task_id = task_id;
		this.flag = flag;
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
		try {
			if (this.dialog.isShowing()) {
				this.dialog.dismiss();
			}
		} catch (Exception e) {
		}
		System.out.println(result);
		try {
			JSONObject resultJson = new JSONObject(result);
			System.out.println(resultJson.toString());
			System.out.println("task id is" + task_id);
			if (resultJson.getString("msg").equals("success")) {
				Gson gson = new Gson();
				Project project = gson.fromJson(resultJson.getString("project"), Project.class);
				ProjectxGlobalState globalState = (ProjectxGlobalState) callingActivity.getApplication();
				globalState.setProject(project);

				if (task_id == 0) {
					Intent projectViewIntent = new Intent(context, projectViewActivity.class);
					callingActivity.startActivity(projectViewIntent);
				} else {
					Intent TaskViewIntent = new Intent(context, TaskViewActivity.class);
					TaskViewIntent.putExtra("task_id", task_id);
					callingActivity.startActivity(TaskViewIntent);
				}
				if (flag == true)
					callingActivity.finish();

			} else {
				Toast.makeText(context, "Unable to get Project", Toast.LENGTH_LONG).show();
			}
		} catch (JSONException e) {
			Toast.makeText(context, R.string.server_error, Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}
}
