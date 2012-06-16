package com.icreate.projectx;

import com.icreate.projectx.datamodel.ProjectxGlobalState;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class projectViewActivity extends Activity {

	private TextView ProjectName;
	private Button createTask;
	private int position;
	private ProjectxGlobalState globalState;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
	    setContentView(R.layout.projectview);
	    //getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.logo1);
	    	    
		final Context cont = this;
		final Activity currentActivity = this;
				
		createTask=(Button)findViewById(R.id.createNewTaskButton);
		
		Bundle extras = getIntent().getExtras();
		globalState = (ProjectxGlobalState)getApplication();
		if(extras!=null)
		{
			position=extras.getInt("position");
			System.out.println(position);
			
			Toast.makeText(cont, "" + globalState.getProjectList().getProjects().get(position).getProject_name(),
				Toast.LENGTH_LONG).show();
		}
		final ListView projectListView = (ListView) findViewById(R.id.memberProgressList);
		
		projectListView.setTextFilterEnabled(true);
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
