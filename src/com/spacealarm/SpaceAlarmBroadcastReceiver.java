package com.spacealarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Receives a startup notification from the Android OS and starts 
 * the Space Alarm service depending on the users preferences
 */
public class SpaceAlarmBroadcastReceiver extends BroadcastReceiver {

	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		// Get preferences
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		Intent serviceIntent = new Intent();
		serviceIntent.setAction("com.spacealarm.SpaceAlarmService");
		boolean serviceEnabled = sharedPreferences.getBoolean("serviceEnabled", true);
		int serviceStart = Integer.parseInt(sharedPreferences.getString("serviceStart", "1"));
		// If service is enabled and set to start with the phone
    	if (serviceEnabled && serviceStart == 1) {
    		// Start the service
    		context.startService(serviceIntent);
		}
	}

}
