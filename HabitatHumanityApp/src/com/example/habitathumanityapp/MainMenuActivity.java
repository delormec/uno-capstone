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
	
	public void populateTemplateGroupSpinner() {
		templateGroupSpinner = (Spinner)findViewById(R.id.typeMenu);
		List<String> templateList = new ArrayList<String>();
		
		// Connect to the database and get a list of all of the templates corresponding to the group.
		OSTDataSource ostDS = new OSTDataSource(this);
		ostDS.open();
		templateList = ostDS.getAllTemplateGroups();
		
		// Fill the drop down boxes with the templates.
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, templateList);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		templateGroupSpinner.setAdapter(dataAdapter);
		
		ostDS.close();
		
	}
	
	public void addTemplateGroupSpinnerListener() {
		templateGroupSpinner = (Spinner)findViewById(R.id.typeMenu);
		templateGroupSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				String templateGroup = parent.getItemAtPosition(pos).toString();

				populateTemplateSpinner(templateGroup);
				addTemplateSpinnerListener();
				
				//Populate second spinner (template group spinner) 
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
			
		});
	}

	//Add items into the Template drop down initially
	public void populateTemplateSpinner(String templateGroup) {
		
		templateSpinner = (Spinner)findViewById(R.id.templateMenu);
		List<MyData> templateList = new ArrayList<MyData>();
		
		// Connect to the database and get a list of all of the template groups.
		OSTDataSource ostDS = new OSTDataSource(this);
		ostDS.open();
		templateList = ostDS.getAllTemplateInfoByGroupMyData(templateGroup);
		
		
		// Fill the drop down boxes with the template groups.
		ArrayAdapter<MyData> dataAdapter = new ArrayAdapter<MyData>(this, android.R.layout.simple_spinner_dropdown_item, templateList);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		templateSpinner.setAdapter(dataAdapter);
		
		ostDS.close();
	}
	
	public void addTemplateSpinnerListener() {
		templateSpinner = (Spinner)findViewById(R.id.templateMenu);
		templateSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				
					//String template = parent.getItemAtPosition(pos).toString();
					MyData template = (MyData) parent.getItemAtPosition(pos);
					showToast("TemplateID =  " + template.getValue());
					
					populateFormSpinner(template.getValue());
					addFormSpinnerListener();
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
		
		// Connect to the database and get a list of all of the forms corresponding to the selected template.
		//OSTDataSource ostDS = new OSTDataSource(this);
		//ostDS.open();
		
		templateList.add("Create New Form...");

		// Fill the drop down boxes with completed forms.
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, templateList);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		formSpinner.setAdapter(dataAdapter);
		
	}

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

	@SuppressWarnings("unchecked")
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
