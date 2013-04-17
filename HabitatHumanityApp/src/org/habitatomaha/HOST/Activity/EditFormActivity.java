package org.habitatomaha.HOST.Activity;

import java.util.Calendar;

import org.habitatomaha.HOST.Helper.OnFlingGestureListener;
import org.habitatomaha.HOST.Helper.Utility;
import org.habitatomaha.HOST.Model.ChoiceQuestion;
import org.habitatomaha.HOST.Model.Error;
import org.habitatomaha.HOST.Model.Error.Severity;
import org.habitatomaha.HOST.Model.Form;
import org.habitatomaha.HOST.Model.LikertScaleQuestion;
import org.habitatomaha.HOST.Model.Question;
import org.habitatomaha.HOST.Model.TextQuestion;
import org.habitatomaha.HOST.Model.Repository.ErrorLog;
import org.habitatomaha.HOST.Model.Repository.OSTDataSource;

import org.habitatomaha.HOST.R;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;



@SuppressLint("NewApi")
public class EditFormActivity extends Activity
{
	private static EditFormActivity currentInstance;		//The current instance of this Activity
	private Form form; 					// The Form object that is being processed by the Activity
	private Question question; 			// The Question that is being displayed by the Activity
	private Integer questionNumber; 	// The current Question's number
	private Toast toast; 				// The Toast object used for any Toasts
	private OSTDataSource database; 	// The interface for interacting with the SQLite Database
	
	private String storedAnswer;
	
	private long formID;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		// Set this instance and attempt to end any instance of SubmitFormActivity
		currentInstance = this;
		if (SubmitFormActivity.getInstance() != null)
		{
			SubmitFormActivity.getInstance().finish();
		}
		
		
		// Set up database object for future use
		database = new OSTDataSource(this);
			
		database.open();
		// This is the initial call to the Activity
		if (savedInstanceState == null)
		{
			
			// Receive the form object and question number
			formID =  getIntent().getExtras().getLong("formID");
			questionNumber = (Integer) getIntent().getExtras().get("questionNumber");
			
			Log.v("ryan_debug", String.format("Received formID %d in Screen 2", formID));
			
			form = database.getFormById(formID);			
		}	
		// This is the activity restoring itself (from something like a screen rotation)
		else
		{
			formID = savedInstanceState.getLong("formID");
			questionNumber = savedInstanceState.getInt("questionNumber");
			
			form = database.getFormById(formID);
		}
		database.close();
		
		
		Log.v("ryan_debug", String.format("Received form %d in Screen 2", form.meta.form_id));
		
		
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
				((ProgressBar) findViewById(R.id.formProgressBar)).setMax(form.questions.size() - 1);
				((ProgressBar) findViewById(R.id.formProgressBar)).setProgress(questionNumber);
				
				findViewById(R.id.question_layout).setOnTouchListener(
					new OnFlingGestureListener() 
					{
						@Override
						public void onTopToBottomSwipe()
						{
							return;
						}
						@Override
						public void onBottomToTopSwipe()
						{
							return;
						}
						@Override
						public void onLeftToRightSwipe()
						{
							prevQuestion(null);
						}
						@Override
						public void onRightToLeftSwipe()
						{
							nextQuestion(null);
						}
					}
				);
				

				((TextView) findViewById(R.id.formTitle)).setText(form.meta.name);
				((Button) findViewById(R.id.navbar_edit_button)).setBackgroundColor(Color.parseColor("#66CCFF"));
				((Button) findViewById(R.id.navbar_home_button)).setBackgroundColor(Color.parseColor("#888888"));
				((Button) findViewById(R.id.navbar_submit_button)).setBackgroundColor(Color.parseColor("#888888"));					
				
