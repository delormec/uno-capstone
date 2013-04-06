package org.habitatomaha.HOST.Helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.inputmethod.InputMethodManager;

/**
 * A class for static methods that are useful in general for Android
 */
public class Utility 
{
	/**
	 * Hides the android keyboard
	 * 
	 * @param activity
	 */
	public static void hideSoftKeyboard(Activity activity) 
	{
	    InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
	    
	    
	    if (activity.getCurrentFocus() != null)
	    {
	    	inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
	    }
	}

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
