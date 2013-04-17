package org.habitatomaha.HOST.Activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.habitatomaha.HOST.R;
import org.habitatomaha.HOST.AsyncTask.DownloadAllTemplates;
import org.habitatomaha.HOST.AsyncTask.UploadAllForms;
import org.habitatomaha.HOST.AsyncTask.UploadForm;
import org.habitatomaha.HOST.Helper.Utility;
import org.habitatomaha.HOST.Model.Error;
import org.habitatomaha.HOST.Model.Form;
import org.habitatomaha.HOST.Model.LayoutInfo;
import org.habitatomaha.HOST.Model.SpinnerData;
import org.habitatomaha.HOST.Model.Error.Severity;
import org.habitatomaha.HOST.Model.Repository.ErrorLog;
import org.habitatomaha.HOST.Model.Repository.OSTDataSource;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

@SuppressLint("NewApi")
public class ScreenOneRework extends Activity
{
	private static final int ALL_GROUPS_ALL_FORMS = -1111;
	private static final int ONE_GROUP_ALL_FORMS = -111;
	public static final int GROUPS = 0;
	public static final int TEMPLATES = 1;
	public static final int FORMS = 2;
	
	
	private static String userName;						// The name of the current user
	private OSTDataSource database;						// An accessor for the SQLite database
	private int currentView;							// Indicated the current view
	private View[] viewStack = new View[3];
	private String[] titles = new String[3];			// For the navigation String at the top		
	private LayoutInfo[] status = new LayoutInfo[3];	// For recovery states
	
	private DownloadAllTemplates downloadTask;	// For managing "DownloadAllTemplates"
	private UploadAllForms uploadTask;			// For managing "UploadAllForms"
	
	
	private Bundle pauseState;					// For recovering in onResume()
	

