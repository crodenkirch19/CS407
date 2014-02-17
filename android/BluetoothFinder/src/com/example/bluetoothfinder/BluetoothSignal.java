package com.example.bluetoothfinder;

public class BluetoothSignal {
	private String name;
	private String addr;
	private short rssi;
	
	public BluetoothSignal(String name, String addr, short rssi) {
		this.name = name;
		this.addr = addr;
		this.rssi = rssi;
	}
	
	String getName() {
		return name;
	}
	String getAddr() {
		return addr;
	}
	short getRSSI() {
		return rssi;
	}
}
