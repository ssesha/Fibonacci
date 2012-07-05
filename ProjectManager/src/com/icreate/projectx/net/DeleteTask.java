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
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.icreate.projectx.datamodel.Project;
import com.icreate.projectx.datamodel.ProjectxGlobalState;
import com.icreate.projectx.datamodel.Task;
import com.icreate.projectx.task.TaskListBaseAdapter;

public class DeleteTask extends AsyncTask<String, Void, String> {

	private final Context context;
	private final Activity callingActivity;
	AdapterContextMenuInfo info;
	private final PullToRefreshListView projectListViewWrapper;
	private final int project_id;
	private final ListView task_projectList;
	private final ProgressDialog dialog;
	private final TextView search;

	public DeleteTask(Context context, Activity callingActivity, AdapterContextMenuInfo info, PullToRefreshListView projectListViewWrapper, int project_id, ListView task_projectList,
			ProgressDialog dialog, TextView search) {
		this.context = context;
		this.callingActivity = callingActivity;
		this.info = info;
		this.projectListViewWrapper = projectListViewWrapper;
		this.project_id = project_id;
		this.task_projectList = task_projectList;
		this.dialog = dialog;
		this.search = search;
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
				search.setText("");
				Gson gson = new Gson();
				Project project = gson.fromJson(resultJson.getString("projectString"), Project.class);
				System.out.println(project.getProject_name());
				ProjectxGlobalState globalState = (ProjectxGlobalState) callingActivity.getApplication();
				globalState.setProject(project);
				for (int i = 0; i < project.getTasks().size(); i++) {
					System.out.println("task" + i + project.getTasks().get(i).getTask_name());
					if (project.getTasks().get(i).getAssignee() != 0) {
						for (int j = 0; j < project.getMembers().size(); j++) {
							if (project.getTasks().get(i).getAssignee() == project.getMembers().get(j).getMember_id())
								project.getTasks().get(i).setAssignee_name(project.getMembers().get(j).getUser_name());
						}
					}
				}
				TaskListBaseAdapter taskListBaseAdapter = new TaskListBaseAdapter(context, (ArrayList<Task>) project.getTasks());
				task_projectList.setAdapter(taskListBaseAdapter);
				if (dialog == null) {
					projectListViewWrapper.onRefreshComplete();
				}
			} else {
				Toast.makeText(context, "Task can't be removed", Toast.LENGTH_SHORT).show();
			}
		} catch (JSONException e) {
			Toast.makeText(context, "Server Error", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}
}
