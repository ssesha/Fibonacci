package com.icreate.projectx;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.icreate.projectx.datamodel.ProjectxGlobalState;

public class AddMemberActivity extends Activity {

	private TextView logoText;
	private EditText memberSearch;
	private ListView addMemberList;
	private ImageButton logoButton;

	private final List<String> studentList = new ArrayList<String>();
	private final ArrayList<String> studentListFilter = new ArrayList<String>();
	private final ArrayList<String> memberid = new ArrayList<String>();
	private final List<String> student_id_list = new ArrayList<String>();
	private List<String> moduleId = new ArrayList<String>();

	String studentlist = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.addmember);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.logo1);

		Typeface font = Typeface.createFromAsset(getAssets(), "EraserDust.ttf");
		final Context cont = this;

		logoButton = (ImageButton) findViewById(R.id.logoImageButton);
		logoText = (TextView) findViewById(R.id.logoText);
		memberSearch = (EditText) findViewById(R.id.memberSearch);
		addMemberList = (ListView) findViewById(R.id.addMemberList);
		addMemberList.setAdapter(new ArrayAdapter<String>(AddMemberActivity.this, R.layout.addmemberlistitem, R.id.memberitemTextView, studentList));

		ProjectxGlobalState globalData = (ProjectxGlobalState) getApplication();
		moduleId = globalData.getModuleId();
		Bundle extras = getIntent().getExtras();

		if (extras != null) {
			studentlist = extras.getString("studentListJson");

			JSONArray arr = null;
			try {
				arr = new JSONArray(studentlist);

				// Log.d("json", json.toString(3));

				for (int i = 0; i < arr.length(); i++) {
					JSONObject obj = arr.getJSONObject(i);
					String name = obj.getString("Name").toLowerCase();
					StringTokenizer st = new StringTokenizer(name, " ", true);
					String token;
					name = "";
					while (st.hasMoreTokens()) {
						token = st.nextToken();
						name += token.substring(0, 1).toUpperCase() + token.substring(1);
					}
					studentList.add(name);
					student_id_list.add(obj.getString("UserID"));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		logoText.setTypeface(font);
		logoText.setText("Add Members");
		logoButton.setBackgroundResource(R.drawable.newprojectbutton);

		logoButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(cont, newProjectActivity.class));
			}
		});

		addMemberList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
				String selectedFromList = (String) (addMemberList.getItemAtPosition(myItemInt));
				int index = studentList.indexOf(selectedFromList);
				memberid.add(student_id_list.get(index));
				addMemberList.setVisibility(View.INVISIBLE);
			}
		});

		memberSearch.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				int textLength2 = memberSearch.getText().length();
				studentListFilter.clear();
				for (int i = 0; i < studentList.size(); i++) {
					if (textLength2 <= studentList.get(i).length()) {
						if (memberSearch.getText().toString().equalsIgnoreCase((String) studentList.get(i).subSequence(0, textLength2))) {
							studentListFilter.add(studentList.get(i));
						}
					}
				}
				addMemberList.setAdapter(new ArrayAdapter<String>(AddMemberActivity.this, R.layout.addmemberlistitem, R.id.memberitemTextView, studentListFilter));

			}
		});

	}

}