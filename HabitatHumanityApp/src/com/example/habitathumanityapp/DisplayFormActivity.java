package com.example.habitathumanityapp;

import java.io.File;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

@SuppressLint({ "HandlerLeak", "ShowToast" })
public class DisplayFormActivity extends Activity {

	public Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			Bundle b = msg.getData();
			Boolean isEmpty = b.isEmpty();
			TextView testTextView = (TextView)findViewById(R.id.testText);
			if(isEmpty){
				testTextView.setText("No Form.");
			}
			else{
				testTextView.setText("Form Received");
			}
		}
	};
	
	private static final String TAG = "DisplayFormActivity";

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_form);
		// Show the Up button in the action bar.
		if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	/*protected void onStart(){
	}*/
	public void onButtonClick(View view){
		
		parseForm(this);
		
		Toast.makeText(this, "Testing...", Toast.LENGTH_SHORT);
	}
	
	public void parseForm(Activity activity) {
		final Activity act = activity;
		
		/*
		final Resources res = activity.getResources();
		final Activity act = activity;
		AssetManager assetManager = getAssets();
		InputStream inStream = null;
		
		final InputStream formInput = activity.getResources().openRawResource(R.raw.form);
		*/
		
		Runnable runnable = new Runnable(){
			@Override
			public void run(){	
				//android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
				Form f = null;
				
				Serializer serializer = new Persister();
				String filename = "form.xml";
				
				File file = new File( act.getFilesDir(), filename);;
				 
				Log.v(TAG,"Filename=" + file.toString());
				
				//Look into serialization/deserialization of objects
				try{
					f = serializer.read(Form.class, file);
					
					long formID = f.getFormId();
					String formName = f.getFormName();
					
					assert f.getFormId() == 351;
					assert f.getFormName() == "Survey";
					
					Log.v(TAG,"Form id is " + formID);
					Log.v(TAG,"Form name is " + formName);
					
						
					
					for( Question q: f.questions){
						System.out.println(q.getClass());
					}
				}catch(Exception e){
					e.printStackTrace();
				}
				
				Message msg = Message.obtain();
				msg.obj = f;
				mHandler.sendMessage(msg);
				}
			};
			
			Thread myThread = new Thread(runnable);
			myThread.start();
			
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
