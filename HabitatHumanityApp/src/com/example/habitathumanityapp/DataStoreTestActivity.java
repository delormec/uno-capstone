package com.example.habitathumanityapp;

import java.util.List;

import com.example.habitathumanityapp.datasource.OSTDataSource;
import com.example.habitathumanityapp.datasource.SharePointDataSource;
import com.example.habitathumanityapp.tasks.connectToSharePoint;
import com.example.habitathumanityapp.tasks.createSharePointListTest;
import com.sun.xml.internal.ws.org.objectweb.asm.Label;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

@SuppressLint({ "HandlerLeak", "ShowToast" })
public class DataStoreTestActivity extends Activity {

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_data_store_test);
		// Show the Up button in the action bar.
		if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
		
		TextView DSTest1 = (TextView)findViewById(R.id.DSTest1);
		TextView DSTest2 = (TextView)findViewById(R.id.DSTest2);
		TextView DSTest3 = (TextView)findViewById(R.id.DSTest3);
		
		//set up a sample form
		TextQuestion tq1 = new TextQuestion();
			tq1.QuestionID = 1;
			tq1.Text = "What is the color blue";
			tq1.HelpText = "its pretty obvious duh";
		LikertScaleQuestion lsq1 = new LikertScaleQuestion();
			lsq1.QuestionID =2;
			lsq1.Text = "On a scale of 1-10 what is awesome?";
			lsq1.HelpText = "...";
			lsq1.steps = "10";
			lsq1.labels.add("high");
			lsq1.labels.add("low");
			lsq1.labels.add("medium");
		ChoiceQuestion cq1 = new ChoiceQuestion();
			cq1.QuestionID = 3;
			cq1.Text = "Choose your favorite color";
			cq1.HelpText = "?";
			cq1.options.add("Blue");
			cq1.options.add("Red");
			cq1.options.add("Green");
		Form form1 = new Form();
			form1.meta.formid = 1;
			form1.meta.autoUpload = "false";
			form1.meta.url = "http://www.google.com";
			form1.meta.name = "Test Form";
			form1.questions.add(tq1);
			form1.questions.add(lsq1);
			form1.questions.add(cq1);
		
		OSTDataSource ostDS = new OSTDataSource(this);
		ostDS.open();
		
		//remove anything in there
		ostDS.removeAllTemplates();
		
		//add 4 forms with different primary keys
		ostDS.addTemplate(form1);
		form1.meta.formid = 5;
		ostDS.addTemplate(form1);
		form1.meta.formid = 7;
		ostDS.addTemplate(form1);
		form1.meta.formid = 8;
		ostDS.addTemplate(form1);

		//get all the templates stored in the database
		List<String[]> template_list = ostDS.getAllTemplateInfo();
		String template_list_string = "";
		
		for (String[] template : template_list) {
			
			template_list_string =template_list_string + template[0] + ":" + template[1]  + "\n" ;
		}
		//display form_id: form_name
		DSTest1.setText(template_list_string);
	
		//get template by ID
		Form form2 = ostDS.getTemplateById(8);
		//test that shows result
		DSTest2.setText(String.valueOf(form2.meta.name));
		
		//set my 'template_id'
		form1.meta.formid=1;
		
		//add my form to the db
		ostDS.addForm(form1);
		ostDS.addForm(form1);
		ostDS.addForm(form1);
		ostDS.addForm(form1);
		
		//get a list of all my forms (i know there's only one)
		List<String[]> forms = ostDS.getAllFormInfoByTemplateId(1);
		
		//get the form_id of the first form
		String form_id = forms.get(0)[0];
		
		//DSTest3.setText(String.valueOf(forms.size()));
		
		//get the first form by id
		Form form3 = ostDS.getFormById(Integer.parseInt(form_id));
		
		//set my new name
		form3.meta.name = "my new name";
		
		//update the existing form in db with new one
		ostDS.updateFormById(form3, Integer.parseInt(form_id));
		
		//this will cause next line to fail
		//ostDS.removeFormById(Integer.parseInt(form_id));
		
		
		Form form4 = ostDS.getFormById(Integer.parseInt(form_id));
		
		//display my new name
		DSTest3.setText(form4.meta.name);

		//remove any templates in db
		ostDS.removeAllTemplates();
		ostDS.removeAllForms();
		ostDS.close();
		
		
		SharePointDataSource spDS = new SharePointDataSource();
		
		//create threads that do shit
		new connectToSharePoint().execute(spDS);
		new createSharePointListTest().execute(spDS);

		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.activity_data_store_test, menu);
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
