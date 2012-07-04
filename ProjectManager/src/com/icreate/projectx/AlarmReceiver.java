package com.icreate.projectx;

import java.util.Calendar;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.RemoteViews;

import com.icreate.projectx.c2dm.C2DMNotificationHandler;

public class AlarmReceiver extends BroadcastReceiver {

	private static int NOTIFICATION_ID = 1;

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle extras = intent.getExtras();
		int taskId = extras.getInt("requestCode");
		Log.d("Ok then", " " + taskId);
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Resources res = context.getResources();
		long[] vibrate = { 0, 100, 200, 300 };
		Notification.Builder builder = new Notification.Builder(context);
		String delegate = "hh:mm aaa";
		String time2 = (String) DateFormat.format(delegate, Calendar.getInstance().getTime());
		Log.d("time2", time2);
		String task_name = extras.getString("task_name");
		String description = extras.getString("description");
		String projectName = extras.getString("project_name");
		int projectId = extras.getInt("project_id");

		Intent intenttoOpen = new Intent(context, C2DMNotificationHandler.class);
		intenttoOpen.putExtra("task_id", taskId);
		intenttoOpen.putExtra("project_id", projectId);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intenttoOpen, 0);

		RemoteViews layout = new RemoteViews(context.getPackageName(), R.layout.notification);
		layout.setTextViewText(R.id.notification_title, task_name);
		layout.setTextViewText(R.id.notification_subtitle, "in " + projectName + " is due");
		layout.setTextViewText(R.id.notification_time, time2);
		Bitmap largeIconTemp = BitmapFactory.decodeResource(res, R.drawable.trialicon);
		Bitmap largeIcon = Bitmap.createScaledBitmap(largeIconTemp, res.getDimensionPixelSize(android.R.dimen.notification_large_icon_width),
				res.getDimensionPixelSize(android.R.dimen.notification_large_icon_height), false);
		largeIconTemp.recycle();

		builder.setLargeIcon(largeIcon);
		builder.setContentIntent(contentIntent).setSmallIcon(R.drawable.trialicon).setTicker("Task " + task_name + " is due").setWhen(System.currentTimeMillis()).setAutoCancel(true)
				.setContent(layout).setVibrate(vibrate).setContentInfo(time2);
		Notification notification = builder.getNotification();
		notification.defaults |= Notification.DEFAULT_ALL;
		notificationManager.notify(NOTIFICATION_ID++, notification);
		PendingIntent sender = PendingIntent.getBroadcast(context, taskId, new Intent(context, AlarmReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);
		sender.cancel();
	}
};