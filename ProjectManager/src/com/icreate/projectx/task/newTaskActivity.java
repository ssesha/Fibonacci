package com.icreate.projectx.task;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.icreate.projectx.AlarmReceiver;
import com.icreate.projectx.MemberProgressBaseAdapter;
import com.icreate.projectx.R;
import com.icreate.projectx.homeActivity;
import com.icreate.projectx.datamodel.Project;
import com.icreate.projectx.datamodel.ProjectMembers;
import com.icreate.projectx.datamodel.ProjectxGlobalState;
import com.icreate.projectx.datamodel.Task;

public class newTaskActivity extends Activity implements AdapterView.OnItemSelectedListener {

	private EditText taskNameTextBox, taskAboutTextBox, taskDateTextBox, projectNameTextBox, parentTextBox;
	private DatePicker taskDate;
	private Button createTask;
	private final List<ProjectMembers> memberList = new ArrayList<ProjectMembers>();
	private final List<String> prioriList = new ArrayList<String>();
	// private final List<String> Members = new ArrayList<String>();
	private final ArrayList<Task> parenttasks = new ArrayList<Task>();
	private Spinner Assignto, Priority, Parent;
	private ArrayAdapter<String> prioriAdapter;
	private TaskListBaseAdapter parentAdapter;
	private String projectString;
	int parentId;
	int memberId;
	private Project project;
	private String status;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.newtask);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.logo1);
		final Context cont = this;
		final Activity currentActivity = this;

		Bundle extras = getIntent().getExtras();
		ProjectxGlobalState global = (ProjectxGlobalState) getApplication();

		if (extras != null) {
			projectString = extras.getString("project");
			memberId = extras.getInt("member", 0);
			parentId = extras.getInt("parent", 0);
			System.out.println(projectString);
			// Toast.makeText(cont, "" + projectId, Toast.LENGTH_LONG).show();
			// get members of project and store in memberlist
			// Members.add("Assign Task to");
			Gson gson = new Gson();
			project = gson.fromJson(projectString, Project.class);

			ProjectMembers dummyMember = new ProjectMembers(0);
			memberList.add(dummyMember);
			memberList.addAll(1, project.getMembers());
			System.out.println("size" + memberList.size());
			/*
			 * for (int i = 0; i < memberList.size(); i++) { Members.add(i + 1,
			 * memberList.get(i).getUser_name()); }
			 */
			// for (int i = 0; i < memberList.size(); i++) {
			// Members.add(i + 1, memberList.get(i).getUser_name());
			// }
		}

		taskNameTextBox = (EditText) findViewById(R.id.taskNameBox);
		taskAboutTextBox = (EditText) findViewById(R.id.taskAboutBox);
		taskDateTextBox = (EditText) findViewById(R.id.taskDeadlineBox);
		Assignto = (Spinner) findViewById(R.id.taskAssignedBox);
		taskDate = (DatePicker) findViewById(R.id.taskDate);
		createTask = (Button) findViewById(R.id.TaskButton);
		Priority = (Spinner) findViewById(R.id.taskPriorityBox);
		Parent = (Spinner) findViewById(R.id.taskParentBox);
		projectNameTextBox = (EditText) findViewById(R.id.project_TaskNameBox);
		projectNameTextBox.setText(project.getProject_name());
		Task dummyTask = new Task(0);

		parenttasks.add(dummyTask);
		parenttasks.addAll(1, project.getTasks());
		parentAdapter = new TaskListBaseAdapter(cont, parenttasks);
		Parent.setAdapter(parentAdapter);

		if (parentId != 0) {
			for (int i = 0; i < project.getTasks().size(); i++) {
				if (parentId == project.getTasks().get(i).getTask_id())
					Parent.setSelection(i + 1);
			}
		}

		if (memberId != 0) {
			for (int i = 0; i < project.getMembers().size(); i++) {
				if (memberId == project.getMembers().get(i).getMember_id())
					Assignto.setSelection(i + 1);
			}
		}

		RelativeLayout tasklayout = (RelativeLayout) findViewById(R.id.newTaskLayout);
		status = "OPEN";

		prioriList.add("Low");
		prioriList.add("Medium");
		prioriList.add("High");
		prioriList.add("Critical");

		prioriAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, prioriList);
		prioriAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Priority.setAdapter(prioriAdapter);

		/*
		 * dataAdapter = new ArrayAdapter<String>(this,
		 * android.R.layout.simple_spinner_item, Members) {
		 * 
		 * @Override public boolean isEnabled(int position) { if (position == 0)
		 * { return false; } else { return true; } }
		 * 
		 * @Override public boolean areAllItemsEnabled() { return false; } }; ;
		 */
		// dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Assignto.setAdapter(new MemberProgressBaseAdapter(cont, memberList, (ArrayList<Task>) project.getTasks()));

		Assignto.setOnItemSelectedListener(this);

		taskDateTextBox.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				taskDate.setVisibility(View.VISIBLE);
			}

		});

		tasklayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				taskDateTextBox.setText(taskDate.getYear() + "-" + (taskDate.getMonth() + 1) + "-" + taskDate.getDayOfMonth());
				taskDate.setVisibility(View.INVISIBLE);
			}
		});

		createTask.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				JSONObject json1 = new JSONObject();
				ProjectxGlobalState glob_data = (ProjectxGlobalState) getApplication();
				try {
					json1.put("taskId", 0);
					json1.put("user", glob_data.getUserid());
					json1.put("projectId", project.getProject_id());
					json1.put("name", taskNameTextBox.getText());
					if (!(Parent.getSelectedItemPosition() == 0))
						json1.put("parentId", parenttasks.get(Parent.getSelectedItemPosition()).getTask_id());
					json1.put("description", taskAboutTextBox.getText());
					json1.put("createdBy", glob_data.getUserid());
					json1.put("duedate", taskDateTextBox.getText());
					if (!(status.equals("OPEN")))
						json1.put("assignee", memberList.get(Assignto.getSelectedItemPosition()).getMember_id());
					json1.put("status", status);
					json1.put("priority", Priority.getSelectedItem());

					Log.d("JSON string", json1.toString());
					ProgressDialog dialog = new ProgressDialog(cont);
					dialog.setMessage("Create Task...");
					CreateTask createTask = new CreateTask(cont, currentActivity, json1, dialog);
					createTask.execute("http://ec2-54-251-4-64.ap-southeast-1.compute.amazonaws.com/api/createTask_not.php");
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		});
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

		Object o = Assignto.getItemAtPosition(position);
		ProjectMembers selectedMember = (ProjectMembers) o;
		String Assignee = selectedMember.getUser_name();
		if (position != 0)
			status = "ASSIGNED";
		else
			status = "OPEN";
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}

	public class CreateTask extends AsyncTask<String, Void, String> {
		private final Context context;
		private final Activity callingActivity;
		private final ProgressDialog dialog;
		private final JSONObject requestJson;

		public CreateTask(Context context, Activity callingActivity, JSONObject requestData, ProgressDialog dialog) {
			this.context = context;
			this.callingActivity = callingActivity;
			this.requestJson = requestData;
			this.dialog = dialog;
		}

		@Override
		protected void onPreExecute() {
			System.out.println(this.dialog.isShowing());
			if (!(this.dialog.isShowing())) {
				this.dialog.show();
				this.dialog.setCanceledOnTouchOutside(false);
			}
		}

		@Override
		protected String doInBackground(String... urls) {
			String response = "";
			for (String url : urls) {
				HttpClient client = new DefaultHttpClient();
				HttpPut httpPut = new HttpPut(url);
				try {
					httpPut.setEntity(new StringEntity(requestJson.toString()));
					HttpResponse execute = client.execute(httpPut);
					InputStream content = execute.getEntity().getContent();
					Log.d("inside", requestJson.toString());
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
				if (resultJson.getString("msg").equals("success")) {
					// TODO : Check which activity to call
					int task_id = resultJson.getInt("task_id");
					Calendar cal = Calendar.getInstance(); // for using this you
					// need to import
					// java.util.Calendar;

					// add minutes to the calendar object
					/*
					 * cal.set(Calendar.MONTH, 4); cal.set(Calendar.YEAR, 2011);
					 * cal.set(Calendar.DAY_OF_MONTH, 5);
					 * cal.set(Calendar.HOUR_OF_DAY, 21);
					 * cal.set(Calendar.MINUTE, 43);
					 */

					// cal.set will set the alarm to trigger exactly at: 21:43,
					// 5
					// May 2011
					// if you want to trigger the alarm after let's say 5
					// minutes
					// after is activated you need to put
					cal.setTimeInMillis(System.currentTimeMillis());
					cal.add(Calendar.SECOND, 3);
					Intent alarmintent = new Intent(getApplicationContext(), AlarmReceiver.class);
					alarmintent.putExtra("title", "Title of our Notification");
					alarmintent.putExtra("note", "Description of our  Notification");
					alarmintent.putExtra("requestCode", task_id);
					// PendingIntent sender =
					// PendingIntent.getBroadcast(getApplicationContext(),
					// HELLO_ID,
					// alarmintent, PendingIntent.FLAG_UPDATE_CURRENT |
					// Intent.FILL_IN_DATA);
					PendingIntent sender = PendingIntent.getBroadcast(getApplicationContext(), task_id, alarmintent, 0);
					Log.d("RequestCode is ", " " + task_id);

					AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
					am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);

					context.startActivity(new Intent(context, homeActivity.class));
					callingActivity.finish();
				} else {
					Toast.makeText(context, "error in creation", Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				Toast.makeText(context, R.string.server_error, Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
		}
	}
}
