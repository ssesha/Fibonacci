package com.icreate.projectx;

import com.icreate.projectx.datamodel.ProjectxGlobalState;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ProjectManagerActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);

		Button loginButton = (Button) findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
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
		        		EditText uName = (EditText) findViewById(R.id.uname);
						EditText pass = (EditText) findViewById(R.id.pass);
						String userName = uName.getText().toString();
						String password = pass.getText().toString();
		        		Log.d("url: " + url, "onPageFinished");
		        		view.loadUrl("javascript:(function() { " +  "document.getElementById('userid').value = '"+ userName +"'; "+ "})()");
		        		view.loadUrl("javascript:(function() { " +  "document.getElementById('password').value = '"+password+"'; "+ "})()");
		        		view.loadUrl("javascript:(function() { " +  "document.getElementById('loginimg1').click(); "+ "})()");
		        		
		        		// when login is complete, the url will be login_result.ashx?r=0
		        		if (url.indexOf("/api/login/login_result.ashx") > 0)
		        		{
		        			// When login is successful, there will be a &r=0 in the url. It also means the return data is the token itself.
		        			if (url.indexOf("&r=0") > 0)
		        			{
		        				Log.d("success", "onPageFinished");        				
		        				Log.i("onPageFinished - before loading javascript", "");
		        				view.loadUrl("javascript:window.HTMLOUT.processHTML(document.getElementsByTagName('body')[0].innerHTML);");
		        				startActivity(new Intent(cont, homeActivity.class));
		        			}
		        		}
		        		else 
		        		{
		        			Log.d("error","login not complete");
		        		}
		        	}
		        });
				
				wv.loadUrl("https://ivle.nus.edu.sg/api/login/?apikey=tlXXFhEsNoTIVTJQruS2o");				
			
			}
		});
	}
	final Context cont = this;
	
	class MyJavaScriptInterface
    {
        @SuppressWarnings("unused")
        public void processHTML(String html)
        {
        	Log.d("onPageFinished ", "inside javascript interface");
        	 ProjectxGlobalState Token = (ProjectxGlobalState)getApplication();
        	 Token.setAuthToken(html);        	 
        }
    }

}