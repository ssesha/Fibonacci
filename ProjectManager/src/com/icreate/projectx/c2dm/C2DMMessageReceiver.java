package com.icreate.projectx.c2dm;

import java.lang.annotation.Annotation;
import java.util.Calendar;

import javax.annotation.RegEx;
import javax.annotation.meta.When;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.widget.RemoteViews;

import com.icreate.projectx.R;
import com.icreate.projectx.homeActivity;
import com.icreate.projectx.task.TaskViewActivity;

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
		String[] temp;
		String delimiter = "-";
		temp = message.split(delimiter);
		Log.d("C2dm received msg", message);
		Log.d("temp", temp[0]);
		Log.d("temp", temp[1]);
		Log.d("temp", temp[2]);
		Log.d("temp", temp[3]);
		Log.d("temp", temp[4]);
		Log.d("temp", temp[5]);
		Log.d("temp", temp[6]);
		Log.d("temp", temp[7]);
		Intent intent = new Intent(context, C2DMNotificationHandler.class);		
		intent.putExtra("activity", temp[2]);
		for(int i=4;i<temp.length;i++){
			Log.d("in for loop", temp[i]+" "+temp[i+1]);
			intent.putExtra(temp[i], Integer.parseInt(temp[++i]));			
		}
		int message_id = Integer.parseInt(temp[1]);
		
		Log.d("C2dm - message-id", temp[1]);
		Log.d("c2dm- message", temp[0]);
		Log.d("c2dm - activity", temp[2]);
		Log.d("c2dm - extra", temp[3]);		
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				intent, 0);
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Resources res = context.getResources();
		long[] vibrate = { 0, 100, 200, 300 };
		Notification.Builder builder = new Notification.Builder(context);
		String delegate = "hh:mm aaa"; 
		String time2 = (String) DateFormat.format(delegate,Calendar.getInstance().getTime());
		Log.d("time2", time2);
		RemoteViews layout = new RemoteViews(context.getPackageName(),
				R.layout.notification);
		layout.setTextViewText(R.id.notification_title, "Project-X");
		layout.setTextViewText(R.id.notification_subtitle, temp[0]);
		layout.setTextViewText(R.id.notification_time, time2);
		Bitmap largeIconTemp = BitmapFactory.decodeResource(res,
                R.drawable.trialicon);
        Bitmap largeIcon = Bitmap.createScaledBitmap(
                largeIconTemp,
                res.getDimensionPixelSize(android.R.dimen.notification_large_icon_width),
                res.getDimensionPixelSize(android.R.dimen.notification_large_icon_height),
                false);
        largeIconTemp.recycle();

        builder.setLargeIcon(largeIcon);		
		builder.setContentIntent(pendingIntent)
				.setSmallIcon(R.drawable.trialicon)
				.setTicker("New Notification from Project-X")
				.setWhen(System.currentTimeMillis()).setAutoCancel(true).setContent(layout)
				.setVibrate(vibrate).setContentInfo(time2);
		Notification notification = builder.getNotification();
		notification.defaults |= Notification.DEFAULT_ALL;
		notificationManager.notify(message_id, notification);
	}

}
