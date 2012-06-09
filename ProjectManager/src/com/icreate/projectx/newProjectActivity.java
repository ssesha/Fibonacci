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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.icreate.projectx.datamodel.ProjectxGlobalState;

public class newProjectActivity extends Activity implements AdapterView.OnItemSelectedListener {
	private ListView membersListView;
	private EditText  membersTextBox1, nameTextBox, aboutTextBox,dueTextBox;
	private Spinner moduleTextBox;
	private DatePicker dp1;
	private Button createProjectButton;
	private Button t;
	private List<Button> alltv = new ArrayList<Button>();
	private List<String> moduleList = new ArrayList<String>();
	private List<String> studentList = new ArrayList<String>();
	private List<String> student_id_list = new ArrayList<String>();
	private ArrayList<String> moduleListFilter = new ArrayList<String>();
	private ArrayList<String> studentListFilter = new ArrayList<String>();
	private ArrayList<String> members = new ArrayList<String>();
	private ArrayList<String> memberid = new ArrayList<String>();
	private View.OnClickListener clickListener;
	private ArrayAdapter<String> dataAdapter ;

	// TODO: is this list needed here? Look for better ways. Maybe add it to
	// global state.
	private List<String> moduleId = new ArrayList<String>();

	int textLength1 = 0;
	int textLength2 = 0;
	int index = 0;
	String text;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectAll().penaltyLog().penaltyDeath().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll()
				.penaltyLog().penaltyDeath().build());
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.newproject);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.logo1);
		final Context cont = this;
		final Activity currentActivity = this;
		
		moduleTextBox = (Spinner) findViewById(R.id.moduleTextBox);
		membersListView = (ListView) findViewById(R.id.membersList);
		membersTextBox1 = (EditText) findViewById(R.id.membersTextBox1);
		nameTextBox = (EditText) findViewById(R.id.nameTextBox);
		aboutTextBox = (EditText) findViewById(R.id.aboutTextBox);
		dueTextBox = (EditText) findViewById(R.id.deadlineTextBox);
		createProjectButton = (Button) findViewById(R.id.loginButton);
		dp1 = (DatePicker) findViewById(R.id.datePicker1);
		RelativeLayout rl = (RelativeLayout) findViewById(R.id.rlayout);

		//List<String> items = new ArrayList<String>();
		//items.add("this");
		//items.add("is");
		moduleList.add("Select Module");
		//moduleTextBox.setOnItemSelectedListener(this);
		dataAdapter= new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, moduleList){
				public boolean isEnabled(int position) {
            if(position == 0){
                return false; 
            }else{
                return true;
            }
          } 
          public boolean areAllItemsEnabled() { 
            return false; 
          } 
        };;
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		moduleTextBox.setAdapter(dataAdapter);
		GetModuleList task = new GetModuleList();
		task.execute();
		moduleTextBox.setOnItemSelectedListener(this);	
		
	//	moduleTextBox.setSelection(0,true);
		
		
			    
		/*
		 * studentList.add("Oinker"); studentList.add("Abs");
		 * studentList.add("Abbinayaa"); studentList.add("oink");
		 * studentList.add("oinky"); studentList.add("piggy"); RelativeLayout rl
		 * = (RelativeLayout)findViewById(R.id.rlayout);
		 * studentList.add("piggy");
		 */

		//moduleListView.setAdapter(new ArrayAdapter<String>(this, R.layout.list,
				//R.id.tv1, moduleList));
		membersListView.setAdapter(new ArrayAdapter<String>(this,
				R.layout.list, R.id.tv1, studentList));


		
		clickListener = new OnClickListener() {
		    public void onClick(View v) {
		        // do something here
		    	int index=v.getId();
		    	LinearLayout ll = (LinearLayout) findViewById(R.id.layout1);
		    	members.remove(index);
		    	ll.removeView(alltv.get(index));
		    	
		    }
		};
		
		membersListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> myAdapter, View myView,
					int myItemInt, long mylng) {
				String selectedFromList = (String) (membersListView
						.getItemAtPosition(myItemInt));
				LinearLayout ll = (LinearLayout) findViewById(R.id.layout1);
				t = new Button(newProjectActivity.this);
				int name_i = selectedFromList.indexOf(" ");
				String name = selectedFromList.substring(0, name_i);
				t.setText(name);
				t.setTextSize(15);
				t.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT));
				ll.addView(t);
				t.setOnClickListener(clickListener);
				membersTextBox1.setText("");
				members.add(selectedFromList);
				t.setId(alltv.size());
				alltv.add(t);
				int index = studentList.indexOf(selectedFromList);
				memberid.add(student_id_list.get(index));
				membersListView.setVisibility(View.INVISIBLE);
			}
		});
		
		

		dueTextBox.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dp1.setVisibility(View.VISIBLE);
			}

		});

		rl.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				dueTextBox.setText(dp1.getDayOfMonth() + "-"
						+ (dp1.getMonth() + 1) + "-" + dp1.getYear());
				dp1.setVisibility(View.INVISIBLE);
			}
		});

	/*	moduleTextBox.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				textLength1 = moduleTextBox.getText().length();

				if (textLength1 == 0)
					moduleListView.setVisibility(View.INVISIBLE);
				moduleListFilter.clear();
				for (int i = 0; i < moduleList.size(); i++) {
					if (textLength1 <= moduleList.get(i).length()) {
						if (moduleTextBox
								.getText()
								.toString()
								.equalsIgnoreCase(
										(String) moduleList.get(i).subSequence(
												0, textLength1))) {
							moduleListFilter.add(moduleList.get(i));
						}
					}
				}

				moduleListView.setAdapter(new ArrayAdapter<String>(
						newProjectActivity.this, R.layout.list, R.id.tv1,
						moduleListFilter));

				if (moduleListView.getCount() >= 1 && textLength1 > 0) {
					moduleListView.setVisibility(View.VISIBLE);
				}

			}
		});*/		

		membersTextBox1.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				textLength2 = membersTextBox1.getText().length();
				if (textLength2 == 0)
					membersListView.setVisibility(View.INVISIBLE);

				studentListFilter.clear();
				for (int i = 0; i < studentList.size(); i++) {
					if (textLength2 <= studentList.get(i).length()) {
						if (membersTextBox1
								.getText()
								.toString()
								.equalsIgnoreCase(
										(String) studentList.get(i)
												.subSequence(0, textLength2))) {

							studentListFilter.add(studentList.get(i));
						}
					}
				}

				membersListView.setAdapter(new ArrayAdapter<String>(
						newProjectActivity.this, R.layout.list, R.id.tv1,
						studentListFilter));

				if (membersListView.getCount() >= 1 && textLength2 > 0) {
					membersListView.setVisibility(View.VISIBLE);
				}

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

	public void onItemSelected(AdapterView<?> parent, View v, int position,
			long id) {
		studentList = new ArrayList<String>();
		String selectedFromList=(String) moduleTextBox.getSelectedItem();
		GetStudentList task2 = new GetStudentList();
        task2.execute(moduleList.indexOf(selectedFromList));
	}

	public void onNothingSelected(AdapterView<?> parent) {
	}
	
	private class GetModuleList extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			// List<String> modulesList = new ArrayList<String>();
			String content = "";
			try {
				HttpClient client = new DefaultHttpClient();
				ProjectxGlobalState globalData = (ProjectxGlobalState) getApplication();
				String getURL = "https://ivle.nus.edu.sg/api/Lapi.svc/Modules_Student?APIKey=tlXXFhEsNoTIVTJQruS2o&AuthToken="
						+ globalData.getAuthToken()
						+ "&Duration=0&IncludeAllInfo=false";
				HttpGet get = new HttpGet(getURL);
				HttpResponse responseGet = client.execute(get);
				HttpEntity mResEntityGet = responseGet.getEntity();
				

				//JSONObject json = new JSONObject();

				if (mResEntityGet != null) {
					content = EntityUtils.toString(mResEntityGet);
					Log.d("response", content);
					/*json = new JSONObject(content);
					String xyz = json.getString("Results");
					JSONArray arr = new JSONArray(xyz);
					// Log.d("json", json.toString(3));

					for (int i = 0; i < arr.length(); i++) {
						JSONObject obj = arr.getJSONObject(i);
						String courseid = obj.getString("CourseCode");
						moduleList.add(courseid);
						
						moduleId.add(obj.getString("ID"));
						Log.d("module - result", courseid);
					}*/
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("module-error", "could not get modules");
			}
			return content;
		}
		
		@Override
		protected void onPostExecute(String result) {
			try{
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
			}
			catch(Exception e)
			{
				Log.e("module-error", "could not get modules");
			}
			

	}
	}
	
	public class CreateProjectTask extends AsyncTask<String, Void, String> {
		private Context context;
		private Activity callingActivity;
		private ProgressDialog dialog;
		private JSONObject requestJson;

		public CreateProjectTask(Context context, Activity callingActivity, JSONObject requestData, ProgressDialog dialog) {
			this.context = context;
			this.callingActivity = callingActivity;
			this.requestJson = requestData;
			this.dialog = dialog;
		}

		@Override
		protected void onPreExecute() {
			System.out.println(this.dialog.isShowing());
			if(!(this.dialog.isShowing())){
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
				System.out.println(resultJson.toString());
				if(resultJson.getString("msg").equals("success")){
				context.startActivity(new Intent(context, homeActivity.class));
				}
				else{
					Toast.makeText(context, R.string.login_error,
							Toast.LENGTH_LONG).show();
				}
				callingActivity.finish();
			} catch (JSONException e) {
				Toast.makeText(context, R.string.server_error,
						Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
		}
	}

	private class GetStudentList extends AsyncTask<Integer, Void, List<String>> {

		@Override
		protected List<String> doInBackground(Integer... params) {

			try {

				HttpClient client = new DefaultHttpClient();
				ProjectxGlobalState globalData = (ProjectxGlobalState) getApplication();
				String getURL = "https://ivle.nus.edu.sg/API/Lapi.svc/Class_Roster?APIKey=tlXXFhEsNoTIVTJQruS2o&AuthToken="
						+ globalData.getAuthToken()
						+ "&CourseID="
						+ moduleId.get(params[0]);

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
					JSONArray arr = new JSONArray(xyz);
					// Log.d("json", json.toString(3));

					for (int i = 0; i < arr.length(); i++) {
						JSONObject obj = arr.getJSONObject(i);
						String name = obj.getString("Name");
						name = name.toLowerCase();
						int index = name.indexOf(" ");
						name = name.substring(0, 1).toUpperCase()
								+ name.subSequence(1, index + 1)
								+ name.substring(index + 1, index + 2)
										.toUpperCase()
								+ name.substring(index + 2);
						studentList.add(name);
						student_id_list.add(obj.getString("UserID"));
						System.out.println("result - project group: " + name);
					}
					System.out.println(arr.length());

				}
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("project members-error", "could not get modules");
			}
			return moduleList;
		}
		
		@Override
        protected void onPostExecute(List<String> result) {
			
			
				
			
        }
	}
}
