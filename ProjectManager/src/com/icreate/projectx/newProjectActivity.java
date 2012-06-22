package com.icreate.projectx;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
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
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.icreate.projectx.datamodel.ProjectxGlobalState;

public class newProjectActivity extends Activity implements AdapterView.OnItemSelectedListener {
	private EditText nameTextBox, aboutTextBox, dueTextBox;
	private TextView newProjectDeadlinetext, newProjectNametext, newProjectAbouttext, newProjectModuletext, logoText;
	private Spinner moduleTextBox;
	private DatePicker dp1;
	private Button createProjectButton, addMemberButton;
	private final List<Button> alltv = new ArrayList<Button>();
	private final List<String> moduleList = new ArrayList<String>();
	private List<String> studentList = new ArrayList<String>();
	private final List<String> student_id_list = new ArrayList<String>();
	private final ArrayList<String> members = new ArrayList<String>();
	private final ArrayList<String> memberid = new ArrayList<String>();
	private ArrayAdapter<String> dataAdapter;
	private final List<String> moduleId = new ArrayList<String>();
	private Activity currentActivity;
	private Context cont;
	private ProgressDialog dialog;

	int textLength1 = 0;
	int textLength2 = 0;
	int index = 0;
	int moduleIndex = 0;
	Intent addMemberIntent;
	String text;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().penaltyDeath().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().penaltyDeath().build());
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.newproject);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.logo1);
		cont = this;
		currentActivity = this;
		dialog = new ProgressDialog(cont);
		moduleTextBox = (Spinner) findViewById(R.id.moduleTextBox);
		nameTextBox = (EditText) findViewById(R.id.nameTextBox);
		aboutTextBox = (EditText) findViewById(R.id.aboutTextBox);
		dueTextBox = (EditText) findViewById(R.id.deadlineTextBox);
		createProjectButton = (Button) findViewById(R.id.loginButton);
		addMemberButton = (Button) findViewById(R.id.addMemberButton);

		newProjectDeadlinetext = (TextView) findViewById(R.id.newProjectDeadlinetext);
		newProjectAbouttext = (TextView) findViewById(R.id.newProjectAbouttext);
		newProjectNametext = (TextView) findViewById(R.id.newProjectNametext);
		newProjectModuletext = (TextView) findViewById(R.id.newProjectModuletext);
		logoText = (TextView) findViewById(R.id.logoText);

		Typeface font = Typeface.createFromAsset(getAssets(), "EraserDust.ttf");
		newProjectDeadlinetext.setTypeface(font);
		newProjectAbouttext.setTypeface(font);
		newProjectNametext.setTypeface(font);
		newProjectModuletext.setTypeface(font);
		nameTextBox.setTypeface(font);
		aboutTextBox.setTypeface(font);
		// deadlineTextBox.setTypeface(font);

		logoText.setTypeface(font);
		logoText.setText("New Project");

		ImageButton homeButton = (ImageButton) findViewById(R.id.logoImageButton);
		homeButton.setBackgroundResource(R.drawable.home_button);

		homeButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				startActivity(new Intent(cont, homeActivity.class));

			}
		});

		addMemberButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				addMemberIntent = new Intent(cont, AddMemberActivity.class);
				startActivity(addMemberIntent);
			}
		});

		dp1 = (DatePicker) findViewById(R.id.datePicker1);
		RelativeLayout rl = (RelativeLayout) findViewById(R.id.rlayout);

		dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, moduleList) {
		};
		;
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		moduleTextBox.setAdapter(dataAdapter);
		GetModuleList task = new GetModuleList(this.cont, this.currentActivity, this.dialog);
		task.execute();

		ProjectxGlobalState globalData = (ProjectxGlobalState) getApplication();
		globalData.setModuleId(moduleId);
		moduleTextBox.setOnItemSelectedListener(this);

		/*
		 * clickListener = new OnClickListener() { public void onClick(View v) {
		 * int index = alltv.indexOf(v); FlowLayout memberLayout = (FlowLayout)
		 * findViewById(R.id.memberLayout); memberLayout.removeView(v);
		 * members.remove(index); alltv.remove(index); } };
		 */

		dueTextBox.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dp1.setVisibility(View.VISIBLE);
			}

		});

		rl.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				dueTextBox.setText(dp1.getYear() + "-" + (dp1.getMonth() + 1) + "-" + dp1.getDayOfMonth());
				dp1.setVisibility(View.INVISIBLE);
			}
		});

		createProjectButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				JSONObject json1 = new JSONObject();
				JSONArray json_array = new JSONArray();
				try {
					json1.put("name", nameTextBox.getText());
					json1.put("description", aboutTextBox.getText());
					ProjectxGlobalState Gs = (ProjectxGlobalState) getApplication();
					json1.put("leader", Gs.getUserid());
					json1.put("moduleCode", moduleTextBox.getSelectedItem());
					json1.put("duedate", dueTextBox.getText());
					for (int i = 0; i < members.size(); i++) {
						JSONObject json2 = new JSONObject();
						json2.put("member_id", memberid.get(i));
						json2.put("member_name", members.get(i));
						json_array.put(json2);
					}
					json1.put("members", json_array);

					Log.d("JSON string", json1.toString());
					ProgressDialog dialog = new ProgressDialog(cont);
					dialog.setMessage("Create Project...");
					CreateProjectTask createProjectTask = new CreateProjectTask(cont, currentActivity, json1, dialog);
					createProjectTask.execute("http://ec2-54-251-4-64.ap-southeast-1.compute.amazonaws.com/api/createProject.php");
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		});
	}

	public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
		studentList = new ArrayList<String>();
		String selectedFromList = (String) moduleTextBox.getSelectedItem();
		moduleIndex = moduleList.indexOf(selectedFromList);
		GetStudentList task2 = new GetStudentList();
		task2.execute(moduleIndex);
	}

	public void onNothingSelected(AdapterView<?> parent) {
	}

	private class GetModuleList extends AsyncTask<Void, Void, String> {
		private final ProgressDialog dialog;
		private final Context context;
		private final Activity callingActivity;

		public GetModuleList(Context context, Activity callingActivity, ProgressDialog dialog) {
			this.context = context;
			this.callingActivity = callingActivity;
			// this.requestJson = requestData;
			this.dialog = dialog;
		}

		@Override
		protected void onPreExecute() {
			if (!this.dialog.isShowing()) {
				this.dialog.setMessage("Retrieving Module List...");
				this.dialog.show();
			}
		}

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			// List<String> modulesList = new ArrayList<String>();
			String content = "";
			try {
				HttpClient client = new DefaultHttpClient();
				ProjectxGlobalState globalData = (ProjectxGlobalState) getApplication();
				String getURL = "https://ivle.nus.edu.sg/api/Lapi.svc/Modules_Student?APIKey=tlXXFhEsNoTIVTJQruS2o&AuthToken=" + globalData.getAuthToken() + "&Duration=0&IncludeAllInfo=false";
				HttpGet get = new HttpGet(getURL);
				HttpResponse responseGet = client.execute(get);
				HttpEntity mResEntityGet = responseGet.getEntity();

				// JSONObject json = new JSONObject();

				if (mResEntityGet != null) {
					content = EntityUtils.toString(mResEntityGet);
					Log.d("response", content);
					/*
					 * json = new JSONObject(content); String xyz =
					 * json.getString("Results"); JSONArray arr = new
					 * JSONArray(xyz); // Log.d("json", json.toString(3));
					 * 
					 * for (int i = 0; i < arr.length(); i++) { JSONObject obj =
					 * arr.getJSONObject(i); String courseid =
					 * obj.getString("CourseCode"); moduleList.add(courseid);
					 * 
					 * moduleId.add(obj.getString("ID"));
					 * Log.d("module - result", courseid); }
					 */
				}

			} catch (Exception e) {
				e.printStackTrace();
				Log.e("module-error", "could not get modules");
			}
			return content;
		}

		@Override
		protected void onPostExecute(String result) {
			if (this.dialog.isShowing()) {
				this.dialog.dismiss();
			}
			try {
				JSONObject json = new JSONObject();
				json = new JSONObject(result);
				String xyz = json.getString("Results");
				JSONArray arr = new JSONArray(xyz);
				// Log.d("json", json.toString(3));

				for (int i = 0; i < arr.length(); i++) {
					JSONObject obj = arr.getJSONObject(i);
					String courseid = obj.getString("CourseCode");
					dataAdapter.add(courseid);

					moduleId.add(obj.getString("ID"));
					Log.d("module - result", courseid);
				}
			} catch (Exception e) {
				Log.e("module-error", "could not get modules");
			}

		}
	}

	public class CreateProjectTask extends AsyncTask<String, Void, String> {
		private final Context context;
		private final Activity callingActivity;
		private final ProgressDialog dialog;
		private final JSONObject requestJson;

		public CreateProjectTask(Context context, Activity callingActivity, JSONObject requestData, ProgressDialog dialog) {
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
					context.startActivity(new Intent(context, homeActivity.class));
				} else {
					Toast.makeText(context, R.string.login_error, Toast.LENGTH_LONG).show();
				}
				callingActivity.finish();
			} catch (JSONException e) {
				Toast.makeText(context, R.string.server_error, Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
		}
	}

	private class GetStudentList extends AsyncTask<Integer, Void, List<String>> {

		@Override
		protected List<String> doInBackground(Integer... modules) {
			for (Integer module : modules) {
				try {

					HttpClient client = new DefaultHttpClient();
					ProjectxGlobalState globalData = (ProjectxGlobalState) getApplication();
					String getURL = "https://ivle.nus.edu.sg/API/Lapi.svc/Class_Roster?APIKey=tlXXFhEsNoTIVTJQruS2o&AuthToken=" + globalData.getAuthToken() + "&CourseID=" + moduleId.get(module);

					HttpGet get = new HttpGet(getURL);
					HttpResponse responseGet = client.execute(get);
					HttpEntity mResEntityGet = responseGet.getEntity();
					String content;

					JSONObject json = new JSONObject();

					if (mResEntityGet != null) {
						content = EntityUtils.toString(mResEntityGet);
						Log.d("response", content);
						json = new JSONObject(content);
						String xyz = json.getString("Results");

						addMemberIntent.putExtra("studentListJson", xyz);
						Log.d("xyz", xyz);

					}
				} catch (Exception e) {
					e.printStackTrace();
					Log.e("project members-error", "could not get members");
				}
			}
			return studentList;
		}
	}

}
