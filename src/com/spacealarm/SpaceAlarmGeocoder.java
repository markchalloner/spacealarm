package com.spacealarm;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

/**
 * The SpaceAlarmGeocoder is a customised geo-coder class which uses 
 * Google Local Search for geo-coding and can do cell lookups.
 */
public class SpaceAlarmGeocoder {
	
	/** The geocoder. */
	private Geocoder geocoder;
	
	/** The locale. */
	private Locale locale;
	
	/** The location manager. */
	private LocationManager locationManager;
	
	/** The criteria. */
	private Criteria criteria;
	
	/**
	 * Instantiates a new space alarm geocoder.
	 *
	 * @param context the context
	 */
	public SpaceAlarmGeocoder(Context context) {
		this(context, Locale.getDefault());
	}
	
	/**
	 * Instantiates a new space alarm geocoder.
	 *
	 * @param context the context
	 * @param locale the locale
	 */
	public SpaceAlarmGeocoder(Context context, Locale locale) {
		this.locale = locale;
		this.geocoder = new Geocoder(context, locale);
		this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		this.criteria = new Criteria();
		criteria.setAccuracy(Criteria.NO_REQUIREMENT);
	}
	
	/**
	 * Gets a list of addresses given a location name.
	 *
	 * @param locationName the location name
	 * @param maxResults the max results
	 * @return the from location name
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws JSONException the jSON exception
	 */
	public List<Address> getFromLocationName(String locationName, int maxResults) throws IOException, JSONException {
		List<Address> addresses = new ArrayList<Address>();
        // Get location to provide a context for the search
		Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, true));
        String locationString = "";
        if (location != null) {
        	locationString = "&sll=" + location.getLatitude() + "," + location.getLongitude();
        }
        // Set the service URL
        URL url = new URL("http://ajax.googleapis.com/ajax/services/search/local?v=1.0&q=" + locationName + "&rsz=" + maxResults + locationString);
        // Connect
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setDoOutput(true);
		connection.setReadTimeout(3000);         
		connection.connect();
		// Read data from the connection
		InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream());
		BufferedReader reader = new BufferedReader(inputStreamReader);
		StringBuilder stringBuilder = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			stringBuilder.append(line + "\n");
		}
		inputStreamReader.close();
		// Parse the response
		JSONObject json = new JSONObject(stringBuilder.toString());
		JSONObject responseData = json.getJSONObject("responseData");
		JSONArray results = responseData.getJSONArray("results");
		// Iterate through the results
		for (int i = 0; i < results.length(); i++) {
			JSONObject result = results.getJSONObject(i);
			// Parse each address and add it to the addresses list
			addresses.add(parseAddress(result));
		}
		return addresses;	
	}
	
	/**
	 * Parses the address.
	 *
	 * @param result the result
	 * @return the address
	 * @throws JSONException the jSON exception
	 */
	private Address parseAddress(JSONObject result) throws JSONException {
		Address address = new Address(locale);
		// Parse address data
		JSONArray addressLines = result.getJSONArray("addressLines");
		address.setAddressLine(0, result.getString("titleNoFormatting"));
		for (int i = 0; i < addressLines.length(); i++) {
			address.setAddressLine(i + 1, addressLines.getString(i));
		}
		address.setLocality(result.getString("region"));
		address.setCountryName(result.getString("country"));
		address.setLatitude(Double.parseDouble(result.getString("lat")));
		address.setLongitude(Double.parseDouble(result.getString("lng")));
		return address;
	}
	
	/**
	 * Gets the address from a location.
	 *
	 * @param latitude the latitude
	 * @param longitude the longitude
	 * @param maxResults the max results
	 * @return the from location
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public List<Address> getFromLocation(double latitude, double longitude, int maxResults) throws IOException {
		// Wrap the Geocoder class
		return geocoder.getFromLocation(latitude, longitude, maxResults);
	}
	
	/**
	 * Gets a list of cells from location.
	 *
	 * @param latitude the latitude
	 * @param longitude the longitude
	 * @param radius the radius
	 * @param maxResults the max results
	 * @return the cells from location
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws SAXException the sAX exception
	 */
	public List<SpaceAlarmCellLocation> getCellsFromLocation(double latitude, double longitude, double radius, int maxResults) throws IOException, ParserConfigurationException, SAXException {
		List<SpaceAlarmCellLocation> cellLocations = new ArrayList<SpaceAlarmCellLocation>();
		// Set the minimum number of results
		int minCellLocations = 1;
		// Set the maximum search radius
		double maxCellRadius = 40000;
		// Set the initial search radius
		double cellRadius = 78.125;
		// Loop until either the minimum nuber of results is found or the maximum search radius is exceeded
		while (cellLocations.size() < minCellLocations && cellRadius <= maxCellRadius) {
			Log.e("SpaceAlarm", "" + cellRadius + "m");
			// Create a bounding box the size of the search radius
			SpaceAlarmGeoBoundingBox geoBoundingBox = new SpaceAlarmGeoBoundingBox(latitude, longitude, cellRadius);
			// Set the service URL
			URL url = new URL("http://www.opencellid.org/cell/getInArea?key=1376c0204770deeab07b7b4757ced2bc&BBOX=" + geoBoundingBox.getBBox() + "&fmt=xml&limit=999");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			SAXParser saxParser = saxParserFactory.newSAXParser();
			XMLReader xmlReader = saxParser.getXMLReader();
			// Create a XML handler to parse the data and pass latitude and logitude for sorting
			SpaceAlarmXmlCellHandler xmlCellHandler = new SpaceAlarmXmlCellHandler(latitude, longitude, radius);
			Log.e("Space Alarm", url.toExternalForm());
			// Connect
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.setReadTimeout(3000);         
			connection.connect();
			// Parse the response
			xmlReader.setContentHandler(xmlCellHandler);
			xmlReader.parse(new InputSource(connection.getInputStream()));
			// Double the cell radius
			cellRadius *= 2;
			// Get the cell locations
			cellLocations = xmlCellHandler.getCellLocations();
		}
		Log.e("SpaceAlarm", "" + cellLocations.size());
		return cellLocations;
	}
	
}
