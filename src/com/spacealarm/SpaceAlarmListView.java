package com.spacealarm;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListView;

/**
 * This class extends ListView to allow the context menu information 
 * such as id and title to be set.
 */
public class SpaceAlarmListView extends ListView {

	/** The context menu info. */
	private SpaceAlarmListContextMenuInfo contextMenuInfo;
	
	/**
	 * Instantiates a new space alarm list view.
	 *
	 * @param context the context
	 */
	public SpaceAlarmListView(Context context) {
		super(context);
	}

	/**
	 * Instantiates a new space alarm list view.
	 *
	 * @param context the context
	 * @param attrs the attrs
	 */
	public SpaceAlarmListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	/**
	 * Instantiates a new space alarm list view.
	 *
	 * @param context the context
	 * @param attrs the attrs
	 * @param defStyle the def style
	 */
	public SpaceAlarmListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	/* (non-Javadoc)
	 * @see android.widget.AbsListView#getContextMenuInfo()
	 */
	@Override
	protected ContextMenuInfo getContextMenuInfo() {
		return contextMenuInfo;
	}
	
	/**
	 * Takes a SpaceAlarmMapContextMenuInfo to pass to the ContextMenu handler.
	 *
	 * @param contextMenuInfo a SpaceAlarmMapContextMenuInfo wrapped around a GeoPoint
	 */
	public void setContextMenuInfo(SpaceAlarmListContextMenuInfo contextMenuInfo) {
		this.contextMenuInfo = contextMenuInfo;
	}
	
}
