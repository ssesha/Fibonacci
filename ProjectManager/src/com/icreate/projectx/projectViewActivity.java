package com.icreate.projectx;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.icreate.projectx.datamodel.Project;
import com.icreate.projectx.datamodel.ProjectxGlobalState;

public class projectViewActivity extends Activity {

	private TextView ProjectName;
	private Button createTask;
	private ProjectxGlobalState globalState;
	private Project project;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.projectview);
		// getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
		// R.layout.logo1);

		final Context cont = this;
		final Activity currentActivity = this;

		createTask = (Button) findViewById(R.id.createNewTaskButton);
		TextView projDesc = (TextView) findViewById(R.id.projDesc);
		// projDesc.setText(globalState.getProjectList().getProjects().get(position).getProject_Desc());
		projDesc.setText("Hello world. I am the first project in this awesome app!");

		final ListView memberListView = (ListView) findViewById(R.id.memberProgressList);
		memberListView.setTextFilterEnabled(true);
		registerForContextMenu(memberListView);
		ArrayList<String> memberList = new ArrayList<String>();
		memberList.add("Abbinayaa Subramanian");
		memberList.add("Achyut Balaji");
		memberList.add("Sesha Sendhil");
		memberList.add("Vandhanaa Lakshminarayanan");
		System.out.println(memberList.toString());
		memberListView.setAdapter(new MemberProgressBaseAdapter(cont,
				memberList));

		Bundle extras = getIntent().getExtras();
		globalState = (ProjectxGlobalState) getApplication();
		if (extras != null) {
			String projectString = extras.getString("projectJson", "");
			Log.d("sdcsd", projectString);
			System.out.println(projectString.isEmpty());
			if (!(projectString.isEmpty())) {
				Gson gson = new Gson();
				project = gson.fromJson(projectString, Project.class);
				Toast.makeText(cont, project.getProject_name(),
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(cont, "Cannot load Project", Toast.LENGTH_LONG)
						.show();
			}
		}

		createTask.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent newTaskIntent = new Intent(cont, newTaskActivity.class);
				newTaskIntent.putExtra("project_Id", project.getProject_id());
				startActivity(newTaskIntent);
			}
		});

	}
}
