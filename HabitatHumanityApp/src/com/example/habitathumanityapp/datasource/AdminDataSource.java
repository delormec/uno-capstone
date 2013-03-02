package com.example.habitathumanityapp.datasource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ManagedClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
//<<<<<<< HEAD
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

//=======
//>>>>>>> XML Parser Helper Classes
import com.example.habitathumanityapp.sharepointauthentication.NTLMSchemeFactory;

import android.util.Log;

public class AdminDataSource {
	
	private String URL;
	private String list_name;

	private DefaultHttpClient httpclient;
	private HttpHost target;
	private HttpContext localContext;
	
	public AdminDataSource()
	{
		URL = "wende.sytes.net";
		list_name = "/api/ApiForm";
	}
	
	public AdminDataSource(String URL, String list_name)
	{
		this.URL = URL;
		this.list_name = list_name;
	}
	
	
	//sets up data connection, returns 0 if successful -1 if failed
	public String getAllTemplates()
	{
        httpclient = new DefaultHttpClient();
        //httpclient.getAuthSchemes().register("ntlm", new NTLMSchemeFactory());
        //NTCredentials creds = new NTCredentials(user_name,password, domain, domain);
        //httpclient.getCredentialsProvider().setCredentials(AuthScope.ANY, creds);

        //TODO, this port will change
        target = new HttpHost(URL, 5432, "http");
        
        localContext = new BasicHttpContext();
        
        HttpGet httpget = new HttpGet("/api/ApiForm");
        httpget.setHeader("Accept", "application/xml");
        //ok here we're trying to get something
        HttpResponse response1 = null;
		try {
			response1 = httpclient.execute(target, httpget, localContext);
			
			
			//something broke if this isn't 200
			if (response1.getStatusLine().getStatusCode() != 200)
			{
			   //return -1;	
			}
			
			//response1.getEntity().consumeContent();
			
			//this would read the contents of the response
				HttpEntity entity1 = response1.getEntity();
	        	BufferedReader reader = new BufferedReader(new InputStreamReader(entity1.getContent(), Charset.forName("UTF-8")));
	        	StringBuilder results = new StringBuilder();
	        	String line;
	        	results.append(reader.readLine());
	        	//while ((line = reader.readLine()) != null) {
	        	//	results.append(line + '\n');
	        	//}
				//Log.v("cody_test", results.toString());		
				return results.toString();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
        //everything was good, return goodness
		return "";
	}

	public String getTemplateXMLByID(String id) {
        httpclient = new DefaultHttpClient();

        //TODO, this port will change
        target = new HttpHost(URL, 5432, "http");
        
        localContext = new BasicHttpContext();
        
        HttpGet httpget = new HttpGet("/api/ApiForm/"+id);
        //httpget.setHeader("Accept", "application/xml");
        //ok here we're trying to get something
        HttpResponse response1 = null;
		try {
			response1 = httpclient.execute(target, httpget, localContext);
			
			
			//something broke if this isn't 200
			if (response1.getStatusLine().getStatusCode() != 200)
			{
			   //return -1;	
			}
			
			//response1.getEntity().consumeContent();
			
			//this would read the contents of the response
				HttpEntity entity1 = response1.getEntity();
	        	BufferedReader reader = new BufferedReader(new InputStreamReader(entity1.getContent(), Charset.forName("UTF-8")));
	        	StringBuilder results = new StringBuilder();
	        	String line;
	        	results.append(reader.readLine());
	        	//while ((line = reader.readLine()) != null) {
	        	//	results.append(line + '\n');
	        	//}
				//Log.v("cody_test", results.toString());		
				return results.toString();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
        //everything was good, return goodness
		return "";
	}
}
