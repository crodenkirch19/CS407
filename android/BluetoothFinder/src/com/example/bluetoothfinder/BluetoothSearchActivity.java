package com.example.bluetoothfinder;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class BluetoothSearchActivity extends ListActivity {

	BluetoothAdapter mBluetoothAdapter;
	BluetoothSignalAdapter mSignalAdapter;
	private static final int REQUEST_ENABLE_BT = 1;
	
	// Create a BroadcastReceiver for BluetoothDevice.ACTION_FOUND
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
	    public void onReceive(Context context, Intent intent) {
	        String action = intent.getAction();
	        
	        // When discovery finds a device
	        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
	           
	        	// Get the BluetoothDevice object from the Intent
	            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	            short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
	            
	            // Log device info
	            Log.d("FoundDevice", "Found device: " + device.getName());
	            Log.d("FoundDevice", "RSSI: " + rssi);
	            
	            // Add data so that it can be displayed in the ListView
	            mSignalAdapter.addSignal(new BluetoothSignal(device.getName(), device.getAddress(), rssi));
	        }
	    }
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Set up our bluetooth adapter for scanning
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		if (mBluetoothAdapter == null) {
		    // Device does not support Bluetooth
			Toast.makeText(this, "Device does not support bluetooth", Toast.LENGTH_SHORT).show();
		}
		else if (!mBluetoothAdapter.isEnabled()) { 
			// Adapter exists, but is disabled
		    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);	
		    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
		else { 
			// Adapter exists and is enabled already
			startBluetoothDiscovery();
		}
		
		// Initialize our signal adapter and hook it up to this activity
		mSignalAdapter = new BluetoothSignalAdapter(this);
		setListAdapter(mSignalAdapter);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(mReceiver); // Unregister our BroadcastReceiver
	}

	private void startBluetoothDiscovery() {
	    // Register the BroadcastReceiver
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
		mBluetoothAdapter.startDiscovery();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
			
		switch (requestCode) {
		case REQUEST_ENABLE_BT:
			if (resultCode == RESULT_OK) {
				Toast.makeText(this, "Bluetooth enabled", Toast.LENGTH_SHORT).show();
				startBluetoothDiscovery();
			}
			else {
				Toast.makeText(this, "Bluetooth disabled", Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}

}
