/**
 * 
 */
package com.tckb.geo.app;

import java.util.List;
import java.util.concurrent.ExecutionException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.Toast;

import com.tckb.geo.api.LocatorREST;
import com.tckb.geo.api.LocatorService;
import com.tckb.geo.stubs.Cluster;
import com.tckb.geo.stubs.Device;

/**
 * @author TCKB
 * 
 */
public class GeoLocatorAsyncService {

	static {
		LocatorREST
				.setEndPoint("http://webapps-tckb.rhcloud.com/GeoLocator/service/geo");
	}
	private static LocatorService remoteService = LocatorREST.getAPI();
	private static Context uiContext = null;

	public static List<Cluster> getAllClustersFromRemote() {
		List<Cluster> list = null;

		if (checkForInternetConnection()) {

			AsyncTask<Void, Void, List<Cluster>> and = new AsyncTask<Void, Void, List<Cluster>>() {

				@Override
				protected List<Cluster> doInBackground(Void... params) {
					List<Cluster> list = remoteService.getAllClusters();
					return list;
				}

				@Override
				protected void onPreExecute() {

					if (uiContext != null) {

						Toast.makeText(uiContext,
								"Getting data from remote...",
								Toast.LENGTH_SHORT).show();
					}
					super.onPreExecute();
				}

			}.execute();

			try {
				list = and.get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return list;
	}

	public static List<Device> getAllDevicesFromRemote() {
		List<Device> list = null;
		if (checkForInternetConnection()) {
			AsyncTask<Void, Void, List<Device>> and = new AsyncTask<Void, Void, List<Device>>() {

				@Override
				protected List<Device> doInBackground(Void... params) {
					List<Device> list = remoteService.getAllDevice();
					return list;
				}

				@Override
				protected void onPreExecute() {

					if (uiContext != null) {

						Toast.makeText(uiContext,
								"Getting data from remote...",
								Toast.LENGTH_SHORT).show();
					}
					super.onPreExecute();
				}
			}.execute();

			try {
				list = and.get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return list;
	}

	public static boolean registerDeviceToRemote(final Device device) {
		int status = 500;

		if (checkForInternetConnection()) {

			AsyncTask<Void, Void, Integer> and = new AsyncTask<Void, Void, Integer>() {

				@Override
				protected Integer doInBackground(Void... params) {
					Integer status = remoteService.registerDevice(device)
							.getStatus();
					return status;
				}

				@Override
				protected void onPreExecute() {

					if (uiContext != null) {

						Toast.makeText(uiContext, "Sending data to remote...",
								Toast.LENGTH_SHORT).show();
					}
					super.onPreExecute();
				}
			}.execute();
			try {
				status = and.get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return (status == 200);

	}

	public static Cluster getClusterFromRemote(final String clstID) {

		Cluster c = null;
		if (checkForInternetConnection()) {

			AsyncTask<Void, Void, Cluster> and = new AsyncTask<Void, Void, Cluster>() {

				@Override
				protected Cluster doInBackground(Void... params) {
					Cluster c = remoteService.getCluster(clstID);
					return c;
				}

				@Override
				protected void onPreExecute() {

					if (uiContext != null) {

						Toast.makeText(uiContext,
								"Getting data from remote...",
								Toast.LENGTH_SHORT).show();
					}
					super.onPreExecute();
				}
			}.execute();

			try {
				c = and.get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return c;
	}

	public static void enableNotification(Context c) {
		uiContext = c;
		
	}

	public static void disableNotification() {
		if (uiContext != null) {
			uiContext = null;
		}
	}

	public static boolean checkForInternetConnection() {
		if (!isConnectedToInternet()) {
			if (uiContext != null) {

				Toast.makeText(
						uiContext,
						"No Internet Connection!\n Internet Connection required for the application to work properly\nPlease check your internet settings",
						Toast.LENGTH_LONG).show();

				// dialog.setTitle("Connection lost");
				// dialog.setMessage("Not Connected to Internet, please check your internet connection!");
				// dialog.show();
			}
			return false;
		}
		return true;
	}

	public static boolean isConnectedToInternet() {

		if (uiContext != null) {

			ConnectivityManager cm = (ConnectivityManager) uiContext
					.getSystemService(Context.CONNECTIVITY_SERVICE);

			NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
			return (activeNetwork != null && activeNetwork
					.isConnectedOrConnecting());
		}
		return false;
	}
}
