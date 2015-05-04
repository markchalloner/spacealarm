package com.spacealarm;

import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.location.LocationManager;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

/**
 * The class receives cell location updates from the OS and starts or 
 * stops the GPS based on whether the current cell matches on in the database 
 */
public class SpaceAlarmPhoneStateListener extends PhoneStateListener {

	/** The context. */
	private Context context;
	
	/** The mnc. */
	private int mnc = -1;
	
	/** The mcc. */
	private int mcc = -1;
	
	
	/**
	 * Instantiates a new space alarm phone state listener.
	 *
	 * @param context the context
	 */
	public SpaceAlarmPhoneStateListener(Context context) {
		this.context = context;
	}
	
	/* (non-Javadoc)
	 * @see android.telephony.PhoneStateListener#onCellLocationChanged(android.telephony.CellLocation)
	 */
	@Override
	public void onCellLocationChanged(CellLocation cellLocation) {
		// Get GSM cell location
		GsmCellLocation gsmCellLocation = (GsmCellLocation) cellLocation;
		Configuration configuration = context.getResources().getConfiguration();
		// Get the phones current cell ID and LAC
		int cell = gsmCellLocation.getCid();
		int lac = gsmCellLocation.getLac();
		// Get the phones current MNC and MCC
		mnc = (mnc == -1) ? configuration.mnc : mnc;
		mcc = (mcc == -1) ? configuration.mcc : mcc;
		Log.e("Space Alarm", "" + cell + ":" + lac + ":" + mnc + ":" + mcc);
		// Open database
		SpaceAlarmDatabase database = new SpaceAlarmDatabase(context);
        database.open();
        // Select matching cells
        Cursor cursor = database.selectCellByCellLacMncMcc(cell, lac, mnc, mcc);
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        SpaceAlarmLocationListener locationListener = new SpaceAlarmLocationListener(context);
        // If there is a matching cell start the GPS
        if (cursor.getCount() > 0) {
        	if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
        		Log.e("Space Alarm", "Started GPS...");
        		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,	0, locationListener);
        	}
        // Other wise stop the GPS
        } else {
        	Log.e("Space Alarm", "Stopped GPS...");
			locationManager.removeUpdates(locationListener);
		}
        // Close the database
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		database.close();
	}
	
	/**
	 * Sets a debug MNC value.
	 *
	 * @param mnc the new debug mnc
	 */
	public void setDebugMnc(int mnc) {
		this.mnc = mnc;
	}
	
	/**
	 * Sets the debug MCC value.
	 *
	 * @param mcc the new debug mcc
	 */
	public void setDebugMcc(int mcc) {
		this.mcc = mcc;
	}
	
}
