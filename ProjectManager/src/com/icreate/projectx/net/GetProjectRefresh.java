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
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.icreate.projectx.R;
import com.icreate.projectx.datamodel.Project;
import com.icreate.projectx.datamodel.ProjectxGlobalState;
import com.icreate.projectx.datamodel.Task;
import com.icreate.projectx.task.TaskListBaseAdapter;

public class GetProjectRefresh extends AsyncTask<String, Void, String> {

	private final Context context;
	private final Activity callingActivity;
	private final ProgressDialog dialog;
	private Project project;
	private TaskListBaseAdapter taskListBaseAdapter;
	private final ListView task_projectListView;
	private final PullToRefreshListView projectListViewWrapper;

	public GetProjectRefresh(Context context, Activity callingActivity, ProgressDialog dialog, ListView task_projectListView, PullToRefreshListView projectListViewWrapper, Project project) {
		this.context = context;
		this.callingActivity = callingActivity;
		this.dialog = dialog;
		this.task_projectListView = task_projectListView;
		this.projectListViewWrapper = projectListViewWrapper;
		this.project = project;
	}

	@Override
	protected void onPreExecute() {
		if (dialog != null) {
			if (!this.dialog.isShowing()) {
				this.dialog.setMessage("Getting Project Info...");
				this.dialog.setCanceledOnTouchOutside(false);
				this.dialog.setCancelable(false);
				this.dialog.show();
			}
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
			if (dialog != null && this.dialog.isShowing()) {
				this.dialog.dismiss();
			}
		} catch (Exception e) {
		}
		System.out.println(result);
		try {
			JSONObject resultJson = new JSONObject(result);
			System.out.println(resultJson.toString());
			if (resultJson.getString("msg").equals("success")) {
				Gson gson = new Gson();
				Project projectobj = gson.fromJson(resultJson.getString("project"), Project.class);
				ProjectxGlobalState globalState = (ProjectxGlobalState) callingActivity.getApplication();
				globalState.setProject(projectobj);

				globalState = (ProjectxGlobalState) callingActivity.getApplication();
				project = globalState.getProject();
				for (int i = 0; i < project.getTasks().size(); i++) {
					System.out.println("task" + i + project.getTasks().get(i).getTask_name());
					if (project.getTasks().get(i).getAssignee() != 0) {
						for (int j = 0; j < project.getMembers().size(); j++) {
							if (project.getTasks().get(i).getAssignee() == project.getMembers().get(j).getMember_id())
								project.getTasks().get(i).setAssignee_name(project.getMembers().get(j).getUser_name());
						}
					}
				}
				taskListBaseAdapter = new TaskListBaseAdapter(context, (ArrayList<Task>) project.getTasks());
				task_projectListView.setAdapter(taskListBaseAdapter);
				if (dialog == null) {
					projectListViewWrapper.onRefreshComplete();
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