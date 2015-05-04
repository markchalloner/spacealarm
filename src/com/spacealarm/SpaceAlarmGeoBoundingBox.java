package com.spacealarm;

/**
 * This class represents a geographical bounding box.
 */
public class SpaceAlarmGeoBoundingBox {

	/** The Constant RADIUS_EARTH. */
	private static final long RADIUS_EARTH = 6371000;
	
	/** The Constant MIN_LAT. */
	private static final double MIN_LAT = Math.toRadians(-90d);  // -PI/2
	
	/** The Constant MAX_LAT. */
	private static final double MAX_LAT = Math.toRadians(90d);   //  PI/2
	
	/** The Constant MIN_LON. */
	private static final double MIN_LON = Math.toRadians(-180d); // -PI*2
	
	/** The Constant MAX_LON. */
	private static final double MAX_LON = Math.toRadians(180d);  //  PI*2
	
	/** The min latitude. */
	private double minLatitude;
	
	/** The max latitude. */
	private double maxLatitude;
	
	/** The min longitude. */
	private double minLongitude;
	
	/** The max longitude. */
	private double maxLongitude;
	
	/**
	 * Instantiates a new space alarm geo bounding box.
	 *
	 * @param minLatitude the min latitude
	 * @param minLongitude the min longitude
	 * @param maxLatitude the max latitude
	 * @param maxLongitude the max longitude
	 */
	public SpaceAlarmGeoBoundingBox(double minLatitude, double minLongitude, double maxLatitude, double maxLongitude) {
		this.minLatitude = minLatitude;
		this.minLongitude = minLongitude;
		this.maxLatitude = maxLatitude;
		this.maxLongitude = maxLongitude;
	}
	
	/**
	 * Instantiates a new space alarm geo bounding box.
	 *
	 * @param minPoint the min point
	 * @param maxPoint the max point
	 */
	public SpaceAlarmGeoBoundingBox(SpaceAlarmGeoPoint minPoint, SpaceAlarmGeoPoint maxPoint) {
		this(minPoint.getLatitude(), minPoint.getLongitude(), maxPoint.getLatitude(), maxPoint.getLongitude());
	}
	
	/**
	 * Instantiates a new space alarm geo bounding box from a centre point and a radius in metres. 
	 * Code adapted from Jan Philip Matuschek's (http://janmatuschek.de/LatitudeLongitudeBoundingCoordinates).
	 *
	 * @param latitude the latitude
	 * @param longitude the longitude
	 * @param radius the radius
	 */
	public SpaceAlarmGeoBoundingBox(double latitude, double longitude, double radius) {
		// Convert from degrees to radians
		double latitudeRadians = Math.toRadians(latitude);
		double longitudeRadians = Math.toRadians(longitude);
		double distanceRadians = radius / RADIUS_EARTH;
		double minLatitudeRadians = latitudeRadians - distanceRadians;
		double maxLatitudeRadians = latitudeRadians + distanceRadians;	
		double minLongitudeRadians;
		double maxLongitudeRadians;
		/* If the minimum latitude is greater than the minimum possible latitude
		 * and the maximum latitude is less than the maximum possible latitude 
		 * then there is no pole in the bounding box
		 */
		if (minLatitudeRadians > MIN_LAT && maxLatitudeRadians < MAX_LAT) {
			// Get the radians on this small circle required to move the longitude distance 
			double deltaLongitude = Math.asin(Math.sin(distanceRadians) / Math.cos(latitudeRadians));
			// Get the longitudes
			minLongitudeRadians = longitudeRadians - deltaLongitude;
			maxLongitudeRadians = longitudeRadians + deltaLongitude;
			// If the minimum longitude is less than the minimum possible longitude we cross the 180th meridian
			if (minLongitudeRadians < MIN_LON) {
				// Wrap longitude back into permissible range
				minLongitudeRadians += 2d * Math.PI;
			}
			// If the maximum longitude is greater than the maximum possible longitude we cross the 180th meridian
			if (maxLongitudeRadians > MAX_LON) {
				// Wrap longitude back into permissible range
				maxLongitudeRadians -= 2d * Math.PI;
			}
		// Otherwise a pole is in the bounding box
		} else {
			// Set the minimum latitude to the greater of the south pole or minimum latitude
			minLatitudeRadians = Math.max(minLatitudeRadians, MIN_LAT);
			// Set the maximum latitude to the lesser of the north pole or maximum latitude
			maxLatitudeRadians = Math.min(maxLatitudeRadians, MAX_LAT);
			// Poles include all longitudes so set the minimum and maximum longitudes to the minimum and maximum possible longitudes
			minLongitudeRadians = MIN_LON;
			maxLongitudeRadians = MAX_LON;
		}
		// Convert from radians to degrees
		this.minLatitude = Math.toDegrees(minLatitudeRadians);
		this.maxLatitude = Math.toDegrees(maxLatitudeRadians);
		this.minLongitude = Math.toDegrees(minLongitudeRadians);
		this.maxLongitude = Math.toDegrees(maxLongitudeRadians);
	}
	
