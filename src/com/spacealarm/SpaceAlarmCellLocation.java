package com.spacealarm;

import android.telephony.CellLocation;

/**
 * Holds the data for mobile phone cells.
 */
public class SpaceAlarmCellLocation extends CellLocation {

	/** The cell. */
	private int cell = 0;
	
	/** The lac. */
	private int lac = 0;
	
	/** The mcc. */
	private int mcc = 0;
	
	/** The mnc. */
	private int mnc = 0;
	
	/** The samples. */
	private int samples = 0;
	
	/** The latitude. */
	private double latitude;
	
	/** The longitude. */
	private double longitude;
	
	/** The radius. */
	private double radius;
	
	/**
	 * Instantiates a new space alarm cell location.
	 *
	 * @param latitude the latitude
	 * @param longitude the longitude
	 * @param cell the cell
	 */
	public SpaceAlarmCellLocation(double latitude, double longitude, int cell) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.cell = cell;
	}
	
	/**
	 * Instantiates a new space alarm cell location.
	 *
	 * @param latitude the latitude
	 * @param longitude the longitude
	 * @param cell the cell
	 * @param lac the lac
	 */
	public SpaceAlarmCellLocation(double latitude, double longitude, int cell, int lac) {
		this(latitude, longitude, cell);
		this.lac = lac;
	}
	
	/**
	 * Instantiates a new space alarm cell location.
	 *
	 * @param latitude the latitude
	 * @param longitude the longitude
	 * @param cell the cell
	 * @param lac the lac
	 * @param mnc the mnc
	 * @param mcc the mcc
	 */
	public SpaceAlarmCellLocation(double latitude, double longitude, int cell, int lac, int mnc, int mcc) {
		this(latitude, longitude, cell, lac);
		this.mnc = mnc;
		this.mcc = mcc;
	}
	
	/**
	 * Instantiates a new space alarm cell location.
	 *
	 * @param latitude the latitude
	 * @param longitude the longitude
	 * @param cell the cell
	 * @param lac the lac
	 * @param mnc the mnc
	 * @param mcc the mcc
	 * @param samples the samples
	 */
	public SpaceAlarmCellLocation(double latitude, double longitude, int cell, int lac, int mnc, int mcc, int samples) {
		this(latitude, longitude, cell, lac, mnc, mcc);
		this.samples = samples;
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
		return (int) (latitude * 1E6);
	}
	
	/**
	 * Gets the longitude as an integer.
	 *
	 * @return the longitude e6
	 */
	public int getLongitudeE6() {
		return (int) (longitude * 1E6);
	}
	
	/**
	 * Gets the cell ID.
	 *
	 * @return the cell
	 */
	public int getCell() {
		return cell;
	}
	
	/**
	 * Gets the LAC.
	 *
	 * @return the lac
	 */
	public int getLac() {
		return lac;
	}
	
	/**
	 * Gets the MNC.
	 *
	 * @return the mnc
	 */
	public int getMnc() {
		return mnc;
	}
	
	/**
	 * Gets the MCC.
	 *
	 * @return the mcc
	 */
	public int getMcc() {
		return mcc;
	}
	
	/**
	 * Gets the number of samples taken of this cell.
	 *
	 * @return the samples
	 */
	public int getSamples() {
		return samples;
	}
	
	/**
	 * Gets the radius of the cell.
	 *
	 * @return the radius
	 */
	public double getRadius() {
		return radius;
	}
	
	/**
	 * Sets the radius of the cell.
	 *
	 * @param radius the new radius
	 */
	public void setRadius(double radius) {
		this.radius = radius;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object object) {
		// Return true if both are the same instance of the object
		if (this == object) {
			return true;
		}
		// Return false if the object is not a SpaceAlarmCellLocation
		if (!(object instanceof SpaceAlarmCellLocation)) {
			return false;
		}
		// Cast the object as a SpaceAlarmCellLocation
		SpaceAlarmCellLocation cell = (SpaceAlarmCellLocation) object;
		// Return true if the cell data matches
		return 
			this.cell == cell.cell && 
			this.lac == cell.lac &&
			this.mnc == cell.mnc &&
			this.mcc == cell.mcc
		;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = 1;
		hash = hash * 31 + cell;
		hash = hash * 31 + lac;
		hash = hash * 31 + mnc;
		hash = hash * 31 + mcc;
		return hash;
	}
	
}
