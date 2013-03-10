package com.example.habitathumanityapp;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;


public class FormHelper 
{
	/**
	  * Takes a Form object and creates a JSON Object to use for SharePoint
	  * 
	  * @param 	form	The form to create the JSON Object from
	  * @return			A JSON Object representing the form
	  */
	public static JSONObject jsonize(Form form)
	{
		JSONObject form_json = new JSONObject();

		
		// Go through each question and add the answer to the field in the json object
		for(Question q : form.questions)
		{
			try
			{
				form_json.put(q.FieldName, q.Answer);
			}
			catch(JSONException e)
			{
				System.out.printf("Error pushing form to sharepoint. JSON Exception: %s", e.toString());
			}
		}
		
		return form_json;
	}
	
	/**
	  * @return		A generic Form object
	  */
	public static Form dummyForm()
	{
		Form dummyForm = new Form();
		
		TextQuestion q1 = new TextQuestion();
		q1.Text = "This is the text of a text question.";
		
		ChoiceQuestion q2 = new ChoiceQuestion();
		q2.Text = "This is the text of a choice question.";
		q2.options.add("Choice Option A");
		q2.options.add("Choice Option B");
		q2.options.add("Choice Option C");
		q2.options.add("Choice Option D");
		
		LikertScaleQuestion q3 = new LikertScaleQuestion();
		q3.Text = "This is the text of a likert scale question.";
		q3.labels.add("Likert Low Label");
		q3.labels.add("Likert High Label");
		q3.steps = "5";
		
		
		dummyForm.questions.add(q1);
		dummyForm.questions.add(q2);
		dummyForm.questions.add(q3);
		
		return dummyForm;
	}
	
	
	/**
	 * Dumps the contents of a form object in to the LogCat
	 * 
	 * @param form	The Form to dump
	 */
	public static void dumpForm(Form form)
	{
		// Feel free to add things if you want 
		
		Log.v("ryan_debug", "Beginning form dump...");
		
		Log.v("ryan_debug", String.format("Template_ID: %s", form.meta.template_id));
		Log.v("ryan_debug", String.format("Form_ID: %s", form.meta.form_id));
		
		for (Question q : form.questions)
		{
			Log.v("ryan_debug", String.format("\tQuestion: %s", q.Text));
			Log.v("ryan_debug", String.format("\t\tAnswer: %s", q.Answer));
		}
	}
}