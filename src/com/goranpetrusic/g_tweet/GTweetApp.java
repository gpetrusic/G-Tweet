package com.goranpetrusic.g_tweet;

import java.util.List;

import winterwell.jtwitter.OAuthSignpostClient;
import winterwell.jtwitter.Status;
import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.util.Log;

public class GTweetApp extends Application implements
		OnSharedPreferenceChangeListener {

	static final String TAG = "GTweetApp";
	private OAuthSignpostClient oauthClient;
	public static final String ACTION_NEW_STATUS = "com.goranpetrusic.g_tweet.NEW_STATUS";
	public static final String ACTION_REFRESH = "com.goranpetrusic.g_tweet.RefreshService";
	public static final String ACTION_REFRESH_ALARM = "com.goranpetrusic.g_tweet.RefreshAlarm";

	SharedPreferences prefs;
	StatusData statusData;

	public Twitter getTwitter() {
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String token = prefs.getString("token", null);
		String tokenSecret = prefs.getString("tokenSecret", null);
		oauthClient = new OAuthSignpostClient(LoginActivity.OAUTH_KEY,
				LoginActivity.OAUTH_SECRET, token, tokenSecret);
		return new Twitter("", oauthClient);
	}

	@Override
	public void onCreate() {
		super.onCreate();

		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.registerOnSharedPreferenceChangeListener(this);

		statusData = new StatusData(this);

	}

	static final Intent refreshAlarm = new Intent(ACTION_REFRESH_ALARM);

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		
		// If there is a change in preferences, notify the Alarm
		// (BootReceiver.class)
		// (e.g. user changes refresh interval)
		sendBroadcast(refreshAlarm);
	}

	long lastTimestampSeen = -1;

	public int pullAndInsert() {
		
		
		
		int count = 0;
		try {
			List<Status> timeline = getTwitter().getHomeTimeline();

			for (Status status : timeline) {
				statusData.insert(status);
				if (status.createdAt.getTime() > lastTimestampSeen) {
					count++;
					lastTimestampSeen = status.createdAt.getTime();
				}
				Log.d(TAG, String.format("%s: %s", status.user.name,
						status.user.status));
			}
		} catch (TwitterException e) {
			Log.e(TAG, "Failed to pull timeline", e);
		}
		if (count > 0) {

			sendBroadcast(new Intent(ACTION_NEW_STATUS)
					.putExtra("count", count));
		}
		return count;
	}
}
