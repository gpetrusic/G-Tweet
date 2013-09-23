package com.goranpetrusic.g_tweet;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import winterwell.jtwitter.Twitter;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class LoginActivity extends Activity {

	Button authorize;

	private static final String TAG = "LoginActivity";
	static final String OAUTH_KEY = "1YopJDUJjTOOOy31TmUpw";
	static final String OAUTH_SECRET = "a2YOGfRariSeQrwubo3lBhtGbAt76snVeXwBJbM";
	private static final String OAUTH_CALLBACK_SCHEME = "x-oauthflow-twitter";
	private static final String OAUTH_CALLBACK_URL = OAUTH_CALLBACK_SCHEME
			+ "://callback";
	//private static final String TWITTER_USER = "goran.petrusic@outlook.com";

	private OAuthConsumer mConsumer;
	private OAuthProvider mProvider;
	private SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		authorize = (Button) findViewById(R.id.button1);

		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		if (prefs.getString("token", null) != null) {
			startActivity(new Intent(this, StatusUpdateActivity.class));
			finish();
		} else
			new OAuthAuthorizeTask().execute();

	}

	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Log.d(TAG, "intent: " + intent);

		// Check if this is a callback from OAuth
		Uri uri = intent.getData();
		if (uri != null && uri.getScheme().equals(OAUTH_CALLBACK_SCHEME)) {
			Log.d(TAG, "callback: " + uri.getPath());

			String verifier = uri.getQueryParameter(OAuth.OAUTH_VERIFIER);
			Log.d(TAG, "verifier: " + verifier);

			new RetrieveAccessTokenTask().execute(verifier);
		}

	}

	public void onClickAuthorize(View view) {
		new OAuthAuthorizeTask().execute();
	}



	/* Responsible for starting the Twitter authorization */
	class OAuthAuthorizeTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			String authUrl;
			String message = null;
			
			mConsumer = new CommonsHttpOAuthConsumer(OAUTH_KEY, OAUTH_SECRET);
			mProvider = new DefaultOAuthProvider(
					"https://api.twitter.com/oauth/request_token",
					"https://api.twitter.com/oauth/access_token",
					"https://api.twitter.com/oauth/authorize");

			try {
				authUrl = mProvider.retrieveRequestToken(mConsumer,
						OAUTH_CALLBACK_URL);
				Intent intent = new Intent(Intent.ACTION_VIEW,
						Uri.parse(authUrl));
				startActivity(intent);
			} catch (OAuthMessageSignerException e) {
				message = "OAuthMessageSignerException";
				e.printStackTrace();
			} catch (OAuthNotAuthorizedException e) {
				message = "OAuthNotAuthorizedException";
				e.printStackTrace();
			} catch (OAuthExpectationFailedException e) {
				message = "OAuthExpectationFailedException";
				e.printStackTrace();
			} catch (OAuthCommunicationException e) {
				message = "OAuthCommunicationException";
				e.printStackTrace();
			}
			return message;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result != null) {
				Toast.makeText(LoginActivity.this, result, Toast.LENGTH_LONG)
						.show();
			}
		}
	}

	/* Responsible for retrieving access tokens from twitter */
	class RetrieveAccessTokenTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			String message = null;
			String verifier = params[0];
			try {
				// Get the token
				Log.d(TAG, "mConsumer: " + mConsumer);
				Log.d(TAG, "mProvider: " + mProvider);
				mProvider.retrieveAccessToken(mConsumer, verifier);
				String token = mConsumer.getToken();
				String tokenSecret = mConsumer.getTokenSecret();
				mConsumer.setTokenWithSecret(token, tokenSecret);

				Log.d(TAG, String.format(
						"verifier: %s, token: %s, tokenSecret: %s", verifier,
						token, tokenSecret));

				// Store token in prefs
				prefs.edit().putString("token", token)
						.putString("tokenSecret", tokenSecret).commit();

				
				Log.d(TAG, "token: " + token);
			} catch (OAuthMessageSignerException e) {
				message = "OAuthMessageSignerException";
				e.printStackTrace();
			} catch (OAuthNotAuthorizedException e) {
				message = "OAuthNotAuthorizedException";
				e.printStackTrace();
			} catch (OAuthExpectationFailedException e) {
				message = "OAuthExpectationFailedException";
				e.printStackTrace();
			} catch (OAuthCommunicationException e) {
				message = "OAuthCommunicationException";
				e.printStackTrace();
			}
			return message;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result != null) {
				Toast.makeText(LoginActivity.this, result, Toast.LENGTH_LONG)
						.show();
			}
		}
	}

	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

}
