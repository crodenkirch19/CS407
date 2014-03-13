package com.example.bluetoothfinder;

import java.util.ArrayList;
import java.util.List;

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
	
	public String toJSON() {
		swapScanLists();
		clean(mPassiveList);
		
		// TODO: Convert passive list to JSON
		
		// Clear passive list after processing
		mPassiveList.clear();
		
		return null;
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
