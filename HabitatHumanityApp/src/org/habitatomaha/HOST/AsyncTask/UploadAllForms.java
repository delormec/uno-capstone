package org.habitatomaha.HOST.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import org.habitatomaha.HOST.Activity.ScreenOneRework;
import org.habitatomaha.HOST.Model.Error;
import org.habitatomaha.HOST.Model.Error.Severity;
import org.habitatomaha.HOST.Model.Form;
import org.habitatomaha.HOST.Model.SpinnerData;
import org.habitatomaha.HOST.Model.Repository.ErrorLog;
import org.habitatomaha.HOST.Model.Repository.OSTDataSource;
import org.habitatomaha.HOST.Model.Repository.SharePointDataSource;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class UploadAllForms extends AsyncTask
{
	private SharedPreferences settings;
	private ProgressDialog progressDialog;
	private OSTDataSource database;
	private List<Integer> failures;
	private Context callingContext;
	private String toastString;
	
	// Constructor
	public UploadAllForms(Context context) 
	{
		this.callingContext = context;
		this.progressDialog = new ProgressDialog(callingContext);
		this.database = new OSTDataSource(callingContext);	
		this.toastString = "";
	}

	
	@Override
	protected void onPreExecute()
	{
		progressDialog.setMessage("Uploading forms...");
		progressDialog.show();
	}
	
	
	@Override
	protected void onPostExecute(Object result)
	{
		progressDialog.dismiss();
		
		Toast.makeText(callingContext, toastString, Toast.LENGTH_LONG).show();
		
		
		if (callingContext instanceof ScreenOneRework)
		{
			ScreenOneRework getMethods = (ScreenOneRework) callingContext;
			getMethods.setView(ScreenOneRework.GROUPS, getMethods.buildGroupsView());
		}
	}
	
	
	/** Changes the ProgressDialog to note how many forms have been uploaded so far
	 *  values[0] should be the current form being uploaded
	 *  values[1] should be the total number of forms being uploaded
	 */
	@Override
	protected void onProgressUpdate(Object... values)
	{
		progressDialog.setMessage(String.format("Uploading form %d of %d...", values[0], values[1]));
	}
	
	
	
	/**
	 * Sets the callingContext of this task to context and resets the progressDialog
	 * 
	 * @param context	The Context that called this method
	 */
	public void rebuild(Context context)
	{
		this.callingContext = context;
		
		progressDialog = new ProgressDialog(callingContext);
		progressDialog.setMessage("Downloading form templates...");
		progressDialog.show();
	}
	
	
	
	@Override
	protected Object doInBackground(Object... params)
	{
		Form form;
		int formID;
		
		String URL;
		String list_name;
		String user_name;
		String password;
		String domain;
		String port;
		
		//Get SharedPreferences manager.
		settings = PreferenceManager.getDefaultSharedPreferences(callingContext);
			
		//User (app) preferences
		user_name = settings.getString("sharepoint_username","");
		password = settings.getString("sharepoint_password","");
		domain = settings.getString("sharepoint_domain", "");
		
		database.open();	
		
		// Get all form info
		List<SpinnerData> formDataList = database.getAllFormInfo();
		
		failures = new ArrayList<Integer>(formDataList.size());
		String[] response;
		
		// For each form in the database, attempt to upload it to SharePoint
		for (int x = 0; x < formDataList.size(); x++)
		{	
			// Update the ProgressDialog
			publishProgress(x + 1, formDataList.size());
			
			// Get the form from the database
			formID = formDataList.get(x).getValue();
			form = database.getFormById(formID);
			
			URL = form.meta.url;
			list_name = form.meta.listname;
			port = form.meta.port;
			
			// Attempt the upload
			response = SharePointDataSource.uploadFormToSharePoint(form, URL, list_name, user_name, password, domain, port);
			
			if (response[0].compareTo("0") == 0)
			{
				// If the upload was successful, remove the form from the database
				database.removeFormById(formID);
			}
			else
			{
				// Take note that this form failed to upload
				ErrorLog.log(callingContext, new Error("Form Upload Error", response[1], Severity.Minor));				
				failures.add(formID);
			}
		}
		
		database.close();


		
		// No forms failed to upload
		if (failures.isEmpty())
		{
			toastString = "All forms uploaded successfully";
		}
		// Some form(s) failed to upload
		else
		{
			toastString = "Failed to upload some forms:";
			for (Integer id : failures)
			{
				toastString += String.format(" %s", id.toString());
			}
		}		
		
		return null;
	}
	
}
