package com.icreate.projectx;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import org.achartengine.GraphicalView;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.icreate.projectx.datamodel.ProjectList;
import com.icreate.projectx.datamodel.ProjectxGlobalState;
import com.icreate.projectx.project.ProjectChartActivity;

public class ProfileActivity extends Activity {
	@SuppressWarnings("unused")
	private TextView logoText, NameText;
	private ImageButton logoButton;
	private Context cont;
	private ProjectxGlobalState global;
	private GraphicalView mChartView;
	private LinearLayout chartLayout;
	private Intent chartIntent;
	private final int subActivityID = 98765;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cont = this;
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.profileview);		
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.logo1);
		Typeface font = Typeface.createFromAsset(getAssets(), "EraserDust.ttf");
		logoText = (TextView) findViewById(R.id.logoText);
		logoText.setTypeface(font);
		logoText.setTextColor(R.color.white);
		logoText.setText("My Profile");
		chartIntent = new Intent(cont, ProjectChartActivity.class);
		chartIntent.putExtra("activity", 0);
		//NameText = (TextView) findViewById(R.id.profileName);
		logoButton = (ImageButton) findViewById(R.id.logoImageButton);
		logoButton.setBackgroundResource(R.drawable.home_button);
		logoButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(cont, homeActivity.class));				
			}
		});
		
		chartLayout = (LinearLayout) findViewById(R.id.chart);
		
		String url = "http://ec2-54-251-4-64.ap-southeast-1.compute.amazonaws.com/api/getProfile.php";
		global = (ProjectxGlobalState) getApplication();
		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair("user_id", global.getUserid()));
		String paramString = URLEncodedUtils.format(params, "utf-8");
		url += "?" + paramString;
		Log.d("profile", url);
		ProgressDialog dialog = new ProgressDialog(cont);
		dialog.setMessage("Loading Profile...");
		ProjectListTask task = new ProjectListTask(cont, this, dialog, chartLayout);
		task.execute(url);
	}
	
	private class ProjectListTask extends AsyncTask<String, Void, String> {
		private final Context context;
		private final Activity callingActivity;
		private final ProgressDialog dialog;
		private final LinearLayout chartLayout;

		public ProjectListTask(Context context, Activity callingActivity, ProgressDialog dialog, LinearLayout chartLayout) {
			this.context = context;
			this.callingActivity = callingActivity;
			this.dialog = dialog;
			this.chartLayout = chartLayout;
		}
		
		@Override
		protected void onPreExecute() {
			if (dialog != null) {
				if (!this.dialog.isShowing()) {
					this.dialog.setMessage("Loading...");
					this.dialog.show();
					this.dialog.setCanceledOnTouchOutside(false);
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
					//Project temp = gson.fromJson(result, Project.class);
					ProjectList projectsContainer = gson.fromJson(result, ProjectList.class);
					global.setProjectList(projectsContainer);
					IDemoChart mCharts = new ProfileProgressChart();
					mChartView = mCharts.execute(cont, projectsContainer.getProjects(), true);
					chartLayout.addView(mChartView, new LayoutParams
							(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
					mChartView.setClickable(true);
					mChartView.setOnClickListener(new View.OnClickListener() {			
						@Override
						public void onClick(View v) {
							startActivityForResult(chartIntent, subActivityID);				
						}
					});		
				} else {
					Toast.makeText(context, "Project Lists empty", Toast.LENGTH_LONG).show();
				}	
			} catch (Exception e) {
				Toast.makeText(context, R.string.server_error, Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
		}
	}	
}
