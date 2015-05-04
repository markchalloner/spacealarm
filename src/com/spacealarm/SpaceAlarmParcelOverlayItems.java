package com.spacealarm;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class allows a list of SpaceAlarmOverlayItems to be passed from Activity to Activity
 */
public class SpaceAlarmParcelOverlayItems implements Parcelable {

	/** The overlays. */
	ArrayList<SpaceAlarmOverlayItem> overlays = new ArrayList<SpaceAlarmOverlayItem>();
	
	/**
	 * Instantiates a new space alarm parcel overlay items.
	 *
	 * @param overlays the overlays
	 */
	public SpaceAlarmParcelOverlayItems(ArrayList<SpaceAlarmOverlayItem> overlays) {
		this.overlays = overlays;
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
		out.writeTypedList(overlays);
	}
	
	/** The Constant CREATOR. */
	public static final Parcelable.Creator<SpaceAlarmParcelOverlayItems> CREATOR = new Parcelable.Creator<SpaceAlarmParcelOverlayItems>() {
		public SpaceAlarmParcelOverlayItems createFromParcel(Parcel in) {
			ArrayList<SpaceAlarmOverlayItem> overlays = in.createTypedArrayList(SpaceAlarmOverlayItem.CREATOR);
			return new SpaceAlarmParcelOverlayItems(overlays);
		}
		public SpaceAlarmParcelOverlayItems[] newArray(int size) {
			return new SpaceAlarmParcelOverlayItems[size];
		}
	};

	
}
