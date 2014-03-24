package com.example.bluetoothfinder;

import java.sql.Timestamp;
<<<<<<< HEAD
=======
import org.json.*;
>>>>>>> 92575cfaca95af6a75fd43b2198411188798f593

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
	
<<<<<<< HEAD
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
	
=======
	public String getName() {
		return name;
	}
	public String getAddr() {
		return addr;
	}
	public String getTimestamp() {
		return timestamp.toString();
	}
	public int getRssi() {
		return rssi;
	}
	public void setRssi(int newRssi) {
		rssi = newRssi;
	}
	public void setTimestamp(long time) {
		timestamp = new Timestamp(time);
	}
	
	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		json.put("addr", addr);
		json.put("dist", rssi);
		json.put("time", timestamp.getTime());
		return json;
	}
>>>>>>> 92575cfaca95af6a75fd43b2198411188798f593
	
}
