package com.spacealarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * This class handles checking the current location against the list of user locations. 
 * If a match is found the class will send a notification to the user.
 */
public class SpaceAlarmLocationListener implements LocationListener {

	//private SpaceAlarm mapActivity;
	/** The context. */
	private Context context;
	
	/** The shared preferences. */
	private SharedPreferences sharedPreferences;
	
    /**
     * Instantiates a new space alarm location listener.
     *
     * @param context the context
     */
    public SpaceAlarmLocationListener(Context context) {
    	super();
    	this.context = context;
    }
	
	/* (non-Javadoc)
	 * @see android.location.LocationListener#onLocationChanged(android.location.Location)
	 */
	public void onLocationChanged(Location location) {
		checkLocations(location.getLatitude(), location.getLongitude());
	}

	/* (non-Javadoc)
	 * @see android.location.LocationListener#onProviderDisabled(java.lang.String)
	 */
	public void onProviderDisabled(String arg0) {

	}

	/* (non-Javadoc)
	 * @see android.location.LocationListener#onProviderEnabled(java.lang.String)
	 */
	public void onProviderEnabled(String arg0) {

	}

	/* (non-Javadoc)
	 * @see android.location.LocationListener#onStatusChanged(java.lang.String, int, android.os.Bundle)
	 */
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {

	}
	
	/**
	 * Checks the current location and the user's locations.
	 *
	 * @param currLatitude the curr latitude
	 * @param currLongitude the curr longitude
	 */
	public void checkLocations(double currLatitude, double currLongitude) {
		Log.e("Space Alarm", "Lat and long: " + currLatitude + ":" + currLongitude);
		// Open database
		SpaceAlarmDatabase database = new SpaceAlarmDatabase(context);
		database.open();
		// Iterate through alarms
        Cursor cursorAlarms = database.selectAlarmAll();
        if (cursorAlarms.moveToFirst()) {
			do {
				// Get alarm data
				long idAlarm = cursorAlarms.getLong(cursorAlarms.getColumnIndex("_id"));
	        	String nameAlarm = cursorAlarms.getString(cursorAlarms.getColumnIndex("name"));
	        	double radius = cursorAlarms.getDouble(cursorAlarms.getColumnIndex("radius"));
	        	// Iterate through alarm locations
	        	Cursor cursorLocations = database.selectLocationByAlarmId(idAlarm);
	        	if (cursorLocations.moveToFirst()) {
	    			do {
	    				// Get location data
	    				long idLocation = cursorLocations.getLong(cursorLocations.getColumnIndex("_id"));
	    				String nameLocation = cursorLocations.getString(cursorLocations.getColumnIndex("name"));
	            		double locationLatitude = cursorLocations.getDouble(cursorLocations.getColumnIndex("latitude"));
	    	        	double locationLongitude = cursorLocations.getDouble(cursorLocations.getColumnIndex("longitude"));
	    	        	boolean inside = cursorLocations.getInt(cursorLocations.getColumnIndex("inside")) > 0 ? true : false;
	    	        	float[] results = new float[1];
	    	        	Location.distanceBetween(currLatitude, currLongitude, locationLatitude, locationLongitude, results);
	    	        	// Check location against current location

	    	        	// If the locations match and last check was outside the radius
	    	        	if (results[0] <= radius && !inside) {
	    	        		Log.d("Space Alarm", nameLocation + " entered\n" + currLatitude + "," + currLongitude + "," + locationLatitude + "," + locationLongitude + "," + (cursorLocations.getInt(cursorLocations.getColumnIndex("inside")) > 0 ? true : false));
	    	        		// Update the location
	    	        		database.updateLocationColumn(idLocation, "inside", 1);
	    	        		// Send an entry notification
	    	        		sendNotification(idAlarm, nameAlarm, nameLocation, true);
	    	        	// Otherwise if the locations do not match and the last check was inside the radius
	    	        	} else if (results[0] > radius && inside) {
	    	        		Log.d("Space Alarm", nameLocation + " left");
	    	        		// Update the location
	    	        		database.updateLocationColumn(idLocation, "inside", 0);
	    	        		// Send an exit notification
	    	        		sendNotification(idAlarm, nameAlarm, nameLocation, false);
	    	        	}
	    			} while (cursorLocations.moveToNext());
	        	}
	        	if (cursorLocations != null && !cursorLocations.isClosed()) {
	        		cursorLocations.close();
	    		}
			} while (cursorAlarms.moveToNext());
		}
        // Close database
		if (cursorAlarms != null && !cursorAlarms.isClosed()) {
			cursorAlarms.close();
		}
        database.close();
	}
	
	/**
	 * Send notification to the user.
	 *
	 * @param id the id
	 * @param nameAlarm the name alarm
	 * @param nameLocation the name location
	 * @param entered the entered
	 */
	private void sendNotification(long id, String nameAlarm, String nameLocation, boolean entered) {
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(ns);
		int icon = R.drawable.ic_notify_spacealarm;
		CharSequence tickerText = "SpaceAlarm: " + nameAlarm;
		long when = System.currentTimeMillis();
		// Create a notification
		Notification notification = new Notification(icon, tickerText, when);
		// Get the user's preferences
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		if (sharedPreferences.getBoolean("notificationSoundEnabled", false)) {
    		// Set the notification sound
			notification.sound = Uri.parse(sharedPreferences.getString("notificationSound", "DEFAULT_NOTIFICATION_URI"));
		}
		if (sharedPreferences.getBoolean("notificationVibrateEnabled", false)) {
			// Set the notification vibration
			notification.defaults |= Notification.DEFAULT_VIBRATE;
		}
		if (sharedPreferences.getBoolean("notificationLightsEnabled", false)) {
			// Set the notification lights
			notification.defaults |= Notification.DEFAULT_LIGHTS;
		}
		Context contextApp = context.getApplicationContext();
		// Set the notification title and text
		CharSequence contentTitle = "SpaceAlarm: " + nameAlarm;
		CharSequence contentText = nameLocation + " " +(entered ? "entered" : "left");
		Intent notificationIntent = new Intent(context, SpaceAlarmActivityMain.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
		// Send the notification
		notification.setLatestEventInfo(contextApp, contentTitle, contentText, contentIntent);
		notificationManager.notify((int) id, notification);
	}

}
