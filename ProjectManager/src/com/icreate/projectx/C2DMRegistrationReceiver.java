package com.icreate.projectx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.util.Log;

import com.icreate.projectx.datamodel.ProjectxGlobalState;

public class C2DMRegistrationReceiver extends BroadcastReceiver {
	ProjectxGlobalState global;
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		//intent
		Log.d("C2DM", "Registration Receiver called");
		if ("com.google.android.c2dm.intent.REGISTRATION".equals(action)) {
			Log.w("C2DM", "Received registration ID");
			final String registrationId = intent
					.getStringExtra("registration_id");
			String error = intent.getStringExtra("error");
			//Bundle extra = intent.getExtras();
			Log.d("C2DM", "dmControl: registrationId = " + registrationId
					+ ", error = " + error);
			String deviceId = Secure.getString(context.getContentResolver(),
					Secure.ANDROID_ID);
			Log.d("Device Id", deviceId);
			createNotification(context, registrationId);
			String url = "http://ec2-54-251-4-64.ap-southeast-1.compute.amazonaws.com/api/createDeviceTokens.php";
			List<NameValuePair> params = new LinkedList<NameValuePair>();
			params.add(new BasicNameValuePair("deviceid", deviceId));
			String paramString = URLEncodedUtils.format(params, "utf-8");
			url += "?" + paramString;
			params.clear();
			params.add(new BasicNameValuePair("devicetoken", registrationId));
			paramString = URLEncodedUtils.format(params, "utf-8");
			url += "&" + paramString;
			params.clear();
			//global = (ProjectxGlobalState) getApplication();
			String userId = ProjectXPreferences.readString(context, ProjectXPreferences.USER, "");
			Log.d("userid", userId);
			//Account[] accounts = AccountManager.get(context).getAccountsByType("com.google");
			params.add(new BasicNameValuePair("userid", userId));
			paramString = URLEncodedUtils.format(params, "utf-8");
			url += "&" + paramString;
			Log.d("create task", url);
			SendRegistrationIdToServer task = new SendRegistrationIdToServer();
			task.execute(url);
			//sendRegistrationIdToServer(deviceId, registrationId);
			// Also save it in the preference to be able to show it later
			//saveRegistrationId(context, registrationId);
		}
	}

	private void saveRegistrationId(Context context, String registrationId) {
	/*	SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor edit = prefs.edit();
		edit.putString(C2DMClientActivity.AUTH, registrationId);
		edit.commit();*/
	}

	public void createNotification(Context context, String registrationId) {
		Intent intent = new Intent(context, homeActivity.class);
		intent.putExtra("registration_id", registrationId);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				intent, 0);
		
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Resources res = context.getResources();
		Notification.Builder builder = new Notification.Builder(context);
		
		builder.setContentIntent(pendingIntent)
		            .setSmallIcon(R.drawable.ic_launcher)
		            .setTicker("New Notification from Project-X")
		            .setWhen(System.currentTimeMillis())
		            .setAutoCancel(true)
		            .setContentTitle("Project-X")
		            .setContentText("You have successfully registered for notifications");
		Notification notification = builder.getNotification();

		notificationManager.notify(0, notification);
	}

	// Incorrect usage as the receiver may be canceled at any time
	// do this in an service and in an own thread
	/*public void sendRegistrationIdToServer(String deviceId,
			String registrationId) {
		Log.d("C2DM", "Sending registration ID to my application server");
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost("http://vogellac2dm.appspot.com/register");
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			// Get the deviceID
			nameValuePairs.add(new BasicNameValuePair("deviceid", deviceId));
			nameValuePairs.add(new BasicNameValuePair("registrationid",
					registrationId));

			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = client.execute(post);
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			String line = "";
			while ((line = rd.readLine()) != null) {
				Log.e("HttpResponse", line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	*/
	private class SendRegistrationIdToServer extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... urls) {
			// TODO Auto-generated method stub
			Log.d("C2DM", "Sending registration ID to my application server");
			for(String url:urls)
			{
				HttpClient client = new DefaultHttpClient();
				HttpPost post = new HttpPost(url);
				try {				
					HttpResponse response = client.execute(post);
					BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

					String line = "";
					while ((line = rd.readLine()) != null) {
						Log.d("HttpResponse", line);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return null;
		}
	}
	
}