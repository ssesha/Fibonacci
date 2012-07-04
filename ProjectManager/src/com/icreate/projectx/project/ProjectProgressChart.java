package com.icreate.projectx.project;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer.Orientation;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;

import com.icreate.projectx.AbstractDemoChart;
import com.icreate.projectx.datamodel.Project;

public class ProjectProgressChart extends AbstractDemoChart {
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

		List<double[]> values = new ArrayList<double[]>();
		double[] tt = new double[4];
		double[] ct = new double[4];
		String[] name = new String[4];

		int[] colors = new int[] { Color.YELLOW, Color.RED };
		XYMultipleSeriesRenderer renderer = buildBarRenderer(colors);
		renderer.setOrientation(Orientation.HORIZONTAL);
		setChartSettings(renderer, "Project Progress", "",
				"No of Tasks", 0, 5, 0, 15, Color.WHITE, Color.TRANSPARENT);

		tt[0] = projects.get(0).getTaskLow();
		ct[0] = projects.get(0).getTaskLowComplete();
		tt[1] = projects.get(0).getTaskMedium();
		ct[1] = projects.get(0).getTaskMediumComplete();
		tt[2] = projects.get(0).getTaskHigh();
		ct[2] = projects.get(0).getTaskHighComplete();
		tt[3] = projects.get(0).getTaskCritical();
		ct[3] = projects.get(0).getTaskCriticalComplete();

		values.add(tt);
		values.add(ct);
		renderer.setApplyBackgroundColor(true);
		renderer.setMarginsColor(Color.rgb(16, 111, 81));
		renderer.setBackgroundColor(Color.rgb(16, 111, 81));
		renderer.setXLabels(1);
		renderer.setXLabelsAlign(Align.LEFT);
		renderer.setYLabels(10);
		renderer.setYLabelsAlign(Align.LEFT);

		renderer.setBarSpacing(1);
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
			renderer.addXTextLabel(0, " ");
			renderer.addXTextLabel(5, " ");
			renderer.addXTextLabel(1, "      Low");
			renderer.addXTextLabel(2, "      Medium");
			renderer.addXTextLabel(3, "      High");
			renderer.addXTextLabel(4, "      Critical");
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
			if (preview) 
				seriesRenderer.setChartValuesTextSize(10f);
			else
				seriesRenderer.setChartValuesTextSize(20f);
		}
		return ChartFactory.getBarChartView(context,
				buildBarDataset(titles, values), renderer, Type.STACKED);
	}
}
