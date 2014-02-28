package com.example.bluetoothfinder;

import java.util.Date;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

public class BluetoothSearchActivity extends ListActivity {

	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothSignalAdapter mSignalAdapter;
	private boolean mScanning;
	private Date mDate;
	private Handler mHandler;
	
	private static final int REQUEST_ENABLE_BT = 1;
	private static final int SCAN_PERIOD = 1000; // Scan for 1 sec at a time
	private static final int WAIT_PERIOD = 5000; // Wait for 20 secs between scans
	
	// Create a callback to be run each time a new BLE device is discovered
	private BluetoothAdapter.LeScanCallback mLeScanCallback = 
			new BluetoothAdapter.LeScanCallback() {
		
		@Override
		public void onLeScan(final BluetoothDevice device, final int rssi, 
							 byte[] scanRecord) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (!mSignalAdapter.hasDeviceWithAddr(device.getAddress())) {
						// Add the BLE signal to our list adapter
						mSignalAdapter.addSignal(
								new BluetoothSignal(device.getName(), 
													device.getAddress(), 
													rssi,
													mDate.getTime()));
					}
					else {
						// Update the stored signal with the given address
						mSignalAdapter.updateRssiForDevice(device.getAddress(), rssi);
						mSignalAdapter.updateTimestampForDevice(device.getAddress(), mDate.getTime());
					}
				}
			});
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mHandler = new Handler();
		
		// Use this check to determine whether BLE is supported on the device. Then
		// you can selectively disable BLE-related features.
		if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
		    Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
		    finish();
		}
		
		BluetoothManager bluetoothManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
		
		// Set up our bluetooth adapter for scanning
		mBluetoothAdapter = bluetoothManager.getAdapter();
		
		if (mBluetoothAdapter == null) {
		    // Device does not support bluetooth
			Toast.makeText(this, R.string.bt_not_supported, Toast.LENGTH_SHORT).show();
			finish();
		}
		else if (!mBluetoothAdapter.isEnabled()) { 
			// Adapter exists, but is disabled
		    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);	
		    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
		else { 
			// Adapter exists and is enabled already
			scanLeDevice(true);
		}
		
		// Initialize our signal adapter and hook it up to this activity
		mSignalAdapter = new BluetoothSignalAdapter(this);
		setListAdapter(mSignalAdapter);
	}
	
	@Override
	protected void onDestroy() {
		// Stop scan if it is running
		scanLeDevice(false);

		super.onDestroy();
	}
	
	private void scanLeDevice(final boolean enable) {
		if (enable) {
			// In SCAN_PERIOD ms, stop the scan
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mScanning = false;
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
					
					mHandler.postDelayed(new Runnable() {
						@Override
						public void run() {
							mSignalAdapter.clearData();
							scanLeDevice(true);
						}
					}, WAIT_PERIOD);
				}
			}, SCAN_PERIOD);
			
			// Start scanning
			mScanning = true;
			mDate = new Date();
			mBluetoothAdapter.startLeScan(mLeScanCallback);
		}
		else {
			// If enable is false, stop the scan.
			mScanning = false;
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (requestCode) {
		case REQUEST_ENABLE_BT:
			if (resultCode == RESULT_OK) {
				Toast.makeText(this, "Bluetooth enabled", Toast.LENGTH_SHORT).show();
				scanLeDevice(true);
			}
			else {
				Toast.makeText(this, "Bluetooth disabled", Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}

}
