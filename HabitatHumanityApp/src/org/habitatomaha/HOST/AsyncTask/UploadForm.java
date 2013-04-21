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
		String port;
		
		//Get SharedPreferences manager.
		settings = PreferenceManager.getDefaultSharedPreferences(callingContext);
			
		Form form = (Form)params[0];
		URL = form.meta.url;
		list_name = form.meta.listname;
		port = form.meta.port;
		
		//User (app) preferences
		user_name = settings.getString("sharepoint_username","");
		password = settings.getString("sharepoint_password","");
		domain = settings.getString("sharepoint_domain", "");

		return SharePointDataSource.uploadFormToSharePoint((Form)params[0], URL, list_name, user_name, password, domain, port);
	
		//this.notifyAll();
	}
}
