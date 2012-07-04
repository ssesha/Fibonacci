package com.icreate.projectx.project;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.achartengine.GraphicalView;
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
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.icreate.projectx.CommentBaseAdapter;
import com.icreate.projectx.IDemoChart;
import com.icreate.projectx.MemberProgressBaseAdapter;
import com.icreate.projectx.MyHorizontalScrollView;
import com.icreate.projectx.ProfileProgressChart;
import com.icreate.projectx.MyHorizontalScrollView.SizeCallback;
import com.icreate.projectx.R;
import com.icreate.projectx.homeActivity;
import com.icreate.projectx.datamodel.ActivityFeed;
import com.icreate.projectx.datamodel.Comment;
import com.icreate.projectx.datamodel.Notification;
import com.icreate.projectx.datamodel.Project;
import com.icreate.projectx.datamodel.ProjectMembers;
import com.icreate.projectx.datamodel.ProjectxGlobalState;
import com.icreate.projectx.datamodel.Task;
import com.icreate.projectx.task.expandTaskViewActivity;
import com.icreate.projectx.task.newTaskActivity;

public class projectViewActivity extends Activity {
	private TextView logoText;
	private TextView ProjectName, projDesc, projDeadline;
	private EditText typeComment;
	private Button createTask, TaskView;
	private Button editProject, postComment;
	private ProjectxGlobalState globalState;
	private Project project;
	private List<ProjectMembers> memberList;
	private String projectString;
	private MyHorizontalScrollView scrollView;
	private final ArrayList<String> commentList = new ArrayList<String>();
	private ArrayList<Comment> comments;
	private final ArrayList<String> activitites = new ArrayList<String>();
	private ArrayList<Notification> activityFeed;
	static boolean menuOut = false;
	Handler handler = new Handler();
	int btnWidth, task_id = 0;
	private View projectView, commentView, logoView;
	private ImageView slide;
	private PullToRefreshListView activitiesWrapper, commentlistWrapper;
	private ListView activities, memberListView;
	private ListView commentlist;
	boolean isFirst = false;
	Context cont;
	Activity currentActivity;
	private LinearLayout chartLayout;
	private GraphicalView mChartView;
	private final int subActivityID = 53769;
	private Intent chartIntent;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		// setContentView(R.layout.projectview);
		// getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
		// R.layout.logo1);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		cont = this;
		currentActivity = this;
		chartIntent = new Intent(cont, ProjectChartActivity.class);
		chartIntent.putExtra("activity", 1);
		LayoutInflater inflater = LayoutInflater.from(this);
		setContentView(inflater.inflate(R.layout.scrollview_comment, null));

		scrollView = (MyHorizontalScrollView) findViewById(R.id.myScrollView);
		logoView = inflater.inflate(R.layout.logo1, null);
		projectView = inflater.inflate(R.layout.projectview, null);
		commentView = inflater.inflate(R.layout.projectextension, null);

		Typeface font = Typeface.createFromAsset(getAssets(), "EraserDust.ttf");
		logoText = (TextView) projectView.findViewById(R.id.projectlogoText);
		logoText.setTypeface(font);
		logoText.setSelected(true);

		ImageButton homeButton = (ImageButton) projectView.findViewById(R.id.projectlogoImageButton);
		homeButton.setBackgroundResource(R.drawable.home_button);
		
		homeButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(cont, homeActivity.class));
			}
		});

		ViewGroup slidelayout = (ViewGroup) projectView.findViewById(R.id.slidelayout_proj);
		slide = (ImageView) slidelayout.findViewById(R.id.BtnSlide_proj);
		slide.setOnClickListener(new ClickListenerForScrolling(scrollView, commentView));

		createTask = (Button) projectView.findViewById(R.id.createNewTaskButton);
		TaskView = (Button) projectView.findViewById(R.id.taskListButton);
		projDesc = (TextView) projectView.findViewById(R.id.projDesc);

		projDeadline = (TextView) projectView.findViewById(R.id.projectDeadline);
		projDesc.setTypeface(font);
		projDeadline.setTypeface(font);
		editProject = (Button) projectView.findViewById(R.id.editProjectButton);
		// projDesc.setText(globalState.getProjectList().getProjects().get(position).getProject_Desc());

		memberListView = (ListView) projectView.findViewById(R.id.memberProgressList);
		memberListView.setTextFilterEnabled(true);
		registerForContextMenu(memberListView);
		activitiesWrapper = (PullToRefreshListView) commentView.findViewById(R.id.activity);
		commentlistWrapper = (PullToRefreshListView) commentView.findViewById(R.id.proj_comments);
		activities = activitiesWrapper.getRefreshableView();
		commentlist = commentlistWrapper.getRefreshableView();
		postComment = (Button) commentView.findViewById(R.id.proj_sendCommentButton);
		typeComment = (EditText) commentView.findViewById(R.id.proj_commentTextBox);
		chartLayout = (LinearLayout) projectView.findViewById(R.id.project_chartlayout);
		
		TabHost tabHost = (TabHost) commentView.findViewById(R.id.tabhost);
		tabHost.setup();
		TabSpec activityspec = tabHost.newTabSpec("Activities");
		activityspec.setIndicator("Activitites", getResources().getDrawable(R.drawable.bulb));
		activityspec.setContent(R.id.activity);
		TabSpec commentsspec = tabHost.newTabSpec("Comments");
		commentsspec.setIndicator("Comments", getResources().getDrawable(R.drawable.dustbin));
		commentsspec.setContent(R.id.project_commentviewlayout);
		
		/*TabSpec chartsspec = tabHost.newTabSpec("Charts");
		chartsspec.setIndicator("Charts", getResources().getDrawable(R.drawable.dustbin));
		chartsspec.setContent(R.id.project_chartlayout);*/
		
		tabHost.addTab(activityspec); 
		tabHost.addTab(commentsspec);
		//tabHost.addTab(chartsspec);
		typeComment.setText("");

		final View[] children = new View[] { commentView, projectView };
		int scrollToViewIdx = 1;
		scrollView.initViews(children, scrollToViewIdx, new SizeCallbackForMenu(slide));
		memberListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Object o = memberListView.getItemAtPosition(position);
				ProjectMembers selectedMember = (ProjectMembers) o;
				double totaltasks = (Double) view.getTag(R.id.member_total_tasks);
				double totalcompletedtasks = (Double) view.getTag(R.id.member_total_completed_tasks);
				double progress = (Double) view.getTag(R.id.member_progress);
				Toast.makeText(cont, "You have chosen: " + " " + selectedMember.getUser_name() + " " + selectedMember.getMember_id(), Toast.LENGTH_LONG).show();
				System.out.println("oi oi test" + project.getTasks(selectedMember.getMember_id()) + " " + totaltasks + " " + totalcompletedtasks + " " + progress);
				Intent memberViewIntent = new Intent(cont, MemberViewActivity.class);
				memberViewIntent.putExtra("memberPosition", position);
				memberViewIntent.putExtra("totaltasks", totaltasks);
				memberViewIntent.putExtra("totalcompletedtasks", totalcompletedtasks);
				memberViewIntent.putExtra("memberProgress", progress);
				startActivity(memberViewIntent);
			}
		});
		
		createTask.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent newTaskIntent = new Intent(cont, newTaskActivity.class);
				startActivity(newTaskIntent);
			}
		});

		TaskView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent TaskViewIntent = new Intent(cont, expandTaskViewActivity.class);
				startActivity(TaskViewIntent);
			}

		});
		editProject.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent editProjectIntent = new Intent(cont, newProjectActivity.class);
				editProjectIntent.putExtra("flag", 1);
				startActivity(editProjectIntent);
				currentActivity.finish();
			}
		});

		commentlistWrapper.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				pullltoRefreshCommentsActivity();
			}
		});

		activitiesWrapper.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				pullltoRefreshCommentsActivity();
			}
		});

		postComment.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				JSONObject json1 = new JSONObject();
				JSONArray json_array = new JSONArray();
				try {
					json1.put("comment", typeComment.getText());
					json1.put("projectId", project.getProject_id());
					ProjectxGlobalState Gs = (ProjectxGlobalState) getApplication();
					System.out.println("createdby: " + Gs.getUserid());
					json1.put("createdBy", Gs.getUserid());

					Log.d("JSON string", json1.toString());
					ProgressDialog dialog = new ProgressDialog(cont);
					dialog.setMessage("Create Comments...");
					CreateCommentTask createCommentTask = new CreateCommentTask(cont, currentActivity, json1, dialog);
					createCommentTask.execute("http://ec2-54-251-4-64.ap-southeast-1.compute.amazonaws.com/api/createProjectComment.php");

					typeComment.setText("");

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});

		logoText.setSelected(true);

		logoText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				logoText.setSelected(true);
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();

		
		if (isFirst) {
			menuOut = true;
			slide.performClick();
		} else {
			isFirst = true;
		}
		logoText.setFocusable(true);
		logoText.requestFocus();
		System.out.println("menu in resume" + menuOut);

		globalState = (ProjectxGlobalState) getApplication();

		project = globalState.getProject();
		Toast.makeText(cont, project.getProject_name(), Toast.LENGTH_LONG).show();
		logoText.setText(project.getProject_name());
		projDesc.setText(project.getProject_desc());
		projDeadline.setText(project.getDue_date());
		IDemoChart mCharts = new ProjectProgressChart();
		List<Project> projects = new ArrayList<Project>();
		projects.add(project);
		mChartView = mCharts.execute(cont, projects, true);
		chartLayout.addView(mChartView, new LayoutParams
				(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		
		mChartView.setClickable(true);
		mChartView.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				startActivityForResult(chartIntent, subActivityID);				
			}
		});
		memberList = project.getMembers();
		for (int i = 0; i < memberList.size(); i++)
			System.out.println("members new:" + memberList.get(i));
		memberListView.setAdapter(new MemberProgressBaseAdapter(cont, memberList, (ArrayList<Task>) project.getTasks()));
		String url = "http://ec2-54-251-4-64.ap-southeast-1.compute.amazonaws.com/api/getActivityFeed.php";
		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair("project_id", new Integer(project.getProject_id()).toString()));
		String paramString = URLEncodedUtils.format(params, "utf-8");
		url += "?" + paramString;
		ProgressDialog dialog = new ProgressDialog(cont);
		dialog.setMessage("Loading Activity Feed...");
		GetActivityFeed task = new GetActivityFeed(cont, this, dialog, activities, commentlist);
		System.out.println(url);
		task.execute(url);
		
	}

	private class CreateCommentTask extends AsyncTask<String, Void, String> {
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
				Log.d("PostComment", resultJson.toString());
				// System.out.println(resultJson.toString());
				if (resultJson.getString("msg").equals("success")) {
					// context.startActivity(new Intent(context,
					// homeActivity.class));
					String url = "http://ec2-54-251-4-64.ap-southeast-1.compute.amazonaws.com/api/getActivityFeed.php";
					List<NameValuePair> params = new LinkedList<NameValuePair>();
					params.add(new BasicNameValuePair("project_id", new Integer(project.getProject_id()).toString()));
					String paramString = URLEncodedUtils.format(params, "utf-8");
					url += "?" + paramString;
					dialog.setMessage("Loading Activity Feed...");
					GetActivityFeed task = new GetActivityFeed(context, callingActivity, dialog, activities, commentlist);
					System.out.println(url);
					task.execute(url);
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
			System.out.println("menu width " + menuWidth + "  menu=" + menuOut);

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

	private void pullltoRefreshCommentsActivity() {
		String url = "http://ec2-54-251-4-64.ap-southeast-1.compute.amazonaws.com/api/getActivityFeed.php";
		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair("project_id", new Integer(project.getProject_id()).toString()));
		String paramString = URLEncodedUtils.format(params, "utf-8");
		url += "?" + paramString;
		GetActivityFeed task = new GetActivityFeed(cont, this, activities, commentlist);
		task.execute(url);
	}

	private class GetActivityFeed extends AsyncTask<String, Void, String> {
		private final Context context;
		private final Activity callingActivity;
		private final ProgressDialog dialog;
		private final ListView activityListView;
		private final ListView commentListView;

		public GetActivityFeed(Context context, Activity callingActivity, ProgressDialog dialog, ListView activityListView, ListView commentListView) {
			this.context = context;
			this.callingActivity = callingActivity;
			this.dialog = dialog;
			this.activityListView = activityListView;
			this.commentListView = commentListView;
		}

		public GetActivityFeed(Context context, Activity callingActivity, ListView activityListView, ListView commentListView) {
			this.context = context;
			this.callingActivity = callingActivity;
			this.dialog = null;
			this.activityListView = activityListView;
			this.commentListView = commentListView;
		}

		@Override
		protected void onPreExecute() {
			if (this.dialog != null) {
				if (!this.dialog.isShowing()) {
					this.dialog.setMessage("Loading...");
					this.dialog.setCanceledOnTouchOutside(false);
					this.dialog.show();
				}
			}
		}

		@Override
		protected String doInBackground(String... urls) {
			String response = "";
			for (String url : urls) {
				Log.d("Activity Feed", url);
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
			if (dialog != null) {
				if (this.dialog.isShowing()) {
					this.dialog.dismiss();
				}
			}
			System.out.println(result);
			try {
				JSONObject resultJson = new JSONObject(result);
				Log.d("ActivityFeed", resultJson.toString());
				if (resultJson.getString("msg").equals("success")) {
					Gson gson = new Gson();
					ActivityFeed feed = gson.fromJson(result, ActivityFeed.class);
					Log.d("activity feed", feed.getNotifications().toString());
					Log.d("activity feed", feed.getComments().toString());
					ArrayList<String> list = new ArrayList<String>();
					ArrayList<String> activityfeed = new ArrayList<String>();
					for (int i = 0; i < feed.getComments().size(); i++) {
						list.add(feed.getComments().get(i).getComment());
					}
					Log.d("comments size", "" + feed.getComments().size());
					Log.d("notifications size", "" + feed.getNotifications().size());
					for (int i = 0; i < feed.getNotifications().size(); i++) {
						activityfeed.add(feed.getNotifications().get(i).getMessage());
					}
					// commentListView.setAdapter(new
					// ArrayAdapter<String>(context,
					// android.R.layout.simple_list_item_1, list));
					// activityListView.setAdapter(new
					// ArrayAdapter<String>(context,
					// android.R.layout.simple_list_item_1, activityfeed));
					commentListView.setAdapter(new CommentBaseAdapter(context, feed.getComments()));
					commentListView.setSelection(commentListView.getCount() - 1);
					activityListView.setAdapter(new ActivityFeedAdapter(context, feed.getNotifications()));
					if (dialog == null) {
						commentlistWrapper.onRefreshComplete();
						activitiesWrapper.onRefreshComplete();
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
}
