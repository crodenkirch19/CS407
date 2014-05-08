package com.example.bluetoothfinder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CouponAdapter extends ArrayAdapter<Coupon> {

	private Context mContext;
	private Coupon[] mCouponList;
	
	public CouponAdapter(Context context, Coupon[] objects) {
		super(context, R.layout.layout_coupon, objects);
		mContext = context;
		mCouponList = objects;
	}
	
	
	@Override
	public int getCount() {
		return mCouponList.length;
	}

	@Override
	public Coupon getItem(int position) {
		return mCouponList[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = 
					(LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.layout_coupon, null);
		}
		
		TextView description = (TextView)rowView.findViewById(R.id.coupon_description);
		TextView price = (TextView)rowView.findViewById(R.id.coupon_price);
		
		description.setText(mCouponList[position].getDescription());
		price.setText(Integer.toString(mCouponList[position].getCost()));
		
		return rowView;
	}

}
