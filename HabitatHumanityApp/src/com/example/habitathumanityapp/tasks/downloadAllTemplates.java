package com.example.habitathumanityapp.tasks;

import com.example.habitathumanityapp.Form;
import com.example.habitathumanityapp.TemplateList;
import com.example.habitathumanityapp.XMLParser;
import com.example.habitathumanityapp.datasource.AdminDataSource;
import com.example.habitathumanityapp.datasource.OSTDataSource;
import com.example.habitathumanityapp.*;

import android.os.AsyncTask;
import android.util.Log;

public class downloadAllTemplates extends AsyncTask {
	@Override
	protected Object doInBackground(Object... params) {
		// TODO Auto-generated method stub
		
		AdminDataSource aDS = new AdminDataSource();
		OSTDataSource oDS = new OSTDataSource((android.content.Context)params[0]);
		oDS.open();
		
		TemplateList tl = XMLParser.getTemplateList(aDS.getAllTemplates());
		
		//if this is 0, then there are no templates and we should notify the user and exit
		if (tl == null)
		{
			oDS.close();
			return null;	
		}
	
		oDS.removeAllForms();
		oDS.removeAllTemplates();
		
		for (String id : tl.ids)
		{
			String xml = aDS.getTemplateXMLByID(id);
			//TODO -- fix this in the admin tool side
			xml=xml.replaceAll("\\\\n", "").replaceAll("\\\\t", "").replaceAll("\\\\\"", "\"");

			//remove quotes from front and back -- odd as fuck
			xml = xml.substring(1, xml.length());
			
			//TODO - replace class names
			xml = xml.replaceAll("TextQuestion", TextQuestion.class.getName()).replaceAll("LikertScaleQuestion", LikertScaleQuestion.class.getName()).replaceAll("ChoiceQuestion", ChoiceQuestion.class.getName());
			
			Form form = XMLParser.getForm(xml);
			
			oDS.addTemplate(form);
			//Log.v("OSTtest", oDS.getAllTemplateInfo().toString());
		}
	
		oDS.close();
		//Log.v("cody_test",tl.ids.toString());
		return null;
	}
}
