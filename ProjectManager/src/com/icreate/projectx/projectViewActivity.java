package com.icreate.projectx;

import java.util.ArrayList;

import com.icreate.projectx.datamodel.Project;
import com.icreate.projectx.datamodel.ProjectxGlobalState;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class projectViewActivity extends Activity {
	private TextView logoText;
	private TextView ProjectName;
	private Button createTask;
	private int position;
	private ProjectxGlobalState globalState;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
	    setContentView(R.layout.projectview);
	    getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.logo1);
	    	    
		final Context cont = this;
		final Activity currentActivity = this;
		
		Typeface font = Typeface.createFromAsset(getAssets(), "EraserDust.ttf");
		logoText = (TextView) findViewById(R.id.logoText);
		logoText.setTypeface(font);
		logoText.setTextColor(R.color.white);
		
		
		createTask=(Button)findViewById(R.id.createNewTaskButton);
		TextView projDesc = (TextView) findViewById(R.id.projDesc);
		//projDesc.setText(globalState.getProjectList().getProjects().get(position).getProject_Desc());
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
		memberListView.setAdapter(new MemberProgressBaseAdapter(cont, memberList));
		
		Bundle extras = getIntent().getExtras();
		String passedUserId = null;
		globalState = (ProjectxGlobalState)getApplication();
		if(extras!=null)
		{
			logoText.setText(globalState.getProjectList().getProjects().get(position).getProject_name());
			
			position=extras.getInt("position");
			System.out.println(position);
			
			Toast.makeText(cont, "" + globalState.getProjectList().getProjects().get(position).getProject_name(),
				Toast.LENGTH_LONG).show();
		}
		
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
