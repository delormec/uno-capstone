package com.example.habitathumanityapp.datasource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.example.habitathumanityapp.sharepointauthentication.NTLMSchemeFactory;

import android.util.Log;

public class SharePointDataSource {
	
	private String URL;
	private String list_name;
	private String user_name;
	private String password;
	private String domain;
	private DefaultHttpClient httpclient;
	private HttpHost target;
	private HttpContext localContext;
	
	public SharePointDataSource()
	{
		URL = "habitat.taic.net";
		list_name = "/omaha/unotestsite/_vti_bin/listdata.svc/ConstructionAtlasTest";
		user_name = "CDelormes";
		password = "CDelorme463";
		domain = "xtranet";
	}
	
	public SharePointDataSource(String URL, String list_name, String user_name, String password, String domain)
	{
		this.URL = URL;
		this.list_name = list_name;
		this.user_name = user_name;
		this.password = password;
		this.domain = domain;
	}
	
	
	//sets up data connection, returns 0 if successful -1 if failed
	public int setupConnection()
	{
        httpclient = new DefaultHttpClient();
        httpclient.getAuthSchemes().register("ntlm", new NTLMSchemeFactory());
        NTCredentials creds = new NTCredentials(user_name,password, domain, domain);
        httpclient.getCredentialsProvider().setCredentials(AuthScope.ANY, creds);

        target = new HttpHost(URL, 80, "http");
        
        localContext = new BasicHttpContext();
        
        HttpGet httpget = new HttpGet("list_name");
        httpget.setHeader("Accept", "application/json");
        
        //ok here we're trying to get something
        HttpResponse response1 = null;
		try {
			response1 = httpclient.execute(target, httpget, localContext);
			
			response1.getEntity().consumeContent();
			
			//this would read the contents of the response
				//HttpEntity entity1 = response1.getEntity();
	        	//BufferedReader reader = new BufferedReader(new InputStreamReader(entity1.getContent(), Charset.forName("UTF-8")));
	        	//StringBuilder results = new StringBuilder();
	        	//String line;
	        	//while ((line = reader.readLine()) != null) {
	        	//	results.append(line + '\n');
	        	//}
				//Log.v("cody_test", results.toString());			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}

        
        //everything was good, return goodness
		return 0;
	}
	
	//creates an entry 0 if success -1 if fail
	public int createListTest()
	{
		HttpPost httppost = new HttpPost("/omaha/unotestsite/_vti_bin/listdata.svc/ConstructionAtlasTest");
		httppost.setHeader("Accept", "application/json");
		
		String newEntry = "{\"ADDRESS\": \"666 Test street\", \"CITY\": \"Omaha\",\"STATE\": null,\"ZIP_CODE\": \"68108\"}";
		
		StringEntity myEntity = null;
		
		//i dont understand why this needs a try/catch
		try {
			myEntity = new StringEntity(newEntry, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return -1;
		}
		
		httppost.setEntity(myEntity);
		
		
		
        //ok here we're trying to get something
        HttpResponse response1 = null;
		try {
			response1 = httpclient.execute(target, httppost, localContext);
			
			//have to run this to free up the connection.
			//response1.getEntity().consumeContent();
			
			//this is where we could print the response if we wanted 
				HttpEntity entity1 = response1.getEntity();
				
				//Log.v(entity1.,"here4");
				
	        	BufferedReader reader = new BufferedReader(new InputStreamReader(entity1.getContent(), Charset.forName("UTF-8")));
	        	StringBuilder results = new StringBuilder();
	        	String line;
	        	while ((line = reader.readLine()) != null) {
	            	results.append(line + '\n');
	        	}
	        		
	        	Log.v("cody_test",results.toString());	
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
		return 0;
	}
}
