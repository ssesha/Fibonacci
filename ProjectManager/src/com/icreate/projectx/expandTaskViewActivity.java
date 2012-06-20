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
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.icreate.projectx.datamodel.Project;
import com.icreate.projectx.datamodel.ProjectList;
import com.icreate.projectx.datamodel.ProjectxGlobalState;
import com.icreate.projectx.datamodel.Task;
import com.icreate.projectx.datamodel.TaskList;
import com.icreate.projectx.net.DeleteProjectTask;

public class expandTaskViewActivity extends Activity {
	private TextView logoText;
	private ProjectxGlobalState globalState;
	private ListView task_projectListView;
	private Context cont;
	private Activity currentActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.task_projectlist);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.logo1);

		cont = this;
		currentActivity = this;

		globalState = (ProjectxGlobalState) getApplication();

		Typeface font = Typeface.createFromAsset(getAssets(), "EraserDust.ttf");
		logoText = (TextView) findViewById(R.id.logoText);
		logoText.setTypeface(font);
		logoText.setTextColor(R.color.white);

		ImageButton homeButton = (ImageButton) findViewById(R.id.logoImageButton);
		homeButton.setBackgroundResource(R.drawable.home_button);

		homeButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				startActivity(new Intent(cont, homeActivity.class));

			}
		});

		task_projectListView = (ListView) findViewById(R.id.taskListView01);
		task_projectListView.setTextFilterEnabled(true);
		registerForContextMenu(task_projectListView);

		Bundle extras = getIntent().getExtras();
		int project_id = 0;
		if (extras != null) {
			project_id = extras.getInt("project_Id");
				logoText.setText("Tasks");
		System.out.println("project_id="+project_id);
			

			Toast.makeText(cont, extras.getString("project_Id"),
					Toast.LENGTH_LONG).show();
			//passedUserId = extras.getString("requiredId");
			 String url = "http://ec2-54-251-4-64.ap-southeast-1.compute.amazonaws.com/api/project_TaskList.php";
			 List<NameValuePair> params = new LinkedList<NameValuePair>();
			 params.add(new BasicNameValuePair("project_id", new Integer(project_id).toString()));
			 String paramString = URLEncodedUtils.format(params, "utf-8");
			 url += "?" + paramString;
			
			ProgressDialog dialog = new ProgressDialog(cont);
			dialog.setMessage("Getting Tasks");
			task_ProjectList projectListTask = new task_ProjectList(cont,
					currentActivity, dialog, task_projectListView);
			System.out.println(url);
			projectListTask.execute(url);
		}

		task_projectListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Object o = task_projectListView.getItemAtPosition(position);
				Task selectedTask = (Task) o;
				Toast.makeText(
						cont,
						"You have chosen: "
								+ " "
								+ selectedTask.getTask_name()
								+ " "
								+ selectedTask.getTask_id()
								+ " "
								+ position
								+ " "
								+ selectedTask.getAssignee_name(),
						Toast.LENGTH_LONG).show();
				//Intent projectViewIntent = new Intent(cont,
						//projectViewActivity.class);
				// String currentUserId = globalData.getUserid();
				// if (!(currentUserId.isEmpty())) {
				//projectViewIntent.putExtra("position", position);
				// }
				// System.out.println(fullObject.getProject_name());
				//startActivity(projectViewIntent);
			}
		});
	}

	public class task_ProjectList extends AsyncTask<String, Void, String> {
		private final Context context;
		private final Activity callingActivity;
		private final ProgressDialog dialog;
		private final ListView task_projectListView;

		public task_ProjectList(Context context, Activity callingActivity,
				ProgressDialog dialog, ListView task_projectListView) {
			this.context = context;
			this.callingActivity = callingActivity;
			this.dialog = dialog;
			this.task_projectListView = task_projectListView;
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
			System.out.println("checking");
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
			System.out.println("checking post execute");
			System.out.println(result);
			try {
				JSONObject resultJson = new JSONObject(result);
				Log.d("TaskList", resultJson.toString());
				if (resultJson.getString("msg").equals("success")) {
					Gson gson = new Gson();
					TaskList tasksContainer = gson.fromJson(result,
							TaskList.class);
					globalState.setTaskList(tasksContainer);
					ArrayList<Task> tasks = tasksContainer.getTasks();
					task_projectListView.setAdapter(new TaskListBaseAdapter(
							context, tasks));
					for (Task task : tasks) {
						System.out.println("hello");
						//System.out.println(task.getTask_name());
						//System.out.println(task.getProject_name());
					}
				} else {
					Toast.makeText(context, "Project Lists empty",
							Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				Toast.makeText(context, R.string.server_error,
						Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("Context Menu");
		menu.add(0, v.getId(), 0, "Delete");
		menu.add(0, v.getId(), 0, "Action 2");
	}

	//@Override
	/*public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		Task selectedTask = (Task) task_projectListView
				.getItemAtPosition(info.position);
		System.out.println(selectedTask.getProject_name() + " "
				+ selectedTask.getTask_name());
		if (item.getTitle() == "Delete") {
			TaskListBaseAdapter taskListBaseAdapter = (TaskListBaseAdapter) task_projectListView
					.getAdapter();

			String url = "http://ec2-54-251-4-64.ap-southeast-1.compute.amazonaws.com/api/deleteProject.php?project_id="
					+ selectedTask.getTask_id();
			DeleteProjectTask deleteProjectTask = new DeleteProjectTask(cont,
					currentActivity, taskListBaseAdapter, info,
					selectedTask);
			deleteProjectTask.execute(url);
			return true;
		} else {
			System.out.println("blsldsdlflsfsdf");
		}
		return false;
	}*/
}