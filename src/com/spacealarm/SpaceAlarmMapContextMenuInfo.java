package com.spacealarm;

import android.location.Address;
import android.view.ContextMenu.ContextMenuInfo;

import com.google.android.maps.GeoPoint;

/**
 * This class is a wrapper for the data sent to the MapView context menu.
 */
public class SpaceAlarmMapContextMenuInfo implements ContextMenuInfo {

	/** The geo point. */
	private GeoPoint geoPoint;
	
	/** The address. */
	private Address address;
	
	/**
	 * Instantiates a new space alarm map context menu info.
	 *
	 * @param geoPoint the geo point
	 */
	public SpaceAlarmMapContextMenuInfo(GeoPoint geoPoint) {
		this.geoPoint = geoPoint;
		this.address = null;
	}
	
	/**
	 * Instantiates a new space alarm map context menu info.
	 *
	 * @param geoPoint the geo point
	 * @param address the address
	 */
	public SpaceAlarmMapContextMenuInfo(GeoPoint geoPoint, Address address) {
		this.geoPoint = geoPoint;
		this.address = address;
	}
	
	/**
	 * Gets the geo point.
	 *
	 * @return the geo point
	 */
	public GeoPoint getGeoPoint(){
		return geoPoint;
	}
	
	/**
	 * Gets the address.
	 *
	 * @return the address
	 */
	public Address getAddress(){
		return address;
	}
	
	/**
	 * Gets the title.
	 *
	 * @return the title
	 */
	public String getTitle(){
		// Get the address if it exists
		if (address != null) {
			return address.getAddressLine(0);
		// Otherwise get the latitude and longitude 
		} else {
			double latitude = geoPoint.getLatitudeE6() / 1E6;
			double longitude = geoPoint.getLongitudeE6() / 1E6;
			return latitude + ", " + longitude;
		}
	}
}
