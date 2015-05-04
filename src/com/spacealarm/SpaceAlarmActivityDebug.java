package com.spacealarm;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Used to display the Debug screen. 
 * 
 * This class allows the user to set a custom Cell-ID, LAC, MNC and MCC.
 * 
 */
public class SpaceAlarmActivityDebug extends Activity {
    
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Load layout
        setContentView(R.layout.debug);
        
        final Context context = this;
        EditText cidEdit = (EditText) findViewById(R.id.debugCidEdit);
        EditText lacEdit = (EditText) findViewById(R.id.debugLacEdit);
        EditText mncEdit = (EditText) findViewById(R.id.debugMncEdit);
        EditText mccEdit = (EditText) findViewById(R.id.debugMccEdit);
        Button cidLacUpdate = (Button) findViewById(R.id.debugCidLacUpdate);
        Configuration configuration = context.getResources().getConfiguration();
        
        // Get current cell location
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        GsmCellLocation cellLocation = (GsmCellLocation) telephonyManager.getCellLocation();
        
        
        // Set Cell ID and LAC
        if (cellLocation == null) {
        	cidEdit.setText("-1");
        	lacEdit.setText("-1");
        } else {
        	cidEdit.setText("" + cellLocation.getCid());
        	lacEdit.setText("" + cellLocation.getLac());
        }
        mncEdit.setText("" + configuration.mnc);
        mccEdit.setText("" + configuration.mcc);
        
        // Add update button handler
        cidLacUpdate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				EditText cidEdit = (EditText) findViewById(R.id.debugCidEdit);
		        EditText lacEdit = (EditText) findViewById(R.id.debugLacEdit);
		        EditText mncEdit = (EditText) findViewById(R.id.debugMncEdit);
		        EditText mccEdit = (EditText) findViewById(R.id.debugMccEdit);
				try {
			        int cid = Integer.parseInt(cidEdit.getText().toString());
					int lac = Integer.parseInt(lacEdit.getText().toString());
					int mnc = Integer.parseInt(mncEdit.getText().toString());
					int mcc = Integer.parseInt(mccEdit.getText().toString());
					// Create a new cell location
					GsmCellLocation cellLocation = new GsmCellLocation();
					SpaceAlarmPhoneStateListener phoneStateListener = new SpaceAlarmPhoneStateListener(context);
					// Set the Cell ID and LAC
					cellLocation.setLacAndCid(lac, cid);
					// Set the debug MNC and MCC values
					phoneStateListener.setDebugMnc(mnc);
					phoneStateListener.setDebugMcc(mcc);
					// Notify the listener that the cell location has changed
					phoneStateListener.onCellLocationChanged(cellLocation);
					// Show confirmation
					Toast toast = Toast.makeText(context, "Sent new Cell ID, LAC, MNC and MCC (" + cid + ", " + lac + ", " + mnc + ", " + mcc + ") to Space Alarm service", Toast.LENGTH_LONG);
					toast.show();
				} catch (Exception e) {
					Toast toast = Toast.makeText(context, "You must enter a number in each field", Toast.LENGTH_LONG);
					toast.show();
				}
			}
        });
    }

}