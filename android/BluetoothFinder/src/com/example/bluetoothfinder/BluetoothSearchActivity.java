package com.example.bluetoothfinder;

import java.util.Date;

<<<<<<< HEAD
import android.app.ListActivity;
=======
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
>>>>>>> 92575cfaca95af6a75fd43b2198411188798f593
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
<<<<<<< HEAD
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
=======
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BluetoothSearchActivity extends Activity {

	private static final int REQUEST_ENABLE_BT = 1;
	private static final int SCAN_PERIOD = 1000; // Scan for 1 sec at a time
	private static final int MAX_SCAN_INTERVAL = 300000; // 5 min


	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothSignalAdapter mSignalAdapter;
	private BluetoothScanCache mScanCache;
	private BluetoothScan mCurrentScan;
	private boolean mScanning;
	private Date mDate;
	private Handler mHandler;
	private boolean mCanSeeBeacon;
	private int mWaitPeriod = 5000; // Wait for 5 secs between scans by default

	// Create a callback to be run each time a new BLE device is discovered
	private BluetoothAdapter.LeScanCallback mLeScanCallback = 
			new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, final int rssi, 
				byte[] scanRecord) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					BluetoothSignal receivedSignal = new BluetoothSignal(
							device.getName(), 
							device.getAddress(), 
							rssi,
							new Date().getTime());

					if (!mSignalAdapter.hasDeviceWithAddr(device.getAddress())) {
						// Signal to app that a beacon is nearby
						mCanSeeBeacon = true;

						// Add the BLE signal to our list adapter
						mSignalAdapter.addSignal(receivedSignal);
>>>>>>> 92575cfaca95af6a75fd43b2198411188798f593
					}
					else {
						// Update the stored signal with the given address
						mSignalAdapter.updateRssiForDevice(device.getAddress(), rssi);
						mSignalAdapter.updateTimestampForDevice(device.getAddress(), mDate.getTime());
					}
<<<<<<< HEAD
=======
					// Add this signal to a list of signals found for this scan
					mCurrentScan.addSignal(receivedSignal);
>>>>>>> 92575cfaca95af6a75fd43b2198411188798f593
				}
			});
		}
	};
<<<<<<< HEAD
	
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
=======

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Initialize our signal adapter and hook it up to this activity
		mSignalAdapter = new BluetoothSignalAdapter(this);
		setContentView(R.layout.activity_bluetooth_search);
		ListView listView = (ListView)findViewById(R.id.list_bt_devices);
		listView.setAdapter(mSignalAdapter);
		mScanCache = new BluetoothScanCache();

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
>>>>>>> 92575cfaca95af6a75fd43b2198411188798f593
			Toast.makeText(this, R.string.bt_not_supported, Toast.LENGTH_SHORT).show();
			finish();
		}
		else if (!mBluetoothAdapter.isEnabled()) { 
			// Adapter exists, but is disabled
<<<<<<< HEAD
		    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);	
		    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
=======
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);	
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
>>>>>>> 92575cfaca95af6a75fd43b2198411188798f593
		}
		else { 
			// Adapter exists and is enabled already
			scanLeDevice(true);
		}
<<<<<<< HEAD
		
		// Initialize our signal adapter and hook it up to this activity
		mSignalAdapter = new BluetoothSignalAdapter(this);
		setListAdapter(mSignalAdapter);
	}
	
=======
	}

>>>>>>> 92575cfaca95af6a75fd43b2198411188798f593
	@Override
	protected void onDestroy() {
		// Stop scan if it is running
		scanLeDevice(false);

		super.onDestroy();
	}
<<<<<<< HEAD
	
	private void scanLeDevice(final boolean enable) {
		if (enable) {
=======

	private void scanLeDevice(final boolean enable) {
		if (enable && !mScanning) {
>>>>>>> 92575cfaca95af6a75fd43b2198411188798f593
			// In SCAN_PERIOD ms, stop the scan
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mScanning = false;
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
<<<<<<< HEAD
					
=======

					// After each scan add the scan data to the cache
					mScanCache.addScan(mCurrentScan);

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

>>>>>>> 92575cfaca95af6a75fd43b2198411188798f593
					mHandler.postDelayed(new Runnable() {
						@Override
						public void run() {
							mSignalAdapter.clearData();
							scanLeDevice(true);
						}
<<<<<<< HEAD
					}, WAIT_PERIOD);
				}
			}, SCAN_PERIOD);
			
			// Start scanning
			mScanning = true;
			mDate = new Date();
=======
					}, mWaitPeriod);
				}
			}, SCAN_PERIOD);

			// Start scanning
			mCanSeeBeacon = false;
			mScanning = true;
			mDate = new Date();
			mCurrentScan = new BluetoothScan();
>>>>>>> 92575cfaca95af6a75fd43b2198411188798f593
			mBluetoothAdapter.startLeScan(mLeScanCallback);
		}
		else {
			// If enable is false, stop the scan.
			mScanning = false;
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
<<<<<<< HEAD
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
=======
			mScanCache.addScan(mCurrentScan);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

>>>>>>> 92575cfaca95af6a75fd43b2198411188798f593
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

<<<<<<< HEAD
=======
	public void onButtonClick(View v) {

		switch (v.getId()) {

		case R.id.button_send:
			// Right now, converts the cached scans into JSON and then prints the JSON
			// out the the debug console with tag "JSON". Now that we have the JSON, 
			// it should be pretty easy to send the data to the server.
			JSONObject scanJson = mScanCache.toJSON();
			try {
				Log.d("JSON", scanJson.toString(2));
			} catch (JSONException e) {
				Log.e("JSON", e.getMessage());
				System.exit(1);
			}
			Toast.makeText(this, "Send JSON placeholder", Toast.LENGTH_SHORT).show();
			// TODO: Send JSON to server
			break;
		}
	}
>>>>>>> 92575cfaca95af6a75fd43b2198411188798f593
}
