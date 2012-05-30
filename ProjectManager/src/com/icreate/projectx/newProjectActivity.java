package com.icreate.projectx;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.icreate.projectx.datamodel.ProjectxGlobalState;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
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
import android.widget.TextView;

public class newProjectActivity extends Activity {
	private ListView moduleListView, membersListView;
	private EditText moduleTextBox, membersTextBox1, nameTextBox, aboutTextBox,
			dueTextBox;
	private DatePicker dp1;
	private Button loginButton;
	private List<String> moduleList = new ArrayList<String>();
	private List<String> studentList = new ArrayList<String>();
	private ArrayList<String> moduleListFilter = new ArrayList<String>();
	private ArrayList<String> studentListFilter = new ArrayList<String>();
	private ArrayList<String> members=new ArrayList<String>();
	int textLength1 = 0;
	int textLength2 = 0;
	int index=0;
	String text;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.newproject);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.logo1);

		moduleListView = (ListView) findViewById(R.id.modulesListView);
		moduleTextBox = (EditText) findViewById(R.id.moduleTextBox);
		membersListView = (ListView) findViewById(R.id.membersListView);
		membersTextBox1 = (EditText) findViewById(R.id.membersTextBox1);
		nameTextBox = (EditText) findViewById(R.id.nameTextBox);
		aboutTextBox = (EditText) findViewById(R.id.aboutTextBox);
		dueTextBox = (EditText) findViewById(R.id.deadlineTextBox);
		loginButton = (Button) findViewById(R.id.loginButton);
		dp1= (DatePicker)findViewById(R.id.datePicker1);
		getModuleList();
		studentList.add("Oinker");
		studentList.add("Abs");
		studentList.add("Abbinayaa");
		studentList.add("oink");
		studentList.add("oinky");
		studentList.add("piggy");
		RelativeLayout rl = (RelativeLayout)findViewById(R.id.rlayout);

		
        
		moduleListView.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, moduleList));
		membersListView.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, studentList));

		moduleListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> myAdapter, View myView,
					int myItemInt, long mylng) {
				String selectedFromList = (String) (moduleListView
						.getItemAtPosition(myItemInt));
				moduleTextBox.setText(selectedFromList);
				moduleListView.setVisibility(View.INVISIBLE);
			}
		});

		membersListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> myAdapter, View myView,
					int myItemInt, long mylng) {
				String selectedFromList = (String) (membersListView
						.getItemAtPosition(myItemInt));
				//membersTextBox1.setText(selectedFromList+"\n");
				LinearLayout ll = (LinearLayout)findViewById(R.id.layout1);
				EditText t = new EditText(newProjectActivity.this);
				t.setText(selectedFromList);
				t.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.formvalidationerror), null);
				t.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
		        ll.addView(t);
		        membersTextBox1.setText("");
		        members.add(selectedFromList);
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
				
				dueTextBox.setText(dp1.getDayOfMonth()+"-"+dp1.getMonth()+"-"+dp1.getYear());
				dp1.setVisibility(View.INVISIBLE);
			}
		});
		
		moduleTextBox.addTextChangedListener(new TextWatcher() {

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
						newProjectActivity.this,
						android.R.layout.simple_list_item_1, moduleListFilter));
				
				if(moduleListView.getCount()>=1 && textLength1>0 )
				{
				moduleListView.setVisibility(View.VISIBLE);
				}

			}
		});

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
						if (membersTextBox1.getText().toString().equalsIgnoreCase(
										(String) studentList.get(i)
												.subSequence(0, textLength2))) {
							studentListFilter.add(studentList.get(i));
						}
					}
				}

				membersListView
						.setAdapter(new ArrayAdapter<String>(
								newProjectActivity.this,
								android.R.layout.simple_list_item_1,
								studentListFilter));
				
				if(membersListView.getCount()>=1 && textLength2>0)
				{
				membersListView.setVisibility(View.VISIBLE);
				}

			}
		});

		loginButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				JSONObject json1 = new JSONObject();
				try {
					json1.put("name", nameTextBox.getText());
					json1.put("description", aboutTextBox.getText());
					json1.put("leader", "u0909080@nus.edu.sg");
					json1.put("moduleCode", moduleTextBox.getText());
					json1.put("duedate", dueTextBox.getText());
					for(int i=0;i<members.size();i++)
					{
						json1.put("member"+i, members.get(i));
					}
					Log.d("JSON string",json1.toString());
				} catch (JSONException e) {
					e.printStackTrace();
				}

				
			}
		});
	}


	private void getModuleList() {
		try {
			HttpClient client = new DefaultHttpClient();
			ProjectxGlobalState globalData = (ProjectxGlobalState) getApplication();
			String getURL = "https://ivle.nus.edu.sg/api/Lapi.svc/Modules_Student?APIKey=tlXXFhEsNoTIVTJQruS2o&AuthToken="
					+ globalData.getAuthToken()
					+ "&Duration=0&IncludeAllInfo=false";
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
					String courseid = obj.getString("CourseCode");
					moduleList.add(courseid);
					Log.d("result", courseid);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("module-error", "could not get modules");
		}

	}

}
