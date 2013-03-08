package com.example.habitathumanityapp;


import java.util.List;
import java.util.Random;

import com.example.habitathumanityapp.datasource.OSTDataSource;

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
	
	
	
	
	/**
	 * Calls DisplayQuestionActivity with the first form in the database
	 * 
	 * @param view The view of the activity that calls the function
	 */
	public void beginForm0(View view)
	{
		Intent intent = new Intent(this, DisplayQuestionActivity.class);
		
		// Get first form from database
		OSTDataSource ostDS = new OSTDataSource(this);
		ostDS.open();	
		List<String[]> templates = ostDS.getAllTemplateInfo();	
		Form form = ostDS.getTemplateById(Long.parseLong(templates.get(0)[0]));
		
		// This is where the form is passed to the DisplayQuestionActivity
		intent.putExtra("formObject", form);
		intent.putExtra("questionNumber", 0);
		
		ostDS.close();
		
		startActivity(intent);
	}
	
	
	public void beginRandomForm(View view)
	{
		Intent intent = new Intent(this, DisplayQuestionActivity.class);
	
		Random random = new Random();	
		OSTDataSource ostDS = new OSTDataSource(this);
		ostDS.open();
		
		// Get random form from database
		List<String[]> templates = ostDS.getAllTemplateInfo();
		
		Log.v("ryan_debug", String.format("There are %d templates in the database.", templates.size()));
		int formNumber = random.nextInt(templates.size());
		Log.v("ryan_debug", String.format("Randomed template %d", formNumber));
		Form form = ostDS.getTemplateById(Long.parseLong(templates.get(formNumber)[0]));
		
		intent.putExtra("formObject", form);
		intent.putExtra("questionNumber", 0);
		
		ostDS.close();
		
		startActivity(intent);
	}
}
