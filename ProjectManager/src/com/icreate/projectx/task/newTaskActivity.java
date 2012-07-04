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
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.icreate.projectx.AssigntoSpinnerBaseAdapter;
import com.icreate.projectx.R;
import com.icreate.projectx.homeActivity;
import com.icreate.projectx.datamodel.Project;
import com.icreate.projectx.datamodel.ProjectMembers;
import com.icreate.projectx.datamodel.ProjectxGlobalState;
import com.icreate.projectx.datamodel.Task;
import com.icreate.projectx.datepicker.DateSlider;
import com.icreate.projectx.datepicker.DefaultDateSlider;
import com.icreate.projectx.net.GetProjectTask;

public class newTaskActivity extends Activity {

	private EditText taskNameTextBox, taskAboutTextBox, taskDateTextBox, projectNameTextBox, parentTextBox;
	private TextView logoText, projNameView, taskNameView, taskDateView, taskAboutView, taskPriorityView;
	private DatePicker taskDate;
	private Button createTask;
	private ImageButton logoButton;
	private final List<ProjectMembers> memberList = new ArrayList<ProjectMembers>();
	private final List<String> prioriList = new ArrayList<String>();
	// private final List<String> Members = new ArrayList<String>();
	private final ArrayList<Task> parenttasks = new ArrayList<Task>();
	private Spinner Assignto, Priority, Parent;
	private ArrayAdapter<String> prioriAdapter;
	private ParentSpinnerBaseAdapter parentAdapter;
	private AssigntoSpinnerBaseAdapter memberAdapter;
	private String projectString;
	int parentId;
	int memberId;
	private Project project;
	private String status;
	static final int DEFAULTDATESELECTOR_ID = 0;

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

