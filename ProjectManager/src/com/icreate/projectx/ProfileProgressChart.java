/**
 * Copyright (C) 2009, 2010 SC 4ViewSoft SRL
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.icreate.projectx;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer.Orientation;

import com.icreate.projectx.datamodel.Project;
import com.icreate.projectx.datamodel.ProjectxGlobalState;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;

/**
 * Sales demo bar chart.
 */
public class ProfileProgressChart extends AbstractDemoChart {

  /**
   * Returns the chart name.
   * 
   * @return the chart name
   */
  public String getName() {
    return "Sales horizontal bar chart";
  }

  /**
   * Returns the chart description.
   * 
   * @return the chart description
   */
  public String getDesc() {
    return "The monthly sales for the last 2 years (horizontal bar chart)";
  }

  /**
   * Executes the chart demo.
   * 
   * @param context the context
   * @return the built intent
   */
  public GraphicalView execute(Context context, List<Project> projects) {
	//ProjectxGlobalState global = (ProjectxGlobalState) getApplication();
    String[] titles = new String[] { "Total Tasks", "Completed Tasks" };
    List<double[]> values = new ArrayList<double[]>();
    /*List<Double> totalTasks = new ArrayList<Double>();
	List<Double> completedTasks = new ArrayList<Double>();
	List<String> projectNames = new ArrayList<String>();*/
	double[] tt = new double[projects.size()];
	double[] ct = new double[projects.size()];
	String[] name = new String[projects.size()];
	
	//totalTasks.add(MAX_TASK);
	//completedTasks.add(MAX_TASK);
	
    int[] colors = new int[] { Color.BLUE, Color.CYAN };
    XYMultipleSeriesRenderer renderer = buildBarRenderer(colors);
    renderer.setOrientation(Orientation.HORIZONTAL);
    setChartSettings(renderer, "Project Progress", "Projects", "No of Tasks", 0,
        projects.size(), 0, 15, Color.GRAY, Color.LTGRAY);
    
    for (int i=0; i< projects.size();i++) {
    	if(projects.get(i).getTotalTasks()!=0){
	    	tt[i]= projects.get(i).getTotalTasks();
			ct[i]=projects.get(i).getTasksCompleted();
			name[i]=projects.get(i).getProject_name();
			renderer.addXTextLabel(i+1, projects.get(i).getProject_name());
    	}
	}
	
    values.add(tt);
    values.add(ct);
    renderer.setBackgroundColor(Color.BLACK);
    renderer.setApplyBackgroundColor(true);
    renderer.setXLabels(1);
    renderer.setXLabelsAlign(Align.LEFT);
    renderer.setYLabels(10);
    renderer.setYLabelsAlign(Align.LEFT);
    renderer.setXLabelsColor(Color.YELLOW);
    /*renderer.addXTextLabel(1, "January and febraury");
    renderer.addXTextLabel(3, "March and APRIL");
    renderer.addXTextLabel(5, "May ND JUNE");
    renderer.addXTextLabel(7, "July ");
    renderer.addXTextLabel(10, "Oct");*/
      
    renderer.setAntialiasing(true);
    renderer.setXLabelsAngle(270f);
    renderer.setScale(0.5f);
    //renderer.setGridColor(Color.TRANSPARENT);
    renderer.setFitLegend(true);
    int length = renderer.getSeriesRendererCount();
    for (int i = 0; i < length; i++) {
      SimpleSeriesRenderer seriesRenderer = renderer.getSeriesRendererAt(i);
      seriesRenderer.setDisplayChartValues(true);
      seriesRenderer.setChartValuesTextSize(10f);
    }
    return ChartFactory.getBarChartView(context, buildBarDataset(titles, values), renderer, Type.STACKED);
    /*return ChartFactory.getBarChartIntent(context, buildBarDataset(titles, values), renderer,
        Type.DEFAULT);*/
  }
}
