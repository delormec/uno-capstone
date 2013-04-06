package com.example.habitathumanityapp.Helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Helper class related to connectivity, currently only a check for network availability.
 * @author Cody
 */
public class Connectivity {

	/**
	 * Use to check if network is available, connection could still fail even if this returns true. <br/>
	 * Bad connection, network restrictions ect.
	 * @param activity Current activity
	 * @return
	 */
	public static boolean isNetworkAvailable(Activity activity)
	{
		ConnectivityManager connectivityManager 
	          = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	/** Displays a popup up notifying user that no network is currently available.
	 * @param activity Current activity
	 */
	public static void displayNetworkUnavailableDialog(Activity activity)
	{
		
		AlertDialog.Builder adb = new AlertDialog.Builder(activity);
		adb.setTitle("Network Unavailable");
		adb.setMessage("There is currently no network, try again when connected to WIFI.");
		adb.setCancelable(false);
		adb.setPositiveButton("Ok", 
								new DialogInterface.OnClickListener()
								{
									public void onClick(DialogInterface dialog, int id)
									{
										return;
									}
								});
		
		AlertDialog alertDialog = adb.create();
		alertDialog.show();
		
		return;
		
	}
}
