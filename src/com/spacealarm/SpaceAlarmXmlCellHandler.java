package com.spacealarm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.location.Location;

/**
 * Parses XML data returned from the cell location API.
 */
public class SpaceAlarmXmlCellHandler extends DefaultHandler {
	
	/** The latitude. */
	private double latitude;
	
	/** The longitude. */
	private double longitude;
	
	/** The cell locations. */
	private List<SpaceAlarmCellLocation> cellLocations = new ArrayList<SpaceAlarmCellLocation>();
	
	/**
	 * Instantiates a new space alarm xml cell handler.
	 *
	 * @param latitude the latitude
	 * @param longitude the longitude
	 * @param radius the radius
	 */
	public SpaceAlarmXmlCellHandler(double latitude, double longitude, double radius) {
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes attributes) throws SAXException {
		if (localName.equals("cell")) {
			try {
				// Get data from XML
				double latitude = Double.parseDouble(attributes.getValue("lat"));
				double longitude = Double.parseDouble(attributes.getValue("lon"));
				int cell = Integer.parseInt(attributes.getValue("cellId"));
				String lacString = attributes.getValue("lac");
				int lac = lacString.length() > 0 ? Integer.parseInt(attributes.getValue("lac")) : 0;
				int mnc = Integer.parseInt(attributes.getValue("mnc"));
				int mcc = Integer.parseInt(attributes.getValue("mcc"));
				int samples = Integer.parseInt(attributes.getValue("nbSamples"));
				// Add a new cell location with this data to the cellLocations list
				cellLocations.add(new SpaceAlarmCellLocation(latitude, longitude, cell, lac, mnc, mcc, samples));
			} catch (Exception e) {}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		
	}
	
	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#endDocument()
	 */
	@Override
	public void endDocument() throws SAXException {
		// Sort the cell locations
		this.sortCellLocations(latitude, longitude);
	}
	
	/**
	 * Gets the cell locations.
	 *
	 * @return the cell locations
	 */
	public List<SpaceAlarmCellLocation> getCellLocations() {
		return cellLocations;
	}
	
	/**
	 * Sort the cell locations from closest to farthest from the input latitude and longitude variables.
	 *
	 * @param latitude the latitude
	 * @param longitude the longitude
	 */
	private void sortCellLocations(final double latitude, final double longitude) {
		Comparator<SpaceAlarmCellLocation> comparator = new Comparator<SpaceAlarmCellLocation>() {
			@Override
			public int compare(SpaceAlarmCellLocation cellLocation1, SpaceAlarmCellLocation cellLocation2) {
				float[] results1 = new float[1];
				float[] results2 = new float[1];
				// Find distance to input latitude and longitude for each result
				Location.distanceBetween(latitude, longitude, cellLocation1.getLatitude(), cellLocation1.getLongitude(), results1);
				Location.distanceBetween(latitude, longitude, cellLocation2.getLatitude(), cellLocation2.getLongitude(), results2);
				return Float.compare(Math.abs(results1[0]), Math.abs(results2[0])); 
			}

		};
		// Sort using comparator
		Collections.sort(cellLocations, comparator);
	}

}