			memberId = extras.getInt("member", 0);
			parentId = extras.getInt("parent", 0);
		}
		System.out.println("member id in new task " + memberId);
		project = global.getProject();

		ProjectMembers dummyMember = new ProjectMembers(0);
		memberList.add(dummyMember);
		memberList.addAll(1, project.getMembers());
		System.out.println("size" + memberList.size());

		Typeface font = Typeface.createFromAsset(getAssets(), "EraserDust.ttf");

		if (project != null) {
			System.out.println("project name" + project.getProject_id() + " " + project.getProject_name());
		}

		taskNameTextBox = (EditText) findViewById(R.id.taskNameBox);
		taskAboutTextBox = (EditText) findViewById(R.id.taskAboutBox);
		taskDateTextBox = (EditText) findViewById(R.id.taskDeadlineBox);
		Assignto = (Spinner) findViewById(R.id.taskAssignedBox);
		createTask = (Button) findViewById(R.id.TaskButton);
		Priority = (Spinner) findViewById(R.id.taskPriorityBox);
		Parent = (Spinner) findViewById(R.id.taskParentBox);
		projectNameTextBox = (EditText) findViewById(R.id.project_TaskNameBox);
		logoText = (TextView) findViewById(R.id.logoText);
		logoButton = (ImageButton) findViewById(R.id.logoImageButton);
		projNameView = (TextView) findViewById(R.id.newTaskProjectNametext);
		taskNameView = (TextView) findViewById(R.id.newTaskNametext);
		taskAboutView = (TextView) findViewById(R.id.newTaskAbouttext);
		taskDateView = (TextView) findViewById(R.id.newTaskDeadlinetext);
		taskPriorityView = (TextView) findViewById(R.id.newTaskPrioritytext);

		logoText.setTypeface(font);
		logoButton.setBackgroundResource(R.drawable.home_button);
		logoText.setText("Create Task");
		projectNameTextBox.setTypeface(font);
		projectNameTextBox.setText(project.getProject_name());
		taskNameTextBox.setTypeface(font);
		taskAboutTextBox.setTypeface(font);
		taskDateTextBox.setTypeface(font);
		taskNameView.setTypeface(font);
		taskAboutView.setTypeface(font);
		taskDateView.setTypeface(font);
		projNameView.setTypeface(font);
		taskPriorityView.setTypeface(font);
		createTask.setTypeface(font);
		logoButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent HomeIntent = new Intent(cont, homeActivity.class);
				HomeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(HomeIntent);
				currentActivity.finish();

			}
		});

		Task dummyTask = new Task(0);

		parenttasks.add(dummyTask);
		for (int i = 0; i < project.getTasks().size(); i++) {

			parenttasks.add(project.getTasks().get(i));

		}
		parentAdapter = new ParentSpinnerBaseAdapter(cont, parenttasks);
		Parent.setAdapter(parentAdapter);

		memberAdapter = new AssigntoSpinnerBaseAdapter(cont, memberList, (ArrayList<Task>) project.getTasks());
		Assignto.setAdapter(memberAdapter);

		if (parentId != 0) {
			for (int i = 0; i < parenttasks.size(); i++) {
				if (parentId == parenttasks.get(i).getTask_id())
					Parent.setSelection(i);
			}
		}

		if (memberId != 0) {
			for (int i = 0; i < project.getMembers().size(); i++) {
				System.out.println("i am in member loop");
				if (memberId == project.getMembers().get(i).getMember_id()) {
					System.out.println("i am in member if loop");
					Assignto.setSelection(i + 1);
				}
			}
		}

		status = "OPEN";

		prioriList.add("Low");
		prioriList.add("Medium");
		prioriList.add("High");
		prioriList.add("Critical");

		prioriAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, prioriList);
		prioriAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Priority.setAdapter(prioriAdapter);

		taskDateTextBox.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(DEFAULTDATESELECTOR_ID);
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
					CreateTask createTask = new CreateTask(cont, currentActivity, json1);
					createTask.execute("http://ec2-54-251-4-64.ap-southeast-1.compute.amazonaws.com/api/createTask_not.php");
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		});

		Parent.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				parentAdapter.setSelectedPosition(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {

			}
		});

		Assignto.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				Object o = Assignto.getItemAtPosition(position);
				ProjectMembers selectedMember = (ProjectMembers) o;
				String Assignee = selectedMember.getUser_name();
				if (position != 0)
					status = "ASSIGNED";
				else
					status = "OPEN";
				memberAdapter.setSelectedPosition(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {

			}
		});
	}

	private final DateSlider.OnDateSetListener mDateSetListener = new DateSlider.OnDateSetListener() {
		@Override
		public void onDateSet(DateSlider view, Calendar selectedDate) {
			taskDateTextBox.setText(selectedDate.get(Calendar.YEAR) + "-" + (selectedDate.get(Calendar.MONTH) + 1) + "-" + selectedDate.get(Calendar.DATE));
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {

		switch (id) {
		case DEFAULTDATESELECTOR_ID:
			final Calendar c = Calendar.getInstance();
			return new DefaultDateSlider(this, mDateSetListener, c);
		}
		return null;
	}

	public class CreateTask extends AsyncTask<String, Void, String> {
		private final Context context;
		private final Activity callingActivity;
		private final JSONObject requestJson;

		public CreateTask(Context context, Activity callingActivity, JSONObject requestData) {
			this.context = context;
			this.callingActivity = callingActivity;
			this.requestJson = requestData;
		}

		@Override
		protected void onPreExecute() {
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
			System.out.println(result);
			try {
				JSONObject resultJson = new JSONObject(result);
				System.out.println(resultJson.toString());
				if (resultJson.getString("msg").equals("success")) {
					// TODO : Check which activity to call
					int projectId = project.getProject_id();
					int taskId = resultJson.getInt("task_id");
					String url = "http://ec2-54-251-4-64.ap-southeast-1.compute.amazonaws.com/api/getProject.php?project_id=" + projectId;
					ProgressDialog dialog = new ProgressDialog(context);
					dialog.setMessage("Creating Task...");
					dialog.show();
					GetProjectTask getProjectTask = new GetProjectTask(context, callingActivity, dialog, taskId, true);
					getProjectTask.execute(url);
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
