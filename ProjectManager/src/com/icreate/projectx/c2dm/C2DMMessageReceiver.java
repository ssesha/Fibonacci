package com.icreate.projectx.c2dm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.RemoteViews;

import com.icreate.projectx.R;
import com.icreate.projectx.homeActivity;

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
		long[] vibrate = { 0, 100, 200, 300 };
		Notification.Builder builder = new Notification.Builder(context);
		RemoteViews layout = new RemoteViews(context.getPackageName(),
				R.layout.notification);
		layout.setTextViewText(R.id.notification_title, "Project-X");
		layout.setTextViewText(R.id.notification_subtitle, message);
		Bitmap largeIconTemp = BitmapFactory.decodeResource(res,
                R.drawable.notification_default_largeicon);
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
				.setVibrate(vibrate);
		Notification notification = builder.getNotification();
		notification.defaults |= Notification.DEFAULT_ALL;
		notificationManager.notify(0, notification);
	}

}
