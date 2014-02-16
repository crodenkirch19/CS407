package com.example.bluetoothfinder;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BluetoothSignalAdapter extends BaseAdapter {
	
	private Context mContext;
	
	ArrayList<BluetoothSignal> mSignals;
	
	
	public BluetoothSignalAdapter(Context c) {
		mContext = c;
		mSignals = new ArrayList<BluetoothSignal>();
		//mSignals.add(new BluetoothSignal("Herp", "Derp", (short)66));
	}

	@Override
	public int getCount() {
		if (mSignals == null) {
			return 0;
		}
		return mSignals.size();
	}

	@Override
	public Object getItem(int position) {
		if (mSignals == null) {
			return null;
		}
		return mSignals.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public boolean isEmpty() {
		if (mSignals == null) {
			return true;
		}
		return (mSignals.size() == 0);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView textView;
		if (convertView == null) {
			// Create new view
			// TODO: Move this into a layout file
			textView = new TextView(mContext);
		}
		else {
			// Recycle old view
			textView = (TextView)convertView;
		}
		BluetoothSignal cur = mSignals.get(position);
		textView.setText("Name: " + cur.getName() + "\n" +
		                 "Addr: " + cur.getAddr() + "\n" + 
				         "RSSI: " + cur.getRSSI() + "dB");
		
		return textView;
	}

	public void addSignal(BluetoothSignal s) {
		mSignals.add(s);
		this.notifyDataSetChanged();
	}
}
