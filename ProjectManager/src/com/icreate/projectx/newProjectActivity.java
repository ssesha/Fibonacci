package com.icreate.projectx;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class newProjectActivity extends Activity {
	private ListView moduleListView,membersListView;
	private EditText moduleTextBox,membersTextBox;
	private List<String> moduleList = new ArrayList<String>();
	private List<String> studentList = new ArrayList<String>();
	private ArrayList<String> moduleListFilter = new ArrayList<String>();
	private ArrayList<String> studentListFilter = new ArrayList<String>();
	int textLength1 = 0;
	int textLength2 = 0;

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
		membersTextBox = (EditText) findViewById(R.id.membersTextBox);
		
		moduleList.add("CS1231");
		moduleList.add("CS1101");
		moduleList.add("CS2103");
		moduleList.add("CS3215");
		moduleList.add("CS4444");
		studentList.add("Oinker");
		studentList.add("Abs");
		studentList.add("Abbinayaa");
		studentList.add("oink");
		studentList.add("oinky");
		studentList.add("piggy");
		
		moduleListView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, moduleList));
		membersListView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, studentList));
		
		moduleTextBox.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				textLength1 = moduleTextBox.getText().length();
				moduleListFilter.clear();
				for (int i = 0; i < moduleList.size(); i++) {
					if (textLength1 <= moduleList.get(i).length()) {
						if (moduleTextBox.getText()
								.toString()
								.equalsIgnoreCase(
										(String) moduleList.get(i).subSequence(0,
												textLength1))) {
							moduleListFilter.add(moduleList.get(i));
						}
					}
				}

				moduleListView.setAdapter(new ArrayAdapter<String>(newProjectActivity.this,
						android.R.layout.simple_list_item_1, moduleListFilter));

			}
		});
		
		membersTextBox.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				textLength2 = membersTextBox.getText().length();
				studentListFilter.clear();
				for (int i = 0; i < studentList.size(); i++) {
					if (textLength2 <= studentList.get(i).length()) {
						if (membersTextBox.getText()
								.toString()
								.equalsIgnoreCase(
										(String) studentList.get(i).subSequence(0,
												textLength2))) {
							studentListFilter.add(studentList.get(i));
						}
					}
				}

				membersListView.setAdapter(new ArrayAdapter<String>(newProjectActivity.this,
						android.R.layout.simple_list_item_1, studentListFilter));

			}
		});
	}

}
