package com.spacealarm;

import java.util.List;
import java.util.Locale;

import android.app.ProgressDialog;
import android.location.Address;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.GestureDetector.OnGestureListener;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

/**
 * This class creates an overlay that can be used to detect long presses on the map. 
 */
public class SpaceAlarmOverlayGestureListener extends Overlay implements OnGestureListener {
	
	/** The gesture detector. */
	private GestureDetector gestureDetector;
	
	/** The context. */
	private SpaceAlarmActivityMap context;

	/**
	 * Instantiates a new space alarm overlay gesture listener.
	 *
	 * @param context the context
	 */
	public SpaceAlarmOverlayGestureListener(SpaceAlarmActivityMap context) {
		this.context = context;
		gestureDetector = new GestureDetector(context, this);
	}
	
	/**
	 * Instantiates a new space alarm overlay gesture listener.
	 *
	 * @param context the context
	 * @param onGestureListener the on gesture listener
	 */
	public SpaceAlarmOverlayGestureListener(SpaceAlarmActivityMap context, OnGestureListener onGestureListener) {
		this(context);
	}
	
	/* (non-Javadoc)
	 * @see com.google.android.maps.Overlay#onTouchEvent(android.view.MotionEvent, com.google.android.maps.MapView)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event, MapView mapView) {
		if (gestureDetector.onTouchEvent(event)) {
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see android.view.GestureDetector.OnGestureListener#onDown(android.view.MotionEvent)
	 */
	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	/* (non-Javadoc)
	 * @see android.view.GestureDetector.OnGestureListener#onFling(android.view.MotionEvent, android.view.MotionEvent, float, float)
	 */
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,	float velocityY) {
		return false;
	}

	/* (non-Javadoc)
	 * @see android.view.GestureDetector.OnGestureListener#onLongPress(android.view.MotionEvent)
	 */
	@Override
	public void onLongPress(MotionEvent event) {
		SpaceAlarmMapView mapView = context.getMapView();
		// Get the point tapped on the map
		final GeoPoint geoPoint = mapView.getProjection().fromPixels((int) event.getX(), (int) event.getY());
		final ProgressDialog progressDialog = ProgressDialog.show(context, "Working...", "Looking up address", true, false);
		// Run in a new thread to prevent apparent freezes
		context.runOnUiThread(new Runnable(){
			public void run(){
				Address address = null;
				SpaceAlarmMapView mapView = context.getMapView();
				double latitude = geoPoint.getLatitudeE6() / 1E6;
				double longitude = geoPoint.getLongitudeE6() / 1E6;
				// Look up the address at latitude and longitude using the geo-coder
				SpaceAlarmGeocoder geocoder = new SpaceAlarmGeocoder(context, Locale.getDefault());
				try {
					List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
					progressDialog.dismiss();
					// Set the address
					if (addresses.size() != 0) {
						address = addresses.get(0);
					}
				} catch (Exception e) {
					progressDialog.dismiss();
				}
				// Set the context menu header
				mapView.setContextMenuInfo(new SpaceAlarmMapContextMenuInfo(geoPoint, address));
				// Open the context menu
				context.openContextMenu(mapView);
			}
		});
	}

	/* (non-Javadoc)
	 * @see android.view.GestureDetector.OnGestureListener#onScroll(android.view.MotionEvent, android.view.MotionEvent, float, float)
	 */
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		return false;
	}

	/* (non-Javadoc)
	 * @see android.view.GestureDetector.OnGestureListener#onShowPress(android.view.MotionEvent)
	 */
	@Override
	public void onShowPress(MotionEvent e) {
	}

	/* (non-Javadoc)
	 * @see android.view.GestureDetector.OnGestureListener#onSingleTapUp(android.view.MotionEvent)
	 */
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	/**
	 * Checks if is longpress enabled.
	 *
	 * @return true, if is longpress enabled
	 */
	public boolean isLongpressEnabled() {
		return gestureDetector.isLongpressEnabled();
	}

	/**
	 * Sets the checks if is longpress enabled.
	 *
	 * @param isLongpressEnabled the new checks if is longpress enabled
	 */
	public void setIsLongpressEnabled(boolean isLongpressEnabled) {
		gestureDetector.setIsLongpressEnabled(isLongpressEnabled);
	}

	

}
