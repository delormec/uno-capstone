package com.example.habitathumanityapp.tasks;

import com.example.habitathumanityapp.Form;
import com.example.habitathumanityapp.TemplateList;
import com.example.habitathumanityapp.XMLParser;
import com.example.habitathumanityapp.datasource.AdminDataSource;
import com.example.habitathumanityapp.datasource.OSTDataSource;
import com.example.habitathumanityapp.*;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;



public class downloadAllTemplates extends AsyncTask {
	
	private ProgressDialog progressDialog;
	private Context callingContext;
	
	public downloadAllTemplates(Context context)
	{
		this.callingContext = context;
		this.progressDialog = new ProgressDialog(context);
	}
	
	
	@Override
	protected void onPreExecute()
	{
		progressDialog.setMessage("Downloading form templates...");
		progressDialog.show();
	}
	
	
	
	@Override
	protected void onPostExecute(Object result)
	{
		if (progressDialog.isShowing())
		{
			progressDialog.dismiss();
		}
		
		if (callingContext instanceof MainMenuActivity)
		{
			// If this was called from MainMenuActivity, then populate the spinners with the newly retrieved templates
			MainMenuActivity getMethods = (MainMenuActivity) callingContext;
			getMethods.startPopulateSpinners();
		}
	}
	
	
	
	@Override
	protected void onProgressUpdate(Object... values)
	{
		progressDialog.setMessage(String.format("Downloading template %d of %d", values[0], values[1]));
	}
	
	
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
		
		int x = 0;
		for (String id : tl.ids)
		{
			publishProgress(x + 1, tl.ids.size());
			
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
			
			x++;
		}
	
		oDS.close();
		//Log.v("cody_test",tl.ids.toString());
		return null;
	}
}
