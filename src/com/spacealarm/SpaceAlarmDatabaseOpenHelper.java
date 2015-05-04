package com.spacealarm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * This class handles opening a database and optionally creating and upgrading. 
 */
public class SpaceAlarmDatabaseOpenHelper extends SQLiteOpenHelper {

	/** The table name alarms. */
	private String tableNameAlarms;
	
	/** The table name locations. */
	private String tableNameLocations;
	
	/** The table name locations cells. */
	private String tableNameLocationsCells;
	
	/** The table name cells. */
	private String tableNameCells;
	
	/** The table name measures. */
	private String tableNameMeasures;

    /**
     * Instantiates a new space alarm database open helper.
     *
     * @param context the context
     * @param databaseName the database name
     * @param databaseVersion the database version
     * @param tableNameAlarms the table name alarms
     * @param tableNameLocations the table name locations
     * @param tableNameLocationsCells the table name locations cells
     * @param tableNameCells the table name cells
     * @param tableNameMeasures the table name measures
     */
    SpaceAlarmDatabaseOpenHelper(
    	Context context, 
    	String databaseName, 
    	int databaseVersion, 
    	String tableNameAlarms, 
    	String tableNameLocations, 
    	String tableNameLocationsCells, 
    	String tableNameCells, 
    	String tableNameMeasures
    ) {
    	super(context, databaseName, null, databaseVersion);
    	this.tableNameAlarms = tableNameAlarms;
    	this.tableNameLocations = tableNameLocations;
    	this.tableNameLocationsCells = tableNameLocationsCells;
    	this.tableNameCells = tableNameCells;
    	this.tableNameMeasures = tableNameMeasures;
    }

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase database) {
		// Create tables
		database.execSQL(
			"CREATE TABLE " + tableNameAlarms + " (" +
				"_id INTEGER PRIMARY KEY, " +
				"name TEXT, " +
				"radius REAL " +
			")"
		);
		database.execSQL(
				"CREATE TABLE " + tableNameLocations + " (" +
				"_id INTEGER PRIMARY KEY, " +
				"alarm_id INTEGER, " +
				"name TEXT, " +
				"latitude REAL, " +
				"longitude REAL, " +
				"inside INTEGER " +
			")"
		);
		database.execSQL(
				"CREATE TABLE " + tableNameLocationsCells + " (" +
				"_id INTEGER PRIMARY KEY, " +
				"location_id INTEGER, " +
				"cell_id INTEGER " +
			")"
		);
		database.execSQL(
				"CREATE TABLE " + tableNameCells + " (" +
				"_id INTEGER PRIMARY KEY, " +
				"cell INTEGER, " +
				"lac INTEGER, " +
				"latitude REAL, " +
				"longitude REAL, " +
				"mnc INTEGER, " +
				"mcc INTEGER, " +
				"samples INTEGER " +
			")"
		);
		database.execSQL(
				"CREATE TABLE " + tableNameMeasures + " (" +
				"_id INTEGER PRIMARY KEY, " +
				"cell INTEGER, " +
				"lac INTEGER, " +
				"latitude REAL, " +
				"longitude REAL, " +
				"mnc INTEGER, " +
				"mcc INTEGER, " +
				"date TEXT " +
			")"
		);
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		// Upgrade tables
		database.execSQL("DROP TABLE IF EXISTS " + tableNameAlarms);
		database.execSQL("DROP TABLE IF EXISTS " + tableNameLocations);
		database.execSQL("DROP TABLE IF EXISTS " + tableNameLocationsCells);
		database.execSQL("DROP TABLE IF EXISTS " + tableNameCells);
		database.execSQL("DROP TABLE IF EXISTS " + tableNameMeasures);
		onCreate(database);
	}

}
