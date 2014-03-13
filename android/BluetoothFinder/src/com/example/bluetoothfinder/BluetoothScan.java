package com.example.bluetoothfinder;

import java.util.ArrayList;

public class BluetoothScan {
	
	private ArrayList<BluetoothSignal> mSignals;
	
	public BluetoothScan() {
		mSignals = new ArrayList<BluetoothSignal>();
	}
	
	public void addSignal(BluetoothSignal signal) {
		mSignals.add(signal);
	}
	
	public String toJSON() {
		// Convert this scan to JSON
		return null;
	}
	
	public int size() {
		return mSignals.size();
	}
}
