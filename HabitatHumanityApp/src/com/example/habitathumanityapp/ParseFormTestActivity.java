package com.example.habitathumanityapp;

import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class ParseFormTestActivity extends Activity {

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_parse_form);
		// Show the Up button in the action bar.
		if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
		// Test for getting the Form from an XML string.
		
		// Test for getting the XML code from a Form Object.
		TextView xmlFile = (TextView)findViewById(R.id.fullXmlCode);
		
		TextQuestion tq1 = new TextQuestion();
		tq1.QuestionID = 1;
		tq1.Text = "What is the color blue";
		tq1.HelpText = "its pretty obvious duh";
		tq1.FieldName = "TestField";
		tq1.FieldType = "FieldType";
	LikertScaleQuestion lsq1 = new LikertScaleQuestion();
		lsq1.QuestionID =2;
		lsq1.Text = "On a scale of 1-10 what is awesome?";
		lsq1.HelpText = "...";
		lsq1.FieldName = "TestField2";
		lsq1.FieldType = "FieldType1";
		lsq1.steps = "10";
		lsq1.labels.add("high");
		lsq1.labels.add("low");
		lsq1.labels.add("medium");
	ChoiceQuestion cq1 = new ChoiceQuestion();
		cq1.QuestionID = 3;
		cq1.Text = "Choose your favorite color";
		cq1.HelpText = "?";
		cq1.FieldName = "TestField3";
		cq1.FieldType = "FieldType3";
		cq1.options.add("Blue");
		cq1.options.add("Red");
		cq1.options.add("Green");
	Form form1 = new Form();
		form1.meta.template_id = 1;
		form1.meta.autoUpload = "false";
		form1.meta.url = "http://www.google.com";
		form1.meta.name = "Test Form";
		form1.meta.group = "Test Group";
		form1.meta.dateCreated ="Today";
		form1.questions.add(tq1);
		form1.questions.add(lsq1);
		form1.questions.add(cq1);
		
		String test = XMLParser.getXML(form1, this);
		xmlFile.setText(test);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_parse_form, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
