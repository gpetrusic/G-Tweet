package com.goranpetrusic.g_tweet;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;

public class RefreshScheduleReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		// Read SharedPreferences in order to get the interval!

		long interval = Long.parseLong(PreferenceManager
				.getDefaultSharedPreferences(context).getString("delay",
						"90000"));

		// Create a pending intent
		PendingIntent operation = PendingIntent.getService(context, -1,
				new Intent(context, RefreshService.class),
				PendingIntent.FLAG_UPDATE_CURRENT);

		// Check the AlarmManager and if there is any previous interval -
		// cancel it
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(operation);

		// Set the new interval!
		if (interval > 0) {

			alarmManager.setInexactRepeating(AlarmManager.RTC,
					System.currentTimeMillis(), interval, operation);
		}
		Log.d("BootReceiver", "onReceive: delay: " + interval);
	}
}
