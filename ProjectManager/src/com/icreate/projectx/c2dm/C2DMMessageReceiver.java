package com.icreate.projectx.c2dm;

import com.icreate.projectx.R;
import com.icreate.projectx.homeActivity;
import com.icreate.projectx.R.drawable;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

public class C2DMMessageReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Log.w("C2DM", "Message Receiver called");
		if ("com.google.android.c2dm.intent.RECEIVE".equals(action)) {
			Log.w("C2DM", "Received message");
			final String message = intent.getStringExtra("message");
			Log.d("C2DM", "dmControl: message = " + message);
			createNotification(context, message);			
		}
	}

	public void createNotification(Context context, String message) {
		Intent intent = new Intent(context, homeActivity.class);
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
        .setContentText(message);
		Notification notification = builder.getNotification();
		notificationManager.notify(0, notification);
	}

}
