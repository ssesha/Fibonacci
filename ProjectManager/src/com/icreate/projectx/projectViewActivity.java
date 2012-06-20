package com.icreate.projectx;

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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.icreate.projectx.datamodel.Project;
import com.icreate.projectx.datamodel.ProjectMembers;
import com.icreate.projectx.datamodel.ProjectxGlobalState;
import com.icreate.projectx.datamodel.Task;
import com.icreate.projectx.datamodel.TaskList;

public class projectViewActivity extends Activity {
	private TextView logoText;
	private TextView ProjectName;
	private Button createTask, TaskView;
	private Button editProject;
	private ProjectxGlobalState globalState;
	private Project project;
	private List<ProjectMembers> memberList;
	String projectString;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.projectview);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.logo1);

		final Context cont = this;
		final Activity currentActivity = this;

		Typeface font = Typeface.createFromAsset(getAssets(), "EraserDust.ttf");
		logoText = (TextView) findViewById(R.id.logoText);
		logoText.setTypeface(font);
		logoText.setTextColor(R.color.white);

		createTask = (Button) findViewById(R.id.createNewTaskButton);
		TaskView = (Button) findViewById(R.id.taskListButton);
		TextView projDesc = (TextView) findViewById(R.id.projDesc);
		editProject = (Button) findViewById(R.id.editProjectButton);
		// projDesc.setText(globalState.getProjectList().getProjects().get(position).getProject_Desc());

		final ListView memberListView = (ListView) findViewById(R.id.memberProgressList);
		memberListView.setTextFilterEnabled(true);
		registerForContextMenu(memberListView);
		// memberList = new ArrayList<ProjectMembers>();
		/*
		 * memberList.add("Abbinayaa Subramanian");
		 * memberList.add("Achyut Balaji"); memberList.add("Sesha Sendhil");
		 * memberList.add("Vandhanaa Lakshminarayanan");
		 */
		// System.out.println(memberList.toString());

		Bundle extras = getIntent().getExtras();
		globalState = (ProjectxGlobalState) getApplication();
		if (extras != null) {

			projectString = extras.getString("projectJson", "");
			Log.d("sdcsd", projectString);
			System.out.println(projectString.isEmpty());
			if (!(projectString.isEmpty())) {
				Gson gson = new Gson();
				project = gson.fromJson(projectString, Project.class);
				Toast.makeText(cont, project.getProject_name(),
						Toast.LENGTH_LONG).show();
				logoText.setText(project.getProject_name());
				projDesc.setText(project.getProject_desc());
				memberList = project.getMembers();
				System.out.println(memberList.toString());
				String url = "http://ec2-54-251-4-64.ap-southeast-1.compute.amazonaws.com/api/project_TaskList.php";
				List<NameValuePair> params = new LinkedList<NameValuePair>();
				params.add(new BasicNameValuePair("project_id", new Integer(
						project.getProject_id()).toString()));
				String paramString = URLEncodedUtils.format(params, "utf-8");
				url += "?" + paramString;
				ProgressDialog dialog = new ProgressDialog(cont);
				GetTaskList ListTasks = new GetTaskList(cont, currentActivity,
						dialog, memberListView);
				System.out.println(url);
				ListTasks.execute(url);
				// memberListView.setAdapter(new MemberProgressBaseAdapter(cont,
				// memberList));

			} else {
				Toast.makeText(cont, "Cannot load Project", Toast.LENGTH_LONG)
						.show();
			}
		}

		createTask.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent newTaskIntent = new Intent(cont, newTaskActivity.class);
				newTaskIntent.putExtra("project_Id", project.getProject_id());
				startActivity(newTaskIntent);
			}
		});

		TaskView.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent TaskViewIntent = new Intent(cont,
						expandTaskViewActivity.class);
				System.out.println("project" + projectString);
				TaskViewIntent.putExtra("project", projectString);
				startActivity(TaskViewIntent);

			}

		});
		editProject.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Intent editProjectIntent = new Intent(cont,
						editProjectActivity.class);
				editProjectIntent.putExtra("project_Id",
						project.getProject_id());
				startActivity(editProjectIntent);
			}
		});
	}

	public class GetTaskList extends AsyncTask<String, Void, String> {
		private final Context context;
		private final Activity callingActivity;
		private final ProgressDialog dialog;
		private final ListView taskListView;

		public GetTaskList(Context context, Activity callingActivity,
				ProgressDialog dialog, ListView taskListView) {
			this.context = context;
			this.callingActivity = callingActivity;
			this.dialog = dialog;
			this.taskListView = taskListView;
		}

		@Override
		protected void onPreExecute() {
			if (!this.dialog.isShowing()) {
				this.dialog.setMessage("Getting Project Details...");
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

					BufferedReader buffer = new BufferedReader(
							new InputStreamReader(content));
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
				Log.d("result msg", resultJson.getString("msg"));
				if (resultJson.getString("msg").equals("success")) {
					System.out.println("i am inside loop");
					Gson gson = new Gson();
					TaskList tasksContainer = gson.fromJson(result,
							TaskList.class);
					globalState.setTaskList(tasksContainer);
					ArrayList<Task> tasks = tasksContainer.getTasks();
					taskListView.setAdapter(new MemberProgressBaseAdapter(
							context, memberList, tasks));
					Log.d("testing", "" + tasks.size());
					for (Task task : tasks) {
						Log.d("testing", "test test");
						System.out.println("task name: " + task.getTask_name());
						System.out.println("project name: "
								+ task.getProject_name());
						System.out.println("task date: " + task.getDue_date());
					}
				} else {
					Toast.makeText(context, "Task Lists empty",
							Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				Toast.makeText(context, R.string.server_error,
						Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
		}
	}
}
