package com.spacealarm;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Used to display the Edit alarm screen.
 */
public class SpaceAlarmActivityEdit extends Activity {

	/** A unique identifier when starting the Map Activity  */
	private static final int MAP_ACTIVITY = 11;
	
	/** Export this as context for use in handlers */
	public final SpaceAlarmActivityEdit context = this;
	
	/** A list of overlays. */
	private ArrayList<SpaceAlarmOverlayItem> overlays = new ArrayList<SpaceAlarmOverlayItem>();
	
	/** The list of deleted overlays. */
	private ArrayList<SpaceAlarmOverlayItem> overlaysDeleted = new ArrayList<SpaceAlarmOverlayItem>();
	
	/** The alarm id if editing an alarm. */
	private long id;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Load layout
		setContentView(R.layout.add);
		
		// Read data passed from the Main Activity
		Bundle data = getIntent().getExtras();
		// Get the alarm id
		id = data.getLong("com.spacealarm.SpaceAlarmActivityAlarm.id");
		
		// Add map button handler
		ImageButton addLocationButton = (ImageButton) findViewById(R.id.addLocationButton);
		addLocationButton.setOnClickListener(new OnClickListener() {
		    public void onClick(View v) {
		    	context.editMap();
		    }
		});
		
		// Add save button handler
		Button addButtonSave = (Button) findViewById(R.id.addButtonSave);
		addButtonSave.setOnClickListener(new OnClickListener() {
		    public void onClick(View v) {
		    	context.save();
		    }
		});
		
		// Add cancel button handler
		Button addButtonCancel = (Button) findViewById(R.id.addButtonCancel);
		addButtonCancel.setOnClickListener(new OnClickListener() {
		    public void onClick(View v) {
		    	context.cancel();
		    }
		});
		
