package com.goranpetrusic.g_tweet;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

@SuppressLint("NewApi")
public class PrefsActivity extends PreferenceActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction().replace(android.R.id.content,
		         new PrefsFragment()).commit();
	
	}
	public static class PrefsFragment extends PreferenceFragment {

	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	    	PreferenceManager.setDefaultValues(getActivity(), R.xml.preference,
	    			false);
	        // Load the preferences from an XML resource
	        addPreferencesFromResource(R.xml.preference);
	    }
	}
}