				displayNewQuestion(question);
			}
			else
			{
				// This block should ideally never be entered
				// Getting here means that one of the Questions in the Form was null.
				// This is probably not a user-caused error. It indicates a problem in the code.
				ErrorLog.log(getInstance(), new Error("Model Error", "Received a null question in EditFormActivity", Severity.Normal));
				this.finish();
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
	protected void onSaveInstanceState(Bundle savedInstanceState)
	{
		super.onSaveInstanceState(savedInstanceState);
		
		saveAnswerToForm();
		savedInstanceState.putLong("formID", formID);
		savedInstanceState.putInt("questionNumber", questionNumber);
	}
	

	
	
	/**
	 * Begins the setup for displaying a new question. <br>
	 * Calls one of three helper methods, one for each type of question.
	 * 
	 * @param question	The new question to display
	 */
	@SuppressLint("NewApi")
	private void displayNewQuestion(Question question)
	{
		if (question != null)
		{				
			// Set the title bar
			ActionBar actionBar = getActionBar();
			actionBar.setTitle(String.format("HOST - %s", form.meta.name));
			
			
			// Set the question view and question text (same for all three types)	
			((TextView) findViewById(R.id.questionNumber)).setText(String.format("Question %d", questionNumber + 1));
			((TextView) findViewById(R.id.questionText)).setText(question.Text);
			
			// Only show the helpText if it was changed in the admin tool
			if (question.HelpText != null)
			{
				if (question.HelpText.compareToIgnoreCase("enter help text here") != 0)
				{
					((TextView) findViewById(R.id.questionHelpText)).setText(question.HelpText);
				}
				else
				{
					((TextView) findViewById(R.id.questionHelpText)).setText("");
				}
			}
			
			
			
			
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
				// This means that a Question was in the form that was not an instance of the three types of Question
				// This is probably not an error caused by the user. It indicates a problem in the code.
				ErrorLog.log(this, new Error("Model Error", 
											"Received a question of unknown type.\n" +
											"Ensure that a Question is always an instance of TextQuestion, ChoiceQuestion, or LikertScaleQuestion.\n", 
											Severity.Critical));
			
				toast = Toast.makeText(this, "Unknown question type", Toast.LENGTH_SHORT);
				toast.show();
				this.prevQuestion(null);
			}
			
			
			
			// Store the answer in memory
			if (question.Answer != null && question.Answer.compareTo("") != 0)
			{
				storedAnswer = question.Answer;
			}
			else
			{
				storedAnswer = null;
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
		findViewById(R.id.answerText).setVisibility(View.GONE);
		findViewById(R.id.dateText).setVisibility(View.GONE);
		findViewById(R.id.questionChoices).setVisibility(View.GONE);
		findViewById(R.id.multipleSelectChoices).setVisibility(View.GONE);
		findViewById(R.id.otherText).setVisibility(View.GONE);
		findViewById(R.id.likertLow).setVisibility(View.GONE);
		findViewById(R.id.likertHigh).setVisibility(View.GONE);
		
		
		// Set up for data types
		EditText answerText = (EditText) findViewById(R.id.answerText);
		// Single-line
		if (question.FieldType.compareToIgnoreCase("single") == 0)
		{
			answerText.setVisibility(View.VISIBLE);	
			answerText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
		}
		
		// Multi-line
		else if (question.FieldType.compareToIgnoreCase("multi") == 0)
		{
			answerText.setVisibility(View.VISIBLE);
			answerText = (EditText) findViewById(R.id.answerText);
			answerText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
			answerText.setSingleLine(false);
		}
		
		// Number
		else if (question.FieldType.compareToIgnoreCase("number") == 0)
		{
			findViewById(R.id.answerText).setVisibility(View.VISIBLE);

			answerText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
		}
		
		// Date
		else if (question.FieldType.compareToIgnoreCase("date") == 0)
		{		
			TextView dateText = (TextView) findViewById(R.id.dateText);
			dateText.setVisibility(View.VISIBLE);
			dateText.setText("00/00/0000");
			
			dateText.setOnClickListener(	new View.OnClickListener()
											{
												@Override
												public void onClick(View v)
												{
													DatePickerFragment.setDateToday();
													showDatePicker(null);
												}
											}
										);
			
			// Fill in pre-existing answer
			if (tq.Answer != null && tq.Answer.compareTo("") != 0)
			{
				DatePickerFragment.setDate(tq.Answer);
				dateText.setText(tq.Answer);
			}	
		}
		
		
		// Fill in pre-existing answer
		if (tq.Answer != null)
		{		
			answerText.setText(tq.Answer);
		}	
	}
	
	
	
	/**
	 * Sets up the View for the given ChoiceQuestion. <br>
	 * This method handles both "single select" and "multiple select" questions.
	 * 
	 * @param cq	The ChoiceQuestion to set up
	 */
	private void setupChoiceQuestionView(final ChoiceQuestion cq)
	{
		// Remove other fields
		findViewById(R.id.dateText).setVisibility(View.GONE);
		findViewById(R.id.answerText).setVisibility(View.GONE);
		findViewById(R.id.likertLow).setVisibility(View.GONE);
		findViewById(R.id.likertHigh).setVisibility(View.GONE);
		findViewById(R.id.otherText).setVisibility(View.GONE);
		
		EditText otherText = (EditText) findViewById(R.id.otherText);
		
		// Set up for data types
		if (question.FieldType.compareToIgnoreCase("single") == 0)
		{
			otherText.setInputType(InputType.TYPE_CLASS_TEXT);
		}
		else if (question.FieldType.compareToIgnoreCase("multi") == 0)
		{
			otherText.setInputType(InputType.TYPE_CLASS_TEXT);
		}
		else if (question.FieldType.compareToIgnoreCase("number") == 0)
		{
			otherText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
		}
		else if (question.FieldType.compareToIgnoreCase("date") == 0)
		{
			otherText.setInputType(InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_DATE);
		}
		
		
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
						
						// Button style
						checkBox.setTextSize(20);
						
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

			// Add "other" option
			if (cq.other.compareToIgnoreCase("true") == 0)
			{
				ErrorLog.log(this, new Error("Question Error", String.format("Location:\nQuestion %d in template: %s\n\nChoiceQuestions should not have both 'multiple select' and 'other' enabled in the admin tool", questionNumber + 1, form.meta.name), Severity.Normal));
				
				// TODO Possibly old code. Keep for now
				/*
				checkBox = new CheckBox(this);
				checkBox.setText("Other");
				
				// Button style
				checkBox.setTextSize(20);
				
				checkBox.setOnCheckedChangeListener(
					new OnCheckedChangeListener()
					{
						@Override
						public void onCheckedChanged(CompoundButton checkBox, boolean checkedState)
						{
							if (checkedState)
							{
								EditText otherText = (EditText) findViewById(R.id.otherText);
								otherText.setVisibility(View.VISIBLE);				
								otherText.setText("");
							}
							else
							{
								findViewById(R.id.otherText).setVisibility(View.GONE);
							}
							return;
						}
					}
						
				);
				
				multipleSelectChoices.addView(checkBox);
				
				// Check if there is already an "other" answer pre-filled
				if (answers != null)
				{
					for (String answer : answers)
					{
						boolean otherFlag = true;
						
						for (String option: cq.options)
						{
							if (option.compareTo(answer) == 0)
							{
								otherFlag = false;
							}
						}
						
						// If the answer does not match one of the options, fill it
						if (otherFlag)
						{
							if (answer.compareTo("") == 0)
							{}
							else
							{
								checkBox.performClick();
								((EditText) findViewById(R.id.otherText)).setText(answer);
								break;
							}
						}
					}
				} */
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
						
						// Button style
						button.setTextSize(20);
						
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
			
			// Add "other" option
			if (cq.other.compareToIgnoreCase("true") == 0)
			{
				button = new RadioButton(this);
				button.setText("Other");
				
				// Button style
				button.setTextSize(20);
				
				button.setOnCheckedChangeListener(
					new OnCheckedChangeListener()
					{
						@Override
						public void onCheckedChanged(CompoundButton radioButton, boolean checkedState)
						{
							if (checkedState)
							{
								findViewById(R.id.otherText).setVisibility(View.VISIBLE);
								((EditText) findViewById(R.id.otherText)).setText("");
							}
							else
							{
								findViewById(R.id.otherText).setVisibility(View.GONE);
							}
							return;
						}
					}	
				);
				
				buttons.addView(button);
				
				
				if (cq.Answer != null)
				{
					boolean otherFlag = true;
					for (String option : cq.options)
					{
						if (option.compareTo(cq.Answer) == 0)
						{
							otherFlag = false;
						}
					}
					
					if (otherFlag)
					{
						if (cq.Answer != null)
						{
							if (cq.Answer.compareTo("") != 0)
							{
								// If the answer does not match one of the options
								button.performClick();
								((EditText) findViewById(R.id.otherText)).setText(cq.Answer);
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
		findViewById(R.id.dateText).setVisibility(View.GONE);
		findViewById(R.id.answerText).setVisibility(View.GONE);
		findViewById(R.id.multipleSelectChoices).setVisibility(View.GONE);
		findViewById(R.id.otherText).setVisibility(View.GONE);
		
		
		// Make LikertScaleQuestion fields visible
		findViewById(R.id.likertHigh).setVisibility(View.VISIBLE);
		findViewById(R.id.likertLow).setVisibility(View.VISIBLE);
		findViewById(R.id.questionChoices).setVisibility(View.VISIBLE);
		
		// Set up for data types
		if (question.FieldType.compareToIgnoreCase("single") == 0)
		{}
		else if (question.FieldType.compareToIgnoreCase("multi") == 0)
		{}
		else if (question.FieldType.compareToIgnoreCase("number") == 0)
		{}
		else if (question.FieldType.compareToIgnoreCase("date") == 0)
		{}
		
		
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
			((ProgressBar) findViewById(R.id.formProgressBar)).setProgress(questionNumber);
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
			((ProgressBar) findViewById(R.id.formProgressBar)).setProgress(questionNumber);
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
	 */
	private void saveAnswerToForm()
	{	
		// Save the TextQuestion answer
		if (question instanceof TextQuestion)
		{
			// Save a date
			if (question.FieldType.compareToIgnoreCase("date") == 0)
			{
				// Pull the answer
				TextView answer = (TextView) findViewById(R.id.dateText);
				
				if (answer != null)
				{
					// Store the answer
					if (answer.getText().toString().compareToIgnoreCase("setdate") != 0)
					{
						form.questions.get(questionNumber).Answer = answer.getText().toString();
					}
				}
			}
			// Save other stuff
			else
			{
				// Pull the answer
				EditText answer = (EditText) findViewById(R.id.answerText);
				
				// Store the answer
				if (answer != null)
				{
					form.questions.get(questionNumber).Answer = answer.getText().toString();
				}
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
						if (checkBox.getText().toString().compareToIgnoreCase("other") == 0)
						{
							answer += String.format("%s;", ((EditText) findViewById(R.id.otherText)).getText().toString());
						}
						else
						{
							answer += String.format("%s;", checkBox.getText().toString());
						}
					}
				}
				
				if (answer.compareTo("") == 0)
				{
					form.questions.get(questionNumber).Answer = null;
				}
				else
				{
					form.questions.get(questionNumber).Answer = answer;
				}
			}
			
			// Save the single-select answer
			else
			{			
				RadioGroup choices = (RadioGroup) findViewById(R.id.questionChoices);
				RadioButton answer = (RadioButton) findViewById(choices.getCheckedRadioButtonId());
			
				if (answer != null)
				{
					if (answer.getText().toString().compareToIgnoreCase("other") == 0)
					{
						form.questions.get(questionNumber).Answer = ((EditText) findViewById(R.id.otherText)).getText().toString();
					}
					else
					{
						form.questions.get(questionNumber).Answer = answer.getText().toString();
					}
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
		
		
		// Don't save 00/00/0000 as an answer to date questions
		if (form.questions.get(questionNumber).Answer != null)
		{		
			if (form.questions.get(questionNumber).Answer.compareTo("00/00/0000") == 0)
			{
				form.questions.get(questionNumber).Answer = null;
			}
		}
	}

	
	
	/**
	 * Saves the form object to the database.
	 */
	public void saveFormToDatabase()
	{
		database.open();
		
		// Avoid the database write if nothing changed
		if (storedAnswer == null && form.questions.get(questionNumber).Answer != null)
		{
				database.updateForm(form);
		}
		else if (storedAnswer != null && form.questions.get(questionNumber).Answer != null)
		{
			if (storedAnswer.compareToIgnoreCase(form.questions.get(questionNumber).Answer) != 0)
			{
				database.updateForm(form);
			}
		}
		
		database.close();
	}
	
	
	
	/**
	 * Returns the most recent instance of DisplayQuestionActivity
	 * 
	 * @return	The most recent instance of DisplayQuestionActivity
	 */
	public static EditFormActivity getInstance()
	{
		return currentInstance;
	}
	
	
	
	/**
	 * Display the datePicker for input
	 * @param view	The View that called this method
	 */
	public void showDatePicker(View view)
	{
		String presetDate;
		
		if (form.questions.get(questionNumber).Answer != null)
		{
			presetDate = form.questions.get(questionNumber).Answer;
		}
		else
		{
			presetDate = null;
		}
		
		DatePickerFragment newFragment = new DatePickerFragment();
		newFragment.setDate(presetDate);
		newFragment.show(getFragmentManager(), "datePicker");
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
		
		intent.putExtra("formID", formID);
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		
		startActivity(intent);
		this.finish();
	}
	
	
	
	
	
	
	public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener
	{
		private static String presetDate;
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState)
		{
			int day, month, year;
			
			if (presetDate != null)
			{
				String[] datePieces = presetDate.split("/");
				
				month = Integer.parseInt(datePieces[0]) - 1;
				day = Integer.parseInt(datePieces[1]);
				year = Integer.parseInt(datePieces[2]); 
			}
			else
			{
				Calendar calendar = Calendar.getInstance();
				year = calendar.get(Calendar.YEAR);
				month = calendar.get(Calendar.MONTH);
				day = calendar.get(Calendar.DAY_OF_MONTH);
			}
			
			return new DatePickerDialog(getInstance(), this, year, month, day);
		}
		
		@Override
		public void onDateSet(DatePicker view, int year, int month, int day)
		{
			TextView dateText = (TextView) getInstance().findViewById(R.id.dateText);
			dateText.setText(String.format("%d/%d/%d", month + 1, day, year));
			
			getInstance().saveAnswerToForm();
		}
		
		
		/**
		 * Provides the DatePicker with a preselected date.<br>
		 * The date should be a String in the form "MM/DD/YYYY"
		 * 
		 * @param date	The date to preselect in the DatePicker
		 */
		public static void setDate(String date)
		{
			if (date != null)
			{
				presetDate = date;
			}
		}
		
		
		
		/**
		 * Sets the picker to today's date
		 */
		public static void setDateToday()
		{
			Calendar calendar = Calendar.getInstance();
			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH);
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			
			
			presetDate = String.format("%d/%d/%d", month + 1, day, year);
		}
	}
}


