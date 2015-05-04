package com.spacealarm;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.LocationManager;
import android.os.IBinder;

/**
 * Defines the SpaceAlarmService that runs in the background 
 * checking the users locations against the current location 
 * and providing notifications when necessary
 */
public class SpaceAlarmService extends Service {

	/* (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onCreate()
	 */
	@Override 
	public void onCreate() {
		super.onCreate();
		start();
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onDestroy()
	 */
	@Override 
	public void onDestroy() {
		super.onDestroy();
		stop();
	}
	
	/**
	 * Start service.
	 */
	private void start() {
		// Open the database
		SpaceAlarmDatabase database = new SpaceAlarmDatabase(this);
        database.open();
        Cursor cursor = database.selectAlarmAll();
        // Only activate the GPS if there are alarms
        if (cursor.getCount() > 0) {
	        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	        // Request periodic location updates from the GPS
	        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
	        	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,	0, new SpaceAlarmLocationListener(this));
	        }
        }
        // Close the database
        cursor.close();
	    database.close();
	}
	
	/**
	 * Stops service.
	 */
	private void stop() {
	}
	
	



}
