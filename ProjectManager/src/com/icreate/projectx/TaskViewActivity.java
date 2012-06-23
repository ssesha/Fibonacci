package com.icreate.projectx;

import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.icreate.projectx.MyHorizontalScrollView.SizeCallback;
import com.icreate.projectx.datamodel.Project;
import com.icreate.projectx.datamodel.ProjectxGlobalState;
import com.icreate.projectx.datamodel.Task;

public class TaskViewActivity extends Activity {

	private TextView logoText, TaskDesc, TaskDeadline;
	private ImageView slide;
	private ProjectxGlobalState globalState;
	private MyHorizontalScrollView scrollView;
	private ListView taskListView;
	private Context cont;
	private Activity currentActivity;
	private String projectString;
	private Project project;
	private Task task;
	private ArrayList<Task> subTasks;
	private View taskview, commentview;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.logo1);

		LayoutInflater inflater = LayoutInflater.from(this);
		scrollView = (MyHorizontalScrollView) inflater.inflate(R.layout.scrollview_comment, null);
		setContentView(scrollView);

		taskview = inflater.inflate(R.layout.taskview, null);
		slide = (ImageView) findViewById(R.id.BtnSlide);
		commentview = inflater.inflate(R.layout.task_commentview, null);
		cont = this;
		currentActivity = this;
		slide.setOnClickListener(new ClickListenerForScrolling(scrollView, taskview));

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

		final View[] children = new View[] { commentview, taskview };

		// Scroll to app (view[1]) when layout finished.
		int scrollToViewIdx = 1;
		scrollView.initViews(children, scrollToViewIdx, new SizeCallbackForMenu(slide));

		taskListView = (ListView) findViewById(R.id.subTaskList);
		TaskDesc = (TextView) findViewById(R.id.taskDesc);
		TaskDeadline = (TextView) findViewById(R.id.taskDeadline);
		taskListView.setTextFilterEnabled(true);
		registerForContextMenu(taskListView);

		Bundle extras = getIntent().getExtras();
		int task_id = 0;
		if (extras != null) {
			projectString = extras.getString("project");
			task_id = extras.getInt("task_id");
			logoText.setText("Tasks");
			System.out.println("project_idsdgfsdfrewsdfwfwesfrewf=" + projectString);
			Gson gson = new Gson();
			project = gson.fromJson(projectString, Project.class);
			ArrayList<Task> alltasks = (ArrayList<Task>) project.getTasks();
			subTasks = new ArrayList<Task>();
			for (int i = 0; i < alltasks.size(); i++) {
				if (alltasks.get(i).getTask_id() == task_id) {
					task = alltasks.get(i);
					break;
				}
			}
			logoText.setText(task.getTask_name());
			if (task.getDescription() != null) {
				TaskDesc.setText(task.getDescription());
			} else
				TaskDesc.setVisibility(View.GONE);
			TaskDeadline.setText(task.getDue_date());
			System.out.println(alltasks.size());
			int sub_taskid;
			for (int i = 0; i < task.getTopSubTasks().size(); i++) {
				sub_taskid = task.getTopSubTasks().get(i);
				System.out.println("sub task id" + sub_taskid);
				for (int j = 0; j < alltasks.size(); j++) {
					System.out.println("j" + j + "all task id" + alltasks.get(j).getTask_id());
					if (sub_taskid == alltasks.get(j).getTask_id()) {
						System.out.println("j inside " + j + "" + alltasks.get(j).getTask_name());
						subTasks.add(alltasks.get(j));
						System.out.println(subTasks.size());
						break;
					}

				}
			}

			taskListView.setAdapter(new TaskViewBaseAdapter(cont, subTasks));

		}

	};

	/**
	 * Helper for examples with a HSV that should be scrolled by a menu View's
	 * width.
	 */
	static class ClickListenerForScrolling implements OnClickListener {
		HorizontalScrollView scrollView;
		View menu;
		/**
		 * Menu must NOT be out/shown to start with.
		 */
		boolean menuOut = false;

		public ClickListenerForScrolling(HorizontalScrollView scrollView, View menu) {
			super();
			this.scrollView = scrollView;
			this.menu = menu;
		}

		public void onClick(View v) {
			Context context = menu.getContext();
			String msg = "Slide " + new Date();
			Toast.makeText(context, msg, 1000).show();
			System.out.println(msg);

			int menuWidth = menu.getMeasuredWidth();

			// Ensure menu is visible
			menu.setVisibility(View.VISIBLE);

			if (!menuOut) {
				// Scroll to 0 to reveal menu
				int left = 0;
				scrollView.smoothScrollTo(left, 0);
			} else {
				// Scroll to menuWidth so menu isn't on screen.
				int left = menuWidth;
				scrollView.smoothScrollTo(left, 0);
			}
			menuOut = !menuOut;
		}
	}

	/**
	 * Helper that remembers the width of the 'slide' button, so that the
	 * 'slide' button remains in view, even when the menu is showing.
	 */
	static class SizeCallbackForMenu implements SizeCallback {
		int btnWidth;
		View btnSlide;

		public SizeCallbackForMenu(View btnSlide) {
			super();
			this.btnSlide = btnSlide;
		}

		public void onGlobalLayout() {
			btnWidth = btnSlide.getMeasuredWidth();
			System.out.println("btnWidth=" + btnWidth);
		}

		public void getViewSize(int idx, int w, int h, int[] dims) {
			dims[0] = w;
			dims[1] = h;
			final int menuIdx = 0;
			if (idx == menuIdx) {
				dims[0] = w - btnWidth;
			}
		}
	}
}
