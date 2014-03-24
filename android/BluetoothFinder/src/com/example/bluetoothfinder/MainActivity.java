package com.example.bluetoothfinder;

import android.app.Activity;
import android.content.Intent;
<<<<<<< HEAD
=======
import android.content.IntentSender.SendIntentException;
>>>>>>> 92575cfaca95af6a75fd43b2198411188798f593
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

<<<<<<< HEAD
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void onButtonClick(View v) {
    	
    	switch (v.getId()) {
    	
    	case R.id.button_search:
    		//Toast.makeText(this, "Search not yet implemented", Toast.LENGTH_SHORT).show();
    		Intent i = new Intent(this, BluetoothSearchActivity.class);
    		startActivity(i);
    		break;
    	case R.id.button_settings:
    		Toast.makeText(this, "Settings not yet implemented", Toast.LENGTH_SHORT).show();
    		break;	
    	default:
    		Toast.makeText(this, "Invalid button ID", Toast.LENGTH_SHORT).show();
    	}
    }
=======
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;


public class MainActivity extends Activity implements ConnectionCallbacks, OnConnectionFailedListener {

	/* Request code used to invoke sign in user interactions. */
	private static final int RC_SIGN_IN = 0;

	/* Client used to interact with Google APIs. */
	private GoogleApiClient mGoogleApiClient;

	/* A flag indicating that a PendingIntent is in progress and prevents
	 * us from starting further intents.
	 */
	private boolean mIntentInProgress;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);


		mGoogleApiClient = new GoogleApiClient.Builder(this)
		.addConnectionCallbacks(this)
		.addOnConnectionFailedListener(this)
		.addApi(Plus.API, null)
		.addScope(Plus.SCOPE_PLUS_LOGIN)
		.build();

	}

	protected void onStart() {
		super.onStart();
		mGoogleApiClient.connect();
	}

	protected void onStop() {
		super.onStop();

		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}
	
	/* Track whether the sign-in button has been clicked so that we know to resolve
	 * all issues preventing sign-in without waiting.
	 */
	private boolean mSignInClicked;

	/* Store the connection result from onConnectionFailed callbacks so that we can
	 * resolve them when the user clicks sign-in.
	 */
	private ConnectionResult mConnectionResult;

	/* A helper method to resolve the current ConnectionResult error. */
	private void resolveSignInError() {
	  if (mConnectionResult.hasResolution()) {
	    try {
	      mIntentInProgress = true;
	      mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
	    } catch (SendIntentException e) {
	      // The intent was canceled before it was sent.  Return to the default
	      // state and attempt to connect to get an updated ConnectionResult.
	      mIntentInProgress = false;
	      mGoogleApiClient.connect();
	    }
	  }
	}

	public void onConnectionFailed(ConnectionResult result) {
	  if (!mIntentInProgress) {
	    // Store the ConnectionResult so that we can use it later when the user clicks
	    // 'sign-in'.
	    mConnectionResult = result;

	    if (mSignInClicked) {
	      // The user has already clicked 'sign-in' so we attempt to resolve all
	      // errors until the user is signed in, or they cancel.
	      resolveSignInError();
	    }
	  }
	}
	

	public void onConnected(Bundle connectionHint) {
		// We've resolved any connection errors.  mGoogleApiClient can be used to
		// access Google APIs on behalf of the user.
	}



	public void onConnectionSuspended(int cause) {
		mGoogleApiClient.connect();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void onButtonClick(View v) {
		
		switch (v.getId()) {
		case R.id.sign_in_button:
			if (!mGoogleApiClient.isConnecting()) {
			    mSignInClicked = true;
			    resolveSignInError();
			}
		case R.id.button_search:
			//Toast.makeText(this, "Search not yet implemented", Toast.LENGTH_SHORT).show();
			Intent i = new Intent(this, BluetoothSearchActivity.class);
			startActivity(i);
			break;
		case R.id.button_settings:
			Toast.makeText(this, "Settings not yet implemented", Toast.LENGTH_SHORT).show();
			break;	
		default:
			Toast.makeText(this, "Invalid button ID", Toast.LENGTH_SHORT).show();
		}
	}
	
	protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
		  if (requestCode == RC_SIGN_IN) {
		    if (responseCode != RESULT_OK) {
		      mSignInClicked = false;
		    }

		    mIntentInProgress = false;

		    if (!mGoogleApiClient.isConnecting()) {
		      mGoogleApiClient.connect();
		    }
		  }
		}
>>>>>>> 92575cfaca95af6a75fd43b2198411188798f593
}
