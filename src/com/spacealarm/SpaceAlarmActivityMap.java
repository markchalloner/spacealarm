package com.spacealarm;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

/**
 * Used to display the Map screen.
 */
public class SpaceAlarmActivityMap extends MapActivity {
	
	/** The context. */
	public final SpaceAlarmActivityMap context = this;
	
	/** The map controller. */
	private MapController mapController;
	
	/** The map view. */
	private SpaceAlarmMapView mapView;
	
	/** The overlays. */
	private List<Overlay> overlays;
	
	/** The gesture listener overlay. */
	private SpaceAlarmOverlayGestureListener gestureListenerOverlay;
	
	/** The itemized add overlay. */
	private SpaceAlarmItemizedOverlay itemizedAddOverlay;
	
	/** The itemized search overlay. */
	private SpaceAlarmItemizedOverlay itemizedSearchOverlay;
	
	/** The my location overlay. */
	private MyLocationOverlay myLocationOverlay;
	
	/* (non-Javadoc)
	 * @see com.google.android.maps.MapActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		
		// Get location data if available
		Bundle data = getIntent().getExtras();
		ArrayList<SpaceAlarmOverlayItem> overlaysLoaded = data.getParcelableArrayList("com.spacealarm.SpaceAlarmActivityMap.overlays");
		
		// Load markers
		this.getResources().getDrawable(R.drawable.marker);
		Drawable drawableAdd = this.getResources().getDrawable(R.drawable.marker_add);
		Drawable drawableSearch = this.getResources().getDrawable(R.drawable.marker_search);
		
		// Get widgets
		mapView = (SpaceAlarmMapView) findViewById(R.id.mapView);		
		mapController = mapView.getController();
		overlays = mapView.getOverlays();
		
		// Create the gesture listener overlay
		gestureListenerOverlay = new SpaceAlarmOverlayGestureListener(this);
		// Create the my location overlay (showing the user's current location)
		myLocationOverlay = new MyLocationOverlay(this, mapView);
		// Create the overlay of added locations
		itemizedAddOverlay = new SpaceAlarmItemizedOverlay(drawableAdd, this, SpaceAlarmItemizedOverlay.TYPE_ADD);
		// Create the overlay of searched locations
		itemizedSearchOverlay = new SpaceAlarmItemizedOverlay(drawableSearch, this, SpaceAlarmItemizedOverlay.TYPE_SEARCH);
		
		// Set loaded items on map
		itemizedAddOverlay.setOverlays(overlaysLoaded);
		
		// Set map options
		mapView.setBuiltInZoomControls(true);
		mapController.setZoom(14);
		registerForContextMenu(mapView);        
		myLocationOverlay.enableMyLocation();
		myLocationOverlay.enableCompass();
		
        overlays.add(gestureListenerOverlay);
        overlays.add(myLocationOverlay);
		
        // If no location data was passed to the map
		if (overlaysLoaded.size() == 0) {
			// Centre on the user's current location
			myLocationOverlay.runOnFirstFix(new Runnable() {
	            public void run() {
	                mapController.animateTo(myLocationOverlay.getMyLocation());
	            }
	        });
		// Otherwise work out the view to be shown
		} else {
			// Get the farthest north south, west and east point
			double maxLatitude = -91d;
			double minLatitude = +91d;
			double maxLongitude = -181d;
			double minLongitude = +181d;
			// Iterate through the locations
			for (int i = 0; i < overlaysLoaded.size(); i++) {
				SpaceAlarmOverlayItem overlay = overlaysLoaded.get(i);
				double latitude = overlay.getLatitude();
				double longitude = overlay.getLongitude();
				Log.e("Space Alarm", "" + latitude + ":" + longitude);
				// If this is the first location set maximum and minimum to be this location
				if (i == 0) {
					maxLatitude = minLatitude = latitude;
					maxLongitude = minLongitude = longitude;
				} else {
					maxLatitude = (maxLatitude < latitude) ?  latitude : maxLatitude;
					minLatitude = (minLatitude > latitude) ?  latitude : minLatitude; 
					maxLongitude = (maxLongitude < longitude) ? longitude : maxLongitude;
					minLongitude = (minLongitude > longitude) ? longitude : minLongitude;
				}
			}
			// Create a bounding box with the maximum and minimum points
			SpaceAlarmGeoBoundingBox boundingBox = new SpaceAlarmGeoBoundingBox(minLatitude, minLongitude, maxLatitude, maxLongitude);
			// Pan to the centre of the box
			mapController.animateTo(boundingBox.getCenterPoint().getGeoPoint());
			// Zoom to enclose the bounding box
			mapController.zoomToSpan(boundingBox.getDifferenceLatitudeE6(), boundingBox.getDifferenceLongitudeE6());
		}
		
		overlays.add(itemizedAddOverlay);
		
		// Save clock handler
		Button mapButtonSave = (Button) findViewById(R.id.mapButtonSave);
		mapButtonSave.setOnClickListener(new OnClickListener() {
		    public void onClick(View v) {
		    	context.save();
		    }
		});
		
		// Cancel click handler
		Button mapButtonCancel = (Button) findViewById(R.id.mapButtonCancel);
		mapButtonCancel.setOnClickListener(new OnClickListener() {
		    public void onClick(View v) {
		    	context.cancel();
		    }
		});
		
	}
	
	/* (non-Javadoc)
	 * @see com.google.android.maps.MapActivity#onPause()
	 */
	@Override
	public void onPause() {
		super.onPause();
		// Disable my location to conserve power
		myLocationOverlay.disableMyLocation();
		myLocationOverlay.disableCompass();
	}
	
