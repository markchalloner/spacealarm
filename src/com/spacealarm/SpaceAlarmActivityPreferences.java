package com.spacealarm;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Used to display the Preferences screen.
 */
public class SpaceAlarmActivityPreferences extends PreferenceActivity {

	 /* (non-Javadoc)
 	 * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
 	 */
 	@Override
     protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 addPreferencesFromResource(R.xml.preferences);
	 }

}
