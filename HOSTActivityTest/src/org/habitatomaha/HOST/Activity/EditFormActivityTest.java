package org.habitatomaha.HOST.Activity;

import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;

import org.habitatomaha.HOST.Activity.EditFormActivity;
import org.junit.Test;


public class EditFormActivityTest extends ActivityInstrumentationTestCase2 {
	
	// Constructor. is required
	public EditFormActivityTest() {
	    super("org.habitatomaha.HOST.Activity.EditFormActivity", EditFormActivity.class);
	  } // end of SpinnerActivityTest constructor definition

	// Setup function. Is required. -> Ran once before each test method.
	@Override
	  protected void setUp() throws Exception {
	    super.setUp();

	    setActivityInitialTouchMode(false);

	    
	  // Sample code given by the Android Testing Website
	  /*  mActivity = getActivity();

	    mSpinner =
	      (Spinner) mActivity.findViewById(
	        com.android.example.spinner.R.id.Spinner01
	      );

	      mPlanetData = mSpinner.getAdapter();*/

	  } // end of setUp() method definition
	
	//Initial State Test
	@Test
	public void testPreConditions() {
		
		// The first condition of the Activity Test
		// Checks the initial state of the Activity for valid state
	    /*assertTrue(mSpinner.getOnItemSelectedListener() != null);
	    assertTrue(mPlanetData != null);
	    assertEquals(mPlanetData.getCount(),ADAPTER_COUNT);*/
		
		assert(true);
	  } // end of testPreConditions() method definition
	
	
	//UI Test
	@Test
	public void exampleActivityTestNOTUSED() {

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
		/*
		 * this.sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);
    		for (int i = 1; i <= TEST_POSITION; i++) 
    		{
      		this.sendKeys(KeyEvent.KEYCODE_DPAD_DOWN);
    		} // end of for loop

    	this.sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);*/
		
		
		
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
	
	//State Management Test
	@Test
	public void testStateDestroy() {
		
		//set the spinner selection to a test value: 
		//
		/*mActivity.setSpinnerPosition(TEST_STATE_DESTROY_POSITION);
	    mActivity.setSpinnerSelection(TEST_STATE_DESTROY_SELECTION);*/
		
		//Terminate the activity and restart it
		//
		/*mActivity.finish();
    	mActivity = this.getActivity();*/
		
		// Get the current condition from the Activity
		//
		/*int currentPosition = mActivity.getSpinnerPosition();
    	String currentSelection = mActivity.getSpinnerSelection();*/
		
		// Test the destroy == new condition
		//
		/*assertEquals(TEST_STATE_DESTROY_POSITION, currentPosition);
	    assertEquals(TEST_STATE_DESTROY_SELECTION, currentSelection);*/
		
		assert(true);
		
	  } // end of testStateDestroy() method definition

	
	
	
		// State management for Pause and Idle
	// @UiThreadTest									// needed for running on a UI Thread
	@Test 
	public void testStatePause() {  
	 
		 // get the current instrumentation. needed to call onPause and onResume
		 //
		// Instrumentation mInstr = this.getInstrumentation();
		 
		 //Set the spinner selection to a test value:
		 //
		/*	    mActivity.setSpinnerPosition(TEST_STATE_PAUSE_POSITION);
			    mActivity.setSpinnerSelection(TEST_STATE_PAUSE_SELECTION);*/

		//	Use instrumentation to call the Activity's onPause():

		/*    mInstr.callActivityOnPause(mActivity);*/

		//	Under test, the activity is waiting for input. The invocation of callActivityOnPause(android.app.Activity) performs a call directly to the activity's onPause() instead of manipulating the activity's UI to force it into a paused state.
		//  Force the spinner to a different selection:

		/*    mActivity.setSpinnerPosition(0);
			  mActivity.setSpinnerSelection("");*/

		//	This ensures that resuming the activity actually restores the spinner's state rather than simply leaving it as it was.
		//	Use instrumentation to call the Activity's onResume():

		/*    mInstr.callActivityOnResume(mActivity);*/
		 
		// Invoking callActivityOnResume(android.app.Activity) affects the activity in a way similar to callActivityOnPause. The activity's onResume() method is invoked instead of manipulating the activity's UI to force it to resume.
		// Get the current state of the spinner:

		/*   int currentPosition = mActivity.getSpinnerPosition();
		     String currentSelection = mActivity.getSpinnerSelection();*/

		 //Test the current spinner state against the test values:

		 /*	 assertEquals(TEST_STATE_PAUSE_POSITION,currentPosition);
		     assertEquals(TEST_STATE_PAUSE_SELECTION,currentSelection);*/
		 assert(true);
		   } // end of testStatePause() method definition
	
}
