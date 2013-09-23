package com.goranpetrusic.g_tweet;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

public class TimelineActivity extends ListActivity {
	private static final String TAG = "TimelineActivity";

	static final String[] FROM = {StatusData.C_USER, StatusData.C_TEXT, StatusData.C_CREATED_AT};
	static final int[] TO = {R.id.text_user, R.id.text_tweet_text, R.id.text_created_at};
	Cursor cursor;
	SimpleCursorAdapter adapter;
	TimelineReceiver receiver;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		cursor = ((GTweetApp) getApplication()).statusData.query();

		adapter = new SimpleCursorAdapter(this,
				R.layout.tweet_layout, cursor, FROM, TO);
		adapter.setViewBinder(VIEW_BINDER);
		
		setTitle(R.string.timeline);
		setListAdapter(adapter);

	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(receiver == null) receiver = new TimelineReceiver();
		registerReceiver(receiver, new IntentFilter(GTweetApp.ACTION_NEW_STATUS));
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		unregisterReceiver(receiver);
	}

	// menu stuff
	
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			// Inflate the menu; this adds items to the action bar if it is present.
			getMenuInflater().inflate(R.menu.status_update, menu);
			getMenuInflater().inflate(R.menu.menu, menu);
			return true;
		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {

			Intent intentUpdater = new Intent(this, UpdaterService.class);
			Intent intentRefresh = new Intent(this, RefreshService.class);

			switch (item.getItemId()) {

			case R.id.start_service:
				startService(intentUpdater);
				return true;
			case R.id.stop_service:
				stopService(intentUpdater);
				return true;
			case R.id.item_refresh_service:
				startService(intentRefresh);
				return true;
			case R.id.item_prefs:
				startActivity(new Intent(this, PrefsActivity.class));
				return true;
			case R.id.item1_status_update:
				startActivity(new Intent(this, StatusUpdateActivity.class));
				return true;
			default:

				return false;

			}
		}

	static final ViewBinder VIEW_BINDER = new ViewBinder (){

		@Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			if( view.getId() != R.id.text_created_at)return false;
			
			long time = cursor.getLong(cursor.getColumnIndex(StatusData.C_CREATED_AT));
			CharSequence relativeTime = DateUtils.getRelativeTimeSpanString( time);
			((TextView)view).setText(relativeTime);
			
			return true;
		}
	};
	
	class TimelineReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			cursor = ((GTweetApp) getApplication()).statusData.query();
			adapter.changeCursor(cursor);
			Log.d(TAG, "TimelineReceiver onReceived with count:" 
					+intent.getIntExtra("count", 0));
		}	
	}
}
