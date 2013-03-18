package com.example.habitathumanityapp;

import java.util.concurrent.ExecutionException;

import com.example.habitathumanityapp.datasource.OSTDataSource;
import com.example.habitathumanityapp.tasks.uploadFormToSharePoint;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class SubmitFormActivity extends Activity
{
	Form form;
	Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		context = this;
		
		// Receive the form object
		form = (Form) getIntent().getExtras().get("formObject");
		
		setContentView(R.layout.activity_submit_form);
		findViewById(R.id.navbar_submit_button).setVisibility(View.GONE);
	}

	
	/**
	 * Uploads the form to SharePoint.
	 * 
	 * @param view The View that called this method
	 */
	public void upload(View view)
	{
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
										AsyncTask<Form, Void, String[]> task = new uploadFormToSharePoint();
										
										//Start the task
										task.execute(form);
										try {
											//Wait for the task to finish and get it's response
											String[] response = task.get();
											Toast.makeText(context, response[1], Toast.LENGTH_LONG).show();

											//reponse of -1 == error
											//response of 0 == success
											if (response[0] == "0")
											{
												discard(null);
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
	 * Deletes the form from the database.
	 * 
	 * @param view	The View that called this method
	 */
	public void discard(View view)
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
										OSTDataSource database = new OSTDataSource(context);
										database.open();
										database.removeFormById(form.meta.form_id);
										database.close();
										
										Toast.makeText(context, "Form removed from database", Toast.LENGTH_SHORT).show();
										form = null;
										
										findViewById(R.id.submit_upload).setVisibility(View.GONE);
										findViewById(R.id.submit_save).setVisibility(View.GONE);
										findViewById(R.id.submit_discard).setVisibility(View.GONE);
										findViewById(R.id.navbar_edit_button).setVisibility(View.GONE);
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
	
	
	
	
	// The following navigate methods are for the implementation of the navbar layout
	public void navigateHome(View view)
	{	
		this.finish();
	}
	public void navigateEdit(View view)
	{
		Intent intent = new Intent(this, DisplayQuestionActivity.class);
		intent.putExtra("formObject", form);
		startActivity(intent);
		this.finish();
	}
	public void navigateSubmit(View view)
	{
		return;
	}
}
