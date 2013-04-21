package org.habitatomaha.HOST.Model.Repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
//<<<<<<< HEAD
//=======
//>>>>>>> XML Parser Helper Classes
import org.habitatomaha.HOST.Model.Error;
import org.habitatomaha.HOST.Model.Error.Severity;

import android.content.Context;

public class AdminDataSource {
	
	private String URL;
	private String list_name;
	private int port;

	private DefaultHttpClient httpclient;
	private HttpHost target;
	private HttpContext localContext;
	
	public Context callingContext;
	
	public AdminDataSource(Context callingContext, String URL, String list_name, int port)
	{
		this.URL = URL;
		this.list_name = list_name;
		this.port = port;
		this.callingContext = callingContext;
		
		//if the list name doesn't end with a / then add one
		if (list_name.substring(list_name.length() - 1).compareTo("/") != 0)
		{
			this.list_name = this.list_name + "/";
		}
	}
	
	/** 
	 * Connect to admin tool and download a list of all the template_id's that are available for download. <br>
	 * This list gets passed to XMLParser which turns them into a List.
	 * @return Returns XML from Admin Tool if successful, otherwise returns a null string (error).
	 */
	public String getAllTemplates()
	{
        httpclient = new DefaultHttpClient();

        target = new HttpHost(URL, port, "http");
        
        localContext = new BasicHttpContext();
        
        HttpGet httpget = new HttpGet(list_name);
        httpget.setHeader("Accept", "application/xml");
        //ok here we're trying to get something
        HttpResponse response1 = null;
		try {
			response1 = httpclient.execute(target, httpget, localContext);
			
			//read the response
			HttpEntity entity1 = response1.getEntity();
	        BufferedReader reader = new BufferedReader(new InputStreamReader(entity1.getContent(), Charset.forName("UTF-8")));
	        StringBuilder results = new StringBuilder();
	        results.append(reader.readLine());
			
			//success, return good data
			if (response1.getStatusLine().getStatusCode() == 200)
			{		
				return results.toString();
			}
			
			//failure, log it and return empty string signifying error
			ErrorLog.log(callingContext, new Error("Template Download Error", results.toString(), Severity.Critical));

			//error
			return null;
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
        //error if we got here
		ErrorLog.log(callingContext, new Error("Template Download Error", "Potential bad port number.", Severity.Critical));
		return null;
	}

	/**
	 * Connect to Admin Tool and download the serialized XML that represents a template.
	 * @param id template_id we want to download
	 * @return Returns XML from Admin Tool if successful, otherwise returns a null string (error).
	 */
	public String getTemplateXMLByID(String id) {
        httpclient = new DefaultHttpClient();

        //TODO, this port will change
        target = new HttpHost(URL, port, "http");
        
        localContext = new BasicHttpContext();
        
        HttpGet httpget = new HttpGet(list_name + id);

        HttpResponse response1 = null;
        
		try {
			response1 = httpclient.execute(target, httpget, localContext);
			
			//read the response
			HttpEntity entity1 = response1.getEntity();
        	BufferedReader reader = new BufferedReader(new InputStreamReader(entity1.getContent(), Charset.forName("UTF-8")));
        	StringBuilder results = new StringBuilder();
        	results.append(reader.readLine());
			
			//success, return the XML we found
			if (response1.getStatusLine().getStatusCode() == 200)
			{
				return results.toString();	
			}
			
			//failure, log it and return empty string signifying error
			ErrorLog.log(callingContext, new Error("Template Download Error", results.toString(), Severity.Critical));

			//error
			return null;

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        //failure
		//failure, log it and return empty string signifying error (i found this happens with a bad port
		ErrorLog.log(callingContext, new Error("Template Download Error", "Potential bad port number.", Severity.Critical));
		return null;
	}
}
