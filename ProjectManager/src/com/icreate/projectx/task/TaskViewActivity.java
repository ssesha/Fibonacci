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
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.icreate.projectx.CommentBaseAdapter;
import com.icreate.projectx.MyHorizontalScrollView;
import com.icreate.projectx.R;
import com.icreate.projectx.homeActivity;
import com.icreate.projectx.MyHorizontalScrollView.SizeCallback;
import com.icreate.projectx.R.color;
import com.icreate.projectx.R.drawable;
import com.icreate.projectx.R.id;
import com.icreate.projectx.R.layout;
import com.icreate.projectx.R.string;
import com.icreate.projectx.datamodel.Comment;
import com.icreate.projectx.datamodel.CommentList;
import com.icreate.projectx.datamodel.Project;
import com.icreate.projectx.datamodel.ProjectxGlobalState;
import com.icreate.projectx.datamodel.Task;

public class TaskViewActivity extends Activity {

	private TextView logoText, TaskDesc, TaskDeadline;
	private ImageView slide;
	private EditText commentTextBox;
	private Button sendComment;
	private ProjectxGlobalState globalState;
	private MyHorizontalScrollView scrollView;
	private ListView taskListView, commentListView;
	private Context cont;
	private Activity currentActivity;
	private String projectString;
	private Project project;
	private Task task;
	private ArrayList<Task> subTasks;
	private View taskview, commentview, logoView;
	private final ArrayList<String> commentList = new ArrayList<String>();
	private ArrayList<Comment> comments;

	boolean menuOut = false;
	Handler handler = new Handler();
	int btnWidth, task_id = 0;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.logo1);

		LayoutInflater inflater = LayoutInflater.from(this);
		setContentView(inflater.inflate(R.layout.scrollview_comment, null));

		scrollView = (MyHorizontalScrollView) findViewById(R.id.myScrollView);
		logoView = inflater.inflate(R.layout.logo1, null);
		taskview = inflater.inflate(R.layout.taskview, null);

		commentview = inflater.inflate(R.layout.task_commentview, null);
		// commentview.setVisibility(View.INVISIBLE);
		cont = this;
		currentActivity = this;

		ViewGroup slidelayout = (ViewGroup) taskview.findViewById(R.id.slidelayout);
		slide = (ImageView) slidelayout.findViewById(R.id.BtnSlide);
		slide.setOnClickListener(new ClickListenerForScrolling(scrollView, commentview));

		globalState = (ProjectxGlobalState) getApplication();

		Typeface font = Typeface.createFromAsset(getAssets(), "EraserDust.ttf");
		logoText = (TextView) logoView.findViewById(R.id.logoText);
		logoText.setTypeface(font);
		logoText.setTextColor(R.color.white);

		ImageButton homeButton = (ImageButton) logoView.findViewById(R.id.logoImageButton);
		homeButton.setBackgroundResource(R.drawable.home_button);

		homeButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				startActivity(new Intent(cont, homeActivity.class));

			}
		});

		taskListView = (ListView) taskview.findViewById(R.id.subTaskList);
		TaskDesc = (TextView) taskview.findViewById(R.id.taskDesc);
		TaskDeadline = (TextView) taskview.findViewById(R.id.taskDeadline);

		commentListView = (ListView) commentview.findViewById(R.id.commentList);
		commentTextBox = (EditText) commentview.findViewById(R.id.commentTextBox);
		sendComment = (Button) commentview.findViewById(R.id.sendCommentButton);

		Bundle extras = getIntent().getExtras();

		if (extras != null) {
			projectString = extras.getString("project");
			task_id = extras.getInt("task_id");
			logoText.setText("Comments");
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

			taskListView.setAdapter(new myTasksBaseAdapter(cont, subTasks));

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

		sendComment.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				JSONObject json1 = new JSONObject();
				JSONArray json_array = new JSONArray();
				try {
					json1.put("comment", commentTextBox.getText());
					json1.put("taskId", task_id);
					ProjectxGlobalState Gs = (ProjectxGlobalState) getApplication();
					json1.put("createdBy", Gs.getUserid());

					Log.d("JSON string", json1.toString());
					ProgressDialog dialog = new ProgressDialog(cont);
					dialog.setMessage("Create Comments...");
					CreateCommentTask createCommentTask = new CreateCommentTask(cont, currentActivity, json1, dialog);
					createCommentTask.execute("http://ec2-54-251-4-64.ap-southeast-1.compute.amazonaws.com/api/createComment.php");

					String url = "http://ec2-54-251-4-64.ap-southeast-1.compute.amazonaws.com/api/commentList.php";
					List<NameValuePair> params = new LinkedList<NameValuePair>();
					params.add(new BasicNameValuePair("task_id", new Integer(task_id).toString()));
					String paramString = URLEncodedUtils.format(params, "utf-8");
					url += "?" + paramString;
					ProgressDialog dialog1 = new ProgressDialog(cont);
					dialog1.setMessage("Getting Comments");
					ListComment ListComments = new ListComment(cont, currentActivity, dialog1, commentListView);
					System.out.println(url);
					ListComments.execute(url);

					commentTextBox.setText("");

				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		});

	};

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
	static class SizeCallbackForMenu implements SizeCallback {
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

	public class ListComment extends AsyncTask<String, Void, String> {
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

}
