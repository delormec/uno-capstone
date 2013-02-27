package com.example.habitathumanityapp.tasks;

import com.example.habitathumanityapp.datasource.SharePointDataSource;

import android.os.AsyncTask;

public class createSharePointListTest extends AsyncTask{

	@Override
	protected Object doInBackground(Object... params) {
		// TODO Auto-generated method stub
		
		((SharePointDataSource)params[0]).createListTest();
	
		return null;
	}

}
