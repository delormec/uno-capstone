package com.example.habitathumanityapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class DisplayQuestionActivity extends Activity 
{

	private Form form;
	private Question question;
	private Integer questionNumber = 0;
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		// Receive the form from whomever called
		form = (Form) getIntent().getExtras().get("formObject");
		questionNumber = (Integer) getIntent().getExtras().get("questionNumber");
		
		
		
		if (form != null)
		{		
			question = form.questions.get(questionNumber);
			
			// Display the question
			if (question != null)
			{
				setContentView(R.layout.activity_display_question);
				TextView questionText = (TextView) findViewById(R.id.questionText);
				questionText.setText(question.Text);
				
				
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
			else
			{
				// No question here. It is probably the end of the form.
				
				// This is where it should go to the save screen.
			}
		}
		else
		{		
			// The is no form object. Not sure what to do in this case. Let's just avoid it.
		}
			
	}
	
	public void nextQuestion(View view)
	{
		Intent intent = new Intent(this, DisplayQuestionActivity.class);
		
		if (form != null)
		{
			// Save the info somewhere?
			
			
			intent.putExtra("formObject", form);
			intent.putExtra("questionNumber", questionNumber + 1);
			
			// Don't stack question activities
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			
			startActivity(intent);
		}
	}
	
	public void prevQuestion(View view)
	{
		Intent intent = new Intent(this, DisplayQuestionActivity.class);
		
		if (form != null)
		{
			if (questionNumber > 0)
			{
				// Save the info somewhere
				
				
				intent.putExtra("formObject", form);
				intent.putExtra("questionNumber", questionNumber - 1);
				
				// Don't stack question activities
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				
				startActivity(intent);
			}
		}
	}
	
}
