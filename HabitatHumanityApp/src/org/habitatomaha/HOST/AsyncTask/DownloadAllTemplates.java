package org.habitatomaha.HOST.AsyncTask;

import org.habitatomaha.HOST.*;
import org.habitatomaha.HOST.Activity.SelectFormActivity;
import org.habitatomaha.HOST.Helper.XMLParser;
import org.habitatomaha.HOST.Model.ChoiceQuestion;
import org.habitatomaha.HOST.Model.Form;
import org.habitatomaha.HOST.Model.LikertScaleQuestion;
import org.habitatomaha.HOST.Model.TemplateIdList;
import org.habitatomaha.HOST.Model.TextQuestion;
import org.habitatomaha.HOST.Model.Repository.AdminDataSource;
import org.habitatomaha.HOST.Model.Repository.OSTDataSource;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;



public class DownloadAllTemplates extends AsyncTask {
	
	private ProgressDialog progressDialog;
	private Context callingContext;
	
	public DownloadAllTemplates(Context context)
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
		
		if (callingContext instanceof SelectFormActivity)
		{
			// If this was called from MainMenuActivity, then populate the spinners with the newly retrieved templates
			SelectFormActivity getMethods = (SelectFormActivity) callingContext;
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
		
		TemplateIdList tl = XMLParser.getTemplateList(aDS.getAllTemplates());
		
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
