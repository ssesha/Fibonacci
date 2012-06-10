package com.icreate.projectx;

import com.icreate.projectx.datamodel.ProjectxGlobalState;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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
		
		ProjectName=(TextView)findViewById(R.id.projectname);
		Bundle extras = getIntent().getExtras();
		
		int project_id=0;
		if(extras!=null)
		{
			project_id=extras.getInt("projectid");
			//System.out.println(project_id);
			Toast.makeText(cont, "" + project_id,
					Toast.LENGTH_LONG).show();
			ProjectxGlobalState global = new ProjectxGlobalState();
			
			//ProjectName.setText(""+ project_id);
		}
		//Log.d("project id", project_id);
		
	}

}
