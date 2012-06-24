package com.icreate.projectx;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.icreate.projectx.datamodel.Project;
import com.icreate.projectx.datamodel.ProjectMembers;
import com.icreate.projectx.datamodel.ProjectxGlobalState;
import com.icreate.projectx.datamodel.Task;

public class projectViewActivity extends Activity {
	private TextView logoText;
	private TextView ProjectName;
	private Button createTask, TaskView;
	private Button editProject;
	private ProjectxGlobalState globalState;
	private Project project;
	private List<ProjectMembers> memberList;
	private String projectString;

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

		Bundle extras = getIntent().getExtras();
		globalState = (ProjectxGlobalState) getApplication();
		if (extras != null) {
			projectString = extras.getString("projectJson", "");
			Log.d("sdcsd", projectString);
			System.out.println(projectString.isEmpty());
			if (!(projectString.isEmpty())) {
				Gson gson = new Gson();
				project = gson.fromJson(projectString, Project.class);
				Toast.makeText(cont, project.getProject_name(), Toast.LENGTH_LONG).show();
				logoText.setText(project.getProject_name());
				projDesc.setText(project.getProject_desc());
				memberList = project.getMembers();
				memberListView.setAdapter(new MemberProgressBaseAdapter(cont, memberList, (ArrayList<Task>) project.getTasks()));
			} else {
				Toast.makeText(cont, "Cannot load Project", Toast.LENGTH_LONG).show();
			}
		}

		memberListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Object o = memberListView.getItemAtPosition(position);
				ProjectMembers selectedMember = (ProjectMembers) o;
				double totaltasks = (Double) view.getTag(R.id.member_total_tasks);
				double totalcompletedtasks = (Double) view.getTag(R.id.member_total_tasks);
				double progress = (Double) view.getTag(R.id.member_progress);
				Toast.makeText(cont, "You have chosen: " + " " + selectedMember.getUser_name() + " " + selectedMember.getMember_id(), Toast.LENGTH_LONG).show();
				System.out.println(project.getTasks(selectedMember.getMember_id()) + " " + totaltasks + " " + totalcompletedtasks);
				Intent memberViewIntent = new Intent(cont, MemberViewActivity.class);
				memberViewIntent.putExtra("memberPosition", position);
				memberViewIntent.putExtra("project", projectString);
				memberViewIntent.putExtra("totaltasks", totaltasks);
				memberViewIntent.putExtra("totalcompletedtasks", totalcompletedtasks);
				memberViewIntent.putExtra("memberProgress", progress);
				startActivity(memberViewIntent);
			}
		});

		createTask.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent newTaskIntent = new Intent(cont, newTaskActivity.class);
				newTaskIntent.putExtra("project", projectString);
				startActivity(newTaskIntent);
			}
		});

		TaskView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent TaskViewIntent = new Intent(cont, expandTaskViewActivity.class);
				System.out.println("project" + projectString);
				TaskViewIntent.putExtra("project", projectString);
				startActivity(TaskViewIntent);

			}

		});
		editProject.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent editProjectIntent = new Intent(cont, editProjectActivity.class);
				editProjectIntent.putExtra("project_Id", project.getProject_id());
				startActivity(editProjectIntent);
			}
		});
	}
}