	/**
	 * Gets the min latitude.
	 *
	 * @return the min latitude
	 */
	public double getMinLatitude() {
		return minLatitude;
	}
	
	/**
	 * Gets the min longitude.
	 *
	 * @return the min longitude
	 */
	public double getMinLongitude() {
		return minLongitude;
	}
	
	/**
	 * Gets the max latitude.
	 *
	 * @return the max latitude
	 */
	public double getMaxLatitude() {
		return maxLatitude;
	}
	
	/**
	 * Gets the max longitude.
	 *
	 * @return the max longitude
	 */
	public double getMaxLongitude() {
		return maxLongitude;
	}
	
	/**
	 * Gets the latitude of the central point.
	 *
	 * @return the center latitude
	 */
	public double getCenterLatitude() {
		return (maxLatitude + minLatitude) / 2;
	}
	
	/**
	 * Gets the longitude of the central point.
	 *
	 * @return the center longitude
	 */
	public double getCenterLongitude() {
		return (maxLongitude + minLongitude) / 2;
	}
	
	/**
	 * Gets the latitude of the central point as an integer.
	 *
	 * @return the center latitude e6
	 */
	public int getCenterLatitudeE6() {
		return (int) (getCenterLatitude() * 1E6);
	}
	
	/**
	 * Gets the longitude of the central point as an integer.
	 *
	 * @return the center longitude e6
	 */
	public int getCenterLongitudeE6() {
		return (int) (getCenterLongitude() * 1E6);
	}
		
	/**
	 * Gets the difference between the maximum and minimum latitudes.
	 *
	 * @return the difference latitude
	 */
	public double getDifferenceLatitude() {
		return maxLatitude - minLatitude;
	}
	
	/**
	 * Gets the difference between the maximum and minimum longitudes.
	 *
	 * @return the difference longitude
	 */
	public double getDifferenceLongitude() {
		return maxLongitude - minLongitude;
	}
	
	/**
	 * Gets the difference between the maximum and minimum latitudes as an integer.
	 *
	 * @return the difference latitude e6
	 */
	public int getDifferenceLatitudeE6() {
		return (int) (getDifferenceLatitude() * 1E6);
	}
	
	/**
	 * Gets the difference between the maximum and minimum longitudes as an integer.
	 *
	 * @return the difference longitude e6
	 */
	public int getDifferenceLongitudeE6() {
		return (int) (getDifferenceLongitude() * 1E6);
	}
	
	/**
	 * Gets the minimum point.
	 *
	 * @return the min point
	 */
	public SpaceAlarmGeoPoint getMinPoint() {
		return new SpaceAlarmGeoPoint(this.getMinLatitude(), this.getMinLongitude());
	}
	
	/**
	 * Gets the maximum point.
	 *
	 * @return the max point
	 */
	public SpaceAlarmGeoPoint getMaxPoint() {
		return new SpaceAlarmGeoPoint(this.getMaxLatitude(), this.getMaxLongitude());
	}
	
	/**
	 * Gets the centre point.
	 *
	 * @return the center point
	 */
	public SpaceAlarmGeoPoint getCenterPoint() {
		return new SpaceAlarmGeoPoint(this.getCenterLatitude(), this.getCenterLongitude());
	}
	
	/**
	 * Gets the BBOX parameter used to send the bounding box to the OpenCellID API.
	 *
	 * @return the b box
	 */
	public String getBBox() {
		return this.getMinLongitude() + "," + this.getMinLatitude() + "," + this.getMaxLongitude() + "," + this.getMaxLatitude();
	}
}
