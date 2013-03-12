package com.example.habitathumanityapp;

import com.example.habitathumanityapp.datasource.OSTDataSource;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.View;
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
	private OSTDataSource database;
	private GestureDetector gestureDetector;

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		
		// Set up database object for future use
		database = new OSTDataSource(this);
		
		
		// This is the initial call to the Activity
		if (savedInstanceState == null)
		{
			// Receive the form object and question number
			form = (Form) getIntent().getExtras().get("formObject");
			questionNumber = (Integer) getIntent().getExtras().get("questionNumber");
			
		}
		
		// This is the activity restoring itself (from something like a screen rotation)
		else
		{
			form = (Form) savedInstanceState.getSerializable("formObject");
			questionNumber = savedInstanceState.getInt("questionNumber");
		}
		
		
		
		
		if (questionNumber == null)
		{
			questionNumber = 0;
		}
		
		
		if (form != null && form.questions.size() > 0)
		{					
			question = form.questions.get(questionNumber);
			
			// Display the question
			if (question != null)
			{			
				setContentView(R.layout.activity_display_question);	
				
				findViewById(R.id.question_layout).setOnTouchListener(
					new OnFlingGestureListener() 
					{
						public void onTopToBottomSwipe()
						{
							return;
						}
						public void onBottomToTopSwipe()
						{
							return;
						}
						public void onLeftToRightSwipe()
						{
							prevQuestion(null);
						}
						public void onRightToLeftSwipe()
						{
							nextQuestion(null);
						}
					}
				);
				
				findViewById(R.id.navbar_edit_button).setVisibility(View.INVISIBLE);
				displayNewQuestion(question);
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
	
	
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState)
	{
		super.onSaveInstanceState(savedInstanceState);
		
		saveAnswerToForm();
		savedInstanceState.putSerializable("formObject", form);
		savedInstanceState.putInt("questionNumber", questionNumber);
	}
	

	
	
	/**
	 * Begins the setup for displaying a new question. <br>
	 * Calls one of three helper methods, one for each type of question.
	 * 
	 * @param question	The new question to display
	 */
	private void displayNewQuestion(Question question)
	{
		if (question != null)
		{						
			// Set the question view and question text (same for all three types)		
			TextView questionText = (TextView) findViewById(R.id.questionText);
			questionText.setText(question.Text);
			
			
			// Set up text question view
			if (question instanceof TextQuestion) 
			{
				// Reset the TextQuestion views
				((TextView) findViewById(R.id.answerText)).setText(null);
							
				setupTextQuestionView((TextQuestion) question);
			}
		
			// Set up the choice question view
			else if (question instanceof ChoiceQuestion)
			{	
				// Reset the ChoiceQuestion views
				((RadioGroup) findViewById(R.id.questionChoices)).removeAllViews();
				((LinearLayout) findViewById(R.id.multipleSelectChoices)).removeAllViews();
				
				
				setupChoiceQuestionView((ChoiceQuestion) question);												
			}

			// Set up the likert scale question view
			else if (question instanceof LikertScaleQuestion)
			{
				// Reset the LikertScaleQuestion views
				((RadioGroup) findViewById(R.id.questionChoices)).removeAllViews();
				((TextView) findViewById(R.id.likertHigh)).setText(null);
				((TextView) findViewById(R.id.likertLow)).setText(null);
					
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
	}
	
	
	
	/**
	 * Sets up the View for the given TextQuestion.
	 * 
	 * @param tq	The TextQuestion to set up
	 */
	private void setupTextQuestionView(TextQuestion tq)
	{	
		// Remove other fields
		findViewById(R.id.questionChoices).setVisibility(View.GONE);
		findViewById(R.id.multipleSelectChoices).setVisibility(View.GONE);
		findViewById(R.id.likertLow).setVisibility(View.GONE);
		findViewById(R.id.likertHigh).setVisibility(View.GONE);
		
		// Make TextQuestion fields visible
		findViewById(R.id.answerText).setVisibility(View.VISIBLE);
		
		// Fill in pre-existing answer
		if (tq.Answer != null)
		{
			EditText textField = (EditText) findViewById(R.id.answerText);
			textField.setText(tq.Answer);
		}
	}
	
	
	
	/**
	 * Sets up the View for the given ChoiceQuestion. <br>
	 * This method handles both "single select" and "multiple select" questions.
	 * 
	 * @param cq	The ChoiceQuestion to set up
	 */
	private void setupChoiceQuestionView(ChoiceQuestion cq)
	{
		// Remove other fields
		findViewById(R.id.answerText).setVisibility(View.GONE);
		findViewById(R.id.likertLow).setVisibility(View.GONE);
		findViewById(R.id.likertHigh).setVisibility(View.GONE);
		
		
		// Set up multiple select ChoiceQuestion
		if (cq.multipleselect.compareToIgnoreCase("true") == 0)
		{
			findViewById(R.id.questionChoices).setVisibility(View.GONE);
			findViewById(R.id.multipleSelectChoices).setVisibility(View.VISIBLE);
			
			
			
			LinearLayout multipleSelectChoices = (LinearLayout) findViewById(R.id.multipleSelectChoices);
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
						// Add the CheckBox
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
			findViewById(R.id.multipleSelectChoices).setVisibility(View.GONE);
			findViewById(R.id.questionChoices).setVisibility(View.VISIBLE);
			
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
	 * Sets up the View for the given LikertScaleQuestion.
	 * 
	 * @param lsq	The LikertScaleQuestion to set up
	 */
	private void setupLikertQuestionView(LikertScaleQuestion lsq)
	{
		// Remove other fields
		findViewById(R.id.answerText).setVisibility(View.GONE);
		findViewById(R.id.multipleSelectChoices).setVisibility(View.GONE);
		
		// Make LikertScaleQuestion fields visible
		findViewById(R.id.likertHigh).setVisibility(View.VISIBLE);
		findViewById(R.id.likertLow).setVisibility(View.VISIBLE);
		findViewById(R.id.questionChoices).setVisibility(View.VISIBLE);
		
		
		if (lsq.labels.size() >= 2)
		{
			TextView lowText = (TextView) findViewById(R.id.likertLow);
			lowText.setText(lsq.labels.get(0));
			TextView highText = (TextView) findViewById(R.id.likertHigh);
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
	 * Attempts to cycle to the next question of the form. <br>
	 * Does nothing if currently on the final question of the form.
	 * 
	 * @param view	The View that called this method
	 */
	public void nextQuestion(View view)
	{	
		if (toast != null) toast.cancel();
		
		Utility.hideSoftKeyboard(this);
			
		if (questionNumber < form.questions.size() - 1)
		{
			// Save the answer and form
			saveAnswerToForm();			
			saveFormToDatabase();
				
			questionNumber++;
			question = form.questions.get(questionNumber);
			displayNewQuestion(question);
		}
		else
		{
			// On last question.			
			toast = Toast.makeText(this, "End of form", Toast.LENGTH_SHORT);
			toast.show();
		}
	}
	
	
	
	/**
	 * Attempts to cycle to the previous question of the form. <br>
	 * Does nothing if currently on question 0.
	 * 
	 * @param view The View that called this method
	 */
	public void prevQuestion(View view)
	{		
		if (toast != null) toast.cancel();
			
		Utility.hideSoftKeyboard(this);
		
		if (questionNumber > 0)
		{
			// Save the answer and form
			saveAnswerToForm();
			saveFormToDatabase();

				
			questionNumber--;
			question = form.questions.get(questionNumber);
			displayNewQuestion(question);
		}
		else
		{
			// On the first question, can't go to previous.				
			toast = Toast.makeText(this, "Beginning of form", Toast.LENGTH_SHORT);
			toast.show();
		}
	}	
	
	
	
	/**
	 * Saves the user-selected answer to the form object
	 * 
	 */
	private void saveAnswerToForm()
	{	
		// Save the TextQuestion answer
		if (question instanceof TextQuestion)
		{
			// Pull the answer
			EditText answer = (EditText) findViewById(R.id.answerText);
			
			// Store the answer
			if (answer != null)
			{
				form.questions.get(questionNumber).Answer = answer.getText().toString();
			}
		}
		
		// Save the ChoiceQuestion answer
		else if (question instanceof ChoiceQuestion)
		{
			ChoiceQuestion cq = ((ChoiceQuestion) question);
			
			// Save the multiple-select answer(s)
			if (cq.multipleselect.compareToIgnoreCase("true") == 0)
			{
				LinearLayout choices = (LinearLayout) findViewById(R.id.multipleSelectChoices);	
				int numBoxes = choices.getChildCount();
				
				String answer = "";
				
				
				// Go through each CheckBox
				for (int x = 0; x < numBoxes; x++)
				{
					CheckBox checkBox = (CheckBox) choices.getChildAt(x);
					
					// Add the answer to the string
					if (checkBox.isChecked())
					{
						answer += String.format("%s;", checkBox.getText().toString());
					}
				}
				
				form.questions.get(questionNumber).Answer = answer;
			}
			
			// Save the single-select answer
			else
			{			
				RadioGroup choices = (RadioGroup) findViewById(R.id.questionChoices);
				RadioButton answer = (RadioButton) findViewById(choices.getCheckedRadioButtonId());
			
				if (answer != null)
				{
					form.questions.get(questionNumber).Answer = answer.getText().toString();
				}
			}
		}
		
		// Save the LikertScaleQuestion answer
		else if (question instanceof LikertScaleQuestion)
		{
			RadioGroup choices = (RadioGroup) findViewById(R.id.questionChoices);
			RadioButton answer = (RadioButton) findViewById(choices.getCheckedRadioButtonId());
			
			if (answer != null)
			{
				form.questions.get(questionNumber).Answer = answer.getText().toString();
			}		
		}
	}

	
	
	/**
	 * Saves the form object to the database.
	 */
	public void saveFormToDatabase()
	{
		database.open();
		database.updateForm(form);
		database.close();
	}
	
	
	
	
	
	// The following navigate methods are for the implementation of the navbar layout
	public void navigateHome(View view)
	{	
		// Save the answer and form first
		saveAnswerToForm();
		saveFormToDatabase();
		
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
		saveFormToDatabase();

		if (toast != null) toast.cancel();
		
		Intent intent = new Intent(this, SubmitFormActivity.class);
		
		intent.putExtra("formObject", form);
		
		startActivity(intent);
		this.finish();
	}
}
