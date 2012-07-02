package com.icreate.projectx;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class ProfileActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.profileview);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.logo1);
	}
}
