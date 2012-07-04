package com.icreate.projectx.task;

import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.icreate.projectx.R;
import com.icreate.projectx.homeActivity;
import com.icreate.projectx.datamodel.Project;
import com.icreate.projectx.datamodel.ProjectxGlobalState;
import com.icreate.projectx.datamodel.Task;

public class expandTaskViewActivity extends Activity {
	private TextView logoText;
	private ProjectxGlobalState globalState;
	private ListView task_projectListView;
	private Context cont;
	private AlertDialog alert;
	private Activity currentActivity;
	private String projectString;
	private Project project;
	private final ArrayList<Task> filteredTasks = new ArrayList<Task>();
	private TaskListBaseAdapter taskListBaseAdapter;

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

		Button projectTaskSearchButton = (Button) findViewById(R.id.projecttaskSearchButton);
		final TextView projectTaskSearch = (TextView) findViewById(R.id.projecttaskSearch);

		ImageButton homeButton = (ImageButton) findViewById(R.id.logoImageButton);
		homeButton.setBackgroundResource(R.drawable.home_button);

		homeButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(cont, homeActivity.class));
			}
		});

		final CharSequence[] items = { "Latest Due", "Assignee", "Priority" };

		AlertDialog.Builder builder = new AlertDialog.Builder(cont);
		builder.setTitle("Sort By");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				projectTaskSearch.setText("");
				switch (item) {
				case 0:
					Collections.sort(project.getTasks(), project.new TaskDueDateComparable());
					break;
				case 1:
					Collections.sort(project.getTasks(), project.new TaskAssigneeComparable());
					break;
				case 2:
					Collections.sort(project.getTasks(), project.new TaskPriorityComparable());
					break;
				}
				taskListBaseAdapter = new TaskListBaseAdapter(cont, (ArrayList<Task>) project.getTasks());
				task_projectListView.setAdapter(taskListBaseAdapter);
			}
		});
		alert = builder.create();

		task_projectListView = (ListView) findViewById(R.id.taskListView01);
		task_projectListView.setTextFilterEnabled(true);
		registerForContextMenu(task_projectListView);

		int project_id = 0;

		projectTaskSearch.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				int textLength2 = projectTaskSearch.getText().length();
				System.out.println(projectTaskSearch.getText());
				filteredTasks.clear();
				for (int i = 0; i < project.getTasks().size(); i++) {
					Log.d("YOLO", project.getTasks().get(i).getTask_name());
					if (textLength2 <= project.getTasks().get(i).getTask_name().length()) {
						if (projectTaskSearch.getText().toString().equalsIgnoreCase((String) project.getTasks().get(i).getTask_name().subSequence(0, textLength2))) {
							filteredTasks.add(project.getTasks().get(i));
						}
					}
				}
				taskListBaseAdapter = new TaskListBaseAdapter(cont, filteredTasks, (ArrayList<Task>) project.getTasks());
				task_projectListView.setAdapter(taskListBaseAdapter);
			}
		});

		projectTaskSearchButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (project != null)
					alert.show();
			}
		});

		task_projectListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Object o = task_projectListView.getItemAtPosition(position);
				Task selectedTask = (Task) o;
				Toast.makeText(cont, "You have chosen: " + " " + selectedTask.getTask_name() + " " + selectedTask.getTask_id() + " " + selectedTask.getAssignee(), Toast.LENGTH_LONG).show();
				Intent TaskViewIntent = new Intent(cont, TaskViewActivity.class);
				TaskViewIntent.putExtra("task_id", selectedTask.getTask_id());
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(projectTaskSearch.getWindowToken(), 0);
				startActivity(TaskViewIntent);
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		globalState = (ProjectxGlobalState) getApplication();
		project = globalState.getProject();
		for (int i = 0; i < project.getTasks().size(); i++) {
			if (project.getTasks().get(i).getAssignee() != 0) {
				for (int j = 0; j < project.getMembers().size(); j++) {
					if (project.getTasks().get(i).getAssignee() == project.getMembers().get(j).getMember_id())
						project.getTasks().get(i).setAssignee_name(project.getMembers().get(j).getUser_name());
				}
			}
		}
		taskListBaseAdapter = new TaskListBaseAdapter(cont, (ArrayList<Task>) project.getTasks());
		task_projectListView.setAdapter(taskListBaseAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.task_list_option_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.createtask:
			Intent newTaskIntent = new Intent(cont, newTaskActivity.class);
			startActivity(newTaskIntent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("Context Menu");
		menu.add(0, v.getId(), 0, "Delete");
		menu.add(0, v.getId(), 0, "Action 2");
	}

	// @Override
	/*
	 * public boolean onContextItemSelected(MenuItem item) {
	 * AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
	 * .getMenuInfo(); Task selectedTask = (Task) task_projectListView
	 * .getItemAtPosition(info.position);
	 * System.out.println(selectedTask.getProject_name() + " " +
	 * selectedTask.getTask_name()); if (item.getTitle() == "Delete") {
	 * TaskListBaseAdapter taskListBaseAdapter = (TaskListBaseAdapter)
	 * task_projectListView .getAdapter();
	 * 
	 * String url =
	 * "http://ec2-54-251-4-64.ap-southeast-1.compute.amazonaws.com/api/deleteProject.php?project_id="
	 * + selectedTask.getTask_id(); DeleteProjectTask deleteProjectTask = new
	 * DeleteProjectTask(cont, currentActivity, taskListBaseAdapter, info,
	 * selectedTask); deleteProjectTask.execute(url); return true; } else {
	 * System.out.println("blsldsdlflsfsdf"); } return false; }
	 */
}