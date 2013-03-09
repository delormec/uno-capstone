package com.example.habitathumanityapp;

import java.util.ArrayList;
import java.util.List;

import com.example.habitathumanityapp.datasource.OSTDataSource;
import com.example.habitathumanityapp.tasks.downloadAllTemplates;


import android.os.Bundle;
import android.app.Activity;
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
	
	void showToast(CharSequence msg){
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
	}
	
	public void addTemplateGroupSpinnerListener() {
		templateGroupSpinner = (Spinner)findViewById(R.id.typeMenu);
		templateGroupSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				String templateGroup = parent.getItemAtPosition(pos).toString();
				
				//showToast("Value is " + templateGroup);
				
				populateTemplateSpinner(templateGroup);
				addTemplateSpinnerListener();
				
				//Populate second spinner (template group spinner) 
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
			
		});
	}

	public void addTemplateSpinnerListener() {
		templateSpinner = (Spinner)findViewById(R.id.templateMenu);
		templateSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				
					String template = parent.getItemAtPosition(pos).toString();
					showToast("Value is " + template);
					
					populateFormSpinner(template);
					addFormSpinnerTemplate();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
	}
	public void addFormSpinnerTemplate() {
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
	public void populateFormSpinner(String template) {
		formSpinner = (Spinner)findViewById(R.id.formMenu);
		List<String> templateList = new ArrayList<String>();
		
		// Connect to the database and get a list of all of the templates corresponding to the group.
		//OSTDataSource ostDS = new OSTDataSource(this);
		//ostDS.open();
		
		templateList.add("Form one");
		templateList.add("Form two");
		templateList.add("Form three");
		
		// Fill the drop down boxes with completed forms.
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, templateList);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		formSpinner.setAdapter(dataAdapter);
		
		
	}
	public void populateTemplateGroupSpinner() {
		templateGroupSpinner = (Spinner)findViewById(R.id.typeMenu);
		List<String> templateList = new ArrayList<String>();
		
		// Connect to the database and get a list of all of the templates corresponding to the group.
		OSTDataSource ostDS = new OSTDataSource(this);
		ostDS.open();
		templateList = ostDS.getAllTemplateGroups();
		
		/*
		templateList.add("Template Group one");
		templateList.add("Template Group two");
		templateList.add("Template Group three");
		*/
		
		// Fill the drop down boxes with the templates.
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, templateList);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		templateGroupSpinner.setAdapter(dataAdapter);
		
	}
	//Add items into the Template drop down initially
	public void populateTemplateSpinner(String template) {
		
		templateSpinner = (Spinner)findViewById(R.id.templateMenu);
		List<String> templateList = new ArrayList<String>();
		
		// Connect to the database and get a list of all of the template groups.
		//OSTDataSource ostDS = new OSTDataSource(this);
		//ostDS.open();
		
		templateList.add("Template one");
		templateList.add("Template two");
		templateList.add("Template three");
		
		// Fill the drop down boxes with the template groups.
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, templateList);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		templateSpinner.setAdapter(dataAdapter);
	}

	public void startFormDownload(View view)
	{
		new downloadAllTemplates().execute(this);
	}
	
	public void startPopulateSpinners(View view)
	{
		populateTemplateGroupSpinner();
		addTemplateGroupSpinnerListener();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main_menu, menu);
		return true;
	}

}
