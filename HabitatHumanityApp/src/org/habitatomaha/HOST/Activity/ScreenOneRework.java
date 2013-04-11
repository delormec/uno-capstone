package org.habitatomaha.HOST.Activity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.habitatomaha.HOST.R;
import org.habitatomaha.HOST.AsyncTask.DownloadAllTemplates;
import org.habitatomaha.HOST.AsyncTask.UploadAllForms;
import org.habitatomaha.HOST.AsyncTask.UploadForm;
import org.habitatomaha.HOST.Helper.Utility;
import org.habitatomaha.HOST.Model.Error;
import org.habitatomaha.HOST.Model.Form;
import org.habitatomaha.HOST.Model.SpinnerData;
import org.habitatomaha.HOST.Model.Error.Severity;
import org.habitatomaha.HOST.Model.Repository.ErrorLog;
import org.habitatomaha.HOST.Model.Repository.OSTDataSource;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ScreenOneRework extends Activity
{
	private static final int ALL_GROUPS_ALL_FORMS = -1111;
	private static final int ONE_GROUP_ALL_FORMS = -111;
	private static final int ONE_TEMPLATE_ALL_FORMS = -11;
	
	private static String currentUser;
	private View[] layoutStack = new View[4];
	private OSTDataSource database;
	private Views currentView;
	private String[] navTitles = new String[4];
	
	private DownloadAllTemplates downloadTask;
	private UploadAllForms uploadTask;
	
	
	private enum Views
	{
		First,
		Groups,
		Templates,
		Forms
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		database = new OSTDataSource(this);
		
		
		// Restore the task info
		AsyncTask task = (AsyncTask) getLastNonConfigurationInstance();
		if (task != null)
		{
			if (task instanceof DownloadAllTemplates)
			{
				downloadTask = (DownloadAllTemplates) task;
				downloadTask.rebuild(this);
			}

			else if (task instanceof UploadAllForms)
			{
				uploadTask = (UploadAllForms) task;
				uploadTask.rebuild(this);
			}
		}
		
		
		
		LinearLayout layout = new LinearLayout(this);
		layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
		layout.setOrientation(LinearLayout.VERTICAL);
		
		
		// Text above EditText for entering user name
		TextView nameViewTitle = new TextView(this);
		nameViewTitle.setText("Current User:");
		nameViewTitle.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
			
		
		// EditText for entering user name
		EditText nameView = new EditText(this);
		nameView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		if (currentUser != null)
		{
			nameView.setText(currentUser);
		}
		else
		{
			nameView.setText("");
		}		
		
		
		// Begin Button
		Button beginButton = new Button(this);
		beginButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		beginButton.setText("Begin");
		beginButton.setOnClickListener(	new View.OnClickListener()
										{
											public void onClick(View view)
											{
												Utility.hideSoftKeyboard(getInstance());
												displayTemplateGroups();											
											}
										}					
									);
		
		
		// Add the Views in vertical order
		layout.addView(nameViewTitle);
		layout.addView(nameView);
		layout.addView(beginButton);
		
		// Put the layout in the view stack
		layoutStack[Views.First.ordinal()] = layout;
		
		
		setContentView(layoutStack[Views.First.ordinal()]);
		navTitles[0] = "Template groups";
		currentView = Views.First;
	}
	
	
	
	@Override
	public void onBackPressed()
	{
		switch (currentView)
		{
			case First:
				this.finish();
				break;
				
			case Groups:
				setContentView(layoutStack[Views.First.ordinal()]);
				currentView = Views.First;
				break;
				
			case Templates:
				setContentView(layoutStack[Views.Groups.ordinal()]);
				currentView = Views.Groups;
				break;
				
			case Forms:
				setContentView(layoutStack[Views.Templates.ordinal()]);
				currentView = Views.Templates;
				break;
		}

	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main_menu, menu);
		return true;
	}
	
	
	
	@Override
	public AsyncTask onRetainNonConfigurationInstance()
	{	
		// Save downloadTask
		if (downloadTask != null)
		{
			if (downloadTask.getStatus() != AsyncTask.Status.FINISHED)
			{
				return downloadTask;
			}
			else
			{
				return null;
			}
		}
		// Save uploadTask
		else if (uploadTask != null)
		{
			if (uploadTask.getStatus() != AsyncTask.Status.FINISHED)
			{
				return uploadTask;
			}
			else
			{
				return null;
			}
		}
		// Save neither task
		else
		{
			return null;
		}
	}
	
	
	
	
	/**
	 * Displays all the template groups
	 */
	private void displayTemplateGroups()
	{
		// Get the template information
		List<String> templateGroupList = new ArrayList<String>();
		
		database.open();
		templateGroupList = database.getAllTemplateGroups();
		database.close();
		
		// Add "All Groups" to beginning of list
		templateGroupList.add(0, "All Groups");
		
		
		
		// Begin the layout
		LinearLayout layout = new LinearLayout(this);
		layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
		layout.setOrientation(LinearLayout.VERTICAL);
		
		
		// Navigation text
		TextView navText = new TextView(this);
		navText.setText(navString(1));
		navText.setTextSize(30);
		navText.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
		
		
		// Create a view for each template group
		for (final String entry : templateGroupList)
		{
			LinearLayout groupView = new LinearLayout(this);
			groupView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
			groupView.setOrientation(LinearLayout.VERTICAL);
			groupView.setOnClickListener(	new View.OnClickListener()
											{
												public void onClick(View view)
												{
													navTitles[1] = String.format("%s templates", entry);
													displayGroupTemplates(entry);
												}
											}					
										);
			
			
			// Group name
			TextView groupName = new TextView(this);
			groupName.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
			groupName.setText(entry);
			groupName.setTextSize(35);
			groupName.setTextColor(Color.parseColor("#0099CC"));
			
			
			
			// Horizontal rule
			View lineBreak = Utility.lineBreakView(this);
			
			
			// Add the Views in vertical order
			groupView.addView(groupName);
			groupView.addView(lineBreak);
			
			
			layout.addView(groupView);		
		}
		
		
		
		// TODO This is super ugly and can probably be better/cleaner.
		// Wrap the layout in a ScrollView
		ScrollView wrapper = new ScrollView(this);
		wrapper.addView(layout);
		// TODO The 160 padding fix should probably be something else 
		wrapper.setPadding(25, 160, 25, 25);
		
		// Put "navText" above the scroll view
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		//params.addRule(RelativeLayout.BELOW, navText.getId());
		wrapper.setLayoutParams(params);
		
		
		RelativeLayout.LayoutParams navparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		navparams.addRule(RelativeLayout.ABOVE, wrapper.getId());
		navText.setLayoutParams(navparams);
		
		
		// Put the layout in the view stack
		RelativeLayout wrapperWrapper = new RelativeLayout(this);
		wrapperWrapper.addView(navText);
		wrapperWrapper.addView(wrapper);
		layoutStack[Views.Groups.ordinal()] = wrapperWrapper;
									
		setContentView(layoutStack[Views.Groups.ordinal()]);
		currentView = Views.Groups;
	}
	
	

	
	
	/**
	 * Displays all templates for a group
	 * 
	 * @param groupName	The name of the group of templates to display
	 */
	private void displayGroupTemplates(final String groupName)
	{
		List<SpinnerData> templateList;
		database.open();
		if (groupName.compareToIgnoreCase("all groups") == 0)
		{
			templateList = database.getAllTemplateInfo();
			
			SpinnerData allForms = new SpinnerData("All Forms", ALL_GROUPS_ALL_FORMS);
			templateList.add(0, allForms);
		}
		else
		{
			// Get the templates from the group
			templateList = database.getAllTemplateInfoByGroup(groupName);
			
			// Add "All Forms" to beginning of list
			SpinnerData allForms = new SpinnerData("All " + groupName + " Forms", ONE_GROUP_ALL_FORMS);
			templateList.add(0, allForms);
		}
		database.close();

		
		// Begin the layout
		LinearLayout layout = new LinearLayout(this);
		layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
		layout.setOrientation(LinearLayout.VERTICAL);
		
		
		// Navigation text
		TextView navText = new TextView(this);
		navText.setText(navString(2));
		navText.setTextSize(30);
		
		
		// Create a View for each template
		for (final SpinnerData templateData : templateList)
		{			
			LinearLayout templateView = new LinearLayout(this);
			templateView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
			templateView.setOrientation(LinearLayout.VERTICAL);
			
			
			// Template name
			TextView templateName = new TextView(this);
			templateName.setText(templateData.getSpinnerText());
			templateName.setTextSize(35);

			
			
			LinearLayout buttons = new LinearLayout(this);
			// No buttons if "all forms"
			if (templateData.getValue() == ONE_GROUP_ALL_FORMS)
			{
				templateView.setOnClickListener(	new View.OnClickListener()
													{
														public void onClick(View view)
														{
															navTitles[2] = "All forms";
															displayTemplateForms(ONE_GROUP_ALL_FORMS, groupName);
														}
													}					
												);
				templateName.setTextColor(Color.parseColor("#0099CC"));
			}
			else if (templateData.getValue() == ALL_GROUPS_ALL_FORMS)
			{
				templateView.setOnClickListener(	new View.OnClickListener()
													{
														public void onClick(View view)
														{
															navTitles[2] = "All forms";
															displayTemplateForms(ALL_GROUPS_ALL_FORMS, null);														
														}
													}					
												);
				templateName.setTextColor(Color.parseColor("#0099CC"));
			}
			else
			{
				// Begin "buttons view"				
				buttons.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
				buttons.setOrientation(LinearLayout.HORIZONTAL);
				
	
				// New button
				Button newButton = new Button(this);
				newButton.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
				newButton.setText("Create new");
				newButton.setOnClickListener(	new View.OnClickListener()
												{
													public void onClick(View view)
													{
														database.open();
														
														// Create a new Form from the template
														Form template = database.getTemplateById(templateData.getValue());
														long formID = database.addForm(template);						
														Form form = database.getFormById(formID);
														
														
														// Pass the new Form to the EditFormActivity
														Intent intent = new Intent(getInstance(), EditFormActivity.class);
														
														intent.putExtra("formObject", form);	
														intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
																	
														startActivity(intent);
														
														database.close();
													}
												}					
											);
				
				
				// Edit button
				Button editButton = new Button(this);
				editButton.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
				editButton.setText("Edit existing");
				editButton.setOnClickListener(	new View.OnClickListener()
												{
													public void onClick(View view)
													{
														navTitles[2] = templateData.getSpinnerText();
														displayTemplateForms(templateData.getValue(), null);
													}
												}					
											);
				
				// Add the button views
				buttons.addView(newButton);

				database.open();
				List<SpinnerData> template_formInfo = database.getAllFormInfoByTemplateId(templateData.getValue());
				database.close();
				if (template_formInfo.size() > 0)
				{
					buttons.addView(editButton);
				}
			}
		
			
			// Horizontal rule
			View lineBreak = Utility.lineBreakView(this);			
			
			
			// Add Views in vertical order
			templateView.addView(templateName);				
			templateView.addView(buttons);
			templateView.addView(lineBreak);
						
			layout.addView(templateView);	
		}
		

		
		// TODO This is super ugly and can probably be better/cleaner.
		// Wrap the layout in a ScrollView
		ScrollView wrapper = new ScrollView(this);
		wrapper.addView(layout);
		// TODO The 160 padding fix should probably be something else 
		wrapper.setPadding(25, 160, 25, 25);
		
		// Put "navText" above the scroll view
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		//params.addRule(RelativeLayout.BELOW, navText.getId());
		wrapper.setLayoutParams(params);
		
		
		RelativeLayout.LayoutParams navparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		navparams.addRule(RelativeLayout.ABOVE, wrapper.getId());
		navText.setLayoutParams(navparams);
		
		
		// Put the layout in the view stack
		RelativeLayout wrapperWrapper = new RelativeLayout(this);
		wrapperWrapper.addView(navText);
		wrapperWrapper.addView(wrapper);
		layoutStack[Views.Templates.ordinal()] = wrapperWrapper;
		
		setContentView(layoutStack[Views.Templates.ordinal()]);
		currentView = Views.Templates;
	}


	
	

	/**
	 * Displays all the forms for a given template
	 * 
	 * @param templateName	The name of the template
	 */
	private void displayTemplateForms(int templateID, String templateGroup)
	{
		List<SpinnerData> formList = new ArrayList<SpinnerData>();
		
		// Get the forms from the template
		database.open();
		if (templateID == ONE_GROUP_ALL_FORMS)
		{
			formList = database.getAllFormInfoByTemplateGroup(templateGroup);
		}
		else if (templateID == ALL_GROUPS_ALL_FORMS)
		{
			formList = database.getAllFormInfo();
		}
		else
		{
			formList = database.getAllFormInfoByTemplateId(templateID);
		}
		database.close();
		
		
		
		
		// Begin the layout
		LinearLayout layout = new LinearLayout(this);
		layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
		layout.setOrientation(LinearLayout.VERTICAL);

		
		// Navigation text
		TextView navText = new TextView(this);
		navText.setText(navString(3));
		navText.setTextSize(30);
		
		
		// Create a layout for each form
		for (final SpinnerData formData : formList)
		{
			LinearLayout formView = new LinearLayout(this);
			formView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
			formView.setOrientation(LinearLayout.VERTICAL);
			
			
			// Form name
			TextView formName = new TextView(this);
			formName.setText(formData.getSpinnerText());
			formName.setTextSize(35);
			
			
			// Begin "buttons view"
			LinearLayout buttons = new LinearLayout(this);		
			buttons.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
			buttons.setOrientation(LinearLayout.HORIZONTAL);
			
			
			// Edit button
			Button editButton = new Button(this);
			editButton.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
			editButton.setText("Edit");
			editButton.setOnClickListener(	new View.OnClickListener()
											{
												public void onClick(View view)
												{
													database.open();
													Form form = database.getFormById(formData.getValue());
													
													// Pass the form to the EditFormActivity
													Intent intent = new Intent(getInstance(), EditFormActivity.class);
													
													intent.putExtra("formObject", form);	
													intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
																
													startActivity(intent);
													database.close();
												}
											}					
										);
			
			
			// Discard button
			Button discardButton = new Button(this);
			discardButton.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
			discardButton.setText("Discard");
			discardButton.setOnClickListener(	new View.OnClickListener()
												{
													public void onClick(View view)
													{
														discardConfirm(view, formData.getValue());
													}
												}					
											);
			
			
			// Upload button
			Button uploadButton = new Button(this);
			uploadButton.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
			uploadButton.setText("Upload");
			uploadButton.setOnClickListener(new View.OnClickListener()
											{
												public void onClick(View view)
												{
													upload(formData.getValue());
												}
											}					
										);

			
			// Horizontal Rule
			View lineBreak = Utility.lineBreakView(this);
			
			
			// Add the Views in vertical order
			formView.addView(formName);
			buttons.addView(editButton);
			buttons.addView(discardButton);
			buttons.addView(uploadButton);
			formView.addView(buttons);
			formView.addView(lineBreak);
			
			layout.addView(formView);
		}
		
		
		
		// TODO This is super ugly and can probably be better/cleaner.
		// Wrap the layout in a ScrollView
		ScrollView wrapper = new ScrollView(this);
		wrapper.addView(layout);
		// TODO The 160 padding fix should probably be something else 
		wrapper.setPadding(25, 160, 25, 25);

		// Put "navText" above the scroll view
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		//params.addRule(RelativeLayout.BELOW, navText.getId());
		wrapper.setLayoutParams(params);
		
		
		RelativeLayout.LayoutParams navparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		navparams.addRule(RelativeLayout.ABOVE, wrapper.getId());
		navText.setLayoutParams(navparams);
		
		
		// Put the layout in the view stack
		RelativeLayout wrapperWrapper = new RelativeLayout(this);
		wrapperWrapper.addView(navText);
		wrapperWrapper.addView(wrapper);
		layoutStack[Views.Forms.ordinal()] = wrapperWrapper;
		
		setContentView(layoutStack[Views.Forms.ordinal()]);
		currentView = Views.Forms;
	}
	

	
	/**
	 * Confirms the user's intent discard before discarding the Form
	 * 
	 * @param view	The View that called this method
	 */
	private void discardConfirm(final View view, final int formID)
	{
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle("Confirm Discard");
		adb.setMessage("Are you sure you want to discard this form?");
		adb.setCancelable(false);
		adb.setPositiveButton("Discard", 
								new DialogInterface.OnClickListener()
								{
									@Override
									public void onClick(DialogInterface dialog, int id)
									{
										discard(formID);
										
										// Remove the view of the form
										View buttons = (LinearLayout) view.getParent();
										View formView = (LinearLayout) buttons.getParent();										
										((LinearLayout) formView.getParent()).removeView(formView);
										
										return;
									}
								});
		adb.setNegativeButton("Keep", new DialogInterface.OnClickListener()
										{
											@Override
											public void onClick(DialogInterface dialog, int id)
											{
												return;
											}
										});
		
		AlertDialog alertDialog = adb.create();
		alertDialog.show();
		
		return;
	}
		
	
	
	/**
	 * Removes a form from the database
	 * 
	 * @param formID	The ID of the form to remove
	 */
	private void discard(long formID)
	{
		database.open();
		database.removeFormById(formID);
		database.close();
		
		Toast.makeText(this, "Form removed from device", Toast.LENGTH_SHORT).show();	
		return;
	}
	
	
	
	
	
	/**
	 * Attempts to upload the Form to SharePoint.
	 * 
	 * @param view The View that called this method
	 */
	public void upload(final long formID)
	{
		//If there is no connectivity, display a popup and return
		if (!Utility.isNetworkAvailable(this))
		{
			Utility.displayNetworkUnavailableDialog(this);
			return;
		}
		
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle("Confirm Upload");
		adb.setMessage("If the upload is successful, the form will be removed from your device.");
		adb.setCancelable(false);
		adb.setPositiveButton("Upload", 
								new DialogInterface.OnClickListener()
								{
									@Override
									public void onClick(DialogInterface dialog, int id)
									{
										//Had to suppress this warning, not sure what the deal is
										@SuppressWarnings("unchecked")
										AsyncTask<Form, Void, String[]> task = new UploadForm(getInstance());
										
										database.open();
										Form form = database.getFormById(formID);
										database.close();
												
										//Start the task
										task.execute(form);
										try {
											//Wait for the task to finish and get its response
											String[] response = task.get();											
											Toast.makeText(getInstance(), response[1], Toast.LENGTH_LONG).show();

											//response of -1 == error
											//response of 0 == success
											if (response[0].compareTo("0") == 0)
											{
												discard(formID);
												getInstance().finish();
											}
											else
											{
												// Log the upload error
												ErrorLog.log(getInstance(), new Error("Form Upload Error", response[1], Severity.Minor));
											}
											
										} catch (InterruptedException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										} catch (ExecutionException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										
										return;
									}
								});
		adb.setNegativeButton("Wait", new DialogInterface.OnClickListener()
										{
											@Override
											public void onClick(DialogInterface dialog, int id)
											{
												return;
											}
										});
		
		AlertDialog alertDialog = adb.create();
		alertDialog.show();
		
		return;
	}
	
	
	
	
	
	
	
	
	
	
	/**
	 * Returns the context of this activity
	 * 
	 * @return	This
	 */
	public ScreenOneRework getInstance()
	{
		return this;
	}
	
	
	
	/**
	 * Builds the navString for the top of the View
	 * 
	 * @param pieces	The number of pieces (1 - 3)
	 * @return			The navString made from navTitles[]
	 */
	private String navString(int pieces)
	{
		switch (pieces)
		{
			case 1:
				return String.format("%s", navTitles[0]);
			case 2:
				return String.format("%s >\n\t%s", navTitles[0], navTitles[1]);
			case 3:
				return String.format("%s >\n\t%s >\n\t\t%s", navTitles[0], navTitles[1], navTitles[2]);
			default:
				return "";
		}
	}
	
	
	
	
	
	/*---- MENU BUTTON IMPLEMENTATIONS ----*/
	/**
	 * Opens the ErrorLog for display (launches new Activity)
	 * 
	 * @param item	The menu item that called this method
	 */
	public void openErrorLog(MenuItem item)
	{
		Intent intent = new Intent(this, ErrorLogActivity.class);
		startActivity(intent);
	}
		
	@SuppressWarnings("unchecked")
	public void startFormDownload(MenuItem item)
	{
		//If there is no connectivity, display a popup and return
		if (!Utility.isNetworkAvailable(this))
		{
			Utility.displayNetworkUnavailableDialog(this);
			return;
		}
		
		
		downloadTask = new DownloadAllTemplates(this);
		downloadTask.execute(this);
		
		// TODO Change the downloadTask postExecute to reload the Activity
	}
	
	
	
	/**
	 * Called from the menu. <br>
	 * Launches the task that attempts to upload all forms on the device to SharePoint
	 * 
	 * @param menu	The MenuItem that called this method
	 */
	public void uploadAllForms(MenuItem menu)
	{
		//If there is no connectivity, display a popup and return
		if (!Utility.isNetworkAvailable(this))
		{
			Utility.displayNetworkUnavailableDialog(this);
			return;
		}
		
		uploadTask = new UploadAllForms(this);
		uploadTask.execute();
		
		// TODO Change the uploadTask postExecute to reload the Activity
	}
	
	
	
	public void openSettingsActivity(MenuItem item){
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
	}
	/*---- END MENU BUTTON IMPLEMENTATIONS ----*/
}
