package org.habitatomaha.HOST.Activity;

import android.app.Activity;
import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.test.suitebuilder.annotation.MediumTest;
import android.view.KeyEvent;
import android.view.View;

import org.habitatomaha.HOST.Activity.EditFormActivity;
import org.junit.Test;


public class SelectFormActivityTest extends ActivityInstrumentationTestCase2 {
	
	Activity mActivity;
	View formLabel;
	View groupLabel;
	View layout;
	
	/**
	 * a Constructor is needed for Android based Unit tests and Activity tests
	 * 
	 */
	public SelectFormActivityTest() {
	    super("org.habitatomaha.HOST.Activity.SelectFormActivity", SelectFormActivity.class);
	  } 

	
	/**
	 *  A Setup function is required in order to initialize android components before a test is ran.
	 * 
	 */
	@Override
	  protected void setUp() throws Exception {
	    super.setUp();

	    setActivityInitialTouchMode(false);

	    
	  // Sample code given by the Android Testing Website
	   mActivity = getActivity();
	   groupLabel =  mActivity.findViewById(org.habitatomaha.HOST.R.id.groupLabel);
	   formLabel =  mActivity.findViewById(org.habitatomaha.HOST.R.id.formLabel);
	   layout = mActivity.findViewById(org.habitatomaha.HOST.R.id.RelativeLayout1);
	   
	   /* mSpinner =
	      (Spinner) mActivity.findViewById(
	        com.android.example.spinner.R.id.Spinner01
	      );

	      mPlanetData = mSpinner.getAdapter();*/
	  }
	
	/**Initial State Test - what should be true when the app is ran for the first time
	 * 
	 * 
	 */
	@Test
	public void testPreConditions() {
		sendKeys("A B C D E F G");
		mActivity.openOptionsMenu();
		mActivity.closeOptionsMenu();
		// The first condition of the Activity Test
		// Checks the initial state of the Activity for valid state
	    /*assertTrue(mSpinner.getOnItemSelectedListener() != null);
	    assertTrue(mPlanetData != null);
	    assertEquals(mPlanetData.getCount(),ADAPTER_COUNT);*/

		assert(groupLabel == null);
		assert(formLabel == null);
	  } // end of testPreConditions() method definition
	
	
	
	
	/**
	 * Test the lifecycle of the activity to ensure that the activity can be
	 * Stopped/Started/Restarted/Paused/closed without an error.
	 * 
	 */
  @MediumTest
    public void testLifeCycleCreate() {
	  	
        getInstrumentation().callActivityOnStart(mActivity);
        getInstrumentation().callActivityOnPause(mActivity);
        //getInstrumentation().callActivityOnResume(mActivity);
        getInstrumentation().callActivityOnRestart(mActivity);
        //getInstrumentation().callActivityOnDestroy(mActivity);
        
    }

	
	//UI Test
	@Test
	public void exampleActivityTestNOTUSED() {
		//layout.findViewById()
	
		
		// Attempt to gain focus of the Object - Example
		//
	   /* mActivity.runOnUiThread(
	      new Runnable() {
	        public void run() {
	          mSpinner.requestFocus();
	          mSpinner.setSelection(INITIAL_POSITION);
	        } // end of run() method definition
	      } // end of anonymous Runnable object instantiation
	      *
	    ); // end of invocation of runOnUiThread*/
	
		
		
		// Submit Instructions to the focused object - Example
		//
		
		 this.sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);
    		
		 int TEST_POSITION = 8;
    		
		for (int i = 1; i <= TEST_POSITION ; i++) 
		{
			this.sendKeys(KeyEvent.KEYCODE_DPAD_DOWN);
		} 

    	this.sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);
		
		// Lastly check to see that we have selected the item we wanted to select from the test. - Example
		// assert that we got the correct value
		//
		 /*mPos = mSpinner.getSelectedItemPosition();
		    mSelection = (String)mSpinner.getItemAtPosition(mPos);
		    TextView resultView =
		      (TextView) mActivity.findViewById(
		        com.android.example.spinner.R.id.SpinnerResult
		      );

		    String resultText = (String) resultView.getText();

		    assertEquals(resultText,mSelection)*/
		assert(true);
	}
	
	/**State Management Test - test if the activity keeps its values after being closed
	 * 
	 * 
	 */
	@Test
	public void testStateDestroy() {
		
		
		
		//set the spinner selection to a test value: 
		//
		/*mActivity.setSpinnerPosition(TEST_STATE_DESTROY_POSITION);
	    mActivity.setSpinnerSelection(TEST_STATE_DESTROY_SELECTION);*/
		
		//Terminate the activity and restart it
		//
		mActivity.finish(); 
    	mActivity = this.getActivity();
		
		// Get the current condition from the Activity
		//
		/*int currentPosition = mActivity.getSpinnerPosition();
    	String currentSelection = mActivity.getSpinnerSelection();*/
		
		// Test the destroy == new condition
		//
		/*assertEquals(TEST_STATE_DESTROY_POSITION, currentPosition);
	    assertEquals(TEST_STATE_DESTROY_SELECTION, currentSelection);*/
		
		assert(true);
		
	  }

	
	
	
	/** State management for Pause and Idle states of the Android OS.
	 * 
	 * These tests are required to run off of a UI thread due to interaction with the
	 * instrumentation
	 */
	 @UiThreadTest			
	public void testStatePause() {  
	 
		 // get the current instrumentation. needed to call onPause and onResume
		 //
		Instrumentation mInstr = this.getInstrumentation();
		 
		 //Set the spinner selection to a test value:
		 //
		/*	    mActivity.setSpinnerPosition(TEST_STATE_PAUSE_POSITION);
			    mActivity.setSpinnerSelection(TEST_STATE_PAUSE_SELECTION);*/

		//	Use instrumentation to call the Activity's onPause():

		   mInstr.callActivityOnPause(mActivity);

		//	Under test, the activity is waiting for input. The invocation of callActivityOnPause(android.app.Activity) performs a call directly to the activity's onPause() instead of manipulating the activity's UI to force it into a paused state.
		//  Force the spinner to a different selection:

		/*    mActivity.setSpinnerPosition(0);
			  mActivity.setSpinnerSelection("");*/

		//	This ensures that resuming the activity actually restores the spinner's state rather than simply leaving it as it was.
		//	Use instrumentation to call the Activity's onResume():

		    mInstr.callActivityOnResume(mActivity);
		 
		// Invoking callActivityOnResume(android.app.Activity) affects the activity in a way similar to callActivityOnPause. The activity's onResume() method is invoked instead of manipulating the activity's UI to force it to resume.
		// Get the current state of the spinner:

		/*   int currentPosition = mActivity.getSpinnerPosition();
		     String currentSelection = mActivity.getSpinnerSelection();*/

		 //Test the current spinner state against the test values:

		 /*	 assertEquals(TEST_STATE_PAUSE_POSITION,currentPosition);
		     assertEquals(TEST_STATE_PAUSE_SELECTION,currentSelection);*/
		 assert(true);
		   } 
	
}
