package org.habitatomaha.HOST.AsyncTask;

import org.habitatomaha.HOST.Model.Form;
import org.habitatomaha.HOST.Model.Repository.SharePointDataSource;


import android.os.AsyncTask;

public class UploadForm extends AsyncTask{

	@Override
	protected Object doInBackground(Object... params) {
		// TODO Auto-generated method stub
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

		return SharePointDataSource.uploadFormToSharePoint((Form)params[0], URL, list_name, user_name, password, domain);
	
		//this.notifyAll();
	}
}
