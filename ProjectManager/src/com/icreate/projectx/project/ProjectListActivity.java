package com.icreate.projectx.project;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.icreate.projectx.R;
import com.icreate.projectx.homeActivity;
import com.icreate.projectx.datamodel.Project;
import com.icreate.projectx.datamodel.ProjectList;
import com.icreate.projectx.datamodel.ProjectxGlobalState;
import com.icreate.projectx.net.DeleteProjectTask;
import com.icreate.projectx.net.GetProjectTask;

public class ProjectListActivity extends Activity {
	private TextView logoText;
	private ProjectxGlobalState globalState;
	private PullToRefreshListView projectListViewWrapper;
	private ListView projectListView;
	private EditText projectSearch;
	private ProjectListBaseAdapter projectListBaseAdapter;
	private Context cont;
	private Activity currentActivity;
	private String passedUserId;
	private ArrayList<Project> projects = new ArrayList<Project>();
	private final ArrayList<Project> filteredProjects = new ArrayList<Project>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.projectlist);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.logo1);

		cont = this;
		currentActivity = this;

		globalState = (ProjectxGlobalState) getApplication();

		Typeface font = Typeface.createFromAsset(getAssets(), "EraserDust.ttf");
		logoText = (TextView) findViewById(R.id.logoText);
		logoText.setTypeface(font);

		ImageButton homeButton = (ImageButton) findViewById(R.id.logoImageButton);
		homeButton.setBackgroundResource(R.drawable.home_button);

		homeButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent HomeIntent = new Intent(cont, homeActivity.class);
				HomeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(HomeIntent);
				currentActivity.finish();
			}
		});

		projectSearch = (EditText) findViewById(R.id.projectSearch);
		projectListViewWrapper = (PullToRefreshListView) findViewById(R.id.ListView01);
		projectListView = projectListViewWrapper.getRefreshableView();
		projectListView.setTextFilterEnabled(true);
		registerForContextMenu(projectListView);

		projectSearch.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				int textLength2 = projectSearch.getText().length();
				filteredProjects.clear();
				for (int i = 0; i < projects.size(); i++) {
					if (textLength2 <= projects.get(i).getProject_name()
							.length()) {
						if (projectSearch
								.getText()
								.toString()
								.equalsIgnoreCase(
										(String) projects.get(i)
												.getProject_name()
												.subSequence(0, textLength2))) {
							filteredProjects.add(projects.get(i));
						}
					}
				}
				projectListBaseAdapter = new ProjectListBaseAdapter(cont,
						filteredProjects);
				projectListView.setAdapter(projectListBaseAdapter);
			}
		});

		Bundle extras = getIntent().getExtras();
		passedUserId = null;
		if (extras != null) {
			passedUserId = extras.getString("requiredId");
			if (passedUserId.equalsIgnoreCase(globalState.getUserid())) {
				logoText.setText("My Projects");
			} else {
				logoText.setText("Projects");
			}

			Toast.makeText(cont, extras.getString("requiredId"),
					Toast.LENGTH_LONG).show();
			passedUserId = extras.getString("requiredId");
			String url = ProjectxGlobalState.urlPrefix + "projectList.php";
			List<NameValuePair> params = new LinkedList<NameValuePair>();
			params.add(new BasicNameValuePair("user_id", passedUserId));
			String paramString = URLEncodedUtils.format(params, "utf-8");
			url += "?" + paramString;
			ProgressDialog dialog = new ProgressDialog(cont);
			dialog.setMessage("Getting Projects");
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			ProjectListTask projectListTask = new ProjectListTask(cont,
					currentActivity, dialog, projectListView);
			System.out.println(url);
			projectListTask.execute(url);
		}

		projectListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Object o = projectListView.getItemAtPosition(position);
				Project selectedProject = (Project) o;
				/*
				 * Toast.makeText( cont, "You have chosen: " + " " +
				 * selectedProject.getProject_name() + " " +
				 * selectedProject.getProject_id() + " " + position + " " +
				 * globalState
				 * .getProjectList().getProjects().get(position).getLeader_id(),
				 * Toast.LENGTH_LONG).show();
				 */
				Intent projectViewIntent = new Intent(cont,
						projectViewActivity.class);

				int projectId = selectedProject.getProject_id();
				String url = ProjectxGlobalState.urlPrefix
						+ "getProject.php?project_id=" + projectId;
				ProgressDialog dialog = new ProgressDialog(cont);
				dialog.setMessage("Getting Project Info...");
				dialog.setCancelable(false);
				dialog.setCanceledOnTouchOutside(false);
				dialog.show();
				GetProjectTask getProjectTask = new GetProjectTask(cont,
						currentActivity, dialog, 0, false);
				getProjectTask.execute(url);
			}
		});

		projectListViewWrapper.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				if (passedUserId != null) {
					projectSearch.setText("");
					String url = ProjectxGlobalState.urlPrefix
							+ "projectList.php";
					List<NameValuePair> params = new LinkedList<NameValuePair>();
					params.add(new BasicNameValuePair("user_id", passedUserId));
					String paramString = URLEncodedUtils
							.format(params, "utf-8");
					url += "?" + paramString;
					ProjectListTask projectListTask = new ProjectListTask(cont,
							currentActivity, projectListView);
					projectListTask.execute(url);
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		projectSearch.setText("");
	}

	public class ProjectListTask extends AsyncTask<String, Void, String> {
		private final Context context;
		private final Activity callingActivity;
		private final ProgressDialog dialog;
		private final ListView projectListView;

		public ProjectListTask(Context context, Activity callingActivity,
				ProgressDialog dialog, ListView projectListView) {
			this.context = context;
			this.callingActivity = callingActivity;
			this.dialog = dialog;
			this.projectListView = projectListView;
		}

		public ProjectListTask(Context context, Activity callingActivity,
				ListView projectListView) {
			this.context = context;
			this.callingActivity = callingActivity;
			this.dialog = null;
			this.projectListView = projectListView;
		}

		@Override
		protected void onPreExecute() {
			if (dialog != null) {
				if (!this.dialog.isShowing()) {
					this.dialog.setMessage("Getting Projects...");
					this.dialog.show();
					this.dialog.setCanceledOnTouchOutside(false);
					this.dialog.setCancelable(false);
				}
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
			if (dialog != null) {
				if (this.dialog.isShowing()) {
					this.dialog.dismiss();
				}
			}
			System.out.println(result);
			try {
				JSONObject resultJson = new JSONObject(result);
				Log.d("ProjectList", resultJson.toString());
				if (resultJson.getString("msg").equals("success")) {
					Gson gson = new Gson();
					ProjectList projectsContainer = gson.fromJson(result,
							ProjectList.class);
					globalState.setProjectList(projectsContainer);
					projects = projectsContainer.getProjects();
					projectListBaseAdapter = new ProjectListBaseAdapter(
							context, projects);
					projectListView.setAdapter(projectListBaseAdapter);
					if (dialog == null) {
						projectListViewWrapper.onRefreshComplete();
					}
				} else {
					Toast.makeText(context, "Project Lists empty",
							Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				Toast.makeText(context, R.string.server_error,
						Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.project_list_option_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.createproject:
			startActivity(new Intent(cont, newProjectActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("Actions");
		menu.add(0, v.getId(), 0, "Delete");
		menu.add(0, v.getId(), 0, "Go to Project");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		final Project selectedProject = (Project) projectListView
				.getItemAtPosition(info.position);
		System.out.println(selectedProject.getProject_name() + " "
				+ selectedProject.getLeader_name());
		if (item.getTitle() == "Delete") {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					currentActivity);
			builder.setCancelable(true);
			builder.setTitle("Delete Project");
			builder.setMessage("Delete removes the project and its tasks permanently. Would you like to continue?");
			builder.setInverseBackgroundForced(true);
			builder.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String url = ProjectxGlobalState.urlPrefix
									+ "deleteProject.php?project_id="
									+ selectedProject.getProject_id();
							DeleteProjectTask deleteProjectTask = new DeleteProjectTask(
									cont, currentActivity,
									projectListBaseAdapter, info, projects);
							deleteProjectTask.execute(url);
						}
					});
			builder.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			AlertDialog alert = builder.create();
			alert.show();
			projectSearch.setText("");
			return true;
		} else if (item.getTitle() == "Go to Project") {
			Intent projectViewIntent = new Intent(cont,
					projectViewActivity.class);

			int projectId = selectedProject.getProject_id();
			String url = ProjectxGlobalState.urlPrefix
					+ "getProject.php?project_id=" + projectId;
			ProgressDialog dialog = new ProgressDialog(cont);
			dialog.setMessage("Getting Project Info...");
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
			GetProjectTask getProjectTask = new GetProjectTask(cont,
					currentActivity, dialog, 0, false);
			getProjectTask.execute(url);
			return true;
		} else {
			System.out.println("blsldsdlflsfsdf");
		}
		return false;
	}
}
