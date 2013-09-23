package com.goranpetrusic.g_tweet;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class RefreshService extends IntentService {

	static final String TAG = "Refresh service";

	public RefreshService() {
		super(TAG);

	}

	@Override
	public void onCreate() {
		super.onCreate();

		Log.d(TAG, "onCreated");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		((GTweetApp) getApplication()).pullAndInsert();
		Log.d(TAG, "onHandleIntent");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroyed");
	}
}