		// If editing an alarm load values into fields
		if (id != 0) {
			load();
		}
		
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		save();
		return;
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_option, menu);
        return true;
    }
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {	
			case R.id.menuAddSave:
				save();
				return true;
			case R.id.menuAddCancel:
				cancel();
				return true;
        }
        return false;
    }
	
    /**
     * Opens the map with a list of markers to add.
     */
    private void editMap(){
    	Intent intent = new Intent(this, SpaceAlarmActivityMap.class);
    	Bundle data = new Bundle();
    	data.putParcelableArrayList("com.spacealarm.SpaceAlarmActivityMap.overlays", overlays);
    	intent.putExtras(data);
        startActivityForResult(intent, MAP_ACTIVITY);
    }
    
    /**
     * Saves the alarm.
     */
    private void save(){
    	EditText addName = (EditText) findViewById(R.id.addName);
    	EditText addRadius = (EditText) findViewById(R.id.addRadius);
    	final String name = addName.getText().toString();
    	final String radiusText = addRadius.getText().toString();
    	// Check values of inputs
    	if (name.length() != 0 && overlays.size() != 0 && radiusText.length() != 0) {
    		// Get database
	    	final SpaceAlarmDatabase database = new SpaceAlarmDatabase(this);
	    	final ProgressDialog progressDialog = ProgressDialog.show(context, "Working...", "Saving location alarm");
	    	final SpaceAlarmGeocoder geocoder = new SpaceAlarmGeocoder(context, Locale.getDefault());
	    	final double radius = Double.parseDouble(radiusText);
			database.open();
			// Run in a thread to avoid freezes
			context.runOnUiThread(new Runnable(){
				public void run(){
						// Insert new alarm
						if (id == 0) {
							id = database.insertAlarm(
								name,
								radius
					        );
							// Insert alarm locations
							for (int i = 0; i < overlays.size(); i++) {
								SpaceAlarmOverlayItem overlay = overlays.get(i);
								String name = overlay.getName();
								long locationId = database.insertLocation(id, name, overlay.getLatitude(), overlay.getLongitude(), overlay.getInside());
								try {
									// Get the cells for each location
									List<SpaceAlarmCellLocation> cells = overlay.getCellLocations(geocoder, radius);
									for (int j = 0; j < cells.size(); j++) {
										// Insert cells and link to location
										SpaceAlarmCellLocation cell = cells.get(j);
										database.insertLocationCell(
											locationId, 
											database.insertCell(
												cell.getCell(), 
												cell.getLac(), 
												cell.getLatitude(), 
												cell.getLongitude(), 
												cell.getMnc(), 
												cell.getMcc(), 
												cell.getSamples()
											)
										);
									}
								} catch (Exception e) { 
									e.printStackTrace();
								}
							}
						} else {
							// Update alarm
							database.updateAlarm(
								id,
								name,
								radius
					        );
							// Delete removed locations
							for (int i = 0; i < overlaysDeleted.size(); i++) {
								SpaceAlarmOverlayItem overlay = overlaysDeleted.get(i);
								long overlayId = overlay.getId();
								if (overlayId != 0) {
									database.deleteLocationById(overlayId);
								}
							}
							// Insert new locations
							for (int i = 0; i < overlays.size(); i++) {
								SpaceAlarmOverlayItem overlay = overlays.get(i);
								String name = overlay.getName();
								long overlayId = overlay.getId();
								if (overlayId == 0) {
									long locationId = database.insertLocation(id, name, overlay.getLatitude(), overlay.getLongitude(), overlay.getInside());
									try {
										// Get the cells for each location
										List<SpaceAlarmCellLocation> cells = overlay.getCellLocations(geocoder, radius);
										for (int j = 0; j < cells.size(); j++) {
											SpaceAlarmCellLocation cell = cells.get(j);
											// Insert cells and link to location
											database.insertLocationCell(
												locationId, 
												database.insertCell(
													cell.getCell(), 
													cell.getLac(), 
													cell.getLatitude(), 
													cell.getLongitude(), 
													cell.getMnc(), 
													cell.getMcc(), 
													cell.getSamples()
												)
											);
										}
									} catch (Exception e) { 
										e.printStackTrace();
									}
								}
							}
						}
						progressDialog.dismiss();
				}
			});
			// Close database
	        database.close();
	        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	        // Get current location
	        SpaceAlarmLocationListener locationListener = new SpaceAlarmLocationListener(this);
	        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
	        	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,	0, new SpaceAlarmLocationListener(this));
	        	Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	        	// Check if current location matches any alarm locations
	        	if (location != null) {
	        		locationListener.checkLocations(location.getLatitude(), location.getLongitude());
	        	}
	        }
	        setResult(RESULT_OK);
	        finish();
    	} else {
    		// Show an error if any fields have not been set
    		Toast toast = Toast.makeText(this, "You must set a name, location and radius", Toast.LENGTH_LONG);
    		toast.show();
    	}
		
    }
    
    /**
     * Cancels discarding changes to the alarm.
     */
    private void cancel() {
		setResult(RESULT_CANCELED);
		finish();
    }
    
    /**
     * Loads data into the fields.
     */
    private void load() {
    	EditText addName = (EditText) findViewById(R.id.addName);
    	TextView addLocationAddress = (TextView) findViewById(R.id.addLocationAddress);
    	EditText addRadius = (EditText) findViewById(R.id.addRadius);
    	// Open database
    	final SpaceAlarmDatabase database = new SpaceAlarmDatabase(this);
		database.open();
		// Get alarm details
		Cursor cursorAlarms = database.selectAlarmById(id);
		String nameAlarm = cursorAlarms.getString(cursorAlarms.getColumnIndex("name"));
		int radius = cursorAlarms.getInt(cursorAlarms.getColumnIndex("radius"));
		// Get locations
		Cursor cursorLocations = database.selectLocationByAlarmId(id);
		// Create overlay markers for the map
		if (cursorLocations.moveToFirst()) {
			do {
				long idLocation = cursorLocations.getLong(cursorLocations.getColumnIndex("_id"));
				String nameLocation = cursorLocations.getString(cursorLocations.getColumnIndex("name"));
				double latitude = cursorLocations.getDouble(cursorLocations.getColumnIndex("latitude"));
				double longitude = cursorLocations.getDouble(cursorLocations.getColumnIndex("longitude"));
				overlays.add(new SpaceAlarmOverlayItem(idLocation, nameLocation, "", latitude, longitude, null)); 
			} while (cursorLocations.moveToNext());
		}
		// Close database
		if (cursorLocations != null && !cursorLocations.isClosed()) {
			cursorLocations.close();
		}
		cursorAlarms.close();
		database.close();
		int count = overlays.size();
  		// Set fields
		addName.setText(nameAlarm);
  	  	addLocationAddress.setText(getResources().getQuantityString(R.plurals.edit_locations_count, count, count));
  	  	addRadius.setText("" + radius);
    	
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
     	super.onActivityResult(requestCode, resultCode, intent);
    	switch (requestCode) {
	    	case MAP_ACTIVITY:
	    		switch (resultCode) {
	    			case RESULT_OK:
	    				// Get overlays
		    			Bundle data = intent.getExtras();
			        	overlays = data.getParcelableArrayList("com.spacealarm.SpaceAlarmActivityMap.overlays");
			        	overlaysDeleted = data.getParcelableArrayList("com.spacealarm.SpaceAlarmActivityMap.overlaysDeleted");
			        	int count = overlays.size();
			        	// Set number of locations
			        	TextView addLocationAddress = (TextView) findViewById(R.id.addLocationAddress);
			        	addLocationAddress.setText(getResources().getQuantityString(R.plurals.edit_locations_count, count, count));
			        	break;
	    			case RESULT_CANCELED:
	    				break;
	    		}
	    		break;
    	}
    }

}
