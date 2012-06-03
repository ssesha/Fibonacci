package com.icreate.projectx;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.icreate.projectx.datamodel.ProjectxGlobalState;

public class ProjectManagerActivity extends Activity {
	ProjectxGlobalState appGlobalState;
	private static int loginAttempts = 0;
	private Activity activity;
	private Context cont;
	private ProgressDialog dialog;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		cont = this;
		activity = this;
		//final ProgressBar progressBar = (ProgressBar) findViewById(R.id.loginProgress);
		dialog = new ProgressDialog(cont);
		dialog.setMessage("Logging in...");
		appGlobalState = (ProjectxGlobalState)getApplication();
		Button loginButton = (Button) findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				loginAttempts = 0;
				dialog.show();
				//progressBar.setVisibility(View.VISIBLE);
				WebView wv = (WebView) findViewById(R.id.WebViewLogin);
				wv.getSettings().setJavaScriptEnabled(true);
				WebSettings settings = wv.getSettings();
				settings.setSavePassword(false);
				wv.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
				wv.setWebViewClient(new WebViewClient() 
		        {               
		        	@Override               
		        	public void onPageFinished(WebView view, String url) 
		        	{
		        		
		        		Log.d("login", "came here");
		        		EditText uName = (EditText) findViewById(R.id.uname);
						EditText pass = (EditText) findViewById(R.id.pass);
						String userName = uName.getText().toString();
						appGlobalState.setUserid(userName);
						String password = pass.getText().toString();
						
						if(loginAttempts==0){
						Log.d("url: " + url, "LoadURL executed");
		        		view.loadUrl("javascript:(function() { " +  "document.getElementById('userid').value = '"+ userName +"'; "+ "document.getElementById('password').value = '"+password+"'; " + "document.getElementById('loginimg1').click(); " + "})()");
						}
		        		// when login is complete, the url will be login_result.ashx?r=0
						if (url.indexOf("/api/login/login_result.ashx") > 0)
		        		{
		        			// When login is successful, there will be a &r=0 in the url. It also means the return data is the token itself.
		        			if (url.indexOf("&r=0") > 0)
		        			{	Log.d("login", "came here");
		        				Log.d("success", "onPageFinished");        				
		        				Log.i("onPageFinished - before loading javascript", "");
		        				view.loadUrl("javascript:window.HTMLOUT.processHTML(document.getElementsByTagName('body')[0].innerHTML);");
		        				//progressBar.setVisibility(View.GONE);
		        			}
		        		}
		        		else if(loginAttempts>0)
		        		{	
		        			Log.d("error","login not complete");
		        			//progressBar.setVisibility(View.GONE);
		        			if (dialog.isShowing()) {
	        					dialog.dismiss();
	        				}
		        			Toast.makeText(cont, R.string.login_error,
									Toast.LENGTH_LONG).show();
		        		}
						System.out.println(loginAttempts);
						loginAttempts++;
		        	}
		        });
				
				wv.loadUrl("https://ivle.nus.edu.sg/api/login/?apikey=tlXXFhEsNoTIVTJQruS2o");
				wv.clearCache(true);
			}
		});
	}
	
	
	class MyJavaScriptInterface
    {
        public void processHTML(String html)
        {
        	Log.d("onPageFinished ", "inside javascript interface");
        	Log.d("authToken", html);
        	appGlobalState.setAuthToken(html);
        	JSONObject requestJson = new JSONObject();
			try {
				requestJson.put("user_id", appGlobalState.getUserid());
				requestJson.put("auth_token", appGlobalState.getAuthToken());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Log.i("sdjkfhdkjs", requestJson.toString());
			String url = "http://ec2-54-251-4-64.ap-southeast-1.compute.amazonaws.com/api/createUser.php";
			LoginTask loginTask = new LoginTask(cont, activity, requestJson, dialog);
			loginTask.execute(url);
        }
    }
	public class LoginTask extends AsyncTask<String, Void, String> {
		private Context context;
		private Activity callingActivity;
		private ProgressDialog dialog;
		private JSONObject requestJson;

		public LoginTask(Context context, Activity callingActivity, JSONObject requestData, ProgressDialog dialog) {
			this.context = context;
			this.callingActivity = callingActivity;
			this.requestJson = requestData;
			this.dialog = dialog;
		}

		@Override
		protected void onPreExecute() {
			if(!this.dialog.isShowing()){
				this.dialog.setMessage("Logging in...");
				this.dialog.show();
			}
		}

		@Override
		protected String doInBackground(String... urls) {
			String response = "";
			for (String url : urls) {
				HttpClient client = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(url);
				try {
					httpPost.setEntity(new StringEntity(requestJson.toString()));
					HttpResponse execute = client.execute(httpPost);
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
			try {
				JSONObject resultJson = new JSONObject(result);
				System.out.println(resultJson.toString());
				if(resultJson.getString("msg").equals("success")){
				context.startActivity(new Intent(cont, homeActivity.class));
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
}