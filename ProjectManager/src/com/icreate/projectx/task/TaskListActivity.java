package com.icreate.projectx.task;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.icreate.projectx.R;
import com.icreate.projectx.homeActivity;
import com.icreate.projectx.datamodel.ProjectxGlobalState;
import com.icreate.projectx.datamodel.Task;
import com.icreate.projectx.datamodel.TaskList;
import com.icreate.projectx.net.GetProjectTask;

public class TaskListActivity extends Activity {
	private TextView logoText;
	private ProjectxGlobalState globalState;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.tasklist);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.logo1);

		final Context cont = this;
		final Activity currentActivity = this;

		globalState = (ProjectxGlobalState) getApplication();

		Typeface font = Typeface.createFromAsset(getAssets(), "EraserDust.ttf");
		logoText = (TextView) findViewById(R.id.logoText);
		logoText.setTypeface(font);
		logoText.setTextColor(R.color.white);

		ImageButton homeButton = (ImageButton) findViewById(R.id.logoImageButton);
		homeButton.setBackgroundResource(R.drawable.home_button);

		homeButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(cont, homeActivity.class));

			}
		});

		final ListView TaskListView = (ListView) findViewById(R.id.taskListView);
		TaskListView.setTextFilterEnabled(true);

		Bundle extras = getIntent().getExtras();
		String passedUserId = null;
		if (extras != null) {
			passedUserId = extras.getString("requiredId");
			if (passedUserId.equalsIgnoreCase(globalState.getUserid())) {
				logoText.setText("My Tasks");
			} else {
				logoText.setText("Tasks");
			}

			Toast.makeText(cont, extras.getString("requiredId"), Toast.LENGTH_LONG).show();
			passedUserId = extras.getString("requiredId");
			String url = "http://ec2-54-251-4-64.ap-southeast-1.compute.amazonaws.com/api/TaskList.php";
			List<NameValuePair> params = new LinkedList<NameValuePair>();
			params.add(new BasicNameValuePair("user_id", passedUserId));
			String paramString = URLEncodedUtils.format(params, "utf-8");
			url += "?" + paramString;
			ProgressDialog dialog = new ProgressDialog(cont);
			dialog.setMessage("Getting Tasks");
			ListTask ListTasks = new ListTask(cont, currentActivity, dialog, TaskListView);
			System.out.println(url);
			ListTasks.execute(url);
		}

		TaskListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Object o = TaskListView.getItemAtPosition(position);
				Task selectedTask = (Task) o;
				Toast.makeText(cont, "You have chosen: " + " " + selectedTask.getTask_name() + " " + selectedTask.getTask_id() + " " + position, Toast.LENGTH_LONG).show();
				Intent TaskViewIntent = new Intent(cont, TaskViewActivity.class);
				int projectId = selectedTask.getProjectId();
				String url = "http://ec2-54-251-4-64.ap-southeast-1.compute.amazonaws.com/api/getProject.php?project_id=" + projectId;
				ProgressDialog dialog = new ProgressDialog(cont);
				dialog.setMessage("Getting Project Info...");
				dialog.show();
				GetProjectTask getProjectTask = new GetProjectTask(cont, currentActivity, dialog, selectedTask.getTask_id());
				getProjectTask.execute(url);
			}
		});
	}

	public class ListTask extends AsyncTask<String, Void, String> {
		private final Context context;
		private final Activity callingActivity;
		private final ProgressDialog dialog;
		private final ListView taskListView;

		public ListTask(Context context, Activity callingActivity, ProgressDialog dialog, ListView taskListView) {
			this.context = context;
			this.callingActivity = callingActivity;
			this.dialog = dialog;
			this.taskListView = taskListView;
		}

		@Override
		protected void onPreExecute() {
			if (!this.dialog.isShowing()) {
				this.dialog.setMessage("Getting Tasks...");
				this.dialog.show();
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
				Log.d("TaskList", resultJson.toString());
				if (resultJson.getString("msg").equals("success")) {
					Gson gson = new Gson();
					TaskList tasksContainer = gson.fromJson(result, TaskList.class);
					globalState.setTaskList(tasksContainer);
					ArrayList<Task> tasks = tasksContainer.getTasks();
					taskListView.setAdapter(new myTasksBaseAdapter(context, tasks));
					Log.d("testing", "" + tasks.size());
					for (Task task : tasks) {
						Log.d("testing", "test test");
						System.out.println("task name" + task.getTask_name());
						System.out.println("project name" + task.getProject_name());
						System.out.println("task date" + task.getDue_date());
					}
				} else {
					Toast.makeText(context, "Task Lists empty", Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				Toast.makeText(context, R.string.server_error, Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
		}
	}

}
