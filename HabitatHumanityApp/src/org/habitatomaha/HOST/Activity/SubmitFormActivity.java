package org.habitatomaha.HOST.Activity;

import java.util.concurrent.ExecutionException;

import org.habitatomaha.HOST.AsyncTask.UploadAllForms;
import org.habitatomaha.HOST.AsyncTask.UploadForm;
import org.habitatomaha.HOST.Helper.Utility;
import org.habitatomaha.HOST.Model.Form;
import org.habitatomaha.HOST.Model.Repository.OSTDataSource;

import org.habitatomaha.HOST.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class SubmitFormActivity extends Activity
{
	private static SubmitFormActivity currentInstance;	// The current instance of the Activity
	
	private Form form;	// The form object being processed by the activity	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		// Set this instance and attempt to end any instance of DisplayQuestionActivity
		currentInstance = this;	
		if (EditFormActivity.getInstance() != null)
		{
			EditFormActivity.getInstance().finish();
		}

		// Receive the form object
		form = (Form) getIntent().getExtras().get("formObject");
	
		setContentView(R.layout.activity_submit_form);
		
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
	 * Uploads the form to SharePoint.
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
									public void onClick(DialogInterface dialog, int id)
									{
										//Had to suppress this warning, not sure what the deal is
										@SuppressWarnings("unchecked")
										AsyncTask<Form, Void, String[]> task = new UploadForm();
										
										//Start the task
										task.execute(form);
										try {
											//Wait for the task to finish and get it's response
											String[] response = task.get();
											Toast.makeText(getInstance(), response[1], Toast.LENGTH_LONG).show();

											//reponse of -1 == error
											//response of 0 == success
											if (response[0].compareTo("0") == 0)
											{
												discard();
												getInstance().finish();
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
	 * This is just a placebo for people who don't know that the form is already saved.
	 * 
	 * @param view	The View that called this method
	 */
	public void save(View view)
	{		
		Toast.makeText(this, "Form saved", Toast.LENGTH_SHORT).show();	
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
		adb.setPositiveButton("Discard", 
								new DialogInterface.OnClickListener()
								{
									public void onClick(DialogInterface dialog, int id)
									{
										discard();
										getInstance().finish();
										return;
									}
								});
		adb.setNegativeButton("Keep", new DialogInterface.OnClickListener()
										{
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
		findViewById(R.id.submit_save).setVisibility(View.GONE);
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
	
	
	
	/**
	 * Called from the menu. <br>
	 * Launches the task that attempts to upload all forms on the device to SharePoint
	 * 
	 * @param menu	The menu that called this method
	 */
	public void uploadAllForms(MenuItem menu)
	{
		//If there is no connectivity, display a popup and return
		if (!Utility.isNetworkAvailable(this))
		{
			Utility.displayNetworkUnavailableDialog(this);
			return;
		}
		
		new UploadAllForms(this).execute();
	
		findViewById(R.id.navbar_edit_button).setVisibility(View.GONE);
		findViewById(R.id.submit_save).setVisibility(View.GONE);
		findViewById(R.id.submit_upload).setVisibility(View.GONE);
		findViewById(R.id.submit_discard).setVisibility(View.GONE);
	}
	
	
		
	// The following navigate methods are for the implementation of the navbar layout
	public void navigateHome(View view)
	{	
		this.finish();
	}
	public void navigateEdit(View view)
	{
		Intent intent = new Intent(this, EditFormActivity.class);
		
		intent.putExtra("formObject", form);
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		
		startActivity(intent);
		this.finish();
	}
	public void navigateSubmit(View view)
	{
		return;
	}
}
