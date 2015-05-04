package com.spacealarm;

import android.content.Context;
import android.content.ContentValues;

import android.database.Cursor;
import android.database.SQLException;

import android.database.sqlite.SQLiteDatabase;

/**
 * A database abstraction layer for the Space Alarm application. 
 * All CRUD SQL should be stored in this class.
 */
public class SpaceAlarmDatabase {
	
	/** The database name. */
	private final String DATABASE_NAME = "spacealarm";
	
	/** The database version. */
	private final int DATABASE_VERSION = 1;
	
	/** The alarms table name. */
	private final String TABLE_NAME_ALARMS = "alarms";
	
	/** The locations table name. */
	private final String TABLE_NAME_LOCATIONS = "locations";
	
	/** The locations_cells table name. */
	private final String TABLE_NAME_LOCATIONS_CELLS = "locations_cells";
	
	/** The cells table name. */
	private final String TABLE_NAME_CELLS = "cells";
	
	/** The measures table name. */
	private final String TABLE_NAME_MEASURES = "measures";
	
	/** The alarms table columns. */
	private final String[] TABLE_NAME_ALARMS_COLS = {
		"_id", 
		"name",
		"radius"
	};
	
	/** The location table columns. */
	private final String[] TABLE_NAME_LOCATIONS_COLS = {
		"_id", 
		"alarm_id", 
		"name",
		"latitude",
		"longitude",
		"inside"	
	};
	
	/** The locations_cells table columns. */
	private final String[] TABLE_NAME_LOCATIONS_CELLS_COLS = {
		"_id",
		"location_id",
		"cell_id"
	};
	
	/** The cell table columns. */
	private final String[] TABLE_NAME_CELLS_COLS = {
		"_id",
		"cell",
		"lac",
		"latitude",
		"longitude",
		"mnc",
		"mcc",
		"samples"
	};
	
	/** The measures table columns. */
	private final String[] TABLE_NAME_MEASURES_COLS = {
		"_id",
		"cell",
		"lac",
		"latitude",
		"longitude",
		"mnc",
		"mcc",
		"date"
	};

	/** The database. */
	private SQLiteDatabase database;
	
	/** The open helper. */
	private SpaceAlarmDatabaseOpenHelper openHelper;
	
	/**
	 * Instantiates a new space alarm database.
	 *
	 * @param context the context
	 */
	public SpaceAlarmDatabase(Context context) {
		openHelper = new SpaceAlarmDatabaseOpenHelper(
			context, 
			DATABASE_NAME, 
			DATABASE_VERSION, 
			TABLE_NAME_ALARMS, 
			TABLE_NAME_LOCATIONS, 
			TABLE_NAME_LOCATIONS_CELLS,
			TABLE_NAME_CELLS,
			TABLE_NAME_MEASURES
		);
	}
	
	 /**
 	 * Open database.
 	 *
 	 * @throws SQLException the sQL exception
 	 */
 	public void open() throws SQLException {
		 database = openHelper.getWritableDatabase();
	 }
    
	/**
	 * Close database.
	 */
	public void close() {
		openHelper.close();
	}

	/**
	 * Insert alarm.
	 *
	 * @param name the name
	 * @param radius the radius
	 * @return the long
	 */
	public long insertAlarm(String name, double radius) {
		return database.insert(
			TABLE_NAME_ALARMS, 
			null, 
			createContentValues(name, radius)
		);
	}
	
	/**
	 * Insert location.
	 *
	 * @param alarm_id the alarm_id
	 * @param name the name
	 * @param latitude the latitude
	 * @param longitude the longitude
	 * @param inside the inside
	 * @return the long
	 */
	public long insertLocation(long alarm_id, String name, double latitude, double longitude, int inside) {
		return database.insert(
			TABLE_NAME_LOCATIONS, 
			null, 
			createContentValues(alarm_id, name, latitude, longitude, inside)
		);
	}
	
