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
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.icreate.projectx.R;
import com.icreate.projectx.homeActivity;
import com.icreate.projectx.datamodel.Project;
import com.icreate.projectx.datamodel.ProjectxGlobalState;
import com.icreate.projectx.datamodel.Task;
import com.icreate.projectx.net.DeleteTask;
import com.icreate.projectx.net.GetProjectRefresh;

public class expandTaskViewActivity extends Activity {
	private TextView logoText;
	private ProjectxGlobalState globalState;
	private ListView task_projectListView;
	private PullToRefreshListView projectListViewWrapper;
	private Context cont;
	private AlertDialog alert;
	private Activity currentActivity;
	private String projectString;
	private Project project;
	private final ArrayList<Task> filteredTasks = new ArrayList<Task>();
	private TaskListBaseAdapter taskListBaseAdapter;
	private TextView projectTaskSearch;

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
		projectTaskSearch = (TextView) findViewById(R.id.projecttaskSearch);

		ImageButton homeButton = (ImageButton) findViewById(R.id.logoImageButton);
		homeButton.setBackgroundResource(R.drawable.home_button);

		homeButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent HomeIntent = new Intent(cont, homeActivity.class);
				HomeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(HomeIntent);
				currentActivity.finish();
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

		projectListViewWrapper = (PullToRefreshListView) findViewById(R.id.taskListView01);
		task_projectListView = projectListViewWrapper.getRefreshableView();
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
				globalState = (ProjectxGlobalState) getApplication();
				project = globalState.getProject();
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

		projectListViewWrapper.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {

				int projectId = project.getProject_id();
				String url = ProjectxGlobalState.urlPrefix + "getProject.php?project_id=" + projectId;
				globalState = (ProjectxGlobalState) getApplication();
				project = globalState.getProject();
				GetProjectRefresh getProjectTask = new GetProjectRefresh(cont, currentActivity, null, task_projectListView, projectListViewWrapper, project);
				getProjectTask.execute(url);
				projectTaskSearch.setText("");
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		projectTaskSearch.setText("");
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
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		final Task selectedTask = (Task) task_projectListView.getItemAtPosition(info.position);
		System.out.println(selectedTask.getTask_id() + " " + selectedTask.getTask_name());
		globalState = (ProjectxGlobalState) getApplication();
		project = globalState.getProject();
		if (item.getTitle() == "Delete") {
			if (selectedTask.getSubTasks().size() == 0) {
				System.out.println("deleting task" + selectedTask.getTask_id() + " " + selectedTask.getTask_name());
				String url = ProjectxGlobalState.urlPrefix + "deleteTask.php?task_id=" + selectedTask.getTask_id() + "&project_id=" + project.getProject_id();
				DeleteTask deletetask = new DeleteTask(cont, currentActivity, info, projectListViewWrapper, project.getProject_id(), task_projectListView, null, projectTaskSearch);
				deletetask.execute(url);
			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity);
				builder.setCancelable(true);
				builder.setTitle("Delete Task");
				builder.setMessage("Deleting the Task will delete all its subtasks.Are you sure you want to delete the task ?");
				builder.setInverseBackgroundForced(true);
				builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						System.out.println("deleting task" + selectedTask.getTask_id() + " " + selectedTask.getTask_name());
						String url = ProjectxGlobalState.urlPrefix + "deleteTask.php?task_id=" + selectedTask.getTask_id() + "&project_id=" + project.getProject_id();
						DeleteTask deletetask = new DeleteTask(cont, currentActivity, info, projectListViewWrapper, project.getProject_id(), task_projectListView, null, projectTaskSearch);
						deletetask.execute(url);
					}
				});
				builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
			}
			return true;
		} else {
			System.out.println("blsldsdlflsfsdf");
		}
		return false;
	}

}