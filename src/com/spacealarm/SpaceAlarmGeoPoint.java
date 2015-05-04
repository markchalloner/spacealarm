package com.spacealarm;

import com.google.android.maps.GeoPoint;

/**
 * This class allows for easy and consistent conversion of integer 
 * latitudes and longitudes to their double values and vice versa. 
 */
public class SpaceAlarmGeoPoint {

	/** The latitude. */
	private double latitude;
	
	/** The longitude. */
	private double longitude;
	
	/**
	 * Instantiates a new space alarm geo point.
	 *
	 * @param latitude the latitude
	 * @param longitude the longitude
	 */
	public SpaceAlarmGeoPoint(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	/**
	 * Instantiates a new space alarm geo point.
	 *
	 * @param latitude the latitude
	 * @param longitude the longitude
	 */
	public SpaceAlarmGeoPoint(int latitude, int longitude) {
		this(latitude / 1E6, longitude/ 1E6);
	}
	
	/**
	 * Instantiates a new space alarm geo point.
	 *
	 * @param geoPoint the geo point
	 */
	public SpaceAlarmGeoPoint(GeoPoint geoPoint) {
		this(geoPoint.getLatitudeE6(), geoPoint.getLongitudeE6());
	}
	
	/**
	 * Gets the latitude.
	 *
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}
	
	/**
	 * Gets the longitude.
	 *
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}
	
	/**
	 * Gets the latitude as an integer.
	 *
	 * @return the latitude e6
	 */
	public int getLatitudeE6() {
		return (int) (getLatitude() * 1E6);
	}
	
	/**
	 * Gets the longitude as an integer.
	 *
	 * @return the longitude e6
	 */
	public int getLongitudeE6() {
		return (int) (getLongitude() * 1E6);
	}
	
	/**
	 * Gets the GeoPoint.
	 *
	 * @return the geo point
	 */
	public GeoPoint getGeoPoint() {
		return new GeoPoint(this.getLatitudeE6(), this.getLongitudeE6());
	}
	
}
