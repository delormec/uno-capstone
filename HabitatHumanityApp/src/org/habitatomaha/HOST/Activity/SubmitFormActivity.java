package org.habitatomaha.HOST.Activity;

import java.util.concurrent.ExecutionException;

import org.habitatomaha.HOST.AsyncTask.UploadForm;
import org.habitatomaha.HOST.Helper.Utility;

import org.habitatomaha.HOST.Model.Form;
import org.habitatomaha.HOST.Model.Repository.ErrorLog;
import org.habitatomaha.HOST.Model.Repository.OSTDataSource;
import org.habitatomaha.HOST.Model.Error;
import org.habitatomaha.HOST.Model.Error.Severity;
import org.habitatomaha.HOST.Model.Question;

import org.habitatomaha.HOST.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SubmitFormActivity extends Activity
{
	private static SubmitFormActivity currentInstance;	// The current instance of the Activity
	
	private Form form;	// The form object being processed by the activity	
	private long formID;
	
	private OSTDataSource database;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		database = new OSTDataSource(this);
		
		// Set this instance and attempt to end any instance of DisplayQuestionActivity
		currentInstance = this;	
		if (EditFormActivity.getInstance() != null)
		{
			EditFormActivity.getInstance().finish();
		}

		
		// Receive the form object
		formID = getIntent().getExtras().getLong("formID");
		database.open();
		form = database.getFormById(formID);
		database.close();
		
		
		setContentView(R.layout.activity_submit_form);
		
		
		LinearLayout body = (LinearLayout) findViewById(R.id.submit_info);
		
		// Name of the form
		TextView templateName = new TextView(this);
		templateName.setText(form.meta.name);
		templateName.setTextSize(25);
		
		
		// List of unanswered questions
		TextView unansweredQuestions = new TextView(this);
		
		String unans = "Unanswered questions:\n";
		int x = 0;
		for (Question q : form.questions)
		{
			if (q.Answer == null || q.Answer.compareTo("") == 0 || q.Answer.compareTo("00/00/0000") == 0)
			{
				unans += String.format("%d ", x + 1);
			}
			x++;
		}
		
		unansweredQuestions.setText(unans);
		unansweredQuestions.setTextSize(20);
		
		
		
		
		body.addView(templateName);
		body.addView(unansweredQuestions);
		
		
		((Button) findViewById(R.id.navbar_submit_button)).setBackgroundColor(Color.parseColor("#66CCFF"));
		((Button) findViewById(R.id.navbar_edit_button)).setBackgroundColor(Color.parseColor("#888888"));
		((Button) findViewById(R.id.navbar_home_button)).setBackgroundColor(Color.parseColor("#888888"));
	
	}


	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_submit_form, menu);
		return true;
	}
	
	
	
	/**
	 * Attempts to upload the Form to SharePoint.
	 * 
	 * @param view The View that called this method
	 */
	public void upload(View view)
	{
		//If there is no connectivity, display a popup and return
		if (!Utility.isNetworkAvailable(this))
		{
			Utility.displayNetworkUnavailableDialog(this);
			return;
		}
		
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle("Confirm Upload");
		adb.setMessage("If the upload is successful, the form will be removed from your device.");
		adb.setCancelable(false);
		adb.setPositiveButton("Upload", 
								new DialogInterface.OnClickListener()
								{
									@Override
									public void onClick(DialogInterface dialog, int id)
									{
										//Had to suppress this warning, not sure what the deal is
										@SuppressWarnings("unchecked")
										AsyncTask<Form, Void, String[]> task = new UploadForm(currentInstance);
										
										
										//Start the task
										task.execute(form);
										try {
											//Wait for the task to finish and get its response
											String[] response = task.get();											
											Toast.makeText(getInstance(), response[1], Toast.LENGTH_LONG).show();

											//response of -1 == error
											//response of 0 == success
											if (response[0].compareTo("0") == 0)
											{
												discard();
												getInstance().finish();
											}
											else
											{
												// Log the upload error
												ErrorLog.log(getInstance(), new Error("Form Upload Error", response[1], Severity.Minor));
											}
											
										} catch (InterruptedException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										} catch (ExecutionException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										
										return;
									}
								});
		adb.setNegativeButton("Wait", new DialogInterface.OnClickListener()
										{
											@Override
											public void onClick(DialogInterface dialog, int id)
											{
												return;
											}
										});
		
		AlertDialog alertDialog = adb.create();
		alertDialog.show();
		
		return;
	}
	
	
	
	/**
	 * Confirms the user's intent discard before discarding the Form
	 * 
	 * @param view	The View that called this method
	 */
	public void discardConfirm(View view)
	{
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle("Confirm Discard");
		adb.setMessage("Are you sure you want to discard this form?");
		adb.setCancelable(false);
		adb.setPositiveButton("Discard", 	new DialogInterface.OnClickListener()
											{
												@Override
												public void onClick(DialogInterface dialog, int id)
												{
													discard();
													getInstance().finish();
													return;
												}
											}
										);
		adb.setNegativeButton("Keep", 	new DialogInterface.OnClickListener()
										{
											@Override
											public void onClick(DialogInterface dialog, int id)
											{
												return;
											}
										}
									);
		
		AlertDialog alertDialog = adb.create();
		alertDialog.show();
		
		return;
	}
	
	
	
	/**
	 * Removes the form associated with this Activity from the database
	 */
	public void discard()
	{
		OSTDataSource database = new OSTDataSource(this);
		database.open();
		database.removeFormById(form.meta.form_id);
		database.close();
		
		Toast.makeText(this, "Form removed from device", Toast.LENGTH_SHORT).show();
		form = null;
		
		findViewById(R.id.submit_upload).setVisibility(View.GONE);
		findViewById(R.id.submit_discard).setVisibility(View.GONE);
		findViewById(R.id.navbar_edit_button).setVisibility(View.GONE);
		
		return;
	}
	
	
	
	/**
	 * Returns the most recent instance of SubmitFormActivity
	 * 
	 * @return	The most recent instance of SubmitFormActivity
	 */
	public static SubmitFormActivity getInstance()
	{
		return currentInstance;
	}
	
	
		
	// The following navigate methods are for the implementation of the navbar layout
	public void navigateHome(View view)
	{	
		this.finish();
	}
	public void navigateEdit(View view)
	{
		Intent intent = new Intent(this, EditFormActivity.class);
		
		intent.putExtra("formID", formID);
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		
		startActivity(intent);
		this.finish();
	}
	public void navigateSubmit(View view)
	{
		return;
	}
	
	public Form getForm()
	{
		return form;
	}
	
	public void setForm(Form form)
	{
		form = this.form;
	}
}
