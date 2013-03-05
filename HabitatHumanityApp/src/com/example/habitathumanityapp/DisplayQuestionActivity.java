package com.example.habitathumanityapp;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class DisplayQuestionActivity extends Activity 
{

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		// Receive the question object from whomever called
		Question question = (Question) getIntent().getExtras().get("questionObject");	
		
		setContentView(R.layout.activity_display_question);
		TextView questionText = (TextView) findViewById(R.id.questionText);
		questionText.setText("Question text doesn't work.");
		
		String questionString = (String) getIntent().getExtras().get("questionText");
		questionText.setText(questionString);
			

		
		// Set up text question view
		if (question instanceof TextQuestion) 
		{
			// Remove other fields
			this.findViewById(R.id.questionChoices).setVisibility(View.GONE);
			this.findViewById(R.id.likertLow).setVisibility(View.GONE);
			this.findViewById(R.id.likertHigh).setVisibility(View.GONE);
		}
		// Set up the choice question view
		else if (question instanceof ChoiceQuestion)
		{	
			// Remove other fields
			this.findViewById(R.id.answerText).setVisibility(View.GONE);
			this.findViewById(R.id.likertLow).setVisibility(View.GONE);
			this.findViewById(R.id.likertHigh).setVisibility(View.GONE);
			
			// Cast as ChoiceQuestion
			ChoiceQuestion choiceQuestion = (ChoiceQuestion) question;
				
			RadioGroup buttons = (RadioGroup) findViewById(R.id.questionChoices);
			RadioButton button;
				
			// Add a radio button to the radiogroup for each choice
			for(String option : choiceQuestion.options)
			{
				button = new RadioButton(this);
				button.setText(option);
				buttons.addView(button);
			}			
		}
		// Set up the likert question view
		else if (question instanceof LikertScaleQuestion)
		{
			// Remove other fields
			this.findViewById(R.id.answerText).setVisibility(View.GONE);
			
			// Cast as LikertScaleQuestion
			LikertScaleQuestion likertScaleQuestion = (LikertScaleQuestion) question;
			
			int steps = Integer.parseInt(likertScaleQuestion.steps);
			
			RadioGroup buttons = (RadioGroup) findViewById(R.id.questionChoices);
			RadioButton button;
			
			for(int x = 0; x < steps; x++)
			{
				button = new RadioButton(this);
				buttons.addView(button);
			}		
		}
	}
}
