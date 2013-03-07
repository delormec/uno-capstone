package com.example.habitathumanityapp;

import com.example.habitathumanityapp.datasource.OSTDataSource;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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
					
					// Fill in pre-existing answer
					if (question.Answer != null)
					{
						EditText textField = (EditText) this.findViewById(R.id.answerText);
						textField.setText(question.Answer);
					}
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
						
						// Select pre-existing answer (by simulating the button press)
						if (question.Answer != null)
						{
							if (option.compareTo(question.Answer) == 0)
							{
								button.performClick();
							}
						}					
					}			
					
					
						
				}
				
				// Set up the likert scale question view
				else if (question instanceof LikertScaleQuestion)
				{
					// Remove other fields
					this.findViewById(R.id.answerText).setVisibility(View.GONE);
					
					// Cast as LikertScaleQuestion
					LikertScaleQuestion likertScaleQuestion = (LikertScaleQuestion) question;
					
					int steps = Integer.parseInt(likertScaleQuestion.steps);
					
					RadioGroup buttons = (RadioGroup) findViewById(R.id.questionChoices);
					RadioButton button;
					
					for(Integer x = 1; x <= steps; x++)
					{
						button = new RadioButton(this);					
						button.setText(x.toString());
						buttons.addView(button);
						
						// Select pre-existing answer (by simulating the button press)
						if (question.Answer != null)
						{
							if (x.toString().compareTo(question.Answer) == 0)
							{
								button.performClick();
							}
						}										
					}		
				}				
			}
			else
			{
				/* No question here. It is probably the end of the form. 
				  (Unless we got here in some unexpected way).  */
				
				// TODO Go to third screen here?
			}
		}
		else
		{				
			// Did not receive a form object
			Toast.makeText(this, "Error. No form received.", Toast.LENGTH_LONG).show();
		}
			
	}
	
	
	/**
	 * Attempts to cycle to the next question of the form.
	 * Does nothing if currently on the final question of the form.
	 * 
	 * @param view
	 */
	public void nextQuestion(View view)
	{
		Intent intent = new Intent(this, DisplayQuestionActivity.class);
		
		if (form != null)
		{
			if (questionNumber < form.questions.size() - 1)
			{
				// Save the answer to the form object
				form.questions.get(questionNumber).Answer = getAnswerText();			
				
				// TODO Write form to database?
				
				// Pass the form and question number to the next activity
				intent.putExtra("formObject", form);
				intent.putExtra("questionNumber", questionNumber + 1);
			
				// Don't stack question activities
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			
				startActivity(intent);
			}
			else
			{
				// On last question.
				// TODO Go to third screen here?
			}
		}
	}
	
	/**
	 * Attempts to cycle to the previous question of the form.
	 * Does nothing if currently on question 0
	 * 
	 * @param view The view from the activity that called this function
	 */
	public void prevQuestion(View view)
	{
		Intent intent = new Intent(this, DisplayQuestionActivity.class);
		
		if (form != null)
		{
			if (questionNumber > 0)
			{
				// Store the answer to the form object
				form.questions.get(questionNumber).Answer = getAnswerText();
				
				//TODO Write form to database?
				
				// Pass the form and question number to the next activity
				intent.putExtra("formObject", form);
				intent.putExtra("questionNumber", questionNumber - 1);
				
				// Don't stack question activities
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				
				startActivity(intent);
			}
			else
			{
				// On the first question, can't go to previous.
			}
		}
	}	
	
	
	/**
	 * Returns the user-selected answer from the currently displayed question.
	 * 
	 * @return	The selected answer of the currently displayed question
	 */
	private String getAnswerText()
	{
		if (question instanceof TextQuestion)
		{
			EditText answer = (EditText)this.findViewById(R.id.answerText);
			
			if (answer != null) return answer.getText().toString();
			else return null;
		}
		else if (question instanceof ChoiceQuestion)
		{
			RadioGroup choices = (RadioGroup) this.findViewById(R.id.questionChoices);
			RadioButton answer = (RadioButton) this.findViewById(choices.getCheckedRadioButtonId());
			
			if (answer != null) return answer.getText().toString();
			else return null;
		}
		else if (question instanceof LikertScaleQuestion)
		{
			RadioGroup choices = (RadioGroup) this.findViewById(R.id.questionChoices);
			RadioButton answer = (RadioButton) this.findViewById(choices.getCheckedRadioButtonId());
			
			if (answer != null) return answer.getText().toString();
			else return null;		
		}
		
		return null;
	}
}
