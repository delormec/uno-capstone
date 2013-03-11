package com.example.habitathumanityapp;

import android.app.Activity;
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
	    inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
	}
}
