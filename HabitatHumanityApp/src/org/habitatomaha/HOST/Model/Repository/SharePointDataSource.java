package org.habitatomaha.HOST.Model.Repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

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
import org.habitatomaha.HOST.Model.ChoiceQuestion;
import org.habitatomaha.HOST.Model.Form;
import org.habitatomaha.HOST.Model.Question;
import org.habitatomaha.HOST.SharePointAuthentication.NTLMSchemeFactory;
import org.json.JSONException;
import org.json.JSONObject;

// XML Parser Helper Classes

/**
 * Interactions with a sharepoint site
 * @author Cody
 */
public class SharePointDataSource {
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
	public static String[] uploadFormToSharePoint(Form form, String URL, String list_name, String user_name, String password, String domain, String port)
	{
		HttpContext localContext;
		
		DefaultHttpClient httpclient = new DefaultHttpClient();
        httpclient.getAuthSchemes().register("ntlm", new NTLMSchemeFactory());
        NTCredentials creds = new NTCredentials(user_name, password, domain, domain);
        httpclient.getCredentialsProvider().setCredentials(AuthScope.ANY, creds);

        String scheme = "http";
        
        //Remove https:// from the URL and set the scheme
        if (URL.startsWith("https://"))
        {
        	URL = URL.substring(8);
        	scheme = "https";
        }
        
        //Remove http:// from the URL and set the scheme
        if (URL.startsWith("http://"))
        {
        	URL = URL.substring(7);
        	scheme = "http";
        }
        
        //If port is equal to 443, then it should be https scheme
        if (port.compareTo(String.valueOf(443)) == 0)
        {
        	scheme = "https";
        }
        
        HttpHost target = new HttpHost(URL, Integer.parseInt(port), scheme);
        localContext = new BasicHttpContext();
        
    	HttpPost httppost = new HttpPost(list_name);
    	httppost.setHeader("Accept", "application/json");
        
    	List<String> fields = new ArrayList<String>();
    	
    	for (Question q : form.questions)
    	{
    		if (q.Answer != null && (q.Answer.compareTo("") != 0))
    		{
    			if (q.getClass() == ChoiceQuestion.class)
    			{
    				if (((ChoiceQuestion)q).multipleselect.compareTo("true") == 0)
    				{					
    					//split multiple choice answers up
    					String[] answers;	
    					answers = q.Answer.split(";");
    
    					String joined_fields = "";
    					
    					//end object looks like this:
    					//"REJECTION_REASON":[{"__metadata": {"uri": "http://habitat.taic.net/omaha/unotestsite/_vti_bin/listdata.svc/ConstructionAtlasTestREJECTION_REASON('No utilities')"}}]
    					for (String answer : answers)
    					{
    						//append https or http to the URL
    						if(scheme.compareTo("https") == 0)
    							joined_fields = joined_fields + "{\"__metadata\": {\"uri\": \"" + "https://" + URL + list_name + q.FieldName + "('" + answer + "')\"}},";
    						else
    							joined_fields = joined_fields + "{\"__metadata\": {\"uri\": \"" + "http://" + URL + list_name + q.FieldName + "('" + answer + "')\"}},";
    					}
    
    					//remove the last comma, god i hate java -- why the fuck is there no join command
    					if (joined_fields.length() != 0)
    						joined_fields = joined_fields.substring(0, joined_fields.length()- 1);
    					
    					
    					fields.add(JSONObject.quote(q.FieldName) + ":[" + joined_fields + "]");
    				}
    				else
    				{
    					fields.add(JSONObject.quote(q.FieldName) + ":" + JSONObject.quote(q.Answer));
    				}
    			}
    			else
    			{
    				fields.add(JSONObject.quote(q.FieldName) + ":" + JSONObject.quote(q.Answer));
    			}
    
    		}
    		//else, do nothing, we won't enter blank answers
    	}
    
    	// If filledBy has a field name
    	if (form.meta.filledByFieldName == null || form.meta.filledByFieldName.compareTo("") == 0)
    	{}
    	else
    	{
    		// If filledBy is not null/empty
    		if (form.meta.filledBy != null && form.meta.filledBy.compareTo("") != 0)
    		{
    			fields.add(JSONObject.quote(form.meta.filledByFieldName) + ":" + JSONObject.quote(form.meta.filledBy));
    		}
    	}
    	
    	// If filledDate has a field name
    	if (form.meta.filledDateFieldName == null || form.meta.filledDateFieldName.compareTo("") == 0)
    	{}
    	else
    	{
    		// If filledDate is not null/empty
    		if (form.meta.filledDate != null && form.meta.filledDate.compareTo("") != 0)
    		{
    			fields.add(JSONObject.quote(form.meta.filledDateFieldName) + ":" + JSONObject.quote(form.meta.filledDate));
    		}
    	}   	
    
    	StringEntity myEntity = null;
    	
    	//i dont understand why this needs a try/catch
    	try {
    		String joined_fields = ""; 
    		
    		for (String temp : fields)
    			joined_fields = joined_fields + temp + ",";
    
    		if (joined_fields.length() != 0)
    			joined_fields = joined_fields.substring(0, joined_fields.length() - 1);
    		
    		joined_fields = "{" + joined_fields + "}";
    		
    		myEntity = new StringEntity(joined_fields, HTTP.UTF_8);
    		//Log.v("cody", joined_fields);
    		
    	}
    	catch (UnsupportedEncodingException e2) {
    		e2.printStackTrace();
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
