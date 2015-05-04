package com.spacealarm;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import android.location.Address;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.maps.OverlayItem;

/**
 * This class extends the OverlayItem class, allowing items to be passed 
 * from Activity to Activity and adding a set of useful methods.
 */
public class SpaceAlarmOverlayItem extends OverlayItem implements Parcelable {

	/** The id. */
	private long id = 0;
	
	/** The address. */
	private Address address = null;
	
	/** The name. */
	private String name = "";
	
	/** The snippet. */
	private String snippet = "";
	
	/** The latitude. */
	private double latitude = 0;
	
	/** The longitude. */
	private double longitude = 0;
	
	/**
	 * Instantiates a new space alarm overlay item.
	 *
	 * @param id the id
	 * @param name the name
	 * @param snippet the snippet
	 * @param point the point
	 * @param address the address
	 */
	public SpaceAlarmOverlayItem(long id, String name, String snippet, SpaceAlarmGeoPoint point, Address address) {
		super(point.getGeoPoint(), name, snippet);
		setAll(id, name, snippet, point.getLatitude(), point.getLongitude(), address);
	}
	
	/**
	 * Instantiates a new space alarm overlay item.
	 *
	 * @param id the id
	 * @param name the name
	 * @param snippet the snippet
	 * @param latitude the latitude
	 * @param longitude the longitude
	 * @param address the address
	 */
	public SpaceAlarmOverlayItem(long id, String name, String snippet, double latitude, double longitude, Address address) {
		super(new SpaceAlarmGeoPoint(latitude, longitude).getGeoPoint(), name, snippet);
		setAll(id, name, snippet, latitude, longitude, address);
	}
	
	/**
	 * Sets member variables to the data passed into the constructors.
	 *
	 * @param id the id
	 * @param name the name
	 * @param snippet the snippet
	 * @param latitude the latitude
	 * @param longitude the longitude
	 * @param address the address
	 */
	private void setAll(long id, String name, String snippet, double latitude, double longitude, Address address) {
		this.id = id;
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.address = address;
		this.snippet = snippet;
	}
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public long getId(){
		return id;
	}
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return getTitle();
	}
	
	/**
	 * Gets the address.
	 *
	 * @return the address
	 */
	public Address getAddress() {
		return address;
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
	 * @return the latitude integer value
	 */
	public int getLatitudeE6() {
		return (int) (latitude * 1E6);
	}
	
	/**
	 * Gets the longitude as an integer.
	 *
	 * @return the longitude integer value
	 */
	public int getLongitudeE6() {
		return (int) (longitude * 1E6);
	}
	
	/**
	 * Gets the inside.
	 *
	 * @return the inside
	 */
	public int getInside() {
		return 0;
	}
	
	/**
	 * Gets the cell locations for this location.
	 *
	 * @param geocoder the geocoder
	 * @param radius the radius
	 * @return the cell locations
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws SAXException the sAX exception
	 */
	public List<SpaceAlarmCellLocation> getCellLocations(SpaceAlarmGeocoder geocoder, double radius) throws IOException, ParserConfigurationException, SAXException {
    	return geocoder.getCellsFromLocation(getLatitude(), getLongitude(), radius, 10);
	}
	
	/* (non-Javadoc)
	 * @see android.os.Parcelable#describeContents()
	 */
	@Override
	public int describeContents() {
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(Parcel out, int flags) {
		Bundle bundle = new Bundle();
		bundle.putLong("id", id);
		bundle.putString("name", name);
		bundle.putString("snippet", snippet);
		bundle.putDouble("latitude", latitude);
		bundle.putDouble("longitude", longitude);
		bundle.putParcelable("address", address);
		out.writeBundle(bundle);
	}
	
	/** The Constant CREATOR. */
	public static final Parcelable.Creator<SpaceAlarmOverlayItem> CREATOR = new Parcelable.Creator<SpaceAlarmOverlayItem>() {
		public SpaceAlarmOverlayItem createFromParcel(Parcel in) {
			Bundle bundle = in.readBundle();
			long id = bundle.getLong("id");
			String name = bundle.getString("name");
			String snippet = bundle.getString("snippet");
			double latitude = bundle.getDouble("latitude");
			double longitude = bundle.getDouble("longitude");
			Address address = bundle.getParcelable("address");
			return new SpaceAlarmOverlayItem(id, name, snippet, latitude, longitude, address);
		}
		public SpaceAlarmOverlayItem[] newArray(int size) {
			return new SpaceAlarmOverlayItem[size];
		}
	};

}
