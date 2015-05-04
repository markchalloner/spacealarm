package com.spacealarm;

import android.view.ContextMenu.ContextMenuInfo;

/**
 * This class is a wrapper for the data sent to the ListView context menu.
 */
public class SpaceAlarmListContextMenuInfo implements ContextMenuInfo {

	/** The id. */
	private long id;
	
	/** The title. */
	private String title;
	
	/**
	 * Instantiates a new space alarm list context menu info.
	 *
	 * @param id the id
	 * @param title the title
	 */
	public SpaceAlarmListContextMenuInfo(long id, String title) {
		this.id = id;
		this.title = title;
	}
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public long getId() {
		return id;
	}
	
	/**
	 * Gets the title.
	 *
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
}
