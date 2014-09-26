package com.tckb.geo.app;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tckb.geo.stubs.Cluster;
import com.tckb.geo.stubs.Device;

public class RegisterDeviceView extends Activity {

	private Device newDevice = new Device();
	private static final long minTime = 1 * 1000; // request location every
													// second
	private static final long minDistance = 10; // request location update of 10
												// mts distance
	private static final int TIME_DIFFERENCE_THRESHOLD = 1 * 60 * 1000; // time
																		// threshold
																		// is 1
																		// min
	private Location oldLocation;
	private LocationManager locationManager;
	private LocationListener locationListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_device_view);
		GetAndSetClustersFromRemote();
		setLocationManager();

	}

	private void setLocationManager() {
		locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		locationListener = new MyLocationListner();

		locationManager.requestLocationUpdates(getProviderName(), minTime,
				minDistance, locationListener);

		((CheckBox) findViewById(R.id.checkBox1))
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							findViewById(R.id.button_register)
									.setEnabled(false);
						} else {
							findViewById(R.id.button_register).setEnabled(true);
						}

					}
				});
	}

	private void GetAndSetClustersFromRemote() {

		List<Cluster> allCluster = GeoLocatorAsyncService
				.getAllClustersFromRemote();

		if (allCluster != null) {

			ArrayList<String> clusterIDs = new ArrayList<String>();

			for (Cluster c : allCluster) {
				clusterIDs.add(c.getID());
			}

			String[] values = new String[allCluster.size()];
			values = clusterIDs.toArray(values);
			Spinner clstSp = (Spinner) findViewById(R.id.cluster_spinner);

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item, values);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			clstSp.setAdapter(adapter);

		}

	}

	public void doRegister(View view) {

		String devIDText = ((EditText) findViewById(R.id.dev_id)).getText()
				.toString();
		String devNameText = ((EditText) findViewById(R.id.dev_name)).getText()
				.toString();
		String clstID = ((Spinner) findViewById(R.id.cluster_spinner))
				.getSelectedItem().toString();

		if (!devIDText.isEmpty() && !devNameText.isEmpty() && !clstID.isEmpty()) {

			newDevice.setDevID(devIDText);
			newDevice.setName(devNameText);
			newDevice.setClustID(clstID);
			newDevice.setWeight(1.0);

			com.tckb.geo.stubs.Location devLoc = new com.tckb.geo.stubs.Location();
			devLoc.setLatitude(oldLocation.getLatitude());
			devLoc.setLongitude(oldLocation.getLongitude());

			newDevice.setLocation(devLoc);

			boolean isRegistered = GeoLocatorAsyncService
					.registerDeviceToRemote(newDevice);

			if (isRegistered) {
				Toast.makeText(getApplicationContext(),
						"Registration successful!!", Toast.LENGTH_SHORT).show();

			} else {
				Toast.makeText(getApplicationContext(), "Registration failed!",
						Toast.LENGTH_SHORT).show();
			}

		} else {
			Toast.makeText(getApplicationContext(),
					"Cannot register if the Fields are empty!",
					Toast.LENGTH_LONG).show();
		}

	}

	@Override
	protected void onDestroy() {
		locationManager.removeUpdates(locationListener);

		super.onDestroy();
	}

	private String getProviderName() {
		LocationManager locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);

		Criteria criteria = new Criteria();
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setSpeedRequired(true);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(false);

		return locationManager.getBestProvider(criteria, true);
	}

	private boolean isBetterLocation(Location oldLocation, Location newLocation) {
		if (oldLocation == null) {
			return true;
		}

		boolean isNewer = newLocation.getTime() > oldLocation.getTime();

		boolean isMoreAccurate = newLocation.getAccuracy() < oldLocation
				.getAccuracy();
		if (isMoreAccurate && isNewer) {
			return true;
		} else if (isMoreAccurate && !isNewer) {
			long timeDifference = newLocation.getTime() - oldLocation.getTime();

			if (timeDifference > -TIME_DIFFERENCE_THRESHOLD) {
				return true;
			}
		}

		return false;
	}

	private Location getOldLocation() {
		return oldLocation;
	}

	private void setOldLocation(Location oldLocation) {
		this.oldLocation = oldLocation;
		TextView devLocText = (TextView) findViewById(R.id.dev_loc);
		devLocText.setText("@lat: " + oldLocation.getLatitude() + "\n@lng:"
				+ oldLocation.getLongitude());

	}

	private class MyLocationListner implements LocationListener {
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		@Override
		public void onProviderEnabled(String provider) {
			Toast.makeText(getApplicationContext(),
					"Provider enabled: " + provider, Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onProviderDisabled(String provider) {
			Toast.makeText(getApplicationContext(),
					"Provider disabled: " + provider, Toast.LENGTH_SHORT)
					.show();
		}

		@Override
		public void onLocationChanged(Location location) {
			doWorkWithNewLocation(location);

			// do register only if auto registration is enabled
			boolean isAutoEnabled = ((CheckBox) findViewById(R.id.checkBox1))
					.isChecked();
			if (isAutoEnabled) {
				doRegister(null);
			}

		}

		private void doWorkWithNewLocation(Location location) {
			if (isBetterLocation(getOldLocation(), location)) {
				Toast.makeText(getApplicationContext(),
						"Better location found: " + location.getProvider(),
						Toast.LENGTH_SHORT).show();
			}
			setOldLocation(location);
		}
	}

}
