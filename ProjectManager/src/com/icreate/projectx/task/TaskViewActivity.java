package com.icreate.projectx.task;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.icreate.projectx.CommentBaseAdapter;
import com.icreate.projectx.MyHorizontalScrollView;
import com.icreate.projectx.MyHorizontalScrollView.SizeCallback;
import com.icreate.projectx.R;
import com.icreate.projectx.homeActivity;
import com.icreate.projectx.datamodel.Comment;
import com.icreate.projectx.datamodel.CommentList;
import com.icreate.projectx.datamodel.Project;
import com.icreate.projectx.datamodel.ProjectMembers;
import com.icreate.projectx.datamodel.ProjectxGlobalState;
import com.icreate.projectx.datamodel.Task;
import com.icreate.projectx.net.GetProjectTask;
import com.icreate.projectx.project.projectViewActivity;

public class TaskViewActivity extends Activity {

	private TextView logoText, TaskDesc, TaskDeadline, ProjectName, TaskName, TaskAssigneeName, TaskCreatorName, TaskStatus, TaskPriority;
	private ImageView slide;
	private Spinner statusSpinner;
	private EditText commentTextBox;
	private Button sendComment, createTask;
	private ProjectxGlobalState globalState;
	private MyHorizontalScrollView scrollView;
	private ListView taskListView, commentListView;
	private Context cont;
	private Activity currentActivity;
	private String projectString;
	private Project project;
	private Task task;
	private ArrayList<Task> subTasks;
	private final ArrayList<String> status = new ArrayList<String>();
	private View taskview, commentview, logoView;
	private ArrayList<Comment> comments;
	private Bundle extras;
	private Button parentTaskButton;
	private ArrayAdapter<String> dataAdapter;

	static boolean menuOut = false;
	int btnWidth, task_id = 0;
	boolean isFirst = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		LayoutInflater inflater = LayoutInflater.from(this);
		setContentView(inflater.inflate(R.layout.scrollview_comment, null));

		scrollView = (MyHorizontalScrollView) findViewById(R.id.myScrollView);
		taskview = inflater.inflate(R.layout.taskview, null);

		commentview = inflater.inflate(R.layout.task_commentview, null);
		cont = this;
		currentActivity = this;

		ViewGroup slidelayout = (ViewGroup) taskview.findViewById(R.id.slidelayout);
		slide = (ImageView) slidelayout.findViewById(R.id.rightlogoImageButtontaskview);
		slide.setOnClickListener(new ClickListenerForScrolling(scrollView, commentview));

		globalState = (ProjectxGlobalState) getApplication();

		Typeface font = Typeface.createFromAsset(getAssets(), "EraserDust.ttf");
		logoText = (TextView) taskview.findViewById(R.id.projectlogoText);
		logoText.setTypeface(font);
		logoText.setText("Task View");
		logoText.setSelected(true);

		ImageButton homeButton = (ImageButton) taskview.findViewById(R.id.projectlogoImageButton);
		homeButton.setBackgroundResource(R.drawable.home_button);

		homeButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(cont, homeActivity.class));
			}
		});

		taskListView = (ListView) taskview.findViewById(R.id.subTaskList);
		TaskDesc = (TextView) taskview.findViewById(R.id.taskDesc);
		TaskDeadline = (TextView) taskview.findViewById(R.id.taskDeadline);
		TaskAssigneeName = (TextView) taskview.findViewById(R.id.taskAssignedToView);
		TaskCreatorName = (TextView) taskview.findViewById(R.id.taskCreatedByView);
		TaskStatus = (TextView) taskview.findViewById(R.id.taskstatusView);
		statusSpinner = (Spinner) taskview.findViewById(R.id.taskstatusSpinner);
		TaskPriority = (TextView) taskview.findViewById(R.id.taskPriorityView);
		TaskName = (TextView) taskview.findViewById(R.id.taskNameTaskView);
		ProjectName = (TextView) taskview.findViewById(R.id.ProjectNameTaskView);

		commentListView = (ListView) commentview.findViewById(R.id.commentList);
		commentTextBox = (EditText) commentview.findViewById(R.id.commentTextBox);
		sendComment = (Button) commentview.findViewById(R.id.sendCommentButton);
		createTask = (Button) taskview.findViewById(R.id.createSubTaskButton);
		extras = getIntent().getExtras();

		if (extras != null) {
			task_id = extras.getInt("task_id");

			project = globalState.getProject();
			ArrayList<Task> alltasks = (ArrayList<Task>) project.getTasks();
			ArrayList<ProjectMembers> member = (ArrayList<ProjectMembers>) project.getMembers();
			subTasks = new ArrayList<Task>();
			for (int i = 0; i < alltasks.size(); i++) {
				if (alltasks.get(i).getTask_id() == task_id) {
					task = alltasks.get(i);
					break;
				}
			}
			status.add("OPEN");
			status.add("ASSIGNED");
			status.add("IN PROGRESS");
			status.add("COMPLETE");
			dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, status);
			/*
			 * {
			 * 
			 * @Override public boolean isEnabled(int position) { if (position
			 * == 0 || position == 1) { return false; } else { return true; } }
			 * 
			 * @Override public boolean areAllItemsEnabled() { return false; }
			 * }; ;
			 */

			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			statusSpinner.setAdapter(dataAdapter);

			for (int i = 0; i < status.size(); i++) {
				if (status.get(i).equalsIgnoreCase(task.getTask_status()))
					statusSpinner.setSelection(i);
			}

			System.out.println("Task details:" + task.getTask_id() + "" + task.getDescription() + "" + task.getDue_date());
			if (task.getDescription() != null) {
				TaskDesc.setText(task.getDescription());
			} else
				TaskDesc.setVisibility(View.GONE);
			TaskDeadline.setText(task.getDue_date());
			TaskName.setText(task.getTask_name());
			for (int i = 0; i < member.size(); i++) {
				if (!(task.getTask_status().equals("OPEN"))) {
					if (member.get(i).getMember_id() == task.getAssignee()) {
						TaskAssigneeName.setText(member.get(i).getUser_name());
					}
				} else
					TaskAssigneeName.setVisibility(View.GONE);
			}
			for (int i = 0; i < member.size(); i++) {
				if (member.get(i).getMember_id() == task.getCreatedBy()) {
					TaskCreatorName.setText(member.get(i).getUser_name());
				}
			}
			TaskPriority.setText(task.getTask_priority());
			TaskStatus.setText(task.getTask_status());
			if (!(task.getTask_status().equals("OPEN"))) {
				for (int i = 0; i < member.size(); i++) {
					System.out.println("check status" + member.get(i).getMember_id() + "  " + task.getAssignee());
					System.out.println("check user id" + globalState.getUserid() + " " + member.get(i).getUser_id());
					if (member.get(i).getMember_id() == task.getAssignee() && globalState.getUserid().equalsIgnoreCase(member.get(i).getUser_id()) && task.getSubTasks().size() == 0) {
						statusSpinner.setVisibility(View.VISIBLE);
						TaskStatus.setVisibility(View.GONE);
					}

				}
			}
			ProjectName.setText(task.getProject_name());
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

			taskListView.setAdapter(new subtaskBaseAdapter(cont, subTasks));

			String url = "http://ec2-54-251-4-64.ap-southeast-1.compute.amazonaws.com/api/commentList.php";
			List<NameValuePair> params = new LinkedList<NameValuePair>();
			params.add(new BasicNameValuePair("task_id", new Integer(task_id).toString()));
			String paramString = URLEncodedUtils.format(params, "utf-8");
			url += "?" + paramString;
			ProgressDialog dialog = new ProgressDialog(cont);
			dialog.setMessage("Getting Comments");
			ListComment ListComments = new ListComment(cont, currentActivity, dialog, commentListView);
			System.out.println(url);
			ListComments.execute(url);

		}

		// View transparent = new TextView(this);
		// transparent.setBackgroundColor(android.R.color.transparent);

		final View[] children = new View[] { commentview, taskview };

		// Scroll to app (view[1]) when layout finished.
		int scrollToViewIdx = 1;
		scrollView.initViews(children, scrollToViewIdx, new SizeCallbackForMenu(slide));
		System.out.println("Menu ScrollViewIdx + " + scrollToViewIdx);

		sendComment.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				JSONObject json1 = new JSONObject();
				JSONArray json_array = new JSONArray();
				try {
					json1.put("comment", commentTextBox.getText());
					json1.put("taskId", task_id);
					ProjectxGlobalState Gs = (ProjectxGlobalState) getApplication();
					System.out.println("createdby: " + Gs.getUserid());
					json1.put("createdBy", Gs.getUserid());

					Log.d("JSON string", json1.toString());
					ProgressDialog dialog = new ProgressDialog(cont);
					dialog.setMessage("Create Comments...");
					CreateCommentTask createCommentTask = new CreateCommentTask(cont, currentActivity, json1, dialog);
					createCommentTask.execute("http://ec2-54-251-4-64.ap-southeast-1.compute.amazonaws.com/api/createComment.php");

					/*
					 * String url =
					 * "http://ec2-54-251-4-64.ap-southeast-1.compute.amazonaws.com/api/commentList.php"
					 * ; List<NameValuePair> params = new
					 * LinkedList<NameValuePair>(); params.add(new
					 * BasicNameValuePair("task_id", new
					 * Integer(task_id).toString())); String paramString =
					 * URLEncodedUtils.format(params, "utf-8"); url += "?" +
					 * paramString; ProgressDialog dialog1 = new
					 * ProgressDialog(cont);
					 * dialog1.setMessage("Getting Comments"); ListComment
					 * ListComments = new ListComment(cont, currentActivity,
					 * dialog1, commentListView); System.out.println(url);
					 * ListComments.execute(url);
					 */

					commentTextBox.setText("");

				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		});

		parentTaskButton = (Button) taskview.findViewById(R.id.goToParent);
		parentTaskButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ArrayList<Task> alltasks = (ArrayList<Task>) project.getTasks();
				Intent parentTaskIntent;
				for (Task taskItem : alltasks) {
					if (taskItem.getTask_id() == extras.getInt("task_id")) {
						if (taskItem.getParentId() != 0) {
							parentTaskIntent = new Intent(cont, TaskViewActivity.class);
							Log.d("taskview to parent", "" + taskItem.getParentId());
							// parentTaskIntent.putExtra("task_id",
							// ""+taskItem.getParentId());
							parentTaskIntent.putExtra("task_id", taskItem.getParentId());
							startActivity(parentTaskIntent);
							currentActivity.finish();
						} else {
							parentTaskIntent = new Intent(cont, projectViewActivity.class);
							parentTaskIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(parentTaskIntent);
						}

					}
				}
			}
		});

		taskListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Object o = taskListView.getItemAtPosition(position);
				Task selectedTask = (Task) o;
				Toast.makeText(cont, "You have chosen: " + " " + selectedTask.getTask_name() + " " + selectedTask.getTask_id() + " " + position + " " + selectedTask.getAssignee_name(),
						Toast.LENGTH_LONG).show();
				Intent TaskViewIntent = new Intent(cont, TaskViewActivity.class);
				TaskViewIntent.putExtra("task_id", selectedTask.getTask_id());
				startActivity(TaskViewIntent);
				currentActivity.finish();
			}
		});

		statusSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				JSONObject json1 = new JSONObject();
				ProjectxGlobalState glob_data = (ProjectxGlobalState) getApplication();
				try {
					json1.put("taskId", task.getTask_id());
					json1.put("user", glob_data.getUserid());
					json1.put("projectId", project.getProject_id());
					json1.put("name", TaskName.getText());
					if (task.getParentId() != 0)
						json1.put("parentId", task.getParentId());
					json1.put("description", TaskDesc.getText());
					for (int i = 0; i < project.getMembers().size(); i++) {
						if (task.getCreatedBy() == project.getMembers().get(i).getMember_id())
							json1.put("createdBy", project.getMembers().get(i).getUser_id());
					}

					json1.put("duedate", TaskDeadline.getText());

					if (!(status.get(position).equals("OPEN")))
						json1.put("assignee", task.getAssignee());
					json1.put("status", status.get(position));
					json1.put("priority", TaskPriority.getText());

					Log.d("JSON string", json1.toString());
					ProgressDialog dialog = new ProgressDialog(cont);
					dialog.setMessage("Create Task...");
					CreateTask createTask = new CreateTask(cont, currentActivity, json1, dialog);
					createTask.execute("http://ec2-54-251-4-64.ap-southeast-1.compute.amazonaws.com/api/createTask_not.php");
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {

			}
		});

		createTask.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent NewTaskIntent = new Intent(cont, newTaskActivity.class);
				NewTaskIntent.putExtra("parent", task_id);
				startActivity(NewTaskIntent);
				currentActivity.finish();
			}
		});

		logoText.setSelected(true);

		logoText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				logoText.setSelected(true);
			}
		});

	};

	@Override
	public void onResume() {
		super.onResume();
		/*
		 * if (isFirst) { menuOut = true; slide.performClick(); } else { isFirst
		 * = true; }
		 */

		logoText.setFocusable(true);
		logoText.requestFocus();
		logoText.setFocusableInTouchMode(true);
	}

	public class CreateCommentTask extends AsyncTask<String, Void, String> {
		private final Context context;
		private final Activity callingActivity;
		private final ProgressDialog dialog;
		private final JSONObject requestJson;

		public CreateCommentTask(Context context, Activity callingActivity, JSONObject requestData, ProgressDialog dialog) {
			this.context = context;
			this.callingActivity = callingActivity;
			this.requestJson = requestData;
			this.dialog = dialog;
		}

		@Override
		protected void onPreExecute() {
			System.out.println(this.dialog.isShowing());
			if (!(this.dialog.isShowing())) {
				this.dialog.show();
			}
		}

		@Override
		protected String doInBackground(String... urls) {
			String response = "";
			for (String url : urls) {
				HttpClient client = new DefaultHttpClient();
				HttpPut httpPut = new HttpPut(url);
				try {
					httpPut.setEntity(new StringEntity(requestJson.toString()));
					HttpResponse execute = client.execute(httpPut);
					InputStream content = execute.getEntity().getContent();
					BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
					String s = "";
					while ((s = buffer.readLine()) != null) {
						response += s;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			if (this.dialog.isShowing()) {
				this.dialog.dismiss();
			}
			System.out.println(result);
			try {
				JSONObject resultJson = new JSONObject(result);
				System.out.println(resultJson.toString());
				if (resultJson.getString("msg").equals("success")) {
					// context.startActivity(new Intent(context,
					// homeActivity.class));
					String url = "http://ec2-54-251-4-64.ap-southeast-1.compute.amazonaws.com/api/commentList.php";
					List<NameValuePair> params = new LinkedList<NameValuePair>();
					params.add(new BasicNameValuePair("task_id", new Integer(task_id).toString()));
					String paramString = URLEncodedUtils.format(params, "utf-8");
					url += "?" + paramString;
					ProgressDialog dialog1 = new ProgressDialog(cont);
					dialog1.setMessage("Getting Comments");
					ListComment ListComments = new ListComment(context, callingActivity, dialog, commentListView);
					System.out.println(url);
					ListComments.execute(url);
				} else {
					Toast.makeText(context, R.string.login_error, Toast.LENGTH_LONG).show();
				}
				// callingActivity.finish();
			} catch (JSONException e) {
				Toast.makeText(context, R.string.server_error, Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
		}
	}

	/**
	 * Helper for examples with a HSV that should be scrolled by a menu View's
	 * width.
	 */
	private static class ClickListenerForScrolling implements OnClickListener {
		HorizontalScrollView scrollView;
		View menu;

		/**
		 * Menu must NOT be out/shown to start with.
		 */

		public ClickListenerForScrolling(HorizontalScrollView scrollView, View menu) {
			super();
			this.scrollView = scrollView;
			this.menu = menu;
		}

		@Override
		public void onClick(View v) {
			Context context = menu.getContext();
			String msg = "Slide " + new Date();
			Toast.makeText(context, msg, 1000).show();
			System.out.println(msg);

			int menuWidth = menu.getMeasuredWidth();
			System.out.println("Guiiiii" + menu.getMeasuredWidth() + " menuOut= " + menuOut);

			// Ensure menu is visible
			// if (menu.getVisibility() == View.INVISIBLE)
			menu.setVisibility(View.VISIBLE);
			// else
			// menu.setVisibility(View.INVISIBLE);

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
	private static class SizeCallbackForMenu implements SizeCallback {
		int btnWidth;
		View btnSlide;

		public SizeCallbackForMenu(View btnSlide) {
			super();
			this.btnSlide = btnSlide;
		}

		@Override
		public void onGlobalLayout() {
			btnWidth = btnSlide.getMeasuredWidth() + 50;
			System.out.println("btnWidth=" + btnWidth);
		}

		@Override
		public void getViewSize(int idx, int w, int h, int[] dims) {
			dims[0] = w;
			dims[1] = h;
			final int menuIdx = 0;
			if (idx == menuIdx) {
				dims[0] = w - btnWidth;
			}
		}
	}

	private class ListComment extends AsyncTask<String, Void, String> {
		private final Context context;
		private final Activity callingActivity;
		private final ProgressDialog dialog;
		private final ListView commentListView;

		public ListComment(Context context, Activity callingActivity, ProgressDialog dialog, ListView commentListView) {
			this.context = context;
			this.callingActivity = callingActivity;
			this.dialog = dialog;
			this.commentListView = commentListView;
		}

		@Override
		protected void onPreExecute() {
			if (!this.dialog.isShowing()) {
				this.dialog.setMessage("Getting Comments...");
				this.dialog.show();
			}
		}

		@Override
		protected String doInBackground(String... urls) {
			String response = "";
			for (String url : urls) {
				HttpClient client = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(url);
				try {
					HttpResponse execute = client.execute(httpGet);
					InputStream content = execute.getEntity().getContent();

					BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
					String s = "";
					while ((s = buffer.readLine()) != null) {
						response += s;
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			if (this.dialog.isShowing()) {
				this.dialog.dismiss();
			}
			System.out.println(result);
			try {
				JSONObject resultJson = new JSONObject(result);
				Log.d("CommentList", resultJson.toString());
				if (resultJson.getString("msg").equals("success")) {
					Gson gson = new Gson();
					CommentList commentsContainer = gson.fromJson(result, CommentList.class);
					globalState.setCommentList(commentsContainer);
					comments = commentsContainer.getComments();
					commentListView.setAdapter(new CommentBaseAdapter(context, comments));
					commentListView.setSelection(commentListView.getCount() - 1);
					Log.d("testing", "" + comments.size());
					for (Comment comment : comments) {
						Log.d("testing", "test test");
						System.out.println("creator id" + comment.getCreated_by());
						System.out.println("creator name" + comment.getCreator_name());
						System.out.println("comment name" + comment.getComment());
					}
				} else {
					Toast.makeText(context, "Comment Lists empty", Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				Toast.makeText(context, R.string.server_error, Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.task_view_option_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.edittask:
			Intent newTaskIntent = new Intent(cont, editTaskActivity.class);
			newTaskIntent.putExtra("task_id", task_id);
			startActivity(newTaskIntent);
			currentActivity.finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public class CreateTask extends AsyncTask<String, Void, String> {
		private final Context context;
		private final Activity callingActivity;
		private final ProgressDialog dialog;
		private final JSONObject requestJson;

		public CreateTask(Context context, Activity callingActivity, JSONObject requestData, ProgressDialog dialog) {
			this.context = context;
			this.callingActivity = callingActivity;
			this.requestJson = requestData;
			this.dialog = dialog;
		}

		@Override
		protected void onPreExecute() {
			System.out.println(this.dialog.isShowing());
			if (!(this.dialog.isShowing())) {
				this.dialog.show();
				this.dialog.setCanceledOnTouchOutside(false);
			}
		}

		@Override
		protected String doInBackground(String... urls) {
			String response = "";
			for (String url : urls) {
				HttpClient client = new DefaultHttpClient();
				HttpPut httpPut = new HttpPut(url);
				try {
					httpPut.setEntity(new StringEntity(requestJson.toString()));
					HttpResponse execute = client.execute(httpPut);
					InputStream content = execute.getEntity().getContent();
					Log.d("inside", requestJson.toString());
					BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
					String s = "";
					while ((s = buffer.readLine()) != null) {
						response += s;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			if (this.dialog.isShowing()) {
				this.dialog.dismiss();
			}
			System.out.println(result);
			try {
				JSONObject resultJson = new JSONObject(result);
				System.out.println(resultJson.toString());
				if (resultJson.getString("msg").equals("success")) {
					int projectId = project.getProject_id();
					int taskId = resultJson.getInt("task_id");
					String url = "http://ec2-54-251-4-64.ap-southeast-1.compute.amazonaws.com/api/getProject.php?project_id=" + projectId;
					ProgressDialog dialog = new ProgressDialog(context);
					dialog.setMessage("Updating Status...");
					dialog.show();
					GetProjectTask getProjectTask = new GetProjectTask(context, callingActivity, dialog, taskId, true);
					getProjectTask.execute(url);
				} else {
					Toast.makeText(context, "error in creation", Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				Toast.makeText(context, R.string.server_error, Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
		}
	}

}
