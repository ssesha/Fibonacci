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
import android.content.Context;
import android.os.AsyncTask;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.icreate.projectx.datamodel.Project;
import com.icreate.projectx.datamodel.ProjectxGlobalState;

public class DeleteTask extends AsyncTask<String, Void, String> {

	private final Context context;
	private final Activity callingActivity;
	AdapterContextMenuInfo info;
	private final PullToRefreshListView projectListViewWrapper;
	private final int project_id;
	private final ListView task_projectList;
	private final Project project;

	public DeleteTask(Context context, Activity callingActivity, AdapterContextMenuInfo info, PullToRefreshListView projectListViewWrapper, int project_id, ListView task_projectList, Project project) {
		this.context = context;
		this.callingActivity = callingActivity;
		this.info = info;
		this.projectListViewWrapper = projectListViewWrapper;
		this.project_id = project_id;
		this.task_projectList = task_projectList;
		this.project = project;
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
		System.out.println(result);
		try {
			JSONObject resultJson = new JSONObject(result);
			System.out.println(resultJson.toString());
			if (resultJson.getString("msg").equals("success")) {
				Toast.makeText(context, " Task removed", Toast.LENGTH_SHORT).show();
				projectListViewWrapper.setRefreshing(false);
				String url = ProjectxGlobalState.urlPrefix + "getProject.php?project_id=" + project_id;
				GetProjectRefresh getProjectTask = new GetProjectRefresh(context, callingActivity, null, task_projectList, projectListViewWrapper, project);
				getProjectTask.execute(url);
			} else {
				Toast.makeText(context, "Task can't be removed", Toast.LENGTH_SHORT).show();
			}
		} catch (JSONException e) {
			Toast.makeText(context, "Server Error", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}
}
