package com.example.bluetoothfinder;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class BluetoothSearchActivity extends Activity {

	private static final int REQUEST_ENABLE_BT = 1;

	private BluetoothSearchService mBoundService;
	private boolean mIsBound;
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothSignalAdapter mSignalAdapter;

	
	private ServiceConnection mConnection = new ServiceConnection() {
	    public void onServiceConnected(ComponentName className, IBinder service) {
	        // This is called when the connection with the service has been
	        // established, giving us the service object we can use to
	        // interact with the service.  Because we have bound to a explicit
	        // service that we know is running in our own process, we can
	        // cast its IBinder to a concrete class and directly access it.
	        mBoundService = ((BluetoothSearchService.BluetoothSearchBinder)service).getService();
	        
	        Toast.makeText(BluetoothSearchActivity.this, "Connected to the service", Toast.LENGTH_SHORT)
				.show();
	    }

	    public void onServiceDisconnected(ComponentName className) {
	        // This is called when the connection with the service has been
	        // unexpectedly disconnected -- that is, its process crashed.
	        // Because it is running in our same process, we should never
	        // see this happen.
	        mBoundService = null;
	        Toast.makeText(BluetoothSearchActivity.this, "Disconnected from the service", Toast.LENGTH_SHORT)
				.show();
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
			// Adapter exists and is enabled already, start the scanning service
			doBindService();
			startService(new Intent(BluetoothSearchActivity.this, BluetoothSearchService.class));
		}
	}

	@Override
	protected void onDestroy() {
		// Stop scan if it is running
		//scanLeDevice(false);

		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case REQUEST_ENABLE_BT:
			if (resultCode == RESULT_OK) {
				Toast.makeText(this, "Bluetooth enabled", Toast.LENGTH_SHORT).show();
				// Start the scanning service
				doBindService();
				startService(new Intent(BluetoothSearchActivity.this, BluetoothSearchService.class));
			}
			else {
				Toast.makeText(this, "Bluetooth disabled", Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}

	public void onButtonClick(View v) {

		switch (v.getId()) {

		case R.id.button_send:
			mBoundService.sendCachedData();
			break;
			
		case R.id.button_start_service:
			doBindService();
			startService(new Intent(BluetoothSearchActivity.this, BluetoothSearchService.class));
			break;
		
		case R.id.button_stop_service:
			doUnbindService();
			stopService(new Intent(BluetoothSearchActivity.this, BluetoothSearchService.class));
			break;
		}
	}
	
	public void doBindService() {
		mIsBound = true;
		bindService(new Intent(this, BluetoothSearchService.class), mConnection, Context.BIND_AUTO_CREATE);
	}
	
	public void doUnbindService() {
		if (mIsBound) {
			unbindService(mConnection);
			mIsBound = false;
 
			Toast.makeText(this, "Unbinding Service", Toast.LENGTH_SHORT).show();
		}
	}
}
