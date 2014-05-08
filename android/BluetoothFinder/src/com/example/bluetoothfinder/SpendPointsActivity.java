package com.example.bluetoothfinder;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SpendPointsActivity extends Activity {
	
	private BroadcastReceiver mBroadcastReceiver;
	private BluetoothSearchService mBoundService;
	private int mPoints;
	private boolean mIsBound;
	
	
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			// This is called when the connection with the service has been
			// established, giving us the service object we can use to
			// interact with the service.  Because we have bound to a explicit
			// service that we know is running in our own process, we can
			// cast its IBinder to a concrete class and directly access it.
			mBoundService = ((BluetoothSearchService.BluetoothSearchBinder)service).getService();

			Toast.makeText(SpendPointsActivity.this, "Connected to the service", Toast.LENGTH_SHORT)
			.show();
			mBoundService.sendPointsToUI();
		}

		public void onServiceDisconnected(ComponentName className) {
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			// Because it is running in our same process, we should never
			// see this happen.
			mBoundService = null;
			Toast.makeText(SpendPointsActivity.this, "Disconnected from the service", Toast.LENGTH_SHORT)
			.show();
		}
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		CouponAdapter adapter = new CouponAdapter(this, getCoupons());		
		setContentView(R.layout.activity_spend_points);

		ListView listView = (ListView)findViewById(R.id.coupon_list);
		listView.setAdapter(adapter);
		
		// Setup receiver for point change broadcasts
		SharedPreferences settings = getSharedPreferences(BluetoothSearchService.PREFS_NAME, 0);
		mPoints = settings.getInt("points", 0);
		updatePointsDisplay();
		mBroadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				int points = intent.getIntExtra("points", -1);
				if (points == -1) {
					SharedPreferences settings = getSharedPreferences(BluetoothSearchService.PREFS_NAME, 0);
					mPoints = settings.getInt("points", 0);
				}
				else {
					mPoints = points;
				}
				
				// Update UI
				updatePointsDisplay();
			}
		};
		
		/**
		* On Click event for single coupon
		**/
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				Coupon coupon = (Coupon)parent.getAdapter().getItem(position);
				Toast.makeText(SpendPointsActivity.this, "Spent " + coupon.getCost(), Toast.LENGTH_SHORT).show();
				SpendPointsActivity.this.mBoundService.subtractPoints(coupon.getCost());
			}
		});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onStart() {
		super.onStart();
		doBindService();
	    LocalBroadcastManager.getInstance(this).registerReceiver(
	    		mBroadcastReceiver, new IntentFilter(BluetoothSearchService.BT_POINTS_SEND));
	}

	@Override
	protected void onStop() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
		doUnbindService();
		super.onStop();
	}
	
	public void doBindService() {
		mIsBound = true;
		bindService(new Intent(this, BluetoothSearchService.class), mConnection, Context.BIND_AUTO_CREATE);
	}

	public void doUnbindService() {
		if (mIsBound) {
			unbindService(mConnection);
			mIsBound = false;

			//Toast.makeText(this, "Unbinding Service", Toast.LENGTH_SHORT).show();
		}
	}
	
	
	private void updatePointsDisplay() {
		TextView textView = (TextView)findViewById(R.id.points_display);
		textView.setText("Points: " + mPoints);
	}
	
	private Coupon[] getCoupons() {
		Coupon[] coupons = new Coupon[5];
		
		coupons[0] = new Coupon(1, "10% off all T-shirts");
		coupons[1] = new Coupon(2, "15% off all notebooks");
		coupons[2] = new Coupon(4, "20% off any $50 purchase");
		coupons[3] = new Coupon(5, "50% off any textbook purchase");
		coupons[4] = new Coupon(10, "All purchases free for one year");
		
		return coupons;
	}
	
}
