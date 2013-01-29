/**
 * 
 */
package com.example.helloworld.test;

import com.example.helloworld.HelloWorldActivity;

import android.test.ActivityInstrumentationTestCase2;

/**
 * @author BigSam
 *
 */
public class HelloWorldActivityTests extends
		ActivityInstrumentationTestCase2<HelloWorldActivity> {

	private HelloWorldActivity mActivity;

	/**
	 * @param name
	 */
	
	public HelloWorldActivityTests(){
		this("HelloWorldActivity");
	}
	
	public HelloWorldActivityTests(String name) {
		super(HelloWorldActivity.class);
		setName("HelloWorldActivity");		
	}

	/* (non-Javadoc)
	 * @see android.test.ActivityInstrumentationTestCase2#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		
		mActivity = getActivity();
	}

	/* (non-Javadoc)
	 * @see android.test.ActivityInstrumentationTestCase2#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

}
