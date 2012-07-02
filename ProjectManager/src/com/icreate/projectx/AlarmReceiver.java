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

public class AlarmReceiver extends BroadcastReceiver {

	private static int NOTIFICATION_ID = 1;

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle extras = intent.getExtras();
		int x = extras.getInt("requestCode");
		Log.d("Ok then", " " + x);
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		PendingIntent contentIntent = PendingIntent.getActivity(context, x, new Intent(context, AlarmReceiver.class), 0);
		Resources res = context.getResources();
		long[] vibrate = { 0, 100, 200, 300 };
		Notification.Builder builder = new Notification.Builder(context);
		String delegate = "hh:mm aaa";
		String time2 = (String) DateFormat.format(delegate, Calendar.getInstance().getTime());
		Log.d("time2", time2);
		String title = extras.getString("title");
		String note = extras.getString("note");
		RemoteViews layout = new RemoteViews(context.getPackageName(), R.layout.notification);
		layout.setTextViewText(R.id.notification_title, "Project-X");
		layout.setTextViewText(R.id.notification_subtitle, title + " " + note);
		layout.setTextViewText(R.id.notification_time, time2);
		Bitmap largeIconTemp = BitmapFactory.decodeResource(res, R.drawable.trialicon);
		Bitmap largeIcon = Bitmap.createScaledBitmap(largeIconTemp, res.getDimensionPixelSize(android.R.dimen.notification_large_icon_width),
				res.getDimensionPixelSize(android.R.dimen.notification_large_icon_height), false);
		largeIconTemp.recycle();

		builder.setLargeIcon(largeIcon);
		builder.setContentIntent(contentIntent).setSmallIcon(R.drawable.trialicon).setTicker("New Notification from Project-X").setWhen(System.currentTimeMillis()).setAutoCancel(true)
				.setContent(layout).setVibrate(vibrate).setContentInfo(time2);
		Notification notification = builder.getNotification();
		notification.defaults |= Notification.DEFAULT_ALL;
		notificationManager.notify(NOTIFICATION_ID++, notification);
	}
};