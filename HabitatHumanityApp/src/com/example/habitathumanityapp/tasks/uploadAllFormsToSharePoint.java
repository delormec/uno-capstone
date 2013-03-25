package com.example.habitathumanityapp.tasks;

import java.util.ArrayList;
import java.util.List;

import com.example.habitathumanityapp.Form;
import com.example.habitathumanityapp.MyData;
import com.example.habitathumanityapp.datasource.OSTDataSource;
import com.example.habitathumanityapp.datasource.SharePointDataSource;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class uploadAllFormsToSharePoint extends AsyncTask
{
	private ProgressDialog progressDialog;
	private OSTDataSource database;
	private List<Integer> failures;
	private Context context;
	private String toastString;
	
	// Constructor
	public uploadAllFormsToSharePoint(Context context) 
	{
		this.context = context;
		this.progressDialog = new ProgressDialog(context);
		this.database = new OSTDataSource(context);	
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
		
		Toast.makeText(context, toastString, Toast.LENGTH_LONG).show();
	}
	
	
	// Changes the ProgressDialog to note how many forms have been uploaded so far
	// values[0] should be the current form being uploaded
	// values[1] should be the total number of forms being uploaded
	@Override
	protected void onProgressUpdate(Object... values)
	{
		progressDialog.setMessage(String.format("Uploading form %d of %d...", values[0], values[1]));
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
		
		//TODO - Eventually these will be pulled from the configuration area of the database
		URL = "habitat.taic.net";
		list_name = "/omaha/unotestsite/_vti_bin/listdata.svc/ConstructionAtlasTest";
		user_name = "CDelorme";
		password = "CDelorme463";
		domain = "xtranet";
		
		
		
		database.open();	
		
		// Get all form info
		List<MyData> formDataList = database.getAllFormInfo();
		
		failures = new ArrayList<Integer>(formDataList.size());
		String[] result;
		
		// For each form in the database, attempt to upload it to SharePoint
		for (int x = 0; x < formDataList.size(); x++)
		{	
			// Update the ProgressDialog
			publishProgress(x + 1, formDataList.size());
			
			// Get the form from the database
			formID = formDataList.get(x).getValue();
			form = database.getFormById(formID);
			
			// Attempt the upload
			result = SharePointDataSource.uploadFormToSharePoint(form, URL, list_name, user_name, password, domain);
			
			if (result[0].compareTo("0") == 0)
			{
				// If the upload was successful, remove the form from the database
				database.removeFormById(formID);
			}
			else
			{
				// Take note that this form failed to upload
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
