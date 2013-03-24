package com.example.habitathumanityapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class NavigationActivity extends Activity{

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_form_screen);
		
		//OSTDataSource OSTDS = new OSTDataSource(this);
		
		//OSTDS.getAllFormIds();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main_screen, menu);
		return true;
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
}
