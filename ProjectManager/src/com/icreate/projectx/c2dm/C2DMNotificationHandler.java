package com.icreate.projectx.c2dm;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;

import com.icreate.projectx.net.GetProjectTask;

public class C2DMNotificationHandler extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		int task_id = extras.getInt("task_id");
		int projectId = extras.getInt("project_id");		
		String url = "http://ec2-54-251-4-64.ap-southeast-1.compute.amazonaws.com/api/getProject.php?project_id=" + projectId;
		
		ProgressDialog dialog = new ProgressDialog(this);
		dialog.setMessage("Getting Project Info...");
		dialog.show();
		Log.d("C2DMHandler", "starting activity taskid ="+task_id+" projectid="+projectId);
		GetProjectTask getProjectTask = new GetProjectTask(this, this, dialog, task_id);
		getProjectTask.execute(url);
	}
}
