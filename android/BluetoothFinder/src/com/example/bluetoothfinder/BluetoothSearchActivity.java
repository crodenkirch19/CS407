package com.example.bluetoothfinder;

import java.util.Date;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BluetoothSearchActivity extends Activity {

	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothSignalAdapter mSignalAdapter;
	private boolean mScanning;
	private Date mDate;
	private Handler mHandler;
	private boolean mCanSeeBeacon;
	private int mWaitPeriod = 5000; // Wait for 5 secs between scans by default
	
	private static final int REQUEST_ENABLE_BT = 1;
	private static final int SCAN_PERIOD = 1000; // Scan for 1 sec at a time
	private static final int MAX_SCAN_INTERVAL = 300000; // 5 min
	
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
						// Signal to app that a beacon is nearby
						mCanSeeBeacon = true;
						
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
					// TODO: Add this signal to a list of signals found for this scan
				}
			});
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Initialize our signal adapter and hook it up to this activity
		mSignalAdapter = new BluetoothSignalAdapter(this);
		setContentView(R.layout.activity_bluetooth_search);
		ListView listView = (ListView)findViewById(R.id.list_bt_devices);
		listView.setAdapter(mSignalAdapter);
		
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
					
					
					if (mCanSeeBeacon) {
						// If a beacon was found, reset the wait period to 5s
						mWaitPeriod = 5000;
					}
					else {
						// Otherwise, back off the scanning interval (up to 5 min)
						mWaitPeriod = Math.min(mWaitPeriod * 2, MAX_SCAN_INTERVAL);
					}
					
					TextView scanIntervalView = (TextView)findViewById(R.id.scan_interval);
					scanIntervalView.setText("Scan interval: " + (mWaitPeriod / 1000.0) + " sec");
					
					// TODO: After each scan, cache scan results to be sent to server
					
					mHandler.postDelayed(new Runnable() {
						@Override
						public void run() {
							mSignalAdapter.clearData();
							scanLeDevice(true);
						}
					}, mWaitPeriod);
				}
			}, SCAN_PERIOD);
			
			// Start scanning
			mCanSeeBeacon = false;
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
