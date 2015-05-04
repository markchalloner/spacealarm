package com.spacealarm;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextMenu.ContextMenuInfo;

import com.google.android.maps.MapView;

/**
 * This class extends MapView to allow the context menu information 
 * such as latitude/longitude and heading to be set.
 */
public class SpaceAlarmMapView extends MapView {

	/** The context menu info. */
	private SpaceAlarmMapContextMenuInfo contextMenuInfo;
	
	/**
	 * Instantiates a new space alarm map view.
	 *
	 * @param context the context
	 * @param apiKey the api key
	 */
	public SpaceAlarmMapView(Context context, String apiKey) {
		super(context, apiKey);
	}

	/**
	 * Instantiates a new space alarm map view.
	 *
	 * @param context the context
	 * @param attrs the attrs
	 */
	public SpaceAlarmMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	/**
	 * Instantiates a new space alarm map view.
	 *
	 * @param context the context
	 * @param attrs the attrs
	 * @param defStyle the def style
	 */
	public SpaceAlarmMapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	/* (non-Javadoc)
	 * @see android.view.View#getContextMenuInfo()
	 */
	@Override
	protected ContextMenuInfo getContextMenuInfo() {
		return contextMenuInfo;
	}

	/**
	 * Sets the context menu info.
	 *
	 * @param contextMenuInfo the new context menu info
	 */
	public void setContextMenuInfo(SpaceAlarmMapContextMenuInfo contextMenuInfo) {
		this.contextMenuInfo = contextMenuInfo;
	}
	
}
