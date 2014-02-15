package com.example.bluetoothfinder;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

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
    		Toast.makeText(this, "Search not yet implemented", Toast.LENGTH_SHORT).show();
    		break;
    	case R.id.button_settings:
    		Toast.makeText(this, "Settings not yet implemented", Toast.LENGTH_SHORT).show();
    		break;	
    	default:
    		Toast.makeText(this, "Invalid button ID", Toast.LENGTH_SHORT).show();
    	}
    }
}
