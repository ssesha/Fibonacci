package com.icreate.projectx;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class myProjectsActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.myproject);
	    getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.logo1);
	}

}
