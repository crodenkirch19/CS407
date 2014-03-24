package com.example.bluetoothfinder;

import org.json.*;
import java.util.ArrayList;

public class BluetoothScan {
	
	private ArrayList<BluetoothSignal> mSignals;
	
	public BluetoothScan() {
		mSignals = new ArrayList<BluetoothSignal>();
	}
	
	public void addSignal(BluetoothSignal signal) {
		mSignals.add(signal);
	}
	
	public JSONArray toJSON() throws JSONException {
		// Convert this scan to JSON
		JSONArray json = new JSONArray();
		for (BluetoothSignal signal : mSignals) {
			json.put(signal.toJSON());
		}
		return json;
	}
	
	public int size() {
		return mSignals.size();
	}
}