	/**
	 * Insert location_cell.
	 *
	 * @param location_id the location_id
	 * @param cell_id the cell_id
	 * @return the long
	 */
	public long insertLocationCell(long location_id, long cell_id) {
		return database.insert(
			TABLE_NAME_LOCATIONS_CELLS, 
			null, 
			createContentValues(location_id, cell_id)
		);
	}
	
	/**
	 * Insert or update cell.
	 *
	 * @param cell the cell
	 * @param lac the lac
	 * @param latitude the latitude
	 * @param longitude the longitude
	 * @param mnc the mnc
	 * @param mcc the mcc
	 * @param samples the samples
	 * @return the long
	 */
	public long insertCell(long cell, long lac, double latitude, double longitude, long mnc, long mcc, long samples) {
		long id;
		Cursor cursor = selectCellByCellLacMncMcc(cell, lac, mnc, mcc);
		if (cursor.getCount() > 0 && cursor.moveToFirst()) {
			id = cursor.getLong(cursor.getColumnIndex("_id"));
			updateCell(id, cell, lac, latitude, longitude, mnc, mcc, samples);
		} else {
			id = database.insert(
				TABLE_NAME_CELLS, 
				null, 
				createContentValues(cell, lac, latitude, longitude, mnc, mcc, samples)
			);
		}
		cursor.close();
		return id;
	}
	
	/**
	 * Insert measure.
	 *
	 * @param cell the cell
	 * @param lac the lac
	 * @param latitude the latitude
	 * @param longitude the longitude
	 * @param mnc the mnc
	 * @param mcc the mcc
	 * @param date the date
	 * @return the long
	 */
	public long insertMeasure(long cell, long lac, double latitude, double longitude, long mnc, long mcc, String date) {
		return database.insert(
			TABLE_NAME_MEASURES, 
			null, 
			createContentValues(cell, lac, latitude, longitude, mnc, mcc, date)
		);
	}

	/**
	 * Delete all alarms and locations.
	 */
	public void deleteAlarmAll() {
		deleteLocationAll();
		database.delete(
			TABLE_NAME_ALARMS, 
			null, 
			null
		);
	}
	
	/**
	 * Delete alarm and locations by id.
	 *
	 * @param id the id
	 */
	public void deleteAlarmById(long id) {
		deleteLocationByAlarmId(id);
		database.delete(
			TABLE_NAME_ALARMS, 
			"_id=" + id, 
			null
		);
	}
	
	/**
	 * Delete all locations and locations_cells.
	 */
	public void deleteLocationAll() {
		deleteLocationCellAll();
		database.delete(
			TABLE_NAME_LOCATIONS, 
			null, 
			null
		);
	}
	
	/**
	 * Delete location and locations_cells by id.
	 *
	 * @param id the id
	 */
	public void deleteLocationById(long id) {
		deleteLocationCellByLocationId(id);
		database.delete(
			TABLE_NAME_LOCATIONS, 
			"_id=" + id, 
			null
		);
	}
	
