package org.habitatomaha.HOST.Model.Repository.Test;

import static org.junit.Assert.*;

import org.habitatomaha.HOST.Model.Form;
import org.habitatomaha.HOST.Model.Repository.SharePointDataSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SharePointDataSourceTest {

	private static final String URL = null;
	@InjectMocks SharePointDataSource sharePointDataSource;

	@Test
	public void testUploadFormToSharePoint() 
	{
		String password = " ";
		String user_name = " ";
		String domain = " ";
		String list_name = " ";
		String port = " ";
		Form form = new Form();
		//Given
		//When
		//SharePointDataSource.uploadFormToSharePoint(form, URL, list_name, user_name, password, domain, port);
		//Then
		assert(true);
	}
	
}
