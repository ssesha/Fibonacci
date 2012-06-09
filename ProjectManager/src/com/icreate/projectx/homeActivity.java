package com.icreate.projectx;

import com.icreate.projectx.datamodel.ProjectxGlobalState;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class homeActivity extends Activity {

	private TextView logoText;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.home);
	    getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.logo1);
	    
	    Typeface font = Typeface.createFromAsset(getAssets(),"EraserDust.ttf");
	    logoText = (TextView) findViewById(R.id.logoText);
	    logoText.setTypeface(font);
	    logoText.setText("Project-X");
	    logoText.setTextColor(R.color.white);
	    
	    ProjectxGlobalState globalData = (ProjectxGlobalState)getApplication();
	    //globalData.getApiKey();
	    
	    ImageButton newProjectButton = (ImageButton) findViewById(R.id.logoImageButton);
	    newProjectButton.setBackgroundResource(R.drawable.houseicon);
	    Button myProjectButton = (Button) findViewById(R.id.myProjectButton);
	    
	    final Context cont = this;
	    newProjectButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(cont, newProjectActivity.class));				
			}
		});
	    myProjectButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(cont, myProjectsActivity.class));				
			}
		});
	    
    }
}