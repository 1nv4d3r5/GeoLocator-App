package com.tckb.geo.app;

import java.util.ArrayList;
import java.util.List;

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

public class ClusterView extends ListActivity {
	List<Cluster> allCluster = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_cluster_view);

		allCluster = GeoLocatorAsyncService.getAllClustersFromRemote();
		final AlertDialog dialog = new AlertDialog.Builder(this).create();
		dialog.setCancelable(false);
		dialog.setButton("ok", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				
			}
		});
		

		if (allCluster != null) {
			ArrayList<String> clusterIDs = new ArrayList<String>();

			for (Cluster c : allCluster) {
				clusterIDs.add(c.getID());
			}

			String[] values = new String[allCluster.size()];

			values = clusterIDs.toArray(values);

			setListAdapter(new ArrayAdapter<String>(this,
					R.layout.activity_cluster_view, values));

			ListView listView = getListView();
			listView.setTextFilterEnabled(true);

			

			listView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					String clstID = ((TextView) view).getText().toString();
					Cluster cluster = GeoLocatorAsyncService.getClusterFromRemote(clstID);
					String locationString = "@ "+cluster.getLocation().getLatitude()+" / " + cluster.getLocation().getLongitude();
					String time = "Mod time: "+cluster.getLocation().getLastModTime();
					dialog.setTitle("Cluster details");
					dialog.setMessage("ID: "+clstID+"\n"+time+"\n"+locationString);
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
