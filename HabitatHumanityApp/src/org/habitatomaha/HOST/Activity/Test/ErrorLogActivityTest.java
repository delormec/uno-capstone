package org.habitatomaha.HOST.Activity.Test;

import static org.junit.Assert.*;

import org.habitatomaha.HOST.Activity.ErrorLogActivity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import android.view.View;

@RunWith(MockitoJUnitRunner.class)
public class ErrorLogActivityTest {

	@InjectMocks ErrorLogActivity errorLogActivity;
	
	@Test
	public void testRemoveEntry() 
	{
		View view = new View(new ErrorLogActivity());
		
		//Given
		//When
		errorLogActivity.removeEntry(view);
		//Then
		assert(true);
	}

}
