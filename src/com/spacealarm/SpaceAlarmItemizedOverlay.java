package com.spacealarm;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.location.Address;

import com.google.android.maps.ItemizedOverlay;

/**
 * Extends the ItemizedOverlay class with useful methods and implements the OnTap handler
 */
public class SpaceAlarmItemizedOverlay extends ItemizedOverlay<SpaceAlarmOverlayItem> {

	/** The Constant TYPE_SEARCH. */
	public final static int TYPE_SEARCH = 0;
	
	/** The Constant TYPE_ADD. */
	public final static int TYPE_ADD = 1;
	
	/** The overlays. */
	private ArrayList<SpaceAlarmOverlayItem> overlays = new ArrayList<SpaceAlarmOverlayItem>();
	
	/** The overlays deleted. */
	private ArrayList<SpaceAlarmOverlayItem> overlaysDeleted = new ArrayList<SpaceAlarmOverlayItem>();
	
	/** The context. */
	private SpaceAlarmActivityMap context;
	
	/** The type. */
	private int type;
	
	/**
	 * Instantiates a new space alarm itemized overlay.
	 *
	 * @param marker the marker
	 * @param context the context
	 * @param type the type
	 */
	public SpaceAlarmItemizedOverlay(Drawable marker, SpaceAlarmActivityMap context, int type) {
		super(boundCenterBottom(marker));
		this.context = context;
		this.type = type;
	}

	/* (non-Javadoc)
	 * @see com.google.android.maps.ItemizedOverlay#createItem(int)
	 */
	@Override
	protected SpaceAlarmOverlayItem createItem(int i) {
		return overlays.get(i);
	}
	

	/* (non-Javadoc)
	 * @see com.google.android.maps.ItemizedOverlay#size()
	 */
	@Override
	public int size() {
		return overlays.size();
	}

	/**
	 * Adds an overlay item to the list of overlays.
	 *
	 * @param overlayitem the overlayitem
	 */
	public void addOverlay(SpaceAlarmOverlayItem overlayitem) {
		overlays.add(overlayitem);
	    populate();
	    setLastFocusedIndex(-1);
	}
	
	/**
	 * Removes item at index from list of overlay items.
	 *
	 * @param index the index
	 */
	public void clearOverlay(int index) {
		SpaceAlarmOverlayItem overlay = overlays.get(index);
		// Add item to the list of deleted overlay items
		overlaysDeleted.add(overlay);
		overlays.remove(index);
		populate();
		// Reset focused index to avoid OutOfBounds Exceptions
		setLastFocusedIndex(-1);
	}
	
	/**
	 * Clears the list of overlay items.
	 */
	public void clearOverlays() {
		// Iterate through the list of overlay items
		for (int i = 0; i < overlays.size(); i++) {
			// Get overlay item
			SpaceAlarmOverlayItem overlay = overlays.get(i);
			// Add item to the list of deleted overlay items
			overlaysDeleted.add(overlay);
		}
		overlays.clear();
		populate();
		// Reset focused index to avoid OutOfBounds Exceptions
		setLastFocusedIndex(-1);
	}
	
	/**
	 * Gets the list of overlay items.
	 *
	 * @return the overlays
	 */
	public ArrayList<SpaceAlarmOverlayItem> getOverlays() {
		return overlays;
	}
	
	/**
	 * Gets the listt of deleted overlay items.
	 *
	 * @return the overlays deleted
	 */
	public ArrayList<SpaceAlarmOverlayItem> getOverlaysDeleted() {
		return overlaysDeleted;
	}
	
	/**
	 * Sets the list of overlay items to the parameter.
	 *
	 * @param overlays the new overlays
	 */
	public void setOverlays(ArrayList<SpaceAlarmOverlayItem> overlays) {
		this.overlays = overlays;
		populate();
		setLastFocusedIndex(-1);
	}
	
	/* (non-Javadoc)
	 * @see com.google.android.maps.ItemizedOverlay#onTap(int)
	 */
	@Override
	protected boolean onTap(final int index) {
		// Get overlay item at index tapped
		SpaceAlarmOverlayItem item = overlays.get(index);
		// Get the item data
		final SpaceAlarmGeoPoint point = new SpaceAlarmGeoPoint(item.getPoint());
		final String title = item.getTitle();
		final String snippet = item.getSnippet();
		final Address address = item.getAddress();
		switch (type) {
			// If this is a list of searched overlay items
			case TYPE_SEARCH:
				// Show a context menu
				AlertDialog dialogSearch = new AlertDialog.Builder(context)
					.setTitle(title)
					// Set an Add item
					.setItems(R.array.map_dialog_add, new DialogInterface.OnClickListener() {
			            public void onClick(DialogInterface dialog, int item) {
			            	// Add the point to the list of added overlay items
			            	context.setAddPoint(point, title, snippet, address);
			            }
					})
					.create();
		        dialogSearch.show();
				break;
			// If this is a list of added overlay items	
			case TYPE_ADD:
				// Show a context menu
				AlertDialog dialogAdd = new AlertDialog.Builder(context)
					.setTitle(item.getTitle())
					// Set an Remove item
					.setItems(R.array.map_dialog_remove, new DialogInterface.OnClickListener() {
			            public void onClick(DialogInterface dialog, int item) {
			            	// Remove the point from the list of added overlay items
			            	context.clearAddPoint(index);
			            }
			        })
			        .create();
		        dialogAdd.show();
				break;
		}
		return true;
	}
}
