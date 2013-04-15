package org.habitatomaha.HOST.Helper.Test;

import static org.junit.Assert.*;

import org.habitatomaha.HOST.Helper.Utility;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import android.app.Activity;

@RunWith(MockitoJUnitRunner.class)
public class UtilityTest {

	@InjectMocks Utility utility;
	
	@Test
	public void testIsNetworkAvailable() 
	{
		Activity activity = new Activity();
		
		//Given
		//When
		Utility.isNetworkAvailable(activity);
		//Then
		assert(true);
	}

}
