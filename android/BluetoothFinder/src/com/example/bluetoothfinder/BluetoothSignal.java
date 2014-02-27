package com.example.bluetoothfinder;

import java.sql.Timestamp;

public class BluetoothSignal {
	private String name;
	private String addr;
	private int rssi;
	private Timestamp timestamp;
	
	public BluetoothSignal(String name, String addr, int rssi, long time) {
		this.name = name;
		this.addr = addr;
		this.rssi = rssi;
		this.timestamp = new Timestamp(time);
	}
	
	String getName() {
		return name;
	}
	String getAddr() {
		return addr;
	}
	String getTimestamp() {
		return timestamp.toString();
	}
	int getRssi() {
		return rssi;
	}
	void setRssi(int newRssi) {
		rssi = newRssi;
	}
	void setTimestamp(long time) {
		timestamp = new Timestamp(time);
	}
	
	
}
