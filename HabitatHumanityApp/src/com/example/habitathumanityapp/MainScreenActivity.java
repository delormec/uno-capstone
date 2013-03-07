package com.example.habitathumanityapp;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
	
	public void parseFormTest(View view){
		Intent intent = new Intent(this, ParseFormTestActivity.class);
		startActivity(intent);
	}
	
	public void mainMenuTest(View view){
		Intent intent = new Intent(this, MainMenuActivity.class);
		startActivity(intent);
	}

	public void showFormScreen(View view){
		setContentView(R.layout.main_form_screen);
	}
	public void showQuestionScreen(View view){
		setContentView(R.layout.main_question_screen);
	}
	public void showSubmitScreen(View view){
		setContentView(R.layout.main_submit_screen);
	}
	
	// Give a form to the DisplayQuestionActivity
	public void beginForm(View view)
	{
		Intent intent = new Intent(this, DisplayQuestionActivity.class);
		
		Form dummyForm =  FormHelper.dummyForm();
		
		// This is where the form is passed
		intent.putExtra("formObject", dummyForm);
		intent.putExtra("questionNumber", 0);
		
		startActivity(intent);
	}
}
