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
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.icreate.projectx.AssigntoSpinnerBaseAdapter;
import com.icreate.projectx.R;
import com.icreate.projectx.homeActivity;
import com.icreate.projectx.datamodel.Project;
import com.icreate.projectx.datamodel.ProjectMembers;
import com.icreate.projectx.datamodel.ProjectxGlobalState;
import com.icreate.projectx.datamodel.Task;
import com.icreate.projectx.datepicker.DateSlider;
import com.icreate.projectx.datepicker.DefaultDateSlider;

public class editTaskActivity extends Activity {
	private EditText taskNameTextBox, taskAboutTextBox, taskDateTextBox, projectNameTextBox, parentTextBox;
	private TextView logoText, projNameView, taskNameView, taskDateView, taskAboutView, taskPriorityView;
	private Button createTask;
	private ImageButton logoButton;
	private final List<ProjectMembers> memberList = new ArrayList<ProjectMembers>();
	private final List<String> prioriList = new ArrayList<String>();
	private final List<String> TaskName = new ArrayList<String>();
	// private final List<String> Members = new ArrayList<String>();
	private final ArrayList<Task> parenttasks = new ArrayList<Task>();
	private ArrayList<Task> tasklist = new ArrayList<Task>();
	private final ArrayList<Task> taskopenlist = new ArrayList<Task>();
	private Task thisTask;
	private Spinner Assignto, Priority, Parent, TaskNameSpinner;
	private ArrayAdapter<String> prioriAdapter, TaskAdapter;
	private ParentSpinnerBaseAdapter parentAdapter;
	private AssigntoSpinnerBaseAdapter memberAdapter;
	static final int DEFAULTDATESELECTOR_ID = 0;
	int parentId;
	int memberId;
	int task_id;
	int Assignee;
	private Project project;
	private String status;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
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
			task_id = extras.getInt("task_id", 0);

			Typeface font = Typeface.createFromAsset(getAssets(), "EraserDust.ttf");
			project = global.getProject();

			ProjectMembers dummyMember = new ProjectMembers(0);
			memberList.add(dummyMember);
			memberList.addAll(1, project.getMembers());
			System.out.println("size" + memberList.size());
			taskNameTextBox = (EditText) findViewById(R.id.taskNameBox);
			taskAboutTextBox = (EditText) findViewById(R.id.taskAboutBox);
			taskDateTextBox = (EditText) findViewById(R.id.taskDeadlineBox);
			Assignto = (Spinner) findViewById(R.id.taskAssignedBox);
			createTask = (Button) findViewById(R.id.TaskButton);
			Priority = (Spinner) findViewById(R.id.taskPriorityBox);
			Parent = (Spinner) findViewById(R.id.taskParentBox);
			TaskNameSpinner = (Spinner) findViewById(R.id.tasknamespinner);
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
			logoText.setText("Edit Task");
			projectNameTextBox.setText(project.getProject_name());
			projectNameTextBox.setTypeface(font);
			taskNameTextBox.setTypeface(font);
			taskAboutTextBox.setTypeface(font);
			taskDateTextBox.setTypeface(font);
			taskNameView.setTypeface(font);
			taskAboutView.setTypeface(font);
			taskDateView.setTypeface(font);
			projNameView.setTypeface(font);
			taskPriorityView.setTypeface(font);
			createTask.setText("Edit");
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
			status = "OPEN";

			prioriList.add("Low");
			prioriList.add("Medium");
			prioriList.add("High");
			prioriList.add("Critical");

			prioriAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, prioriList);
			prioriAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			Priority.setAdapter(prioriAdapter);

			if (memberId != 0) {
				for (int i = 0; i < project.getMembers().size(); i++) {
					if (memberId == project.getMembers().get(i).getMember_id())
						Assignto.setSelection(i + 1);
				}
				taskNameTextBox.setVisibility(View.GONE);
				TaskNameSpinner.setVisibility(View.VISIBLE);
				tasklist = (ArrayList<Task>) project.getTasks();

				TaskName.add("Choose Task Name");
				for (int i = 0; i < tasklist.size(); i++) {
					if (tasklist.get(i).getTask_status().equalsIgnoreCase("OPEN")) {
						TaskName.add(tasklist.get(i).getTask_name());
						taskopenlist.add(tasklist.get(i));
					}
				}
				TaskAdapter = new ArrayAdapter<String>(cont, android.R.layout.simple_spinner_dropdown_item, TaskName);
				TaskAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				TaskNameSpinner.setAdapter(TaskAdapter);

				taskAboutTextBox.setVisibility(View.GONE);
				taskDateTextBox.setVisibility(View.GONE);
				Priority.setVisibility(View.GONE);
				Parent.setVisibility(View.GONE);
				taskAboutView.setVisibility(View.GONE);
				taskDateView.setVisibility(View.GONE);
				taskPriorityView.setVisibility(View.GONE);

			} else if (task_id != 0) {
				tasklist = (ArrayList<Task>) project.getTasks();
				for (int i = 0; i < tasklist.size(); i++) {
					if (tasklist.get(i).getTask_id() == task_id)
						thisTask = tasklist.get(i);
				}
				taskNameTextBox.setText(thisTask.getTask_name());
				taskAboutTextBox.setText(thisTask.getDescription());
				taskDateTextBox.setText(thisTask.getDue_date());
				for (int i = 0; i < memberList.size(); i++) {
					if (memberList.get(i).getMember_id() == thisTask.getAssignee())
						Assignto.setSelection(i);
				}

				for (int i = 0; i < prioriList.size(); i++) {
					if (prioriList.get(i).equalsIgnoreCase(thisTask.getTask_priority()))
						Priority.setSelection(i);
				}
				if (thisTask.getParentId() == 0)
					Parent.setSelection(0);
				else {
					for (int i = 0; i < parenttasks.size(); i++) {
						if (parenttasks.get(i).getTask_id() == thisTask.getParentId()) {
							Parent.setSelection(i);
							System.out.println("parent" + thisTask.getParentId());
						}

					}
				}

			}

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
						if (thisTask != null) {
							json1.put("taskId", thisTask.getTask_id());
							json1.put("user", glob_data.getUserid());
							json1.put("projectId", project.getProject_id());
							json1.put("name", taskNameTextBox.getText());
							if (!(Parent.getSelectedItemPosition() == 0))
								json1.put("parentId", parenttasks.get(Parent.getSelectedItemPosition()).getTask_id());
							json1.put("description", taskAboutTextBox.getText());
							for (int i = 0; i < project.getMembers().size(); i++) {
								if (thisTask.getCreatedBy() == project.getMembers().get(i).getMember_id())
									json1.put("createdBy", project.getMembers().get(i).getUser_id());
							}

							json1.put("duedate", taskDateTextBox.getText());

							if (!(status.equals("OPEN")))
								json1.put("assignee", Assignee);
							System.out.println("assignee=" + Assignee);
							json1.put("status", status);
							json1.put("priority", Priority.getSelectedItem());
							ProgressDialog dialog = new ProgressDialog(cont);
							dialog.setMessage("Editing Task...");
							dialog.setCancelable(false);
							dialog.setCanceledOnTouchOutside(false);
							dialog.show();
							Log.d("JSON string", json1.toString());
							CreateTask createTask = new CreateTask(cont, currentActivity, json1, dialog);
							createTask.execute(ProjectxGlobalState.urlPrefix + "createTask_not.php");
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}

				}
			});

			TaskNameSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
					if (position == 0) {
						taskAboutTextBox.setText("");
						taskDateTextBox.setText("");
						thisTask = null;
						Priority.setSelection(0);
						Parent.setSelection(0);
						taskAboutTextBox.setVisibility(View.GONE);
						taskAboutView.setVisibility(View.GONE);
						taskDateView.setVisibility(View.GONE);
						taskDateTextBox.setVisibility(View.GONE);
						Priority.setVisibility(View.GONE);
						Parent.setVisibility(View.GONE);
						taskPriorityView.setVisibility(View.GONE);
					} else {
						taskAboutTextBox.setVisibility(View.VISIBLE);
						taskDateTextBox.setVisibility(View.VISIBLE);
						Priority.setVisibility(View.VISIBLE);
						Parent.setVisibility(View.VISIBLE);
						taskAboutView.setVisibility(View.VISIBLE);
						taskPriorityView.setVisibility(View.VISIBLE);
						taskDateView.setVisibility(View.VISIBLE);
						thisTask = taskopenlist.get(position - 1);
						taskNameTextBox.setText(taskopenlist.get(position - 1).getTask_name());
						taskAboutTextBox.setText(taskopenlist.get(position - 1).getDescription());
						taskDateTextBox.setText(taskopenlist.get(position - 1).getDue_date());
						for (int i = 0; i < prioriList.size(); i++) {
							if (prioriList.get(i).equalsIgnoreCase(taskopenlist.get(position - 1).getTask_priority()))
								Priority.setSelection(i);
						}
						if (taskopenlist.get(position - 1).getParentId() == 0)
							Parent.setSelection(0);
						else {
							for (int i = 0; i < parenttasks.size(); i++) {
								System.out.println("in for" + parenttasks.get(i).getTask_id() + "parent" + taskopenlist.get(position - 1).getParentId());
								if (parenttasks.get(i).getTask_id() == taskopenlist.get(position - 1).getParentId()) {
									Parent.setSelection(i);
									System.out.println("parent" + taskopenlist.get(position - 1).getParentId());
								}

							}
						}
					}

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
					Assignee = selectedMember.getMember_id();
					if (position != 0)
						status = "ASSIGNED";
					else
						status = "OPEN";
				}

				@Override
				public void onNothingSelected(AdapterView<?> parentView) {

				}
			});

		}
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
		private final ProgressDialog dialog;

		public CreateTask(Context context, Activity callingActivity, JSONObject requestData, ProgressDialog dialog) {
			this.context = context;
			this.callingActivity = callingActivity;
			this.requestJson = requestData;
			this.dialog = dialog;
		}

		@Override
		protected void onPreExecute() {
			if (!this.dialog.isShowing()) {
				this.dialog.setMessage("Logging in...");
				this.dialog.show();
				this.dialog.setCanceledOnTouchOutside(false);
				this.dialog.setCancelable(false);
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

			try {
				JSONObject resultJson = new JSONObject(result);

				if (resultJson.getString("msg").equals("success")) {
					int projectId = project.getProject_id();
					int taskId = resultJson.getInt("task_id");
					Gson gson = new Gson();
					Project project = gson.fromJson(resultJson.getString("projectString"), Project.class);
					System.out.println(project.getProject_name());
					ProjectxGlobalState globalState = (ProjectxGlobalState) callingActivity.getApplication();
					globalState.setProject(project);
					Intent TaskViewIntent = new Intent(context, TaskViewActivity.class);
					TaskViewIntent.putExtra("task_id", taskId);
					callingActivity.startActivity(TaskViewIntent);
					callingActivity.finish();
				} else {

				}
			} catch (JSONException e) {

				e.printStackTrace();
			}
		}
	}

}