	/**
	 * Delete location by alarm id.
	 *
	 * @param alarm_id the alarm_id
	 */
	public void deleteLocationByAlarmId(long alarm_id) {
		Cursor cursor = selectLocationByAlarmId(alarm_id);
		if (cursor.moveToFirst()) {
			do {
				long location_id = cursor.getLong(cursor.getColumnIndex("_id"));
				deleteLocationCellByLocationId(location_id);
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		database.delete(
			TABLE_NAME_LOCATIONS, 
			"alarm_id=" + alarm_id, 
			null
		);
	}
	
	/**
	 * Delete all locations_cells.
	 */
	public void deleteLocationCellAll() {
		database.delete(
			TABLE_NAME_LOCATIONS_CELLS, 
			null, 
			null
		);
	}
	
	/**
	 * Delete location_cell by id.
	 *
	 * @param id the id
	 */
	public void deleteLocationCellById(long id) {
		database.delete(
			TABLE_NAME_LOCATIONS_CELLS, 
			"_id=" + id, 
			null
		);
	}
	
	/**
	 * Delete locations_cells and cells by location id.
	 *
	 * @param location_id the location_id
	 */
	public void deleteLocationCellByLocationId(long location_id) {
		deleteCellByLocationId(location_id);
		database.delete(
			TABLE_NAME_LOCATIONS_CELLS, 
			"location_id=" + location_id, 
			null
		);
	}
	
	/**
	 * Delete locations_cells by cell id.
	 *
	 * @param cell_id the cell_id
	 */
	public void deleteLocationCellByCellId(long cell_id) {
		database.delete(
			TABLE_NAME_LOCATIONS_CELLS, 
			"cell_id=" + cell_id, 
			null
		);
	}
	
	/**
	 * Delete all cells.
	 */
	public void deleteCellAll() {
		database.delete(
			TABLE_NAME_CELLS, 
			null, 
			null
		);
	}
	
	/**
	 * Delete cell by id.
	 *
	 * @param id the id
	 */
	public void deleteCellById(long id) {
		database.delete(
			TABLE_NAME_CELLS, 
			"_id=" + id, 
			null
		);
	}
	
	/**
	 * Delete cells by location id.
	 *
	 * @param location_id the location_id
	 */
	public void deleteCellByLocationId(long location_id) {
		// Get locations_cells associated with this location 
		Cursor cursor = selectLocationCellByLocationId(location_id);
		// Iterate through locations_cells
		if (cursor.moveToFirst()) {
			do {
				// Get cell id
				long cell_id = cursor.getLong(cursor.getColumnIndex("cell_id"));
				// If this cell is exclusively associated with this location
				if (isCellExclusivelyLocation(cell_id, location_id)) {
					// Delete cell
					deleteCellById(cell_id);
				}
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
	}
	
	/**
	 * Delete all measures.
	 */
	public void deleteMeasureAll() {
		database.delete(
			TABLE_NAME_MEASURES, 
			null, 
			null
		);
	}
	
	/**
	 * Delete measure by id.
	 *
	 * @param id the id
	 */
	public void deleteMeasureById(long id) {
		database.delete(
			TABLE_NAME_MEASURES, 
			"_id=" + id, 
			null
		);
	}
	
	/**
	 * Select all alarms.
	 *
	 * @return the cursor
	 */
	public Cursor selectAlarmAll() {
		return database.query(
			TABLE_NAME_ALARMS, 
			TABLE_NAME_ALARMS_COLS,
			null, 
			null, 
			null, 
			null, 
			null
		);
	}

	/**
	 * Select alarm by id.
	 *
	 * @param id the id
	 * @return the cursor
	 * @throws SQLException the sQL exception
	 */
	public Cursor selectAlarmById(long id) throws SQLException {
		Cursor cursor = database.query(
			true, 
			TABLE_NAME_ALARMS, 
			TABLE_NAME_ALARMS_COLS,
			"_id=" + id, 
			null,
			null, 
			null, 
			null, 
			null
		);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}
	
	/**
	 * Select all locations.
	 *
	 * @return the cursor
	 */
	public Cursor selectLocationAll() {
		return database.query(
			TABLE_NAME_LOCATIONS,
			TABLE_NAME_LOCATIONS_COLS,
			null, 
			null, 
			null, 
			null, 
			null
		);
	}
	
	/**
	 * Select location by id.
	 *
	 * @param id the id
	 * @return the cursor
	 */
	public Cursor selectLocationById(long id) {
		return database.query(
			TABLE_NAME_LOCATIONS, 
			TABLE_NAME_LOCATIONS_COLS,
			"_id=" + id, 
			null, 
			null, 
			null, 
			null
		);
	}
	
	/**
	 * Select location by alarm id.
	 *
	 * @param alarm_id the alarm_id
	 * @return the cursor
	 */
	public Cursor selectLocationByAlarmId(long alarm_id) {
		return database.query(
			TABLE_NAME_LOCATIONS, 
			TABLE_NAME_LOCATIONS_COLS,
			"alarm_id=" + alarm_id, 
			null, 
			null, 
			null, 
			null
		);
	}
	
	/**
	 * Select all locations_cells.
	 *
	 * @return the cursor
	 */
	public Cursor selectLocationCellAll() {
		return database.query(
			TABLE_NAME_LOCATIONS_CELLS,
			TABLE_NAME_LOCATIONS_CELLS_COLS,
			null, 
			null, 
			null, 
			null, 
			null
		);
	}
	
	/**
	 * Select location_cell by id.
	 *
	 * @param id the id
	 * @return the cursor
	 */
	public Cursor selectLocationCellById(long id) {
		return database.query(
			TABLE_NAME_LOCATIONS_CELLS, 
			TABLE_NAME_LOCATIONS_CELLS_COLS,
			"_id=" + id, 
			null, 
			null, 
			null, 
			null
		);
	}
	
	/**
	 * Select locations_cells by location id.
	 *
	 * @param location_id the location_id
	 * @return the cursor
	 */
	public Cursor selectLocationCellByLocationId(long location_id) {
		return database.query(
			TABLE_NAME_LOCATIONS_CELLS, 
			TABLE_NAME_LOCATIONS_CELLS_COLS,
			"location_id=" + location_id, 
			null, 
			null, 
			null, 
			null
		);
	}
	
	/**
	 * Select locations_cells by cell id.
	 *
	 * @param cell_id the cell_id
	 * @return the cursor
	 */
	public Cursor selectLocationCellByCellId(long cell_id) {
		return database.query(
			TABLE_NAME_LOCATIONS_CELLS, 
			TABLE_NAME_LOCATIONS_CELLS_COLS,
			"cell_id=" + cell_id, 
			null, 
			null, 
			null, 
			null
		);
	}
	
	/**
	 * Select all cells.
	 *
	 * @return the cursor
	 */
	public Cursor selectCellAll() {
		return database.query(
			TABLE_NAME_CELLS,
			TABLE_NAME_CELLS_COLS,
			null, 
			null, 
			null, 
			null, 
			null
		);
	}
	
	/**
	 * Select cell by id.
	 *
	 * @param id the id
	 * @return the cursor
	 */
	public Cursor selectCellById(long id) {
		return database.query(
			TABLE_NAME_CELLS, 
			TABLE_NAME_CELLS_COLS,
			"_id=" + id, 
			null, 
			null, 
			null, 
			null
		);
	}
	
	/**
	 * Select cell by cell ID, LAC, MNC and MCC.
	 *
	 * @param cell the cell
	 * @param lac the lac
	 * @param mnc the mnc
	 * @param mcc the mcc
	 * @return the cursor
	 */
	public Cursor selectCellByCellLacMncMcc(long cell, long lac, long mnc, long mcc) {
		return database.query(
			TABLE_NAME_CELLS, 
			TABLE_NAME_CELLS_COLS,
			"cell=" + cell + " AND lac=" + lac + " AND mnc=" + mnc + " AND mcc=" + mcc, 
			null, 
			null, 
			null, 
			null
		);
	}
	
	/**
	 * Select all measures.
	 *
	 * @return the cursor
	 */
	public Cursor selectMeasureAll() {
		return database.query(
			TABLE_NAME_MEASURES,
			TABLE_NAME_MEASURES_COLS,
			null, 
			null, 
			null, 
			null, 
			null
		);
	}
	
	/**
	 * Select measure by id.
	 *
	 * @param id the id
	 * @return the cursor
	 */
	public Cursor selectMeasureById(long id) {
		return database.query(
			TABLE_NAME_MEASURES, 
			TABLE_NAME_MEASURES_COLS,
			"_id=" + id, 
			null, 
			null, 
			null, 
			null
		);
	}

	/**
	 * Update an alarm.
	 *
	 * @param id the id
	 * @param name the name
	 * @param radius the radius
	 * @return true, if successful
	 */
	public boolean updateAlarm(long id, String name, double radius) {
		return database.update(
			TABLE_NAME_ALARMS, 
			createContentValues(name, radius), 
			"_id=" + id, 
			null
		) > 0;
	}

	/**
	 * Update a single column for an alarm with a double.
	 *
	 * @param id the id
	 * @param name the name
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean updateAlarmColumn(long id, String name, double value) {
		if (name == "radius") {
			return database.update(
				TABLE_NAME_ALARMS, 
				createContentValuesColumn(name, value), 
				"_id=" + id, 
				null
			) > 0;
		}
		return false;
	}
	
	/**
	 * Update single column for an alarm with a String.
	 *
	 * @param id the id
	 * @param name the name
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean updateAlarmColumn(long id, String name, String value) {
		if (name == "name") {
			return database.update(
				TABLE_NAME_ALARMS, 
				createContentValuesColumn(name, value), 
				"_id=" + id, 
				null
			) > 0;
		}
		return false;
	}
	
	/**
	 * Update a location.
	 *
	 * @param id the id
	 * @param alarm_id the alarm_id
	 * @param name the name
	 * @param latitude the latitude
	 * @param longitude the longitude
	 * @param inside the inside
	 * @return true, if successful
	 */
	public boolean updateLocation(long id, long alarm_id, String name, double latitude, double longitude, long inside) {
		return database.update(
			TABLE_NAME_LOCATIONS, 
			createContentValues(alarm_id, name, latitude, longitude, inside), 
			"_id=" + id, 
			null
		) > 0;
	}
	
	/**
	 * Update a single column for a location with a String.
	 *
	 * @param id the id
	 * @param name the name
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean updateLocationColumn(long id, String name, String value) {
		if (name == "name") {
			return database.update(
				TABLE_NAME_LOCATIONS, 
				createContentValuesColumn(name, value), 
				"_id=" + id, null
			) > 0;
		}
		return false;
	}
	
	/**
	 * Update a single column for a location with a long.
	 *
	 * @param id the id
	 * @param name the name
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean updateLocationColumn(long id, String name, long value) {
		if (
			name == "alarm_id" || 
			name == "inside"
		) {
			return database.update(
				TABLE_NAME_LOCATIONS, 
				createContentValuesColumn(name, value), 
				"_id=" + id, 
				null
			) > 0;
		}
		return false;
	}
	
	/**
	 * Update a single column for a location with a double.
	 *
	 * @param id the id
	 * @param name the name
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean updateLocationColumn(long id, String name, double value) {
		if (
			name == "latitude" || 
			name == "longitude" 
		) {
			return database.update(
				TABLE_NAME_LOCATIONS, 
				createContentValuesColumn(name, value), 
				"_id=" + id, 
				null
			) > 0;
		}
		return false;
	}
	
	/**
	 * Update a location_cell.
	 *
	 * @param id the id
	 * @param location_id the location_id
	 * @param cell_id the cell_id
	 * @return true, if successful
	 */
	public boolean updateLocationCell(long id, long location_id, long cell_id) {
		return database.update(
			TABLE_NAME_LOCATIONS_CELLS, 
			createContentValues(location_id, cell_id),
			"_id=" + id, 
			null
		) > 0;
	}
	
	/**
	 * Update a single column for a location_cell with a long.
	 *
	 * @param id the id
	 * @param name the name
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean updateLocationCellColumn(long id, String name, long value) {
		if (
			name == "location_id" || 
			name == "cell_id"
		) {
			return database.update(
				TABLE_NAME_LOCATIONS_CELLS, 
				createContentValuesColumn(name, value), 
				"_id=" + id, 
				null
			) > 0;
		}
		return false;
	}
	
	/**
	 * Update a cell.
	 *
	 * @param id the id
	 * @param cell the cell
	 * @param lac the lac
	 * @param latitude the latitude
	 * @param longitude the longitude
	 * @param mnc the mnc
	 * @param mcc the mcc
	 * @param samples the samples
	 * @return true, if successful
	 */
	public boolean updateCell(long id, long cell, long lac, double latitude, double longitude, long mnc, long mcc, long samples) {
		return database.update(
			TABLE_NAME_CELLS, 
			createContentValues(cell, lac, latitude, longitude, mnc, mcc, samples),
			"_id=" + id, 
			null
		) > 0;
	}
	
	/**
	 * Update a single column for a cell with a long.
	 *
	 * @param id the id
	 * @param name the name
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean updateCellColumn(long id, String name, long value) {
		if (
			name == "cell" || 
			name == "lac" || 
			name == "mnc" || 
			name == "mcc" || 
			name == "samples"
		) {
			return database.update(
				TABLE_NAME_LOCATIONS_CELLS, 
				createContentValuesColumn(name, value), 
				"_id=" + id, 
				null
			) > 0;
		}
		return false;
	}
	
	/**
	 * Update a single column for a cell with a double.
	 *
	 * @param id the id
	 * @param name the name
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean updateCellColumn(long id, String name, double value) {
		if (
			name == "latitude" || 
			name == "longitude"
		) {
			return database.update(
				TABLE_NAME_LOCATIONS_CELLS, 
				createContentValuesColumn(name, value), 
				"_id=" + id, 
				null
			) > 0;
		}
		return false;
	}
	
	/**
	 * Update a measure.
	 *
	 * @param id the id
	 * @param cell the cell
	 * @param lac the lac
	 * @param latitude the latitude
	 * @param longitude the longitude
	 * @param mnc the mnc
	 * @param mcc the mcc
	 * @param date the date
	 * @return true, if successful
	 */
	public boolean updateMeasure(long id, long cell, long lac, double latitude, double longitude, long mnc, long mcc, String date) {
		return database.update(
			TABLE_NAME_MEASURES, 
			createContentValues(cell, lac, latitude, longitude, mnc, mcc, date),
			"_id=" + id, 
			null
		) > 0;
	}
	
	/**
	 * Update a single column for a measure with a long.
	 *
	 * @param id the id
	 * @param name the name
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean updateMeasureColumn(long id, String name, long value) {
		if (
			name == "cell" || 
			name == "lac" || 
			name == "mnc" ||
			name == "mcc"
		) {
			return database.update(
				TABLE_NAME_LOCATIONS_CELLS, 
				createContentValuesColumn(name, value), 
				"_id=" + id, 
				null
			) > 0;
		}
		return false;
	}
	
	/**
	 * Update a single column for a measure with a double.
	 *
	 * @param id the id
	 * @param name the name
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean updateMeasureColumn(long id, String name, double value) {
		if (
			name == "latitude" || 
			name == "longitude"
		) {
			return database.update(
				TABLE_NAME_LOCATIONS_CELLS, 
				createContentValuesColumn(name, value), 
				"_id=" + id, 
				null
			) > 0;
		}
		return false;
	}
	
	/**
	 * Update a single column for a measure with a String.
	 *
	 * @param id the id
	 * @param name the name
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean updateMeasureColumn(long id, String name, String value) {
		if (
			name == "date"
		) {
			return database.update(
				TABLE_NAME_LOCATIONS_CELLS, 
				createContentValuesColumn(name, value), 
				"_id=" + id, 
				null
			) > 0;
		}
		return false;
	}
	
	/**
	 * Checks if the cell is exclusively associated with the location.
	 *
	 * @param cell_id the cell_id
	 * @param location_id the location_id
	 * @return true, if the cell is exclusively associated with the location
	 */
	public boolean isCellExclusivelyLocation(long cell_id, long location_id) {
		// Get locations_cells
		Cursor cursor = selectLocationCellByCellId(cell_id);
		boolean exclusive = true;
		// Iterate through cells
		if (cursor.moveToFirst()) {
			do {
				// If location_cell location_id always equals location_id then this cell is exclusively associated with the location 
				exclusive = location_id == cursor.getLong(cursor.getColumnIndex("location_id"));
			} while (cursor.moveToNext() && exclusive);
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return exclusive;
	}
	
	/**
	 * Creates a single content values column with a long.
	 *
	 * @param name the name
	 * @param value the value
	 * @return the content values
	 */
	private ContentValues createContentValuesColumn(String name, long value) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(name, value);
		return contentValues;	
	}
	
	/**
	 * Creates a single content values column with a double.
	 *
	 * @param name the name
	 * @param value the value
	 * @return the content values
	 */
	private ContentValues createContentValuesColumn(String name, double value) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(name, value);
		return contentValues;	
	}
	
	/**
	 * Creates a single content values column with a String.
	 *
	 * @param name the name
	 * @param value the value
	 * @return the content values
	 */
	private ContentValues createContentValuesColumn(String name, String value) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(name, value);
		return contentValues;
	}
	
	/**
	 * Creates the content values for alarms.
	 *
	 * @param name the name
	 * @param radius the radius
	 * @return the content values
	 */
	private ContentValues createContentValues(String name, double radius) {
		ContentValues contentValues = new ContentValues();
		contentValues.put("name", name);
		contentValues.put("radius", radius);
		return contentValues;
	}
	
	/**
	 * Creates the content values for locations.
	 *
	 * @param alarm_id the alarm_id
	 * @param name the name
	 * @param latitude the latitude
	 * @param longitude the longitude
	 * @param inside the inside
	 * @return the content values
	 */
	private ContentValues createContentValues(long alarm_id, String name, double latitude, double longitude, long inside) {
		ContentValues contentValues = new ContentValues();
		contentValues.put("alarm_id", alarm_id);
		contentValues.put("name", name);
		contentValues.put("latitude", latitude);
		contentValues.put("longitude", longitude);
		contentValues.put("inside", inside);
		return contentValues;
	}
	
	/**
	 * Creates the content values for locations_cells.
	 *
	 * @param location_id the location_id
	 * @param cell_id the cell_id
	 * @return the content values
	 */
	private ContentValues createContentValues(long location_id, long cell_id) {
		ContentValues contentValues = new ContentValues();
		contentValues.put("location_id", location_id);
		contentValues.put("cell_id", cell_id);
		return contentValues;
	}

	/**
	 * Creates the content values for cells.
	 *
	 * @param cell the cell
	 * @param lac the lac
	 * @param latitude the latitude
	 * @param longitude the longitude
	 * @param mnc the mnc
	 * @param mcc the mcc
	 * @param samples the samples
	 * @return the content values
	 */
	private ContentValues createContentValues(long cell, long lac, double latitude, double longitude, long mnc, long mcc, long samples) {
		ContentValues contentValues = new ContentValues();
		contentValues.put("cell", cell);
		contentValues.put("lac", lac);
		contentValues.put("latitude", latitude);
		contentValues.put("longitude", longitude);
		contentValues.put("mnc", mnc);
		contentValues.put("mcc", mcc);
		contentValues.put("samples", samples);
		return contentValues;
	}

	/**
	 * Creates the content values for measures.
	 *
	 * @param cell the cell
	 * @param lac the lac
	 * @param latitude the latitude
	 * @param longitude the longitude
	 * @param mnc the mnc
	 * @param mcc the mcc
	 * @param date the date
	 * @return the content values
	 */
	private ContentValues createContentValues(long cell, long lac, double latitude, double longitude, long mnc, long mcc, String date) {
		ContentValues contentValues = new ContentValues();
		contentValues.put("cell", cell);
		contentValues.put("lac", lac);
		contentValues.put("latitude", latitude);
		contentValues.put("longitude", longitude);
		contentValues.put("mnc", mnc);
		contentValues.put("mcc", mcc);
		contentValues.put("date", date);
		return contentValues;
	}

}
