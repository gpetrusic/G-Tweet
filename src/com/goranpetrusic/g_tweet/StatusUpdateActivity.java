package com.goranpetrusic.g_tweet;

import winterwell.jtwitter.OAuthSignpostClient;
import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class StatusUpdateActivity extends Activity {

	EditText statusUpdate;
	private OAuthSignpostClient oauthClient;
	private SharedPreferences prefs;
	ProgressDialog pDialog;
	
	public Twitter getTwitter() {
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String token = prefs.getString("token", null);
		String tokenSecret = prefs.getString("tokenSecret", null);
		oauthClient = new OAuthSignpostClient(LoginActivity.OAUTH_KEY,
				LoginActivity.OAUTH_SECRET, token, tokenSecret);
		return new Twitter("", oauthClient);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_status_update);

		statusUpdate = (EditText) findViewById(R.id.editText_statusUpdate);

	}

	

	@Override
	protected void onStop() {
		super.onStop();
		// stop tracing
		// Debug.stopMethodTracing();
	}

	public void onClick(View v) {
		final String statusText = statusUpdate.getText().toString();

		new PostToTwitter().execute(statusText);

		Log.d("StatusActivity", "onClicked with text: " + statusText);

	}

	// New thread
	class PostToTwitter extends AsyncTask<String, Void, String> {

		// notify the user with a dialog about ongoing update
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(StatusUpdateActivity.this);
	        pDialog.setMessage("Updating to twitter...");
	        pDialog.setIndeterminate(false);
	        pDialog.setCancelable(false);
	        pDialog.show();
		}

		@Override
		// String... - variable length argument - no limit
		protected String doInBackground(String... params) {
			try {

				getTwitter().setStatus(params[0]);
				return "Status successfully updated!\n" + params[0];

			} catch (TwitterException e) {

				e.printStackTrace();
				return "Failed posting!\n" + params[0];

			}
		}

		@Override
		// UI thread
		
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			pDialog.dismiss();
			Toast.makeText(StatusUpdateActivity.this, result, Toast.LENGTH_LONG)
					.show();
			
			statusUpdate.setText("");
		}
	}

}
