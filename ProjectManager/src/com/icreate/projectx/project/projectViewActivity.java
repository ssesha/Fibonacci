package com.icreate.projectx.project;

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
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
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
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.icreate.projectx.MemberProgressBaseAdapter;
import com.icreate.projectx.MyHorizontalScrollView;
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
	private TextView ProjectName;
	private Button createTask, TaskView;
	private Button editProject;
	private ProjectxGlobalState globalState;
	private Project project;
	private List<ProjectMembers> memberList;
	private String projectString;
	private MyHorizontalScrollView scrollView;
	private final ArrayList<String> commentList = new ArrayList<String>();
	private ArrayList<Comment> comments;
	private final ArrayList<String> activitites = new ArrayList<String>();
	private ArrayList<Notification> activityFeed;
	boolean menuOut = false;
	Handler handler = new Handler();
	int btnWidth, task_id = 0;
	private View projectView, commentView, logoView;
	private ImageView slide;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		// setContentView(R.layout.projectview);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.logo1);

		final Context cont = this;
		final Activity currentActivity = this;

		LayoutInflater inflater = LayoutInflater.from(this);
		setContentView(inflater.inflate(R.layout.scrollview_comment, null));

		scrollView = (MyHorizontalScrollView) findViewById(R.id.myScrollView);
		logoView = inflater.inflate(R.layout.logo1, null);
		projectView = inflater.inflate(R.layout.projectview, null);
		commentView = inflater.inflate(R.layout.projectextension, null);

		Typeface font = Typeface.createFromAsset(getAssets(), "EraserDust.ttf");
		logoText = (TextView) logoView.findViewById(R.id.logoText);
		logoText.setTypeface(font);
		logoText.setTextColor(R.color.white);

		ImageButton homeButton = (ImageButton) logoView
				.findViewById(R.id.logoImageButton);
		homeButton.setBackgroundResource(R.drawable.home_button);

		homeButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				startActivity(new Intent(cont, homeActivity.class));

			}
		});

		ViewGroup slidelayout = (ViewGroup) projectView
				.findViewById(R.id.slidelayout_proj);
		slide = (ImageView) slidelayout.findViewById(R.id.BtnSlide_proj);
		slide.setOnClickListener(new ClickListenerForScrolling(scrollView,
				commentView));

		createTask = (Button) projectView
				.findViewById(R.id.createNewTaskButton);
		TaskView = (Button) projectView.findViewById(R.id.taskListButton);
		TextView projDesc = (TextView) projectView.findViewById(R.id.projDesc);
		editProject = (Button) projectView.findViewById(R.id.editProjectButton);
		// projDesc.setText(globalState.getProjectList().getProjects().get(position).getProject_Desc());

		final ListView memberListView = (ListView) projectView
				.findViewById(R.id.memberProgressList);
		memberListView.setTextFilterEnabled(true);
		registerForContextMenu(memberListView);
		final ListView activities = (ListView) commentView
				.findViewById(R.id.activity);
		final ListView commentlist = (ListView) commentView
				.findViewById(R.id.proj_comments);
		final Button postComment = (Button) commentView
				.findViewById(R.id.proj_sendCommentButton);
		final EditText typeComment = (EditText) commentView
				.findViewById(R.id.proj_commentTextBox);

		Bundle extras = getIntent().getExtras();
		globalState = (ProjectxGlobalState) getApplication();
		if (extras != null) {
			projectString = extras.getString("projectJson", "");
			Log.d("sdcsd", projectString);
			System.out.println(projectString.isEmpty());
			if (!(projectString.isEmpty())) {
				Gson gson = new Gson();
				project = gson.fromJson(projectString, Project.class);
				Toast.makeText(cont, project.getProject_name(),
						Toast.LENGTH_LONG).show();
				logoText.setText(project.getProject_name());
				projDesc.setText(project.getProject_desc());
				memberList = project.getMembers();
				memberListView.setAdapter(new MemberProgressBaseAdapter(cont,
						memberList, (ArrayList<Task>) project.getTasks()));
				String url = "http://ec2-54-251-4-64.ap-southeast-1.compute.amazonaws.com/api/getActivityFeed.php";
				List<NameValuePair> params = new LinkedList<NameValuePair>();
				params.add(new BasicNameValuePair("project_id", new Integer(
						project.getProject_id()).toString()));
				String paramString = URLEncodedUtils.format(params, "utf-8");
				url += "?" + paramString;
				ProgressDialog dialog = new ProgressDialog(cont);
				dialog.setMessage("Loading Activity Feed...");
				GetActivityFeed task = new GetActivityFeed(cont, this, dialog,
						activities);
				System.out.println(url);
				task.execute(url);
				url = "http://ec2-54-251-4-64.ap-southeast-1.compute.amazonaws.com/api/getActivityFeed.php";

				TabHost tabHost = (TabHost) commentView
						.findViewById(R.id.tabhost);
				tabHost.setup();
				TabSpec activityspec = tabHost.newTabSpec("Activities");
				activityspec.setIndicator("Activitites", getResources()
						.getDrawable(R.drawable.bulb));
				// Intent activitiesIntent = new Intent(this,
				// ProjectFeedActivity.class);
				// activitiesIntent.putExtra("project_id", new
				// Integer(project.getProject_id()).toString());
				activityspec.setContent(R.id.activity);

				TabSpec commentsspec = tabHost.newTabSpec("Comments");
				commentsspec.setIndicator("Comments", getResources()
						.getDrawable(R.drawable.dustbin));
				commentsspec.setContent(R.id.project_commentviewlayout);

				tabHost.addTab(activityspec); // Adding photos tab
				tabHost.addTab(commentsspec);

			} else {
				Toast.makeText(cont, "Cannot load Project", Toast.LENGTH_LONG)
						.show();
			}
		}
		final View[] children = new View[] { commentView, projectView };
		int scrollToViewIdx = 1;
		scrollView.initViews(children, scrollToViewIdx,
				new SizeCallbackForMenu(slide));
		memberListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Object o = memberListView.getItemAtPosition(position);
				ProjectMembers selectedMember = (ProjectMembers) o;
				double totaltasks = (Double) view
						.getTag(R.id.member_total_tasks);
				double totalcompletedtasks = (Double) view
						.getTag(R.id.member_total_tasks);
				double progress = (Double) view.getTag(R.id.member_progress);
				Toast.makeText(
						cont,
						"You have chosen: " + " "
								+ selectedMember.getUser_name() + " "
								+ selectedMember.getMember_id(),
						Toast.LENGTH_LONG).show();
				System.out.println(project.getTasks(selectedMember
						.getMember_id())
						+ " "
						+ totaltasks
						+ " "
						+ totalcompletedtasks);
				Intent memberViewIntent = new Intent(cont,
						MemberViewActivity.class);
				memberViewIntent.putExtra("memberPosition", position);
				memberViewIntent.putExtra("project", projectString);
				memberViewIntent.putExtra("totaltasks", totaltasks);
				memberViewIntent.putExtra("totalcompletedtasks",
						totalcompletedtasks);
				memberViewIntent.putExtra("memberProgress", progress);
				startActivity(memberViewIntent);
			}
		});

		createTask.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent newTaskIntent = new Intent(cont, newTaskActivity.class);
				newTaskIntent.putExtra("project", projectString);
				startActivity(newTaskIntent);
			}
		});

		TaskView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent TaskViewIntent = new Intent(cont,
						expandTaskViewActivity.class);
				System.out.println("project" + projectString);
				TaskViewIntent.putExtra("project", projectString);
				startActivity(TaskViewIntent);
			}

		});
		editProject.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent editProjectIntent = new Intent(cont,
						editProjectActivity.class);
				editProjectIntent.putExtra("project_Id",
						project.getProject_id());
				startActivity(editProjectIntent);
			}
		});
	}

	private static class ClickListenerForScrolling implements OnClickListener {
		HorizontalScrollView scrollView;
		View menu;
		/**
		 * Menu must NOT be out/shown to start with.
		 */
		boolean menuOut = false;

		public ClickListenerForScrolling(HorizontalScrollView scrollView,
				View menu) {
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

		public void onGlobalLayout() {
			btnWidth = btnSlide.getMeasuredWidth() + 50;
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

	private class GetActivityFeed extends AsyncTask<String, Void, String> {
		private final Context context;
		private final Activity callingActivity;
		private final ProgressDialog dialog;
		private final ListView activityListView;

		public GetActivityFeed(Context context, Activity callingActivity,
				ProgressDialog dialog, ListView activityListView) {
			this.context = context;
			this.callingActivity = callingActivity;
			this.dialog = dialog;
			this.activityListView = activityListView;
		}

		@Override
		protected void onPreExecute() {
			if (!this.dialog.isShowing()) {
				this.dialog.setMessage("Loading...");
				this.dialog.show();
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

					BufferedReader buffer = new BufferedReader(
							new InputStreamReader(content));
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
				Log.d("ActivityFeed", resultJson.toString());
				if (resultJson.getString("msg").equals("success")) {
					Gson gson = new Gson();
					ActivityFeed feed = gson.fromJson(result,
							ActivityFeed.class);
					Log.d("activity feed", feed.toString());
					activityListView.setAdapter(new ActivityFeedAdapter(
							context, feed.getNotifications()));
				} else {
					Toast.makeText(context, "Comment Lists empty",
							Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				Toast.makeText(context, R.string.server_error,
						Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
		}
	}
}
