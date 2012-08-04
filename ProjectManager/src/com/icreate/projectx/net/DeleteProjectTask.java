package com.icreate.projectx.net;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.icreate.projectx.datamodel.Project;
import com.icreate.projectx.project.ProjectListBaseAdapter;

public class DeleteProjectTask extends AsyncTask<String, Void, String> {

	private final Context context;
	private final Activity callingActivity;
	ProjectListBaseAdapter projectListBaseAdapter;
	AdapterContextMenuInfo info;
	ArrayList<Project> projects;

	public DeleteProjectTask(Context context, Activity callingActivity, ProjectListBaseAdapter projectListBaseAdapter, AdapterContextMenuInfo info, ArrayList<Project> projects) {
		this.context = context;
		this.callingActivity = callingActivity;
		this.projectListBaseAdapter = projectListBaseAdapter;
		this.info = info;
		this.projects = projects;
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
		Project project = (Project) projectListBaseAdapter.getItem(info.position - 1);
		try {
			JSONObject resultJson = new JSONObject(result);
			if (resultJson.getString("msg").equals("success")) {
				projectListBaseAdapter.removeItem(info.position - 1);
				projectListBaseAdapter.notifyDataSetChanged();
				projects.remove(project);

			} else {

			}
		} catch (JSONException e) {

			e.printStackTrace();
		}
	}
}
