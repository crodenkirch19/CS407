package com.example.bluetoothfinder;

import java.util.ArrayList;
import java.util.List;

import org.json.*;

import android.util.Log;

public class BluetoothScanCache {

	private ArrayList<BluetoothScan> mScanList;

	public BluetoothScanCache() {
		mScanList = new ArrayList<BluetoothScan>();
	}

	public void addScan(BluetoothScan scan) {
		mScanList.add(scan);
	}

	public JSONObject toJSON() {
		clean(mScanList);

		// Convert passive list to JSON
		JSONObject json = new JSONObject();
		try {
			json.put("token", "foo bar baz");
			JSONArray scanArray = new JSONArray();
			for (BluetoothScan scan : mScanList) {
				scanArray.put(scan.toJSON());
			}
			json.put("scans", scanArray);
		} catch (JSONException e) {
			Log.e("JSON", e.getMessage());
			System.exit(1);
		}

		return json;
	}
	
	public void clear() {
		mScanList.clear();
	}

	/**
	 * Removes any empty scans from the cache. To be used before converting
	 * data to JSON and sending to the server
	 */
	private void clean(List<BluetoothScan> scans) {
		for (int i = 0; i < scans.size(); i++) {
			if (scans.get(i).size() == 0) {
				scans.remove(i);
			}
		}
	}
}
