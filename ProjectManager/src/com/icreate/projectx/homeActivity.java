package com.icreate.projectx;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.icreate.projectx.datamodel.ProjectxGlobalState;
import com.icreate.projectx.project.ProjectListActivity;
import com.icreate.projectx.project.newProjectActivity;
import com.icreate.projectx.task.TaskListActivity;

public class homeActivity extends Activity {
	private ProjectxGlobalState globalData;
	private TextView logoText;
	private ImageButton logoButton, myProjectButton, myTaskButton, logoutButton, profileButton, calendarButton, findButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.home);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.logo1);

		globalData = (ProjectxGlobalState) getApplication();
		final Context cont = this;
		Typeface font = Typeface.createFromAsset(getAssets(), "EraserDust.ttf");

		logoButton = (ImageButton) findViewById(R.id.logoImageButton);
		logoText = (TextView) findViewById(R.id.logoText);

		profileButton = (ImageButton) findViewById(R.id.profileButton);
		myProjectButton = (ImageButton) findViewById(R.id.myProjectButton);
		myTaskButton = (ImageButton) findViewById(R.id.myTaskButton);
		logoutButton = (ImageButton) findViewById(R.id.logoutButton);

		logoText.setTypeface(font);
		logoText.setText("Project-X");
		logoButton.setBackgroundResource(R.drawable.newprojectbutton);

		logoButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(cont, newProjectActivity.class));
			}
		});

		profileButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(cont, ProfileActivity.class));
			}
		});

		myProjectButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent projectListIntent = new Intent(cont, ProjectListActivity.class);
				String currentUserId = globalData.getUserid();
				if (!(currentUserId.isEmpty())) {
					projectListIntent.putExtra("requiredId", currentUserId);
					startActivity(projectListIntent);
				}
			}
		});

		myTaskButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent TaskListIntent = new Intent(cont, TaskListActivity.class);
				String currentUserId = globalData.getUserid();
				if (!(currentUserId.isEmpty())) {
					TaskListIntent.putExtra("requiredId", currentUserId);
					startActivity(TaskListIntent);
				}
			}
		});

		logoutButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ProjectXPreferences.getEditor(cont).clear().commit();
				Intent HomeIntent = new Intent(cont, ProjectManagerActivity.class);
				HomeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(HomeIntent);
				finish();
			}
		});
	}
}
