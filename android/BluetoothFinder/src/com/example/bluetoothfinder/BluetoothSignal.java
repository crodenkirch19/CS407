package com.example.bluetoothfinder;

import java.sql.Timestamp;
import org.json.*;

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
		String timeStr = timestamp.toString().replace(' ', 'T');
		JSONObject json = new JSONObject();
		json.put("addr", addr);
		json.put("dist", rssi);
		json.put("time", timeStr);
		return json;
	}
	
}
