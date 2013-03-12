package com.example.habitathumanityapp;

import com.example.habitathumanityapp.datasource.OSTDataSource;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class SubmitFormActivity extends Activity
{
	Form form;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
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
		// TODO Upload the form to SharePoint
		// TODO Delete form from database?
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
		OSTDataSource database = new OSTDataSource(this);
		database.open();
		database.removeFormById(form.meta.form_id);
		database.close();
		
		Toast.makeText(this, "Form deleted", Toast.LENGTH_SHORT).show();
		form = null;
		
		findViewById(R.id.submit_upload).setVisibility(View.GONE);
		findViewById(R.id.submit_save).setVisibility(View.GONE);
		findViewById(R.id.submit_discard).setVisibility(View.GONE);
		findViewById(R.id.navbar_edit_button).setVisibility(View.GONE);
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
