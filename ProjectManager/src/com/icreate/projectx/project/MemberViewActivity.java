package com.icreate.projectx.project;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.icreate.projectx.R;
import com.icreate.projectx.R.color;
import com.icreate.projectx.R.id;
import com.icreate.projectx.R.layout;
import com.icreate.projectx.datamodel.Project;
import com.icreate.projectx.datamodel.ProjectMembers;
import com.icreate.projectx.datamodel.ProjectxGlobalState;
import com.icreate.projectx.datamodel.Task;
import com.icreate.projectx.task.TaskListBaseAdapter;

public class MemberViewActivity extends Activity {
	private TextView logoText;
	private ProjectxGlobalState globalState;
	private Project project;
	private String projectString;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.memberview);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.logo1);

		final Context cont = this;
		final Activity currentActivity = this;

		Typeface font = Typeface.createFromAsset(getAssets(), "EraserDust.ttf");
		logoText = (TextView) findViewById(R.id.logoText);
		logoText.setTypeface(font);
		logoText.setTextColor(R.color.white);

		System.out.println("ok then");
		final ListView taskListView = (ListView) findViewById(R.id.assigneetasklist);
		taskListView.setTextFilterEnabled(true);
		registerForContextMenu(taskListView);

		Bundle extras = getIntent().getExtras();
		globalState = (ProjectxGlobalState) getApplication();
		if (extras != null) {
			int memberPosition = extras.getInt("memberPosition", -1);
			double memberProgress = extras.getDouble("memberProgress", -1);
			double totaltasks = extras.getDouble("totaltasks", -1);
			double totalcompletedtasks = extras.getDouble("totalcompletedtasks", -1);
			ProjectMembers currentMember = null;
			projectString = extras.getString("project", "");
			Log.d("sdcsd", projectString);
			System.out.println(projectString.isEmpty());
			if (!(projectString.isEmpty())) {
				Gson gson = new Gson();
				project = gson.fromJson(projectString, Project.class);
				Toast.makeText(cont, project.getProject_name(), Toast.LENGTH_LONG).show();
				currentMember = project.getMembers().get(memberPosition);
				if (currentMember != null) {
					logoText.setText(currentMember.getUser_name());
				}
				taskListView.setAdapter(new TaskListBaseAdapter(cont, (ArrayList<Task>) project.getTasks(currentMember.getMember_id())));
				System.out.println(project.getTasks().size());
			} else {
				Toast.makeText(cont, "Cannot load Project", Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void onContentChanged() {
		super.onContentChanged();

		View empty = findViewById(R.id.assigneetasklistempty);
		ListView list = (ListView) findViewById(R.id.assigneetasklist);
		list.setEmptyView(empty);
	}
}
