package com.icreate.projectx;

import com.icreate.projectx.datamodel.ProjectxGlobalState;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class projectViewActivity extends Activity {

	private TextView ProjectName;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    setContentView(R.layout.projectview);
		final Context cont = this;
		final Activity currentActivity = this;
		
		ProjectName=(TextView)findViewById(R.id.projectName);
		Bundle extras = getIntent().getExtras();
		ProjectxGlobalState global = (ProjectxGlobalState)getApplication();
		int position=0;
		if(extras!=null)
		{
			position=extras.getInt("position");
			System.out.println(position);
			
			Toast.makeText(cont, "" + global.getProjectList().getProjects().get(position).getProject_name(),
				Toast.LENGTH_LONG).show();
		}
		
		ProjectName.setText(global.getProjectList().getProjects().get(position).getProject_name());
		//Log.d("project id", project_id);
		
	}

}
