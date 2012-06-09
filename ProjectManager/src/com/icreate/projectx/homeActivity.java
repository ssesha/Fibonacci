package com.icreate.projectx;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;

import com.icreate.projectx.datamodel.ProjectxGlobalState;

public class homeActivity extends Activity {
	private ProjectxGlobalState globalData;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.home);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.logo2);

		globalData = (ProjectxGlobalState) getApplication();

		ImageButton newProjectButton = (ImageButton) findViewById(R.id.newProjectButton);
		Button myProjectButton = (Button) findViewById(R.id.myProjectButton);

		final Context cont = this;
		newProjectButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(cont, newProjectActivity.class));
			}
		});
		myProjectButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent projectListIntent = new Intent(cont, ProjectListActivity.class);
				String currentUserId = globalData.getUserid();
				if (!(currentUserId.isEmpty())) {
					projectListIntent.putExtra("requiredId", currentUserId);
				}
				System.out.println(currentUserId);
				startActivity(projectListIntent);
			}
		});

	}
}