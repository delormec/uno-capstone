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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;

// XML Parser Helper Classes
import com.example.habitathumanityapp.Form;
import com.example.habitathumanityapp.Question;
import com.example.habitathumanityapp.sharepointauthentication.NTLMSchemeFactory;

/**
 * Interactions with a sharepoint site
 * @author Cody
 */
public class SharePointDataSource {
	
	/*
	public SharePointDataSource()
	{
		URL = "habitat.taic.net";
		list_name = "/omaha/unotestsite/_vti_bin/listdata.svc/ConstructionAtlasTest";
		user_name = "CDelorme";
		password = "CDelorme463";
		domain = "xtranet";
	}*/
	
	/**
	 * Uploads a given form to an OData endpoint using windows Authentication. <br/>
	 * Will likely not work if Windows authentication is not enabled on the server.
	 * @param form Form you want to upload.
	 * @param URL URL of the OData endpoint - 'habitat.taic.net'
	 * @param list_name path and listname - '/omaha/unotestsite/_vti_bin/listdata.svc/ConstructionAtlasTest'
	 * @param user_name 
	 * @param password Plain text password.
	 * @param domain Windows domain.
	 * @return
	 */
	public static String[] uploadFormToSharePoint(Form form, String URL, String list_name, String user_name, String password, String domain)
	{
		HttpContext localContext;
		
		DefaultHttpClient httpclient = new DefaultHttpClient();
        httpclient.getAuthSchemes().register("ntlm", new NTLMSchemeFactory());
        NTCredentials creds = new NTCredentials(user_name, password, domain, domain);
        httpclient.getCredentialsProvider().setCredentials(AuthScope.ANY, creds);

        HttpHost target = new HttpHost(URL, 80, "http");
        localContext = new BasicHttpContext();
        
		HttpPost httppost = new HttpPost(list_name);
		httppost.setHeader("Accept", "application/json");
		JSONObject jsonObj = new JSONObject();
		
		try
		{
			for (Question q : form.questions)
			{
				if (q.Answer != null && (q.Answer.compareTo("") != 0))
					jsonObj.put(q.FieldName, q.Answer);
				//else, do nothing, we won't enter blank answers
			}
		}
		catch (JSONException e1)
		{
			e1.printStackTrace();
			return new String[] {"-1", "Error: JSON error"};
		}

		StringEntity myEntity = null;
		
		//i dont understand why this needs a try/catch
		try {
			myEntity = new StringEntity(jsonObj.toString(), HTTP.UTF_8);
			//Log.v("cody", jsonObj.toString());
			
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return new String[] {"-1","Error: Unsuccessful encoding error"};
		} 
		
		
		myEntity.setContentType("application/json");		
		httppost.setEntity(myEntity);

        //ok here we're trying to get something
        HttpResponse response1 = null;
		try {
			response1 = httpclient.execute(target, httppost, localContext);
			


			//this is where we could print the response if we wanted 
			HttpEntity entity1 = response1.getEntity();
			
			//Log.v(entity1.,"here4");
			
			//Read response, but we don't need this right now
        	BufferedReader reader = new BufferedReader(new InputStreamReader(entity1.getContent(), Charset.forName("UTF-8")));
        	StringBuilder results = new StringBuilder();
        	String line;
        	while ((line = reader.readLine()) != null) {
            	results.append(line + '\n');
        	}
        		
        	//Log.v("cody_test1", response1.getStatusLine().toString());
        	//Log.v("cody_test2",results.toString());
        	
        	//have to run this to free up the connection.
			
			//something broke if this isn't 201
			if (response1.getStatusLine().getStatusCode() != 201)
			{
			   //return new String[] {"-1","Error: SharePont Response: " + response1.getStatusLine().toString()};
				return new String[] {"-1", results.toString()};
			}
			
        	response1.getEntity().consumeContent();
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new String[] {"-1", "Error: ClientProtocol error."};
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new String[] {"-1", "Error: IOException error."};
		}

		return new String[] {"0", "Success: Form uploaded to SharePoint."};
	}
}
