package com.icreate.projectx;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.icreate.projectx.datamodel.Project;
import com.icreate.projectx.datamodel.ProjectxGlobalState;
import com.icreate.projectx.datamodel.Task;

public class TaskViewActivity extends Activity {

	private TextView logoText,TaskDesc,TaskDeadline;
	private ProjectxGlobalState globalState;
	private ListView taskListView;
	private Context cont;
	private Activity currentActivity;
	private String projectString;
	private Project project;
	private Task task;
	private ArrayList<Task> subTasks;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.taskview);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.logo1);

		cont = this;
		currentActivity = this;

		globalState = (ProjectxGlobalState) getApplication();

		Typeface font = Typeface.createFromAsset(getAssets(), "EraserDust.ttf");
		logoText = (TextView) findViewById(R.id.logoText);
		logoText.setTypeface(font);
		logoText.setTextColor(R.color.white);
		

		ImageButton homeButton = (ImageButton) findViewById(R.id.logoImageButton);
		homeButton.setBackgroundResource(R.drawable.home_button);

		homeButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				startActivity(new Intent(cont, homeActivity.class));

			}
		});

		taskListView = (ListView) findViewById(R.id.subTaskList);
		TaskDesc=(TextView) findViewById(R.id.taskDesc);
		TaskDeadline=(TextView) findViewById(R.id.taskDeadline);
		taskListView.setTextFilterEnabled(true);
		registerForContextMenu(taskListView);

		Bundle extras = getIntent().getExtras();
		int task_id = 0;
		if (extras != null) {
			projectString = extras.getString("project");
			task_id=extras.getInt("task_id");
			logoText.setText("Tasks");
			System.out.println("project_idsdgfsdfrewsdfwfwesfrewf="
					+ projectString);
			Gson gson = new Gson();
			project = gson.fromJson(projectString, Project.class);
			ArrayList<Task> alltasks=(ArrayList<Task>) project.getTasks();
			subTasks = new ArrayList<Task>();
			for(int i=0;i<alltasks.size();i++)
			{
				if(alltasks.get(i).getTask_id()==task_id)
				{
					task=alltasks.get(i);
					break;
				}
			}
			logoText.setText(task.getTask_name());
			if(task.getDescription()!=null)
			{
			TaskDesc.setText(task.getDescription());
			}
			else
				TaskDesc.setVisibility(View.GONE);
			TaskDeadline.setText(task.getDue_date());
			System.out.println(alltasks.size());
			int sub_taskid;
			for(int i=0;i<task.getTopSubTasks().size();i++)
			{
				sub_taskid=task.getTopSubTasks().get(i);
				System.out.println("sub task id"+ sub_taskid);
				for(int j=0;j<alltasks.size();j++)
				{
					System.out.println("j"+j+"all task id"+alltasks.get(j).getTask_id());
					if( sub_taskid== alltasks.get(j).getTask_id())
					{
						System.out.println("j inside "+j+""+alltasks.get(j).getTask_name());
						subTasks.add(alltasks.get(j));
						System.out.println(subTasks.size());
						break;
					}
					
				}
			}
			
			taskListView.setAdapter(new TaskViewBaseAdapter(cont,subTasks));
			
	
	}

};
}
