package com.example.habitathumanityapp;

import com.example.habitathumanityapp.datasource.OSTDataSource;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class DisplayQuestionActivity extends Activity 
{
	private Form form;
	private Question question;
	private Integer questionNumber;
	private Toast toast;
	private OSTDataSource ostDS;
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
				
		// Receive the form from whomever called
		form = (Form) getIntent().getExtras().get("formObject");
		questionNumber = (Integer) getIntent().getExtras().get("questionNumber");
		
		
		if (form != null && form.questions.size() > 0)
		{					
			question = form.questions.get(questionNumber);
			
			// Display the question
			if (question != null)
			{
				setContentView(R.layout.activity_display_question);
				TextView questionText = (TextView) findViewById(R.id.questionText);
				questionText.setText(question.Text);						
				this.findViewById(R.id.navbar_edit_button).setVisibility(View.INVISIBLE);
				
				//Log.v("ryan_debug", String.format("Displaying Template %s - Question %d", String.valueOf(form.meta.template_id), questionNumber));			
				
				
				
				
				
				// Set up text question view
				if (question instanceof TextQuestion) 
				{
					setupTextQuestionView((TextQuestion) question);
				}
				
				// Set up the choice question view
				else if (question instanceof ChoiceQuestion)
				{	
					setupChoiceQuestionView((ChoiceQuestion) question);												
				}
		
				// Set up the likert scale question view
				else if (question instanceof LikertScaleQuestion)
				{
					setupLikertQuestionView((LikertScaleQuestion) question);		
				}
				
				else
				{
					// This block should ideally never be entered
					Log.v("ryan_debug", "Entered unexpectedblock in method onCreate() in class DisplayQuestionActivity (received qestion of unknown type)");
					
					toast = Toast.makeText(this, "Unknown question type", Toast.LENGTH_SHORT);
					toast.show();
					this.prevQuestion(null);
				}			
			}
			else
			{
				// This block should ideally never be entered
				Log.v("ryan_debug", "Entered unexpected block in method onCreate() in class DisplayQuestionActivity (received null question)");
			}
		}
		else
		{				
			// Did not receive a form object
			toast = Toast.makeText(this, "No form or empty form received.", Toast.LENGTH_SHORT);
			toast.show();
			this.finish();
		}		
	}
	
	
	
	
	
	
	
	/**
	 * Sets up the View for the given TextQuestion
	 * 
	 * @param tq	The TextQuestion to set up
	 */
	private void setupTextQuestionView(TextQuestion tq)
	{	
		// Remove other fields
		this.findViewById(R.id.questionChoices).setVisibility(View.GONE);
		this.findViewById(R.id.likertLow).setVisibility(View.GONE);
		this.findViewById(R.id.likertHigh).setVisibility(View.GONE);
		
		// Fill in pre-existing answer
		if (tq.Answer != null)
		{
			EditText textField = (EditText) this.findViewById(R.id.answerText);
			textField.setText(tq.Answer);
		}
	}
	
	
	
	/**
	 * Sets up the View for the given ChoiceQuestion
	 * 
	 * @param cq	The ChoiceQuestion to set up
	 */
	private void setupChoiceQuestionView(ChoiceQuestion cq)
	{
		// Remove other fields
		this.findViewById(R.id.answerText).setVisibility(View.GONE);
		this.findViewById(R.id.likertLow).setVisibility(View.GONE);
		this.findViewById(R.id.likertHigh).setVisibility(View.GONE);
		
		
		// Set up multiple select ChoiceQuestion
		if (cq.multipleselect.compareToIgnoreCase("true") == 0)
		{
			LinearLayout multipleSelectChoices = (LinearLayout) this.findViewById(R.id.multipleSelectChoices);
			CheckBox checkBox;
			String[] answers;
			
			// Retrieve possible multiple answer list
			if (cq.Answer != null)
			{
				answers = cq.Answer.split(";");
			}
			else
			{
				answers = null;
			}
			
			
			// Add a CheckBox to the layout for each option
			for (String option : cq.options)
			{
				if (option != null)
				{
					if (option.compareTo("") != 0)
					{
						// Add the checkbox
						checkBox = new CheckBox(this);
						checkBox.setText(option);
						multipleSelectChoices.addView(checkBox);
						
						// Select pre-existing answer(s) (by simulating the button press)
						if (answers != null)
						{
							for (String answer : answers)
							{
								if (option.compareTo(answer) == 0)
								{
									checkBox.performClick();
								}
							}
						}
					}
				}
			}
		}
		// Set up normal ChoiceQuestion
		else
		{	
			RadioGroup buttons = (RadioGroup) findViewById(R.id.questionChoices);
			RadioButton button;
			
			// Add a RadioButton to the RadioGroup for each choice
			for (String option : cq.options)
			{
				if (option != null)
				{
					if (option.compareTo("") != 0)
					{
						// Add the button
						button = new RadioButton(this);
						button.setText(option);
						buttons.addView(button);
					
						// Select pre-existing answer (by simulating the button press)
						if (cq.Answer != null)
						{
							if (option.compareTo(cq.Answer) == 0)
							{
								button.performClick();
							}
						}	
					}
				}
			}
		}
	}
	
	
	
	/**
	 * Sets up the View for the given LikertScaleQuestion
	 * 
	 * @param lsq	The LikertScaleQuestion to set up
	 */
	private void setupLikertQuestionView(LikertScaleQuestion lsq)
	{
		// Remove other fields
		this.findViewById(R.id.answerText).setVisibility(View.GONE);
		
		if (lsq.labels.size() >= 2)
		{
			TextView lowText = (TextView) this.findViewById(R.id.likertLow);
			lowText.setText(lsq.labels.get(0));
			TextView highText = (TextView) this.findViewById(R.id.likertHigh);
			highText.setText(lsq.labels.get(1));
		}
		
		
		Integer steps = Integer.parseInt(lsq.steps);
		
		if (steps != null)
		{
			RadioGroup buttons = (RadioGroup) findViewById(R.id.questionChoices);
			RadioButton button;
			
			for(Integer x = 1; x <= steps; x++)
			{
				button = new RadioButton(this);					
				button.setText(x.toString());
				buttons.addView(button);
			
				// 	Select pre-existing answer (by simulating the button press)
				if (lsq.Answer != null)
				{
					if (x.toString().compareTo(lsq.Answer) == 0)
					{
						button.performClick();
					}
				}										
			}
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
			if (toast != null) toast.cancel();
			
			if (questionNumber < form.questions.size() - 1)
			{
				// Save the answer and form
				saveAnswerToForm();			
				// TODO save form to database here
				
				
				// Pass the form and question number to the next activity
				intent.putExtra("formObject", form);
				intent.putExtra("questionNumber", questionNumber + 1);
			
				// Don't stack question activities
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				
				startActivity(intent);
			}
			else
			{
				// On last question.'				
				toast = Toast.makeText(this, "End of form", Toast.LENGTH_SHORT);
				toast.show();
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
			if (toast != null) toast.cancel();
			
			if (questionNumber > 0)
			{
				// Save the answer and form
				saveAnswerToForm();
				// TODO save form to database here
				
				
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
				if (toast != null) toast.cancel();
				
				toast = Toast.makeText(this, "Beginning of form", Toast.LENGTH_SHORT);
				toast.show();
			}
		}
	}	
	
	
	
	/**
	 * Returns the user-selected answer from the currently displayed question.
	 * 
	 * @return	The selected answer of the currently displayed question
	 */
	private void saveAnswerToForm()
	{
		if (question instanceof TextQuestion)
		{
			// Pull the answer
			EditText answer = (EditText) this.findViewById(R.id.answerText);
			
			// Store the answer
			if (answer != null)
			{
				form.questions.get(questionNumber).Answer = answer.getText().toString();
			}
		}
		else if (question instanceof ChoiceQuestion)
		{
			ChoiceQuestion cq = ((ChoiceQuestion) question);
			
			if (cq.multipleselect.compareToIgnoreCase("true") == 0)
			{
				LinearLayout choices = (LinearLayout) this.findViewById(R.id.multipleSelectChoices);	
				int numBoxes = choices.getChildCount();
				
				String answer = "";
				
				
				// Go through each CheckBox
				for (int x = 0; x < numBoxes; x++)
				{
					CheckBox checkBox = (CheckBox) choices.getChildAt(x);
								
					if (checkBox.isChecked())
					{
						answer += String.format("%s;", checkBox.getText().toString());
					}
				}
				
				form.questions.get(questionNumber).Answer = answer;
			}
			else
			{
				RadioGroup choices = (RadioGroup) this.findViewById(R.id.questionChoices);
				RadioButton answer = (RadioButton) this.findViewById(choices.getCheckedRadioButtonId());
			
				if (answer != null)
				{
					form.questions.get(questionNumber).Answer = answer.getText().toString();
				}
			}
		}
		else if (question instanceof LikertScaleQuestion)
		{
			RadioGroup choices = (RadioGroup) this.findViewById(R.id.questionChoices);
			RadioButton answer = (RadioButton) this.findViewById(choices.getCheckedRadioButtonId());
			
			if (answer != null)
			{
				form.questions.get(questionNumber).Answer = answer.getText().toString();
			}		
		}
	}
	
	
	
	
	
	
	// The following navigate methods are for the implementation of the navbar layout
	public void navigateHome(View view)
	{	
		// Save the answer and form first
		saveAnswerToForm();
		// TODO save form to database here
		
		if (toast != null) toast.cancel();
		
		this.finish();
	}
	public void navigateEdit(View view)
	{
		return;
	}
	public void navigateSubmit(View view)
	{
		// Save the answer and form first
		saveAnswerToForm();
		// TODO save form to database here

		if (toast != null) toast.cancel();
		
		// TODO Go to third screen (once it exists).
	}
}
