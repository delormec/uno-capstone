package com.example.habitathumanityapp;

import org.json.JSONObject;
import org.json.JSONException;


public class FormHelper 
{
	// Turns the form into a JSON object for pushing to SharePoint
	public static JSONObject jsonize(Form form)
	{
		JSONObject form_json = new JSONObject();		
			
		// Go through each question and add the answer to the field in the json object
		for(Question q : form.questions)
		{
			try
			{
				// Generic answer will be replaced with q.answer when the Question object is updated
				form_json.put(q.FieldName, q.Answer);
			}
			catch(JSONException e)
			{
				System.out.printf("Error pushing form to sharepoint. JSON Exception: %s", e.toString());
			}
		}
			
		return form_json;
	}
}
