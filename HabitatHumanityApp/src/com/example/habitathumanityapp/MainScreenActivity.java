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

	
	
	// Give a form to the DisplayQuestionActivity
	public void beginForm(View view)
	{
		Intent intent = new Intent(this, DisplayQuestionActivity.class);
		
		Form dummy =  FormHelper.dummyForm();
		
		intent.putExtra("formObject", dummy);
		intent.putExtra("questionNumber", 0);
		intent.putExtra("questionText", dummy.questions.get(0).Text);
		
		startActivity(intent);
	}
	
	// Displays a text question
	public void displayTextQuestion(View view)
	{	
		// Set up a dummy text question
		TextQuestion tq = new TextQuestion();
		tq.Text = "This is a text question.";
		
		Intent intent = new Intent(this, DisplayQuestionActivity.class);
		intent.putExtra("questionObject", tq);
		intent.putExtra("questionText", tq.Text); // Will try to figure out how to not need this line
		startActivity(intent);
	}
	
	// Displays a choice question
	public void displayChoiceQuestion(View view)
	{
		// Set up a dummy choice question
		ChoiceQuestion cq = new ChoiceQuestion();
		cq.Text = "This is a choice question.";
		cq.options.add("option1");
		cq.options.add("option2");
		cq.options.add("option3");
		
		Intent intent = new Intent(this, DisplayQuestionActivity.class);
		intent.putExtra("questionObject", cq);
		intent.putExtra("questionText", cq.Text);  // Will try to figure out how to not need this line
		startActivity(intent);
	}
	
	// Displays a likert scale question
	public void displayLikertQuestion(View view)
	{
		// Set up a dummy likert scale question
		LikertScaleQuestion lsq = new LikertScaleQuestion();
		lsq.Text = "This is a likert scale question.";
		lsq.labels.add("low label");
		lsq.labels.add("high label");
		lsq.steps = "5";
		
		Intent intent = new Intent(this, DisplayQuestionActivity.class);
		intent.putExtra("questionObject", lsq);
		intent.putExtra("questionText", lsq.Text); // Will try to figure out how to not need this line
		startActivity(intent);
	}
}
