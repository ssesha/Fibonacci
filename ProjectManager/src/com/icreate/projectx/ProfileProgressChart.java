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
	 * @param context
	 *            the context
	 * @return the built intent
	 */
	public GraphicalView execute(Context context, List<Project> projects,
			boolean preview) {
		// String[] titles = new String[] { "Total Tasks", "Completed Tasks" };
		List<double[]> values = new ArrayList<double[]>();
		double[] tt = new double[projects.size()];
		double[] ct = new double[projects.size()];
		String[] name = new String[projects.size()];

		int[] colors = new int[] { Color.YELLOW, Color.RED };
		XYMultipleSeriesRenderer renderer = buildBarRenderer(colors);
		renderer.setOrientation(Orientation.HORIZONTAL);
		setChartSettings(renderer, "Project Progress", "", "No of Tasks", 0,
				projects.size()+1, 0, 15, Color.WHITE, Color.TRANSPARENT);

		for (int i = 0; i < projects.size(); i++) {
			if (projects.get(i).getTotalTasks() != 0) {
				tt[i] = projects.get(i).getTotalTasks();
				ct[i] = projects.get(i).getTasksCompleted();
				name[i] = projects.get(i).getProject_name();
				renderer.addXTextLabel(i + 1, "       "
						+ projects.get(i).getProject_name());
			}
		}
		values.add(tt);
		values.add(ct);
		renderer.setApplyBackgroundColor(true);
		renderer.setMarginsColor(Color.rgb(16, 111, 81));
		renderer.setBackgroundColor(Color.rgb(16, 111, 81));
		renderer.setXLabels(1);
		renderer.setXLabelsAlign(Align.LEFT);
		renderer.setYLabels(10);
		renderer.setYLabelsAlign(Align.LEFT);
		renderer.setBarSpacing(0);
		renderer.setShowAxes(true);
		renderer.setScale(0.5f);
		String[] titles;
		if (preview) {
			renderer.setXLabelsColor(Color.TRANSPARENT);
			renderer.setXLabelsAngle(0f);
			renderer.setLabelsColor(Color.TRANSPARENT);
			renderer.setZoomEnabled(false);
			titles = new String[] { "Total", "Comp" };
			renderer.setShowLabels(true);
			renderer.setShowLegend(false);
			renderer.setLabelsTextSize(14);
			renderer.setChartTitleTextSize(14);
		} else {
			renderer.setXLabelsColor(Color.BLACK);
			renderer.setXLabelsAngle(270f);
			renderer.setLabelsColor(Color.WHITE);
			renderer.setZoomEnabled(true);
			renderer.setLabelsTextSize(24);
			renderer.setAxisTitleTextSize(24);
			renderer.setAntialiasing(true);
			renderer.setChartTitleTextSize(40);
			renderer.setLegendTextSize(24);
			renderer.setShowLabels(true);
			renderer.setShowLegend(true);
			renderer.setFitLegend(true);
			renderer.setLegendHeight(5);
			titles = new String[] { "Total Tasks", "Completed Tasks" };
		}
		renderer.setClickEnabled(true);
		renderer.setDisplayChartValues(true);
		int length = renderer.getSeriesRendererCount();
		for (int i = 0; i < length; i++) {
			SimpleSeriesRenderer seriesRenderer = renderer
					.getSeriesRendererAt(i);
			seriesRenderer.setDisplayChartValues(true);
			if (preview) 
				seriesRenderer.setChartValuesTextSize(10f);
			else
				seriesRenderer.setChartValuesTextSize(20f);
		}
		return ChartFactory.getBarChartView(context,
				buildBarDataset(titles, values), renderer, Type.STACKED);
	}
}
