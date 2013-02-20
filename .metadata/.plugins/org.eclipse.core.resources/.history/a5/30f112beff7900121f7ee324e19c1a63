package com.example.habitathumanityapp;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;


public class MainScreenActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_screen);
		
		//OSTDataSource OSTDS = new OSTDataSource(this);
		
		//OSTDS.getAllFormIds();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main_screen, menu);
		return true;
	}
	/**
	 * Handles the button click to open the DisplayFormACtivity.
	 * Will need to add functionality to pass more information later on.
	 * @param view 
	 */
	public void createForm(View view){
		Intent intent = new Intent(this, DisplayFormActivity.class);
		startActivity(intent);
	}
	/**
	 * Handles the button click to open the DataStoreTestActivity.
	 * @param view 
	 */
	public void dataStoreTest(View view){
		Intent intent = new Intent(this, DataStoreTestActivity.class);
		startActivity(intent);
	}

}
