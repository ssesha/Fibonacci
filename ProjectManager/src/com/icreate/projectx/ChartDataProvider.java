package com.icreate.projectx;

import static com.googlecode.charts4j.Color.BLACK;
import static com.googlecode.charts4j.Color.GOLD;
import static com.googlecode.charts4j.Color.RED;
import static com.googlecode.charts4j.Color.WHITE;
import static com.googlecode.charts4j.UrlUtil.normalize;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.googlecode.charts4j.AxisLabels;
import com.googlecode.charts4j.AxisLabelsFactory;
import com.googlecode.charts4j.AxisStyle;
import com.googlecode.charts4j.AxisTextAlignment;
import com.googlecode.charts4j.BarChart;
import com.googlecode.charts4j.BarChartPlot;
import com.googlecode.charts4j.Color;
import com.googlecode.charts4j.Data;
import com.googlecode.charts4j.DataUtil;
import com.googlecode.charts4j.Fills;
import com.googlecode.charts4j.GCharts;
import com.googlecode.charts4j.LinearGradientFill;
import com.googlecode.charts4j.Plots;
import com.icreate.projectx.datamodel.Project;

public class ChartDataProvider {
	public static String getBarChartUrl(List<Project> list) {

		/*
		 * List<Integer> totalTasks = new ArrayList<Integer>(); List<Integer>
		 * completedTasks = new ArrayList<Integer>(); List<String> projectNames
		 * = new ArrayList<String>(); for (Project projectItem : list) {
		 * totalTasks.add(projectItem.getTotalTasks());
		 * completedTasks.add(projectItem.getTasksCompleted());
		 * projectNames.add(""+projectItem.getProject_id()); } BarChartPlot
		 * team1 = Plots.newBarChartPlot(Data.newData(totalTasks),
		 * LIGHTGOLDENRODYELLOW, "Total Task"); BarChartPlot team2 =
		 * Plots.newBarChartPlot(Data.newData(completedTasks), ORANGERED,
		 * "Completed");
		 * 
		 * // int test = Color.newColor(LIGHTGOLDENRODYELLOW, 0); Color testing
		 * = Color.newColor(GREEN, 1); // new Color(1, 1, 1, 1); //
		 * Instantiating chart. BarChart chart = GCharts.newBarChart(team1,
		 * team2);
		 * 
		 * // Defining axis info and styles AxisStyle axisStyle =
		 * AxisStyle.newAxisStyle(BLACK, 13, AxisTextAlignment.CENTER);
		 * AxisLabels score = AxisLabelsFactory.newAxisLabels("Tasks", 20.0);
		 * score.setAxisStyle(axisStyle); AxisLabels year =
		 * AxisLabelsFactory.newAxisLabels("Projects", 20.0);
		 * year.setAxisStyle(axisStyle);
		 * 
		 * // Adding axis info to chart.
		 * chart.addXAxisLabels(AxisLabelsFactory.newAxisLabels(projectNames));
		 * chart.addYAxisLabels(AxisLabelsFactory .newNumericRangeAxisLabels(0,
		 * 5)); chart.addYAxisLabels(score); chart.addXAxisLabels(year);
		 * 
		 * chart.setSize(600, 450); chart.setBarWidth(10);
		 * chart.setSpaceWithinGroupsOfBars(5); chart.setDataStacked(true);
		 * chart.setTitle("Project Progress", BLACK, 16); chart.setGrid(100, 10,
		 * 3, 2); //chart.setBackgroundFill(Fills.newSolidFill(GREEN));
		 * chart.setBackgroundFill(Fills.newSolidFill(ALICEBLUE));
		 * LinearGradientFill fill = Fills.newLinearGradientFill(0, LAVENDER,
		 * 100); fill.addColorAndOffset(WHITE, 0); chart.setAreaFill(fill);
		 * chart.setAreaFill(Fills.newSolidFill(GREEN)); String url =
		 * chart.toURLString();
		 */
		final int MAX_TASK = 15;
		List<Integer> totalTasks = new ArrayList<Integer>();
		List<Integer> completedTasks = new ArrayList<Integer>();
		List<String> projectNames = new ArrayList<String>();
		// totalTasks.add(MAX_TASK);
		// completedTasks.add(MAX_TASK);
		for (Project projectItem : list) {
			totalTasks.add(projectItem.getTotalTasks());
			completedTasks.add(projectItem.getTasksCompleted());
			projectNames.add("" + projectItem.getProject_id());
		}
		Collections.reverse(projectNames);
		Data totalTasksData = DataUtil.scaleWithinRange(0, MAX_TASK, totalTasks);
		Data completedTaskData = DataUtil.scaleWithinRange(0, MAX_TASK, completedTasks);

		BarChartPlot total = Plots.newBarChartPlot(totalTasksData, GOLD, "Total");
		BarChartPlot completed = Plots.newBarChartPlot(completedTaskData, RED, "Completed");

		BarChart chart = GCharts.newBarChart(total, completed);

		// Defining axis info and styles
		AxisStyle axisStyle = AxisStyle.newAxisStyle(WHITE, 13, AxisTextAlignment.CENTER);
		AxisLabels project = AxisLabelsFactory.newAxisLabels("Projects", 50.0);
		project.setAxisStyle(axisStyle);
		AxisLabels projNames = AxisLabelsFactory.newAxisLabels(projectNames);
		projNames.setAxisStyle(axisStyle);
		AxisLabels tasks = AxisLabelsFactory.newAxisLabels("Tasks", 50.0);
		tasks.setAxisStyle(axisStyle);
		AxisLabels taskCount = AxisLabelsFactory.newNumericRangeAxisLabels(0, MAX_TASK);
		taskCount.setAxisStyle(axisStyle);

		// Adding axis info to chart.
		chart.addXAxisLabels(taskCount);
		chart.addXAxisLabels(tasks);
		chart.addYAxisLabels(projNames);
		chart.addYAxisLabels(project);
		chart.addTopAxisLabels(taskCount);
		chart.setHorizontal(true);
		chart.setSize(450, 650);
		chart.setDataStacked(true);
		chart.setSpaceBetweenGroupsOfBars(10);
		chart.setTitle("Project Progress", WHITE, 16);
		chart.setGrid((50.0 / MAX_TASK) * 20, 200, 3, 2);
		chart.setBackgroundFill(Fills.newSolidFill(BLACK));
		chart.setBarWidth(15);
		LinearGradientFill fill = Fills.newLinearGradientFill(0, Color.newColor("E37600"), 100);
		fill.addColorAndOffset(Color.newColor("DC4800"), 0);
		chart.setAreaFill(Fills.newSolidFill(BLACK));
		String url = chart.toURLString();
		return normalize(url);
	}
}
