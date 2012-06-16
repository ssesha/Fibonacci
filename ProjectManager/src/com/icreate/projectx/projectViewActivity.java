package com.icreate.projectx;

import com.icreate.projectx.datamodel.ProjectxGlobalState;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class projectViewActivity extends Activity {

	private TextView ProjectName;
	private Button createTask;
	private int position;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    setContentView(R.layout.projectview);
		final Context cont = this;
		final Activity currentActivity = this;
		
		ProjectName=(TextView)findViewById(R.id.projectName);
		createTask=(Button)findViewById(R.id.createNewTaskButton);
		
		Bundle extras = getIntent().getExtras();
		ProjectxGlobalState global = (ProjectxGlobalState)getApplication();
		if(extras!=null)
		{
			position=extras.getInt("position");
			System.out.println(position);
			
			Toast.makeText(cont, "" + global.getProjectList().getProjects().get(position).getProject_name(),
				Toast.LENGTH_LONG).show();
		}
		
		ProjectName.setText(global.getProjectList().getProjects().get(position).getProject_name());
		//Log.d("project id", project_id);
		
		createTask.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent newTaskIntent = new Intent(cont,
						newTaskActivity.class);
					newTaskIntent.putExtra("position",position );
				startActivity(newTaskIntent);
				
			}
			
		});
	}

}
