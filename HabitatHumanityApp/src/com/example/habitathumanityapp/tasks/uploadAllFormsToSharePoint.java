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
		
		List<MyData> formDataList = database.getAllFormInfo();
		failures = new ArrayList<Integer>(formDataList.size());
		String[] result;
		for (int x = 0; x < formDataList.size(); x++)
		{	
			publishProgress(x + 1, formDataList.size());
			
			formID = formDataList.get(x).getValue();
			form = database.getFormById(formID);
			
			result = SharePointDataSource.uploadFormToSharePoint(form, URL, list_name, user_name, password, domain);
			
			if (result[0] == "0")
			{
				database.removeFormById(formID);
			}
			else
			{
				failures.add(formID);
			}
		}
		
		database.close();
		
		
		if (failures.isEmpty())
		{
			toastString = "All forms uploaded successfully";
		}
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
