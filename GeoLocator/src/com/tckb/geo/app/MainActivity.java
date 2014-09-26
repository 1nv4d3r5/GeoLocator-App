package com.tckb.geo.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// Show notifications when interacting with remote
		GeoLocatorAsyncService.enableNotification(getBaseContext());
		if(!GeoLocatorAsyncService.checkForInternetConnection()){
			new AlertDialog.Builder(this)
			 .setCancelable(false)
			 .setPositiveButton("OK", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					finish();
					
				}
			})
			.setTitle("No connection")
			.setMessage("A working internet connection is required for GeoLocator to work. Please check your connection")
			.create().show();
			
		}
			
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void showCluster(View view) {
		Intent i = new Intent(getApplicationContext(), ClusterView.class);
		startActivity(i);

	}

	public void joinCluster(View view) {
		Intent i = new Intent(getApplicationContext(), ClusterView.class);
		startActivity(i);

	}

	public void showDevices(View view) {
		Intent i = new Intent(getApplicationContext(), DeviceView.class);
		startActivity(i);
	}

	public void showRegister(View view) {
		Intent i = new Intent(getApplicationContext(), RegisterDeviceView.class);
		startActivity(i);
	}



}
