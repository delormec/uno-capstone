package com.example.habitathumanityapp;

import java.util.ArrayList;
import java.util.List;

import com.example.habitathumanityapp.datasource.OSTDataSource;
import com.example.habitathumanityapp.tasks.downloadAllTemplates;


import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainMenuActivity extends Activity {
	
	private Spinner templateSpinner, templateGroupSpinner, formSpinner;
	private TextView selectedFormBox;
	private int templ_id;
	private String templ_grp;
	private int cntr = 1;
	
	void showToast(CharSequence msg){
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		
		findViewById(R.id.navbar_home_button).setVisibility(View.GONE);
	}
	
	/** Populates the first spinner with a list of template groups.
	 *  Sets a listener to the spinner to handle input selections.
	 */
	public void populateTemplateGroupSpinner() {
		templateGroupSpinner = (Spinner)findViewById(R.id.typeMenu);
		List<String> templateGroupList = new ArrayList<String>();
		
		// Connect to the database and get a list of all of the templates corresponding to the group.
		OSTDataSource ostDS = new OSTDataSource(this);
		ostDS.open();
		templateGroupList = ostDS.getAllTemplateGroups();
		
		// Add a blank entry into the beginning of the templateList
		templateGroupList.add(0, "All Groups");
		
		// Fill the drop down boxes with the templates.
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, templateGroupList);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		templateGroupSpinner.setAdapter(dataAdapter);
		
		ostDS.close();
		
	}
	
	/** Adds a listener to the first spinner handling any changes to the selected item in the spinner.
	 *  Populates template spinner based on the selection.
	 */
	public void addTemplateGroupSpinnerListener() {
		templateGroupSpinner = (Spinner)findViewById(R.id.typeMenu);
		templateGroupSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				String templateGroup = parent.getItemAtPosition(pos).toString();
				
				if(pos == 0) //If selection is "AllGroups", populate templateSpinner with all templates.
				{
					populateTemplateSpinner();
					templ_grp = "";
				}
				else // Populate templateSpinner with filtered results by template group.
				{
					populateTemplateSpinner(templateGroup);
					templ_grp = templateGroup;
				}

				addTemplateSpinnerListener();
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
			
		});
	}
	/** Populates the second spinner with a list of all templates..
	 *  Sets a listener on the spinner to handle user selections.
	 */
	public void populateTemplateSpinner(){
		templateSpinner = (Spinner)findViewById(R.id.templateMenu);
		List<MyData> templateList = new ArrayList<MyData>();
		
		// Connect to the database and get a list of all of the template groups.
		OSTDataSource ostDS = new OSTDataSource(this);
		ostDS.open();
		templateList = ostDS.getAllTemplateInfo();
		
		// Add a "All Forms" selection.
		MyData allForms = new MyData("All Forms", -1);
		templateList.add(0,allForms);

		
		// Fill the drop down boxes with the template groups.
		ArrayAdapter<MyData> dataAdapter = new ArrayAdapter<MyData>(this, android.R.layout.simple_spinner_dropdown_item, templateList);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		templateSpinner.setAdapter(dataAdapter);
		
		ostDS.close();		
	}
	
	/** Populates the second spinner with a list of templates filtered by templateGroup.
	 *  Sets a listener on the second spinner to handle user selection.
	 * @param templateGroup
	 */
	public void populateTemplateSpinner(String templateGroup) {
		
		templateSpinner = (Spinner)findViewById(R.id.templateMenu);
		List<MyData> templateList = new ArrayList<MyData>();
		
		// Connect to the database and get a list of all of the template groups.
		OSTDataSource ostDS = new OSTDataSource(this);
		ostDS.open();
		templateList = ostDS.getAllTemplateInfoByGroup(templateGroup);

		MyData allForms = new MyData("All " + templateGroup + " Forms", -1);
		templateList.add(0,allForms);
		
		// Fill the drop down boxes with the template groups.
		ArrayAdapter<MyData> dataAdapter = new ArrayAdapter<MyData>(this, android.R.layout.simple_spinner_dropdown_item, templateList);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		templateSpinner.setAdapter(dataAdapter);
		
		ostDS.close();
	}
	
	/** Adds a listener to the second spinner handling any changes to the selected item in the spinner.
	 *  Populates form spinner based on the selection.
	 */
	public void addTemplateSpinnerListener() {
		templateSpinner = (Spinner)findViewById(R.id.templateMenu);
		templateSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				
					//String template = parent.getItemAtPosition(pos).toString();
					MyData template = (MyData) parent.getItemAtPosition(pos);
					showToast("TemplateID =  " + template.getValue());
					templ_id = template.getValue();
					
					if(templ_id == -1) //If All ... Forms is selected, populate "all" forms
					{
						populateFormSpinner();
					}
					else //Populate forms based on the template id.
					{
						populateFormSpinner(template.getValue());
					}
					addFormSpinnerListener();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
	}
	
	/** Populates the third spinner with either all forms or filtered on template group.
	 *  Adds a listener to spinner to handle user selections.
	 */
	public void populateFormSpinner() {
		formSpinner = (Spinner)findViewById(R.id.formMenu);
		List<MyData> formList = new ArrayList<MyData>();
		
		// Connect to the database and get a list of all of the forms corresponding to the selected template.
		OSTDataSource ostDS = new OSTDataSource(this);
		ostDS.open();
		if( templ_grp == "") // If there is no template group selected, get all Forms.
		{
			formList = ostDS.getAllFormInfo();
		}
		else // Filter groups based on selected template group.
		{
			formList = ostDS.getAllFormInfoByTemplateGroup(templ_grp);
		}

		// Fill the drop down boxes with completed forms.
		ArrayAdapter<MyData> dataAdapter = new ArrayAdapter<MyData>(this, android.R.layout.simple_spinner_dropdown_item, formList);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		formSpinner.setAdapter(dataAdapter);
		
		ostDS.close();
		
	}
	/** Populates the form spinner based on the selected template_id.
	 *  Adds a listener to the forms spinner.
	 * @param template_id
	 */
	public void populateFormSpinner(int template_id) {
		formSpinner = (Spinner)findViewById(R.id.formMenu);
		List<MyData> templateList = new ArrayList<MyData>();
		
		// Connect to the database and get a list of all of the forms corresponding to the selected template.
		OSTDataSource ostDS = new OSTDataSource(this);
		ostDS.open();
		templateList = ostDS.getAllFormInfoByTemplateId(template_id);
		
		//Add option to create new form.
		MyData createNew = new MyData("Create new form...", -1);
		templateList.add(0,createNew);

		// Fill the drop down boxes with completed forms.
		ArrayAdapter<MyData> dataAdapter = new ArrayAdapter<MyData>(this, android.R.layout.simple_spinner_dropdown_item, templateList);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		formSpinner.setAdapter(dataAdapter);
		
		ostDS.close();
	}

	/** Adds a listener to the third spinner handling any changes to the selected item in the spinner.
	 *  Will pass the selected form value to the next screen.
	 */
	public void addFormSpinnerListener() {
		formSpinner = (Spinner)findViewById(R.id.formMenu);
		formSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
					
					String form = parent.getItemAtPosition(pos).toString();
					
					selectedFormBox = (TextView)findViewById(R.id.selectedForm);
					selectedFormBox.setText(form);		
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
			
		});
	}

	/** Starts asynchronous task to empty database and download all templates.
	 * 
	 * @param view
	 */
	@SuppressWarnings("unchecked")
	public void startFormDownload(View view)
	{
		new downloadAllTemplates().execute(this);
	}
	
	/** Begins population of spinners.
	 * 
	 * @param view
	 */
	public void startPopulateSpinners(View view)
	{
		populateTemplateGroupSpinner();
		addTemplateGroupSpinnerListener();
	}
	
	/*
	/** Creates a dummy form with the selected template.
	 *  Works for the most part, errors on templates further down the list.
	 * @param view
	 
	public void createNewForm(View view)
	{
		OSTDataSource ostDS = new OSTDataSource(this);
		ostDS.open();
		if(templ_id > -1)
		{	
			Form f;
			try
			{
				f = ostDS.getTemplateById(templ_id);

				//Set key field.
				f.questions.get(0).Answer="key"+cntr;
				cntr++;

				ostDS.addForm(f);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
		}
		else{
			showToast("Template id is -1");
		}
		ostDS.close();
		
		if( templ_id == -1){
			populateFormSpinner(templ_id);
		}
	}
	*/
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main_menu, menu);
		return true;
	}

	
	
	
	
	
	
	
	
	// The following navigate methods are for the implementation of the navbar layout
	public void navigateHome(View view)
	{	
		return;
	}
	
	
	/**
	 * Attempts to send the selected form to DisplayQuestionActivity. <br>
	 * Creates a new form to send if "new form" is selected. <br>
	 * Does nothing if no template or form is selected.
	 * 
	 * @param view The View that called this method
	 */
	public void navigateEdit(View view)
	{
		long formID;
		Form form;
		
		if (formSpinner != null)
		{
			if (formSpinner.getSelectedItem() != null)
			{
			
				MyData templateData = (MyData) templateSpinner.getSelectedItem();
				MyData formData = (MyData) formSpinner.getSelectedItem();
			
				OSTDataSource database = new OSTDataSource(this);
				database.open();
			
				formID = formData.value;
			
				if (formID == -1)
				{
					// Start a new form from the template
					Form template = database.getTemplateById(templateData.value);
					formID = database.addForm(template);
				
					form = database.getFormById(formID);
					form.meta.form_id = formID;	
				}
				else
				{
					form = database.getFormById(formID);
					form.meta.form_id = formID;	
				}
				
				Log.v("ryan_debug", String.format("Screen1: Passing screen 2 form with database ID: %s", String.valueOf(form.meta.form_id)));
			
				// Pass the form to the DisplayQuestionActivity
				Intent intent = new Intent(this, DisplayQuestionActivity.class);
				intent.putExtra("formObject", form);	
				startActivity(intent);
			
				database.close();
			}
			else
			{
				Toast.makeText(this, "No template or form selected", Toast.LENGTH_SHORT).show();
			}
		}
		else
		{
			Toast.makeText(this, "No template or form selected", Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * Attempts to send the selected form to SubmitFormActivity. <br>
	 * Does nothing if no form is selected.
	 * 
	 * @param view The View that called this method
	 */
	public void navigateSubmit(View view)
	{
		Form form;
		long formID;
		
		if (formSpinner != null)
		{
			if (formSpinner.getSelectedItem() != null)
			{
				MyData formData = (MyData) formSpinner.getSelectedItem();		
				formID = formData.value;
			
				if (formID == -1)
				{}
				else
				{
					// Get the form from the database
					OSTDataSource database = new OSTDataSource(this);
					database.open();
					form = database.getFormById(formID);
					form.meta.form_id = formID;	
					database.close();
				
					// Send the form to SubmitFormActivity
					Intent intent = new Intent(this, SubmitFormActivity.class);
					intent.putExtra("formObject", form);
					startActivity(intent);
				}
			}
			else
			{
				Toast.makeText(this, "No form selected", Toast.LENGTH_SHORT).show();
			}
		}
		else
		{
			Toast.makeText(this, "No form selected", Toast.LENGTH_SHORT).show();
		}
	}
}
