package com.example.bluetoothfinder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

public class BluetoothSearchService extends Service {

	private static final int SCAN_PERIOD = 1000; // Scan for 1 sec at a time
	private static final int MAX_SCAN_INTERVAL = 1000 * 60 * 5; // 5 min
	//private static final int CACHE_FLUSH_INTERVAL = 1000 * 60 * 60; // 1 hour
	private static final int CACHE_FLUSH_INTERVAL = 1000 * 20; // 10 secs
	public static final String PREFS_NAME = "btfPrefs";
	public static final String BT_POINTS_SEND = "com.example.bluetoothfinder.SEND_POINTS";
	private static String uniqueID = null;
	private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";

	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothScanCache mScanCache;
	private BluetoothScan mCurrentScan;
	private boolean mScanning;
	private Date mDate;
	private Handler mHandler;
	private boolean mCanSeeBeacon;
	private int mWaitPeriod = 5000; // Wait for 5 secs between scans by default
	
	private LocalBroadcastManager mBroadcaster;
	private SharedPreferences.Editor mPrefEditor;
	private int mPoints = 0;

	
	/////////////// SERVICE METHODS ///////////////////////

	/**
	 * The binder is used to allow clients to access the service
	 */
	public class BluetoothSearchBinder extends Binder {
		BluetoothSearchService getService() {
			return BluetoothSearchService.this;
		}
	}
	// An interface object used by clients to communicate with the service.
	private final IBinder mBinder = new BluetoothSearchBinder();
	
	@Override
	public void onCreate() {
		//super.onCreate();
		//Toast.makeText(this, "Service Created", Toast.LENGTH_SHORT).show();
		mBroadcaster = LocalBroadcastManager.getInstance(this);
		
		// Setup shared preferences
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		mPoints = settings.getInt("points", 0);
		mPrefEditor = settings.edit();
		
		// Initialize our signal adapter and hook it up to this activity
		mScanCache = new BluetoothScanCache(this.id());

		mHandler = new Handler();

		BluetoothManager bluetoothManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);

		// Set up our bluetooth adapter for scanning
		mBluetoothAdapter = bluetoothManager.getAdapter();

		// Adapter exists and is enabled already (because we do so explicitly in the activity)
		scanLeDevice(true);
		
		// Send cached data every CACHE_FLUSH_INTERVAL ms
		setupCacheFlushHandler();
	}

	@Override
	public void onDestroy() {
		//super.onDestroy();
		scanLeDevice(false);
		Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
		return Service.START_STICKY;
	}

	@Override
	public IBinder onBind(Intent i) {
		//Toast.makeText(this, "Binding Service", Toast.LENGTH_SHORT).show();
		return mBinder;
	}
	
	public void sendPointsToUI() {
		Intent intent = new Intent(BT_POINTS_SEND);
		intent.putExtra("points", mPoints);
		mBroadcaster.sendBroadcast(intent);
		Log.d("Points", "Points: " + mPoints);
	}
	
	public String id() {
	    if (uniqueID == null) {
	        SharedPreferences sharedPrefs = this.getSharedPreferences(
	                PREF_UNIQUE_ID, Context.MODE_PRIVATE);
	        uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null);
	        if (uniqueID == null) {
	            uniqueID = UUID.randomUUID().toString();
	            Editor editor = sharedPrefs.edit();
	            editor.putString(PREF_UNIQUE_ID, uniqueID);
	            editor.commit();
	        }
	    }
	    return uniqueID;
	}
	
	//////////////// BLUETOOTH SCANNING METHODS ////////////////////
	
	// Create a callback to be run each time a new BLE device is discovered
	private BluetoothAdapter.LeScanCallback mLeScanCallback = 
			new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, final int rssi, 
				byte[] scanRecord) {
			if (device.getName() != null) {
				mCanSeeBeacon = true;
				BluetoothSignal receivedSignal = new BluetoothSignal(
						device.getName(), 
						device.getAddress(), 
						rssi,
						new Date().getTime());

				// Add this signal to a list of signals found for this scan
				mCurrentScan.addSignal(receivedSignal);
				
				// Increment the user's points and update the UI
				mPoints++;
				sendPointsToUI();
				
				Log.d("Scan", "Found device with RSSI " + rssi);
			}
		}
	};
	
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
						Toast.makeText(BluetoothSearchService.this, "Resetting to " + mWaitPeriod / 1000.0 + " secs", Toast.LENGTH_SHORT).show();
					}
					else {
						// Otherwise, back off the scanning interval (up to 5 min)
						mWaitPeriod = Math.min(mWaitPeriod * 2, MAX_SCAN_INTERVAL);
						Toast.makeText(BluetoothSearchService.this, "Backing off to " + mWaitPeriod / 1000.0 + " secs", Toast.LENGTH_SHORT).show();
					}

					mHandler.postDelayed(new Runnable() {
						@Override
						public void run() {
							sendPointsToUI();
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
			mHandler.removeCallbacksAndMessages(null);
			mScanning = false;
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
			mScanCache.addScan(mCurrentScan);
		}
	}
	
	public void resetPoints() {
		mPoints = 0;
		mPrefEditor.putInt("points", mPoints);
		mPrefEditor.commit();
		sendPointsToUI();
	}
	
	public void subtractPoints(int amount) {
		mPoints -= amount;
		mPrefEditor.putInt("points", mPoints);
		mPrefEditor.commit();
		sendPointsToUI();
	}
	
	/////////////////// DATA SENDING METHODS /////////////////////////
	
	public void setupCacheFlushHandler() {
		
		// Flush the cache every CACHE_FLUSH_INTERVAL ms
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				sendCachedData(); // send data
				
				// After data is sent, set up another cache flusher
				setupCacheFlushHandler();
			}
		}, CACHE_FLUSH_INTERVAL);
	}
	
	public void sendCachedData() {
		Toast.makeText(this, "Sending JSON to server...", Toast.LENGTH_SHORT).show();
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
	}
	
	private static String inputStreamToString(InputStream inputStream) throws IOException{
		BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while((line = bufferedReader.readLine()) != null)
			result += line;

		inputStream.close();
		return result;

	}   

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
			//Toast.makeText(BluetoothSearchService.this, "Data Sent!", Toast.LENGTH_SHORT).show();
			//Toast.makeText(BluetoothSearchService.this, result, Toast.LENGTH_SHORT).show();
			
			// Clear the scan cache only if the data was successfully sent
			String resultString = null;
			try {
				JSONObject resultJSON = new JSONObject(result);
				resultString = resultJSON.getString("response");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (resultString != null && resultString.equals("success!")) {
				Toast.makeText(BluetoothSearchService.this, "Data sent, clearing scan cache", Toast.LENGTH_SHORT).show();
				mScanCache.clear();
				
				// when we flush the scan cache, also commit the user's points to storage
				mPrefEditor.putInt("points", mPoints);
				mPrefEditor.commit();
			}
			else {
				Toast.makeText(BluetoothSearchService.this, "Failed to send data, keeping cached scans", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
