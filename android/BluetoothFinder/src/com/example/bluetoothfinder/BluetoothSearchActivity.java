package com.example.bluetoothfinder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class BluetoothSearchActivity extends Activity {

	private static final int REQUEST_ENABLE_BT = 1;
	//private static final int SCAN_PERIOD = 1000; // Scan for 1 sec at a time
	//private static final int MAX_SCAN_INTERVAL = 300000; // 5 min


	private BluetoothSearchService mBoundService;
	private boolean mIsBound;
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothSignalAdapter mSignalAdapter;
	//private BluetoothScanCache mScanCache;
	//private BluetoothScan mCurrentScan;
	//private boolean mScanning;
	//private Date mDate;
	//private Handler mHandler;
	//private boolean mCanSeeBeacon;
	//private int mWaitPeriod = 5000; // Wait for 5 secs between scans by default
	
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


	/*
	// Create a callback to be run each time a new BLE device is discovered
	private BluetoothAdapter.LeScanCallback mLeScanCallback = 
			new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, final int rssi, 
				byte[] scanRecord) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (device.getName() != null) {
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
						}
						else {
							// Update the stored signal with the given address
							mSignalAdapter.updateRssiForDevice(device.getAddress(), rssi);
							mSignalAdapter.updateTimestampForDevice(device.getAddress(), mDate.getTime());
						}
						// Add this signal to a list of signals found for this scan
						mCurrentScan.addSignal(receivedSignal);
					}
				}
			});
		}
	};
	*/

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Initialize our signal adapter and hook it up to this activity
		mSignalAdapter = new BluetoothSignalAdapter(this);
		setContentView(R.layout.activity_bluetooth_search);
		ListView listView = (ListView)findViewById(R.id.list_bt_devices);
		listView.setAdapter(mSignalAdapter);
		//mScanCache = new BluetoothScanCache();

		//mHandler = new Handler();

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
			//scanLeDevice(true);
		}
	}

	@Override
	protected void onDestroy() {
		// Stop scan if it is running
		//scanLeDevice(false);

		super.onDestroy();
	}
/*
	private void scanLeDevice(final boolean enable) {
		if (enable && !mScanning) {
			// In SCAN_PERIOD ms, stop the scan
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mScanning = false;
					mBluetoothAdapter.stopLeScan(mLeScanCallback);

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
			mCurrentScan = new BluetoothScan();
			mBluetoothAdapter.startLeScan(mLeScanCallback);
		}
		else {
			// If enable is false, stop the scan.
			mScanning = false;
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
			mScanCache.addScan(mCurrentScan);
		}
	}
	*/

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case REQUEST_ENABLE_BT:
			if (resultCode == RESULT_OK) {
				Toast.makeText(this, "Bluetooth enabled", Toast.LENGTH_SHORT).show();
				//scanLeDevice(true);
			}
			else {
				Toast.makeText(this, "Bluetooth disabled", Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}

	/*
	private static String inputStreamToString(InputStream inputStream) throws IOException{
		BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while((line = bufferedReader.readLine()) != null)
			result += line;

		inputStream.close();
		return result;

	}   
	*/


	public void onButtonClick(View v) {

		switch (v.getId()) {

		case R.id.button_send:
			mBoundService.sendCachedData();
			/*
			JSONObject scanJson = mScanCache.toJSON();
			try {
				Log.d("JSON", scanJson.toString(2));
			} catch (JSONException e) {
				Log.e("JSON", e.getMessage());
				System.exit(1);
			}
			// Send JSON to server
			new HttpAsyncTask(scanJson).execute(
					"http://beacon-adventure.herokuapp.com/api/send_scans/");
			*/
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

	/*
	public String postData(String url, JSONObject jsonObject) {

		// Create HTTP client
		HttpClient httpClient = new DefaultHttpClient();

		// Make post request to the given URL
		HttpPost httpPost = new HttpPost(url);

		String json = jsonObject.toString();
		StringEntity se = null;
		try {
			se = new StringEntity(json);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		httpPost.setEntity(se);
		// Set some headers to inform server about the type of the content   
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-type", "application/json");

		String result = "";
		try {
			// Execute POST request to the given URL
			HttpResponse httpResponse = httpClient.execute(httpPost);

			// receive response as inputStream
			InputStream inputStream = httpResponse.getEntity().getContent();

			// convert input stream to string
			if(inputStream != null)
				result = inputStreamToString(inputStream);
			else
				result = "Did not work!";
		} catch (IOException e) {
			e.printStackTrace();
		}
		//Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
		return result;
	}


	private class HttpAsyncTask extends AsyncTask<String, Void, String> {
		private JSONObject jsonObject;

		public HttpAsyncTask(JSONObject jsonObject) {
			super();
			this.jsonObject = jsonObject;
		}
		@Override
		protected String doInBackground(String... urls) {
			return postData(urls[0], jsonObject);
		}
		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String result) {
			//Toast.makeText(getBaseContext(), "Data Sent!", Toast.LENGTH_SHORT).show();
			Toast.makeText(getBaseContext(), result, Toast.LENGTH_LONG).show();
		}
	}
	*/

}
