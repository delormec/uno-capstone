package com.example.habitathumanityapp;

import java.io.File;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;

public class DisplayFormActivity extends Activity {

	final Handler mHandler = new Handler();
	
	//This will post results from form back to UI thread
	final Runnable mParseForm = new Runnable(){
		public void run(){
			postToUI();
		}
	};
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_form);
		// Show the Up button in the action bar.
		if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}	
		
		startParse();
		//parseForm.run();
	}
	
	protected void postToUI() {
		// TODO Auto-generated method stub
		
	}

	protected void startParse(){
		
		Thread t = new Thread() {
			public void run(){
				Form mResults = parseForm();
				mHandler.post(mParseForm);	
			}
		};
		
		t.start();
	}


	protected Form parseForm() {
		// TODO Auto-generated method stub
		
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
		Form f = null;
		
		Serializer serializer = new Persister();
		
		File formFile = new File("Form.xml");
		
		//Scanner in = new Scanner(System.in);
		
		//String test = in.next();
		
		//System.out.println(test);
		
		try{
			f = serializer.read(Form.class, formFile);
			
			for( Question q: f.questions){
				System.out.println(q.getClass());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return f;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_display_form, menu);
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