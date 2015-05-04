package com.spacealarm;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Used to display the Main screen.
 */
public class SpaceAlarmActivityMain extends Activity 
{
	
	/** A unique identifier when starting the Edit Alarm Activity  */
	private static final int ALARM_ACTIVITY = 1;
	
	/** A unique identifier when starting the Preference Activity  */
	private static final int PREFERENCE_ACTIVITY = 2;
	
	/** A unique identifier when starting the Debug Activity  */
	private static final int DEBUG_ACTIVITY = 3;
	
	/** The shared preferences. */
	private SharedPreferences sharedPreferences;
	
	/** The service intent. */
	private Intent serviceIntent;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
    }
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();
		manageService();
		showList();
	}
    
	/**
	 * Shows a list of added alarms.
	 */
	private void showList() {
		// Get widgets
		TextView instructionsAlarmsNone = (TextView) findViewById(R.id.instructionsAlarmsNone);
		TextView instructionsAlarmsAdd = (TextView) findViewById(R.id.instructionsAlarmsAdd);	
		final SpaceAlarmListView alarmsList = (SpaceAlarmListView) findViewById(R.id.alarmsList);
		TextView alarmsListCount = (TextView) findViewById(R.id.alarmsListCount);
		final SpaceAlarmActivityMain context = this;
		
		// Get database
		final SpaceAlarmDatabase database = new SpaceAlarmDatabase(this);
		
		// Add a context menu to each item of the list
		registerForContextMenu(alarmsList);
		
		// Open database
		database.open();
        Cursor cursor = database.selectAlarmAll();
        this.startManagingCursor(cursor);
        int count = cursor.getCount();
        // If one or more alarms have been created
        if (count > 0) {
			// Pass the cursor into the list and provide options to show the list
        	alarmsList.setAdapter(
				new SimpleCursorAdapter(this,
					R.layout.alarmlist,
					cursor,
					new String[] { "name" },
					new int[] { R.id.alarmListText }
				)
			);
        	
        	// Set an click handler for the list
			alarmsList.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
					// Edit the list item
					editAlarm(id);
				}
			});
			// Set a long click handler for the list
			alarmsList.setOnItemLongClickListener(new OnItemLongClickListener(){
				@Override
				public boolean onItemLongClick(AdapterView<?> adapter, View view, int position, long id) {
					// Get alarm information
					Cursor cursor = (Cursor) alarmsList.getItemAtPosition(position);
					String name = cursor.getString(cursor.getColumnIndex("name"));
					// Set context menu heading
					alarmsList.setContextMenuInfo(new SpaceAlarmListContextMenuInfo(id, name));
					// Show context menu
					context.openContextMenu(alarmsList);
					return true;
				}
			});
			// Set an inication of the number of items above the list
			alarmsListCount.setText(getResources().getQuantityString(R.plurals.main_alarm_count, count, count));		
        	// Hide the text instructions
			instructionsAlarmsNone.setVisibility(TextView.GONE);
        	instructionsAlarmsAdd.setVisibility(TextView.GONE);
        	// Show the list
        	alarmsListCount.setVisibility(TextView.VISIBLE);
        	alarmsList.setVisibility(ListView.VISIBLE);
        // Otherwise if no alarms  have been created 
        } else {
        	// Show the instructions
        	instructionsAlarmsAdd.setText(Html.fromHtml(getString(R.string.instructions_alarms_add)));
        	instructionsAlarmsNone.setVisibility(TextView.VISIBLE);
        	instructionsAlarmsAdd.setVisibility(TextView.VISIBLE);
        	// Hide the list
        	alarmsListCount.setVisibility(TextView.GONE);
        	alarmsList.setVisibility(ListView.GONE);
        }
        // Close the database
		database.close();
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		// Show debug item if enabled in preferences
		if (sharedPreferences.getBoolean("debugEnabled", false))  {
			MenuItem item = menu.findItem(R.id.menuMainDebug);
			item.setVisible(true);
		}
		return true;
	}
	
    /* (non-Javadoc)
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_option, menu);
        return true;
    }
    
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		SpaceAlarmListContextMenuInfo info = (SpaceAlarmListContextMenuInfo) menuInfo;
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_context, menu);
		String title = info.getTitle();
		menu.setHeaderTitle(title);
	}
    
    /* (non-Javadoc)
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
    	switch (item.getItemId()) {
			case R.id.menuMainNew:
				editAlarm(0);
				return true;
			case R.id.menuMainPreferences:
				intent = new Intent(this, SpaceAlarmActivityPreferences.class);
		        startActivityForResult(intent, PREFERENCE_ACTIVITY);
				return true;
			case R.id.menuMainDebug:
				intent = new Intent(this, SpaceAlarmActivityDebug.class);
				startActivityForResult(intent, DEBUG_ACTIVITY);
				return true;
   
        }
        return false;
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
     */
    @Override
	public boolean onContextItemSelected(MenuItem item) {
    	SpaceAlarmListContextMenuInfo menuInfo = (SpaceAlarmListContextMenuInfo) item.getMenuInfo();
    	final SpaceAlarmActivityMain context = this;
    	final long id = menuInfo.getId();
		switch (item.getItemId()) {
			case R.id.contextMainEdit:
				editAlarm(id);
				return true;
			case R.id.contextMainDelete:
				// Show a warning if an alarm is to be deleted
				AlertDialog alertDialog = new AlertDialog.Builder(this).create();
			    alertDialog.setTitle("Delete");
			    alertDialog.setMessage("This alarm will be deleted");
			    alertDialog.setIcon(R.drawable.ic_dialog_warning);
			    // Set the OK button click handler
	    		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// Delete the alarm if OK has been clicked
						final SpaceAlarmDatabase database = new SpaceAlarmDatabase(context);
						database.open();
						database.deleteAlarmById(id);
						showList();
						return;
					} 
				});
	    		// Set the Cancel button click handler
			    alertDialog.setButton2("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						return;
					}
				});
			    alertDialog.show();
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}
    
    /**
     * Edits the alarm.
     *
     * @param id the id
     */
    private void editAlarm(long id) {
    	// Show the Edit Activity
    	Intent intent = new Intent(this, SpaceAlarmActivityEdit.class);
		Bundle data = new Bundle();
		data.putLong("com.spacealarm.SpaceAlarmActivityAlarm.id", id);
		intent.putExtras(data);
		startActivityForResult(intent, ALARM_ACTIVITY);
    }
    
    /**
     * Manage service.
     */
    public void manageService() {
    	// Start or stop service according to user preference
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		serviceIntent = new Intent(this, SpaceAlarmService.class);
    	if (sharedPreferences.getBoolean("serviceEnabled", false)) {
			this.startService(serviceIntent);
		} else {
			this.stopService(serviceIntent);
		}
    }
}