	/*---------- BEGIN OVERRIDE METHODS ----------*/
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
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
		
		
		// Restore Activity
		if (savedInstanceState != null)
		{	
			userName = savedInstanceState.getString("userName");
			
	
			// Retrieve information about the Views
			status[GROUPS] = (LayoutInfo) savedInstanceState.getSerializable("statusGroups");
			status[TEMPLATES] = (LayoutInfo) savedInstanceState.getSerializable("statusTemplates");
			status[FORMS] = (LayoutInfo) savedInstanceState.getSerializable("statusForms");	
			
			
			// Rebuild each of the Views
			titles = savedInstanceState.getStringArray("titles");
			
			viewStack[GROUPS] = buildGroupsView();
			
			if (status[TEMPLATES] != null)
			{
				viewStack[TEMPLATES] = buildTemplatesView(status[TEMPLATES].groupName);
			}
			if (status[FORMS] != null)
			{
				viewStack[FORMS] = buildFormsView(status[FORMS].templateID, status[FORMS].groupName);
			}
			
			
			// Set View to the saved currentView
			currentView = savedInstanceState.getInt("currentView");		
			setContentView(viewStack[currentView]);
		}
		// New instance of Activity
		else
		{	
			titles[0] = "Template Groups";
					
			displayTemplateGroups();		
		}
	}
	
	
	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState)
	{
		super.onSaveInstanceState(savedInstanceState);

		savedInstanceState.putString("userName", userName);
		savedInstanceState.putStringArray("titles", titles);
		
		savedInstanceState.putInt("currentView", currentView);
		
		savedInstanceState.putSerializable("statusGroups", status[GROUPS]);
		savedInstanceState.putSerializable("statusTemplates", status[TEMPLATES]);
		savedInstanceState.putSerializable("statusForms", status[FORMS]);			
	}
	
	
	
	@Override
	public void onBackPressed()
	{
		switch (currentView)
		{		
			case GROUPS:
				// Go back to home
				this.finish();
				break;
				
			case TEMPLATES:
				// Go back to groups
				viewStack[GROUPS] = buildGroupsView();
				setContentView(viewStack[GROUPS]);
				currentView = GROUPS;
				break;
				
			case FORMS:
				// Go back to templates
				viewStack[TEMPLATES] = buildTemplatesView(status[TEMPLATES].groupName);
				setContentView(viewStack[TEMPLATES]);
				currentView = TEMPLATES;
				break;
		}
		
		invalidateOptionsMenu();
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main_menu, menu);
		return true;
	}
	
	
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		if (currentView == GROUPS)
		{
			menu.getItem(0).setEnabled(true);
			menu.getItem(1).setEnabled(true);
			menu.getItem(2).setEnabled(true);
			menu.getItem(3).setEnabled(true);
		}
		else
		{
			menu.getItem(0).setEnabled(false);
			menu.getItem(1).setEnabled(false);
			menu.getItem(2).setEnabled(false);
			menu.getItem(3).setEnabled(false);
		}
		
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
	
	
	
	@Override
	protected void onPause()
	{
		super.onPause();
		
		pauseState = new Bundle();
		
		pauseState.putString("userName", userName);
		
		pauseState.putSerializable("statusGroups", status[GROUPS]);
		pauseState.putSerializable("statusTemplates", status[TEMPLATES]);
		pauseState.putSerializable("statusForms", status[FORMS]);
		
		pauseState.putStringArray("titles", titles);
		pauseState.putInt("currentView", currentView);
	}
	
	
	
	@Override
	protected void onResume()
	{
		super.onResume();
		
		if (pauseState != null)
		{
			userName = pauseState.getString("userName");
			
			
			// Retrieve information about the Views
			status[GROUPS] = (LayoutInfo) pauseState.getSerializable("statusGroups");
			status[TEMPLATES] = (LayoutInfo) pauseState.getSerializable("statusTemplates");
			status[FORMS] = (LayoutInfo) pauseState.getSerializable("statusForms");	
			
			
			// Rebuild each of the Views
			titles = pauseState.getStringArray("titles");
			
			viewStack[GROUPS] = buildGroupsView();
			
			if (status[TEMPLATES] != null)
			{
				viewStack[TEMPLATES] = buildTemplatesView(status[TEMPLATES].groupName);
			}
			if (status[FORMS] != null)
			{
				viewStack[FORMS] = buildFormsView(status[FORMS].templateID, status[FORMS].groupName);
			}
			
			
			// Set View to the saved currentView
			currentView = pauseState.getInt("currentView");		
			setContentView(viewStack[currentView]);
			
			pauseState = null;
		}
	}
	
	/*---------- END OVERRIDE METHODS ----------*/
	
	
	
	
	
	
	
	
	/*---------- BEGIN DISPLAY METHODS ----------*/
	
	/**
	 * Displays all the template groups and the sign-in
	 */
	private void displayTemplateGroups()
	{
		// Build View
		RelativeLayout groupsView = (RelativeLayout) buildGroupsView();
		
		// Store the relevant information about the View for rebuilding
		LayoutInfo groupsLayoutInfo = new LayoutInfo();
		status[GROUPS] = groupsLayoutInfo;
				
		// Set View
		viewStack[GROUPS] = groupsView;
		setContentView(viewStack[GROUPS]);
		currentView = GROUPS;
		
		invalidateOptionsMenu();
	}
	
		
	
	/**
	 * Displays all templates for a group
	 * 
	 * @param groupName	The name of the group of templates to display
	 */
	private void displayGroupTemplates(final String groupName)
	{
		// Build View
		RelativeLayout templatesView = (RelativeLayout) buildTemplatesView(groupName);
		
		// Store the relevant information about the View for rebuilding
		LayoutInfo templatesLayoutInfo = new LayoutInfo();
		templatesLayoutInfo.groupName = groupName;
		
		status[TEMPLATES] = templatesLayoutInfo;
		
		// Set View
		viewStack[TEMPLATES] = templatesView;
		setContentView(viewStack[TEMPLATES]);
		currentView = TEMPLATES;
		
		invalidateOptionsMenu();
	}

	

	/**
	 * Displays all the forms for a given template
	 * 
	 * @param templateID	The ID of the template whose forms to display
	 * @param templateGroup	The name of the template group
	 */
	private void displayTemplateForms(int templateID, String templateGroup)
	{	
		// Build the View
		RelativeLayout formsView = (RelativeLayout) buildFormsView(templateID, templateGroup);
		
		// Store the relevant information about the View for rebuilding
		LayoutInfo formsLayoutInfo = new LayoutInfo();
		formsLayoutInfo.templateID = templateID;
		formsLayoutInfo.groupName = templateGroup;
		
		status[FORMS] = formsLayoutInfo;
		
		// Set the View
		viewStack[FORMS] = formsView;
		setContentView(viewStack[FORMS]);
		currentView = FORMS;
		
		invalidateOptionsMenu();
	}
	
	/*---------- END DISPLAY METHODS ----------*/


	
	
	
	
	
	
	
	
	/*---------- BEGIN VIEW BUILDER METHODS ----------*/
	
	/**
	 * Builds a View of all the template groups
	 * 
	 * @return	The View of the template groups layout
	 */
	public View buildGroupsView()
	{		
		// Get the template information
		List<String> templateGroupList = new ArrayList<String>();
		
		database.open();
		templateGroupList = database.getAllTemplateGroups();		
		templateGroupList.add(0, "All Templates");
		database.close();
		
		
		// Pre-declare the Views/IDs for RelativeLayout
		TextView navText = new TextView(this);
		navText.setId(1);
		
		ScrollView scrollView = new ScrollView(this);
		scrollView.setId(2);
		
		LinearLayout signInView = new LinearLayout(this);
		signInView.setId(3);
		
		RelativeLayout.LayoutParams relParams;
		LinearLayout.LayoutParams linParams;

		
		// TODO All groups View separate from other groups
		// TODO Button margins
		
			
		
		signInView.setOrientation(LinearLayout.VERTICAL);
		
		relParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		relParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		
		signInView.setLayoutParams(relParams);

		
		
		// Display the userName/Login View
		if (userName != null && userName.compareTo("") != 0)
		{				
			TextView nameText = new TextView(this);
			nameText.setText(String.format("Signed in as: %s   ", userName));
			nameText.setTextSize(20);
			
			// Change name button
			Button changeButton = new Button(this);
			changeButton.setText("Change");
			changeButton.setOnClickListener(	new View.OnClickListener()
												{
													public void onClick(View view)
													{				
														AlertDialog.Builder signInAlert = createSignInAlert();
														signInAlert.show();
													}
												}
											);
			
			// Logout button
			Button logoutButton = new Button(this);
			logoutButton.setText("Log out");
			logoutButton.setOnClickListener(	new View.OnClickListener()
												{
													public void onClick(View view)
													{				
														userName = null;
														setView(GROUPS, buildGroupsView());
													}
												}
											);
			
			signInView.addView(nameText);
			signInView.addView(changeButton);
			signInView.addView(logoutButton);
		}
		// Display the "sign in" View
		else
		{
			//TextView nameText = new TextView(this);
			//nameText.setText("Not signed in   ");
			//nameText.setTextSize(20);
			
			Button signInButton = new Button(this);
			signInButton.setText("Sign in");
			signInButton.setOnClickListener(	new View.OnClickListener()
												{
													public void onClick(View view)
													{			
														AlertDialog.Builder signInAlert = createSignInAlert();
														signInAlert.show();
													}
												}
											);
			
			//signInView.addView(nameText);
			signInView.addView(signInButton);
		}
		
		
		
		
		
		// Begin the groups layout
		LinearLayout layoutOfGroups = new LinearLayout(this);
		layoutOfGroups.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		layoutOfGroups.setOrientation(LinearLayout.VERTICAL);
		
		
		
		// Create a view for each template group
		for (final String entry : templateGroupList)
		{	
			LinearLayout groupView = new LinearLayout(this);
			
			linParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			linParams.setMargins(25, 20, 25, 0);
			groupView.setLayoutParams(linParams);
			
			groupView.setBackgroundColor(Color.parseColor("#CCCCCC"));
			groupView.setOrientation(LinearLayout.VERTICAL);
			
			
			
			
			// Group name
			TextView groupName = new TextView(this);
			
			linParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			linParams.setMargins(25, 0, 0, 0);
			groupName.setLayoutParams(linParams);
			
			groupName.setTextSize(35);
			groupName.setTextColor(Color.parseColor("#0099CC"));
			
			
			// "All Templates" Group
			if (entry.compareToIgnoreCase("all templates") == 0)
			{
				groupName.setText("View All Templates");
				groupName.setGravity(Gravity.CENTER_HORIZONTAL);
				
				groupView.setOnClickListener(	new View.OnClickListener()
													{
														public void onClick(View view)
														{
															titles[1] = String.format("All Groups' Templates");
															displayGroupTemplates("all templates");
														}
													}
												);
				
				
				// "-OR-"
				TextView orView = new TextView(this);
				orView.setText("-OR-");
				orView.setTextSize(20);
				orView.setGravity(Gravity.CENTER_HORIZONTAL);
				
				// "Choose a Template Group"
				TextView chooseView = new TextView(this);
				chooseView.setText("Choose a Template Group");
				chooseView.setTextSize(20);
				chooseView.setGravity(Gravity.CENTER_HORIZONTAL);
				
				
				// Add the views and continue to next iteration
				groupView.addView(Utility.lineBreakView(this));
				groupView.addView(groupName);
				groupView.addView(Utility.lineBreakView(this));
				
				layoutOfGroups.addView(groupView);
				layoutOfGroups.addView(orView);
				layoutOfGroups.addView(chooseView);
				
				continue;
			}
			// Normal Template Group
			else
			{
				groupName.setText(entry);
				
				groupView.setOnClickListener(	new View.OnClickListener()
												{
													public void onClick(View view)
													{
														titles[1] = String.format("%s templates", entry);
														displayGroupTemplates(entry);
													}
												}					
											);
			}

			
			// Add the Views in vertical order
			groupView.addView(Utility.lineBreakView(this));
			groupView.addView(groupName);
			groupView.addView(Utility.lineBreakView(this));
			
			
			layoutOfGroups.addView(groupView);	
		}
		
		
				
		// Navigation text
		//navText.setText(titles[0]);
		//navText.setTextSize(30);
		//navText.setPadding(0, 35, 0, 0);
		
		relParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		relParams.addRule(RelativeLayout.BELOW, signInView.getId());
		navText.setLayoutParams(relParams);

		
		// Put the layout of groupViews into a scrollView
		relParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		relParams.addRule(RelativeLayout.BELOW, signInView.getId());
		scrollView.setLayoutParams(relParams);
	
		scrollView.addView(layoutOfGroups);
		
		
		// Put it all in one wrapping layout
		RelativeLayout wholeLayout = new RelativeLayout(this);		
		wholeLayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
		wholeLayout.setBackgroundColor(Color.parseColor("#EEEEEE"));
		wholeLayout.setPadding(25, 25, 25, 0);
		
		wholeLayout.addView(signInView);
		//wholeLayout.addView(navText);
		wholeLayout.addView(scrollView);		
		
		return wholeLayout;
	}
	
	
	
	/**
	 * Builds the View of templates with given group name
	 * 
	 * @param groupName	The name of the group
	 * 
	 * @return	The View of the templates with group groupName
	 */
	private View buildTemplatesView(final String groupName)
	{
		List<SpinnerData> templateList;
		
		database.open();
		if (groupName.compareToIgnoreCase("all templates") == 0)
		{
			templateList = database.getAllTemplateInfo();
			
			// Add "All Forms" to the beginning of list
			List<SpinnerData> allFormInfo = database.getAllFormInfo();
			SpinnerData allForms = new SpinnerData(String.format("All Created Forms (%d)", allFormInfo.size()), ALL_GROUPS_ALL_FORMS);
			templateList.add(0, allForms);
		}
		else
		{
			// Get the templates from the group
			templateList = database.getAllTemplateInfoByGroup(groupName);
			
			// Add "All Forms" to beginning of list
			List<SpinnerData> templateForms = database.getAllFormInfoByTemplateGroup(groupName);
			SpinnerData allForms = new SpinnerData(String.format("All %s Forms (%d)", groupName, templateForms.size()), ONE_GROUP_ALL_FORMS);
			templateList.add(0, allForms);
		}
		database.close();

		
		
		// Pre-declare the Views/IDs for RelativeLayout
		TextView navText = new TextView(this);
		navText.setId(1);
		
		ScrollView scrollView = new ScrollView(this);
		scrollView.setId(2);
		
		RelativeLayout.LayoutParams relParams;
		LinearLayout.LayoutParams linParams;
		
		
		// Begin the layout
		LinearLayout layoutOfTemplates = new LinearLayout(this);
		layoutOfTemplates.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
		layoutOfTemplates.setOrientation(LinearLayout.VERTICAL);
		
		
		
		// Create a View for each template
		for (final SpinnerData templateData : templateList)
		{			
			LinearLayout templateView = new LinearLayout(this);
			
			linParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			linParams.setMargins(25, 10, 25, 10);
			templateView.setLayoutParams(linParams);
			
			templateView.setBackgroundColor(Color.parseColor("#CCCCCC"));
			templateView.setOrientation(LinearLayout.VERTICAL);
			


			
			// Template name
			TextView templateName = new TextView(this);
			
			linParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			linParams.setMargins(25, 0, 0, 0);
			templateName.setLayoutParams(linParams);
			
			templateName.setTextSize(35);
			templateName.setTextColor(Color.parseColor("#0099CC"));
			
			templateName.setText(templateData.getSpinnerText());
			
			

			
			// "Create New" and "Edit Existing"
			LinearLayout buttons = new LinearLayout(this);
			// No buttons if "all forms"
			if (templateData.getValue() == ONE_GROUP_ALL_FORMS)
			{
				templateView.setOnClickListener(	new View.OnClickListener()
													{
														public void onClick(View view)
														{
															titles[2] = String.format("All %s forms", groupName);
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
															titles[2] = "All Groups' Forms";
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
				newButton.setText("Create New");
				newButton.setOnClickListener(	new View.OnClickListener()
												{
													public void onClick(View view)
													{
														database.open();
														
														// Create a new Form from the template
														Form template = database.getTemplateById(templateData.getValue());
														long formID = database.addForm(template);						
														Form form = database.getFormById(formID);
														form.meta.form_id = formID;
														
														
														// Store filledDate and filledBy
														Calendar cal = Calendar.getInstance();
														form.meta.filledDate = String.format("%d/%d/%d", cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.YEAR));
														form.meta.filledBy = userName;
														
														
														database.updateForm(form);
														
														// Pass the new Form to the EditFormActivity
														Intent intent = new Intent(getInstance(), EditFormActivity.class);
														
														intent.putExtra("formID", formID);	
														intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
																	
														startActivity(intent);
														
														database.close();
													}
												}					
											);
				
				
				// Edit button
				Button editButton = new Button(this);
				editButton.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
				editButton.setOnClickListener(	new View.OnClickListener()
												{
													public void onClick(View view)
													{
														titles[2] = String.format("%s Forms", templateData.getSpinnerText());
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
					editButton.setText(String.format("Edit Existing (%d)", template_formInfo.size()));
					buttons.addView(editButton);
				}
			}
		 			
			
			
			// Add Views in vertical order
			templateView.addView(Utility.lineBreakView(this));
			templateView.addView(templateName);				
			templateView.addView(buttons);
			templateView.addView(Utility.lineBreakView(this));
						
			layoutOfTemplates.addView(templateView);	
		}
		

		
		// Navigation text
		navText.setText(titles[1]);
		navText.setTextSize(30);

		relParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		relParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		navText.setLayoutParams(relParams);


		// Put the layout of groupViews into a scrollView
		relParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		relParams.addRule(RelativeLayout.BELOW, navText.getId());
		scrollView.setLayoutParams(relParams);

		scrollView.addView(layoutOfTemplates);


		// Put it all in one wrapping layout
		RelativeLayout wholeLayout = new RelativeLayout(this);		
		wholeLayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
		wholeLayout.setBackgroundColor(Color.parseColor("#EEEEEE"));

		wholeLayout.addView(navText);
		wholeLayout.addView(scrollView);	

		return wholeLayout;
	}
	
	
	
	/**
	 * Builds a View of the Forms with given templateID/templateGroup
	 * 
	 * @param templateID	The ID of the template
	 * @param templateGroup	The group of the template
	 * 
	 * @return	A View of the Forms for templateID/templateGroup
	 */
	private View buildFormsView(int templateID, String templateGroup)	 
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
		
		
		
		// Pre-declare the Views/IDs for RelativeLayout
		TextView navText = new TextView(this);
		navText.setId(1);

		ScrollView scrollView = new ScrollView(this);
		scrollView.setId(2);

		RelativeLayout.LayoutParams relParams;
		LinearLayout.LayoutParams linParams;
		
		
		
		// Begin the layout
		LinearLayout layoutOfForms = new LinearLayout(this);
		layoutOfForms.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
		layoutOfForms.setOrientation(LinearLayout.VERTICAL);
		
		
		
		// TODO Button margins
		
		
		
		// Create a layout for each form
		for (final SpinnerData formData : formList)
		{
			LinearLayout formView = new LinearLayout(this);
			
			linParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			linParams.setMargins(25, 20, 25, 0);
			formView.setLayoutParams(linParams);
			
			formView.setBackgroundColor(Color.parseColor("#CCCCCC"));
			formView.setOrientation(LinearLayout.VERTICAL);
			
			
			// Form name
			TextView formName = new TextView(this);
			formName.setText(formData.getSpinnerText());
			formName.setTextSize(35);
			formName.setTextColor(Color.parseColor("#0099CC"));
			
			
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
													
													// Pass the form to the EditFormActivity
													Intent intent = new Intent(getInstance(), EditFormActivity.class);
													
													intent.putExtra("formID", (long) formData.getValue());	
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


			
			// Add the Views in vertical order
			formView.addView(Utility.lineBreakView(this));
			formView.addView(formName);
			
			buttons.addView(editButton);
			buttons.addView(discardButton);
			buttons.addView(uploadButton);
			
			formView.addView(buttons);
			formView.addView(Utility.lineBreakView(this));
			
			layoutOfForms.addView(formView);
		}
			
		
		// Navigation text
		navText.setText(titles[2]);
		navText.setTextSize(30);

		relParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		relParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		navText.setLayoutParams(relParams);


		// Put the layout of groupViews into a scrollView
		relParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		relParams.addRule(RelativeLayout.BELOW, navText.getId());
		scrollView.setLayoutParams(relParams);

		scrollView.addView(layoutOfForms);


		// Put it all in one wrapping layout
		RelativeLayout wholeLayout = new RelativeLayout(this);		
		wholeLayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
		wholeLayout.setBackgroundColor(Color.parseColor("#EEEEEE"));

		wholeLayout.addView(navText);
		wholeLayout.addView(scrollView);	

		return wholeLayout;
	}
	
	/*---------- END VIEW BUILDER METHODS ----------*/
	
	
	
	
	
	
	
	
	
	
	/*---------- BEGIN METHODS THAT IMPLEMENT VARIOUS BUTTONS ----------*/
	
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
	 * Removes a Form from the database
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
	
	/*---------- END METHODS THAT IMPLEMENT VARIOUS BUTTONS ----------*/
	
	
	
	
	
	
	
	
	
	
	/*---------- BEGIN MENU BUTTON METHODS ----------*/
	
	/**
	 * Opens the ErrorLog for display (launches new Activity)
	 * 
	 * @param item	The MenuItem that called this method
	 */
	public void openErrorLog(MenuItem item)
	{
		Intent intent = new Intent(this, ErrorLogActivity.class);
		startActivity(intent);
	}
		
	
	
	/**
	 * Begins the Download Task
	 * 
	 * @param item	The MenuItem that called this method
	 */
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
	}
	
	
	
	/**
	 * Opens the Settings Activity
	 * 
	 * @param item	The MenuItem that called this method
	 */
	public void openSettingsActivity(MenuItem item){
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
	}
	
	/*---------- END MENU BUTTON METHODS ----------*/
	
	
	
	
	
	
	
	/*---------- BEGIN UNCATEGORIZED METHODS ----------*/
	
	// TODO javadoc
	private AlertDialog.Builder createSignInAlert()
	{
		// Set up the sign-in EditText Alert
		AlertDialog.Builder signInAlertBuilder = new AlertDialog.Builder(this);
		
		signInAlertBuilder.setTitle("Sign in");
		signInAlertBuilder.setMessage("Enter your name:");
		
		final EditText nameEdit = new EditText(this);
						
		signInAlertBuilder.setView(nameEdit);
		signInAlertBuilder.setPositiveButton("Sign in", new DialogInterface.OnClickListener()
												{
													public void onClick(DialogInterface dialog, int button)
													{
														// Reset the view to reflect the sign-in change
														userName = nameEdit.getText().toString();
														setView(GROUPS, buildGroupsView());															
													}
												}
											);
		return signInAlertBuilder;
	}
	
	
	
	// TODO javadoc
	public void setView(int viewSlot, View view)
	{
		viewStack[viewSlot] = view;
		setContentView(viewStack[viewSlot]);
		currentView = viewSlot;
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
	
	/*---------- END UNCATEGORIZED METHODS ----------*/
}



