package com.example.bluetoothfinder;

import java.util.ArrayList;
import java.util.List;

import org.json.*;

import android.util.Log;

public class BluetoothScanCache {

	// Use two scan lists so that we can process one complete scan list
	// without affecting the active scan list which may be receiving new scans.
	private ArrayList<BluetoothScan> mActiveList;
	private ArrayList<BluetoothScan> mPassiveList;

	public BluetoothScanCache() {
		mActiveList = new ArrayList<BluetoothScan>();
		mPassiveList = new ArrayList<BluetoothScan>();
	}

	public void addScan(BluetoothScan scan) {
		mActiveList.add(scan);
	}

	public JSONObject toJSON() {
		swapScanLists();
		clean(mPassiveList);

		// Convert passive list to JSON
		JSONObject json = new JSONObject();
		try {
			json.put("token", "foo bar baz");
			JSONArray scanJSON = new JSONArray();
			for (BluetoothScan scan : mPassiveList) {
				scanJSON.put(scan.toJSON());
			}
			json.put("scans", scanJSON);
		} catch (JSONException e) {
			Log.e("JSON", e.getMessage());
			System.exit(1);
		}

		// Clear passive list after processing
		mPassiveList.clear();

		return json;
	}

	private void swapScanLists() {
		ArrayList<BluetoothScan> temp = mActiveList;
		mActiveList = mPassiveList;
		mPassiveList = temp;
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
