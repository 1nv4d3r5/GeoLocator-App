package com.tckb.geo.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.tckb.geo.stubs.Cluster;
import com.tckb.geo.stubs.Device;

public class DeviceView extends ListActivity {

	List<Device> allDevices = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		allDevices = GeoLocatorAsyncService.getAllDevicesFromRemote();
		final AlertDialog dialog = new AlertDialog.Builder(this).create();
		dialog.setCancelable(false);
		dialog.setButton("ok", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();

			}
		});

		if (allDevices != null) {
			ArrayList<String> devices = new ArrayList<String>();
			final Map<String, Device> map = new HashMap<String, Device>();
			for (Device d : allDevices) {
				devices.add(d.getName());
				map.put(d.getName(), d);
			}

			String[] values = new String[devices.size()];

			values = devices.toArray(values);

			setListAdapter(new ArrayAdapter<String>(this,
					R.layout.activity_device_view, values));

			ListView listView = getListView();
			listView.setTextFilterEnabled(true);

			listView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {

					String dName = ((TextView) view).getText().toString();
					Device d = map.get(dName);

					String locationString = "@ "
							+ d.getLocation().getLatitude() + " / "
							+ d.getLocation().getLongitude();
					String time = "Create time: " + d.getCreationTime() + "\n"
							+ "Mod time: " + d.getLocation().getLastModTime();

					dialog.setTitle("Device: " + d.getName());
					dialog.setMessage("ID: " + d.getDevID() + "\nCluster: "
							+ d.getClustID() + "\nWeight: " + d.getWeight()
							+ "\n" + time + "\n" + locationString);
					dialog.show();
					//
					// // When clicked, show a toast with the TextView text
					// Toast.makeText(getApplicationContext(),
					// ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
				}
			});

		}
	}

}
