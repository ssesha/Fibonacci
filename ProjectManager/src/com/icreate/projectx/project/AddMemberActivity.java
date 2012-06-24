package com.icreate.projectx.project;

import java.util.ArrayList;
import java.util.List;

import com.icreate.projectx.R;
import com.icreate.projectx.R.drawable;
import com.icreate.projectx.R.id;
import com.icreate.projectx.R.layout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class AddMemberActivity extends Activity {

	private TextView logoText;
	private EditText memberSearch;
	private ListView addMemberList;
	private ImageButton logoButton;

	private final List<Pair<String, Boolean>> studentPair = new ArrayList<Pair<String, Boolean>>();
	private final List<Pair<String, Boolean>> filteredPair = new ArrayList<Pair<String, Boolean>>();
	private List<String> student_id_list = new ArrayList<String>();

	private final ArrayList<String> memberid = new ArrayList<String>();
	private final ArrayList<String> membername = new ArrayList<String>();

	public final static int SUCCESS_RETURN_CODE = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.addmember);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.logo1);

		Typeface font = Typeface.createFromAsset(getAssets(), "EraserDust.ttf");

		logoButton = (ImageButton) findViewById(R.id.logoImageButton);
		logoText = (TextView) findViewById(R.id.logoText);
		memberSearch = (EditText) findViewById(R.id.memberSearch);
		addMemberList = (ListView) findViewById(R.id.addMemberList);

		Bundle extras = getIntent().getExtras();

		if (extras != null) {
			ArrayList<String> studentList = extras.getStringArrayList("studentList");
			student_id_list = extras.getStringArrayList("student_id_list");
			ArrayList<String> selectedMembers = extras.getStringArrayList("selectedMembers");

			for (int i = 0; i < studentList.size(); i++) {
				Pair<String, Boolean> tempPair = null;
				boolean added = false;
				for (int j = 0; j < selectedMembers.size(); j++) {
					if (selectedMembers.get(j).equals(studentList.get(i))) {
						membername.add(selectedMembers.get(j));
						memberid.add(student_id_list.get(i));
						tempPair = new Pair<String, Boolean>(studentList.get(i), true);
						added = true;
					}
				}
				if (!added) {
					tempPair = new Pair<String, Boolean>(studentList.get(i), false);
				}
				studentPair.add(tempPair);
			}

		}
		filteredPair.addAll(studentPair);
		addMemberList.setAdapter(new AddMemberBaseAdapter(AddMemberActivity.this));

		logoText.setTypeface(font);
		logoText.setText("Add Members");
		logoButton.setBackgroundResource(R.drawable.newprojectbutton);

		logoButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent intent = new Intent();
				Bundle b = new Bundle();
				b.putStringArrayList("MemberNameList", membername);
				b.putStringArrayList("MemberIdList", memberid);
				intent.putExtras(b);
				setResult(SUCCESS_RETURN_CODE, intent);
				finish();
			}
		});

		memberSearch.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				int textLength2 = memberSearch.getText().length();
				filteredPair.clear();
				for (int i = 0; i < studentPair.size(); i++) {
					if (textLength2 <= studentPair.get(i).first.length()) {
						if (memberSearch.getText().toString().equalsIgnoreCase((String) studentPair.get(i).first.subSequence(0, textLength2))) {
							Pair<String, Boolean> tempPair = new Pair<String, Boolean>(studentPair.get(i).first, studentPair.get(i).second);
							filteredPair.add(tempPair);
						}
					}
				}
				addMemberList.setAdapter(new AddMemberBaseAdapter(AddMemberActivity.this));
			}
		});

	}

	public class AddMemberBaseAdapter extends BaseAdapter {

		private final LayoutInflater mInflater;

		public AddMemberBaseAdapter(Context context) {
			mInflater = LayoutInflater.from(context);
		}

		public int getCount() {
			return filteredPair.size();
		}

		public Object getItem(int position) {
			return filteredPair.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			Typeface font = Typeface.createFromAsset(getAssets(), "EraserDust.ttf");
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.addmemberlistitem, null);
				holder = new ViewHolder();
				holder.studentName = (TextView) convertView.findViewById(R.id.memberitemTextView);
				holder.studentName.setTypeface(font);
				holder.status = (CheckBox) convertView.findViewById(R.id.memberitemCheckBox);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.studentName.setText(filteredPair.get(position).first);
			holder.status.setChecked(filteredPair.get(position).second);
			holder.status.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					if (holder.status.isChecked()) {
						int index = studentPair.indexOf(filteredPair.get(position));
						studentPair.set(studentPair.indexOf(filteredPair.get(position)), new Pair<String, Boolean>(filteredPair.get(position).first, true));
						filteredPair.set(position, new Pair<String, Boolean>(filteredPair.get(position).first, true));
						membername.add(studentPair.get(index).first);
						memberid.add(student_id_list.get(index));
					} else {
						int index = studentPair.indexOf(filteredPair.get(position));
						studentPair.set(studentPair.indexOf(filteredPair.get(position)), new Pair<String, Boolean>(filteredPair.get(position).first, false));
						filteredPair.set(position, new Pair<String, Boolean>(filteredPair.get(position).first, false));
						membername.remove(studentPair.get(index).first);
						memberid.remove(student_id_list.get(index));
					}
				}
			});
			return convertView;
		}

		class ViewHolder {
			TextView studentName;
			CheckBox status;
		}

	}
}