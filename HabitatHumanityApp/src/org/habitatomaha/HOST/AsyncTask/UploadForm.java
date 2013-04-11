package org.habitatomaha.HOST.AsyncTask;

import org.habitatomaha.HOST.Model.Form;
import org.habitatomaha.HOST.Model.Repository.SharePointDataSource;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

public class UploadForm extends AsyncTask{

	private SharedPreferences settings;
	
	// Would it be a problem to add a constructor for this task? Needs context for shared Preferences.

	private Context callingContext;
	
	public UploadForm(Context context){
		this.callingContext = context;
	}
	@Override
	protected Object doInBackground(Object... params) {
		// TODO Auto-generated method stub
		String URL;
		String list_name;
		String user_name;
		String password;
		String domain;
		
		//Get SharedPreferences manager.
		settings = PreferenceManager.getDefaultSharedPreferences(callingContext);
		
		//TODO Preferences that are needed to connect to the SharePoint site.
		
		//Form preferences
		URL = "habitat.taic.net";
		list_name = "/omaha/unotestsite/_vti_bin/listdata.svc/ConstructionAtlasTest";
		
		//User (app) preferences
		user_name = settings.getString("sharepoint_username","");
		password = settings.getString("sharepoint_password","");
		domain = settings.getString("sharepoint_domain", "");

		return SharePointDataSource.uploadFormToSharePoint((Form)params[0], URL, list_name, user_name, password, domain);
	
		//this.notifyAll();
	}
}
