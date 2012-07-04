package com.icreate.projectx.project;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.GraphicalView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.icreate.projectx.IDemoChart;
import com.icreate.projectx.ProfileProgressChart;
import com.icreate.projectx.R;
import com.icreate.projectx.datamodel.Project;
import com.icreate.projectx.datamodel.ProjectxGlobalState;

public class ProjectChartActivity extends Activity {
	private Project project;
	private ProjectxGlobalState globalState;
	private LinearLayout chartLayout;
	private GraphicalView mChartView;
	public final static int SUCCESS_RETURN_CODE = 1;
	private List<Project> projects;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.chartview);
		int activity = getIntent().getIntExtra("activity", 666);
		
		globalState = (ProjectxGlobalState) getApplication();
		chartLayout = (LinearLayout) findViewById(R.id.full_chartview);
		if (activity == 1) {
			project = globalState.getProject();
			projects = new ArrayList<Project>();
			projects.add(project);
			IDemoChart mCharts = new ProjectProgressChart();
			mChartView = mCharts.execute(this, projects, false);			
			chartLayout.addView(mChartView, new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			mChartView.setClickable(true);
		} else if (activity == 0) {
			IDemoChart mCharts = new ProfileProgressChart();
			mChartView = mCharts.execute(this, globalState.getProjectList().getProjects(), false);
			chartLayout.addView(mChartView, new LayoutParams
					(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			mChartView.setClickable(true);
		}
		/*
		 * mChartView.setOnClickListener(new View.OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { Intent intent = new Intent();
		 * setResult(SUCCESS_RETURN_CODE, intent); finish(); } });
		 */
		// mChartView.setOnLongClickListener(l)
	}
}