	/* (non-Javadoc)
	 * @see com.google.android.maps.MapActivity#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		myLocationOverlay.enableMyLocation();
		myLocationOverlay.enableCompass();
	}
	
	/* (non-Javadoc)
	 * @see com.google.android.maps.MapActivity#onNewIntent(android.content.Intent)
	 */
	@Override
	public void onNewIntent(Intent intent) {
	    setIntent(intent);
	    handleIntent(intent);
	}

	/**
	 * Handles searches.
	 *
	 * @param intent the intent
	 */
	private void handleIntent(Intent intent) {
	    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
	      String query = intent.getStringExtra(SearchManager.QUERY);
	      double latitude = -91 * 1E6;
	      double longitude = -181 * 1E6;
	      // Check if search is of the form "latitude, longitude"
	      if (query.indexOf(",") != -1) {
	    	  String[] components = query.split(",");
	    	  if (components.length == 2) {
	    		  try {
	    			  latitude = Double.parseDouble(components[0]);
	    			  longitude = Double.parseDouble(components[1]);
	    		  } catch (Exception e) {}
	    	  }
	      }
	      // If latitude/longitude the search by coordinates
	      if (latitude >= (-90 * 1E6) && longitude >= (-180 * 1E6)) {
	    	  searchByLatLong(latitude, longitude);
	      // Otherwise search by query given
	      } else {
	    	  searchByAddress(query);
	      }
	    }
	    
	}
	
	/* (non-Javadoc)
	 * @see com.google.android.maps.MapActivity#isRouteDisplayed()
	 */
	@Override
	protected boolean isRouteDisplayed() {
		return false;
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
        inflater.inflate(R.menu.map_option, menu);
        return true;
    }
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {	
			case R.id.menuMapSave:
				save();
				return true;
			case R.id.menuMapCancel:
				cancel();
				return true;
        }
        return false;
    }
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		SpaceAlarmMapContextMenuInfo info = (SpaceAlarmMapContextMenuInfo) menuInfo;
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.map_context, menu);
		String title = info.getTitle();
		menu.setHeaderTitle(title);
	}   
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.mapContextSelect:
				SpaceAlarmMapContextMenuInfo info = (SpaceAlarmMapContextMenuInfo) item.getMenuInfo();
				SpaceAlarmGeoPoint point = new SpaceAlarmGeoPoint(info.getGeoPoint());
				setAddPoint(point, info.getTitle(), "", null);
				mapController.animateTo(point.getGeoPoint());
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}
	
	/**
	 * Gets the map view.
	 *
	 * @return the map view
	 */
	public SpaceAlarmMapView getMapView() {
		return mapView;
	}
	
	/**
	 * Clear add overlay points.
	 */
	public void clearAddPoints() {
		itemizedAddOverlay.clearOverlays();
		overlays.add(itemizedAddOverlay);
		mapView.invalidate();
	}
	
	/**
	 * Clear search overlay points.
	 */
	public void clearSearchPoints() {
		itemizedSearchOverlay.clearOverlays();
		overlays.add(itemizedSearchOverlay);
		mapView.invalidate();
	}
	
	/**
	 * Clear a single add overlay point by index.
	 *
	 * @param index the index
	 */
	public void clearAddPoint(int index) {
		itemizedAddOverlay.clearOverlay(index);
		overlays.add(itemizedAddOverlay);
		mapView.invalidate();
	}
	
	/**
	 * Clear a single search overlay point by index.
	 *
	 * @param index the index
	 */
	public void clearSearchPoint(int index) {
		itemizedSearchOverlay.clearOverlay(index);
		overlays.add(itemizedSearchOverlay);
		mapView.invalidate();
	}
	
	/**
	 * Adds a point to the add overlay.
	 *
	 * @param point the point
	 * @param name the name
	 * @param snippet the snippet
	 * @param address the address
	 */
	public void setAddPoint(SpaceAlarmGeoPoint point, String name, String snippet, Address address) {
		SpaceAlarmOverlayItem overlayItem = new SpaceAlarmOverlayItem(0, name, snippet, point, address);
		itemizedAddOverlay.addOverlay(overlayItem);
		overlays.add(itemizedAddOverlay);
		mapView.invalidate();
	}
	
	/**
	 * Adds a point to the search overlay.
	 *
	 * @param point the point
	 * @param name the name
	 * @param snippet the snippet
	 * @param address the address
	 */
	public void setSearchPoint(SpaceAlarmGeoPoint point, String name, String snippet, Address address) {
		SpaceAlarmOverlayItem overlayItem = new SpaceAlarmOverlayItem(0, name, snippet, point, address);
		itemizedSearchOverlay.addOverlay(overlayItem);
		overlays.add(itemizedSearchOverlay);
		mapView.invalidate();
	}
	
	/**
	 * Save and return location data to the Edit Activity.
	 */
	private void save() {
		ArrayList<SpaceAlarmOverlayItem> overlays = itemizedAddOverlay.getOverlays();
		ArrayList<SpaceAlarmOverlayItem> overlaysDeleted = itemizedAddOverlay.getOverlaysDeleted();
		if (overlays.size() != 0 || overlaysDeleted.size() != 0) {
			Intent intent = new Intent();
			Bundle data = new Bundle();
			data.putParcelableArrayList("com.spacealarm.SpaceAlarmActivityMap.overlays", overlays);
			data.putParcelableArrayList("com.spacealarm.SpaceAlarmActivityMap.overlaysDeleted", overlaysDeleted);
			intent.putExtras(data);
			setResult(RESULT_OK, intent);
			finish();
		} else {
			cancel();
		}
	}
	
	/**
	 * Cancel.
	 */
	private void cancel() {
		setResult(RESULT_CANCELED, null);
		finish();
	}
	
	/**
	 * Search by latitude and longitude.
	 *
	 * @param latitude the latitude
	 * @param longitude the longitude
	 */
	private void searchByLatLong(final double latitude, final double longitude) {
		final SpaceAlarmGeoPoint geoPoint = new SpaceAlarmGeoPoint(latitude, longitude);
		final ProgressDialog progressDialog = ProgressDialog.show(this, "Working...", "Looking up address", true, false);
		context.runOnUiThread(new Runnable(){
			public void run(){
				Address address = null;
				String title = latitude + ", " + longitude;
				Geocoder geocoder = new Geocoder(context, Locale.getDefault());
				// Get location from geo-coder
				try {
					List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
					progressDialog.dismiss();
					// Get the first location
					if (addresses.size() != 0) {
						address = addresses.get(0);
						title = address.getAddressLine(0);
					}
				// Error if address could not be found
				} catch (Exception e) {
					progressDialog.dismiss();
					Dialog locationError = new AlertDialog.Builder(context)
					.setIcon(0)
					.setTitle("Error")
					.setPositiveButton(R.string.alert_ok, null)
					.setMessage("Could not search for latitude/longitude.")
					.create();
					locationError.show();
				}
				// Clear the search overlay of existing points
				clearSearchPoints();
				// Add the new location to the search overlay
				setSearchPoint(geoPoint, title, "", address);
				// Pan the map to the new point
				mapController.animateTo(geoPoint.getGeoPoint());
			}
		});
	}
	
	/**
	 * Search by address.
	 *
	 * @param query the query
	 */
	private void searchByAddress(final String query) {
		final ProgressDialog progressDialog = ProgressDialog.show(this, "Working...", "Searching address", true, false);
		runOnUiThread(new Runnable(){
			public void run(){
				SpaceAlarmGeoPoint geoPoint;
				SpaceAlarmGeocoder geocoder = new SpaceAlarmGeocoder(context, Locale.getDefault()); //create new geocoder instance
				double maxLatitude = -91d;
				double minLatitude = +91d;
				double maxLongitude = -181d;
				double minLongitude = +181d;
				try {
					// Find a maximum of 5 locations 
					List<Address> addresses = geocoder.getFromLocationName(query, 5);
					progressDialog.dismiss();
					// Alert if no locations could be found
					if (addresses.size() == 0) {
						Dialog locationError = new AlertDialog.Builder(context)
							.setIcon(0)
							.setTitle("Error")
							.setPositiveButton(R.string.alert_ok, null)
							.setMessage("Could not find address.")
							.create();
						locationError.show();
					// Otherwise
					} else {
						// Clear the search overlay of existing points
						clearSearchPoints();
						// Iterate through results
						for (int i = 0; i < addresses.size(); ++i) {
							Address address = addresses.get(i);
							String name = address.getAddressLine(0);
							double latitude = address.getLatitude();
							double longitude = address.getLongitude();
							// Calculate the minimum and maximum latitudes and longitudes
							if (i == 0) {
								maxLatitude = minLatitude = latitude;
								maxLongitude = minLongitude = longitude;
							} else {
								maxLatitude = (maxLatitude < latitude) ?  latitude : maxLatitude;
								minLatitude = (minLatitude > latitude) ?  latitude : minLatitude; 
								maxLongitude = (maxLongitude < longitude) ? longitude :  maxLongitude;
								minLongitude = (minLongitude > longitude) ? longitude :  minLongitude;
							}
							geoPoint = new SpaceAlarmGeoPoint(latitude, longitude);
							// Add the new location to the search overlay
							setSearchPoint(geoPoint, name, "", address);
						}
						// Create a bounding box with the maximum and minimum points
						SpaceAlarmGeoBoundingBox boundingBox = new SpaceAlarmGeoBoundingBox(minLatitude, minLongitude, maxLatitude, maxLongitude);
						// Pan to the centre of the box	
						mapController.animateTo(boundingBox.getCenterPoint().getGeoPoint());
						// Zoom to enclose the bounding box	
						mapController.zoomToSpan(boundingBox.getDifferenceLatitudeE6(), boundingBox.getCenterLongitudeE6());
					}
				// Error if no locations found
				} catch (Exception e) {
					progressDialog.dismiss();
					Dialog locationError = new AlertDialog.Builder(context)
						.setIcon(0)
						.setTitle("Error")
						.setPositiveButton(R.string.alert_ok, null)
						.setMessage("Could not search for address.")
						.create();
					locationError.show();
				}
			}
		});
	}

}
