package org.habitatomaha.HOST.AsyncTask;

import org.habitatomaha.HOST.Activity.SelectFormActivity;
import org.habitatomaha.HOST.Activity.SelectFormActivityOld;
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
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;



public class DownloadAllTemplates extends AsyncTask {
	
	public Context callingContext;
	public int x;
	
	private ProgressDialog progressDialog;
	private SharedPreferences settings;
	
	
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
		
		if (callingContext instanceof SelectFormActivityOld)
		{
			// If this was called from SelectFormActivity, then populate the spinners with the newly retrieved templates
			SelectFormActivityOld getMethods = (SelectFormActivityOld) callingContext;
			getMethods.startPopulateSpinners();
		}
		
		if (callingContext instanceof SelectFormActivity)
		{
			SelectFormActivity getMethods = (SelectFormActivity) callingContext;
			getMethods.displayTemplateGroups();
		}
	}
	
	
	/** Changes the ProgressDialog to note how many templates have been downloaded so far
	 *  values[0] should be the current template being downloaded
	 *  values[1] should be the total number of templates being downloaded
	 */
	@Override
	protected void onProgressUpdate(Object... values)
	{
		progressDialog.setMessage(String.format("Downloading template %d of %d", values[0], values[1]));
	}
	
	
	
	/**
	 * Sets the callingContext of this task to context and resets the progressDialog
	 * 
	 * @param context	The Context that called this method
	 */
	public void rebuild(Context context)
	{
		this.callingContext = context;
		
		progressDialog = new ProgressDialog(callingContext);
		progressDialog.setMessage("Downloading form templates...");
		progressDialog.show();
	}
	
	
	
	
	@Override
	protected Object doInBackground(Object... params) {
		// TODO Auto-generated method stub
		String URL;
		String list_name;
		String port_string;
		int port;
		
		settings = PreferenceManager.getDefaultSharedPreferences(callingContext);
		
		URL = settings.getString("admin_tool_url","");
		list_name = settings.getString("list_name", "");
		port_string = settings.getString("admin_tool_port","80");
		port = Integer.parseInt(port_string);
		
		Log.v("Downloadtask", "Here is port " + port);
		//AdminDataSource aDS = new AdminDataSource();
		AdminDataSource aDS = new AdminDataSource((android.content.Context)params[0], URL, list_name, port);
		OSTDataSource oDS = new OSTDataSource((android.content.Context)params[0]);
		oDS.open();
		
		String XMLResult = aDS.getAllTemplates();
		
		//if null was returned then we had some type of error when trying to download ids
		if (XMLResult == null)
		{
			oDS.close();
			return null;
		}
		
		TemplateIdList tl = XMLParser.getTemplateList(XMLResult);
		
		//if this is 0, then there are no templates and we should notify the user and exit
		if (tl == null)
		{
			oDS.close();
			return null;	
		}
	
		oDS.removeAllForms();
		oDS.removeAllTemplates();
		
		x = 0;
		for (String id : tl.ids)
		{
			publishProgress(x + 1, tl.ids.size());
			
			String xml = aDS.getTemplateXMLByID(id);
			//TODO -- fix this in the admin tool side
			try
			{
				xml=xml.replaceAll("\\\\n", "").replaceAll("\\\\t", "").replaceAll("\\\\\"", "\"");

				//remove quotes from front and back -- odd as fuck
				xml = xml.substring(1, xml.length()); // This has the potential to throw a String Index out of Bounds Error
			
				//TODO - replace class names
				xml = xml.replaceAll("TextQuestion", TextQuestion.class.getName()).replaceAll("LikertScaleQuestion", LikertScaleQuestion.class.getName()).replaceAll("ChoiceQuestion", ChoiceQuestion.class.getName());
			
				Form form = XMLParser.getForm(xml);
			
				oDS.addTemplate(form);
			}
			catch(Exception e)
			{
				// added by Scott. Found by Monkey
				e.getStackTrace();
			}
			x++;
			
			
		}
	
		oDS.close();
		return null;
	}
}
