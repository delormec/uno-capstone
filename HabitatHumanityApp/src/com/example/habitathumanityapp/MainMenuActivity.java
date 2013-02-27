package com.example.habitathumanityapp;

import java.util.ArrayList;
import java.util.List;

import com.example.habitathumanityapp.datasource.OSTDataSource;


import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class MainMenuActivity extends Activity {
	
	private Spinner templateSpinner, templateGroupSpinner, formSpinner;
	void showToast(CharSequence msg){
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		
		populateTemplateSpinner();
		addTemplateSpinnerListener();
	}
	
	public void addTemplateSpinnerListener() {
		templateSpinner = (Spinner)findViewById(R.id.templateMenu);
		templateSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				showToast("Value is " + parent.getItemAtPosition(pos).toString());
				
				//Populate second spinner (template group spinner) 
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
			
		});
	}

	//Add items into the Template drop down initially
	public void populateTemplateSpinner() {
		
		templateSpinner = (Spinner)findViewById(R.id.templateMenu);
		List<String> templateList = new ArrayList<String>();
		
		//OSTDataSource ostDS = new OSTDataSource(this);
		//ostDS.open();
		//templateList = ostDS.getAllTemplateGroups();
		
		templateList.add("Item one");
		templateList.add("Item two");
		templateList.add("Item three");
		
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, templateList);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		templateSpinner.setAdapter(dataAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main_menu, menu);
		return true;
	}

}
