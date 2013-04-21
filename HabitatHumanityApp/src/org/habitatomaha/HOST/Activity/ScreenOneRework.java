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
import org.habitatomaha.HOST.Model.LayoutInfo;
import org.habitatomaha.HOST.Model.SpinnerData;
import org.habitatomaha.HOST.Model.Error.Severity;
import org.habitatomaha.HOST.Model.Repository.ErrorLog;
import org.habitatomaha.HOST.Model.Repository.OSTDataSource;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")

public class ScreenOneRework extends Activity
{
	private static final int ALL_GROUPS_ALL_FORMS = -1111;
	private static final int ONE_GROUP_ALL_FORMS = -111;
	private static final int HOME = 0;
	private static final int GROUPS = 1;
	private static final int TEMPLATES = 2;
	private static final int FORMS = 3;
	
	
	private static String userName;						// The name of the current user
	private OSTDataSource database;						// An accessor for the SQLite database
	private int currentView;							// Indicated the current view
	private View[] viewStack = new View[4];
	private String[] navTitles = new String[4];			// For the navigation String at the top		
	private LayoutInfo[] status = new LayoutInfo[4];	// For recovery states
	
	private DownloadAllTemplates downloadTask;	// For managing "DownloadAllTemplates"
	private UploadAllForms uploadTask;			// For managing "UploadAllForms"
	
	
	private Bundle pauseState;
	
	
	
	

	/*---------- BEGIN OVERRIDE METHODS ----------*/
	
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
		
		
		// Restore Activity
		if (savedInstanceState != null)
		{	
			userName = savedInstanceState.getString("userName");
			
	
			// Retrieve information about the Views
			status[HOME] = (LayoutInfo) savedInstanceState.getSerializable("statusHome");
			status[GROUPS] = (LayoutInfo) savedInstanceState.getSerializable("statusGroups");
			status[TEMPLATES] = (LayoutInfo) savedInstanceState.getSerializable("statusTemplates");
			status[FORMS] = (LayoutInfo) savedInstanceState.getSerializable("statusForms");	
			
			
			// Rebuild each of the Views
			navTitles = savedInstanceState.getStringArray("navTitles");
			
			viewStack[HOME] = buildHomeView();
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
			navTitles[0] = "Template groups";
					
			displayHome();		
		}
	}
	
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState)
	{
		super.onSaveInstanceState(savedInstanceState);
		
		saveUserName();
		
		savedInstanceState.putString("userName", userName);
		savedInstanceState.putStringArray("navTitles", navTitles);
		
		savedInstanceState.putInt("currentView", currentView);
		
		savedInstanceState.putSerializable("statusHome", status[HOME]);
		savedInstanceState.putSerializable("statusGroups", status[GROUPS]);
		savedInstanceState.putSerializable("statusTemplates", status[TEMPLATES]);
		savedInstanceState.putSerializable("statusForms", status[FORMS]);			
	}
	
	
	
	@Override
	public void onBackPressed()
	{
		switch (currentView)
		{
			case HOME:
				this.finish();
				break;
				
			case GROUPS:
				// Go back to home
				viewStack[HOME] = buildHomeView();
				setContentView(viewStack[HOME]);
				currentView = HOME;
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
		if (currentView == HOME)
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
		
		saveUserName();
		pauseState.putString("userName", userName);
		
		pauseState.putSerializable("statusHome", status[HOME]);
		pauseState.putSerializable("statusGroups", status[GROUPS]);
		pauseState.putSerializable("statusTemplates", status[TEMPLATES]);
		pauseState.putSerializable("statusForms", status[FORMS]);
		
		pauseState.putStringArray("navTitles", navTitles);
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
			status[HOME] = (LayoutInfo) pauseState.getSerializable("statusHome");
			status[GROUPS] = (LayoutInfo) pauseState.getSerializable("statusGroups");
			status[TEMPLATES] = (LayoutInfo) pauseState.getSerializable("statusTemplates");
			status[FORMS] = (LayoutInfo) pauseState.getSerializable("statusForms");	
			
			
			// Rebuild each of the Views
			navTitles = pauseState.getStringArray("navTitles");
			
			viewStack[HOME] = buildHomeView();
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
	 * Displays the home View for the Activity
	 */
	private void displayHome()
	{
		// Build View
		RelativeLayout homeView = (RelativeLayout) buildHomeView();
		
		// Store relevant info about the View for rebuilding
		LayoutInfo homeLayoutInfo = new LayoutInfo();
		status[HOME] = homeLayoutInfo;

		// Set View
		viewStack[HOME] = homeView;
		setContentView(viewStack[HOME]);
		currentView = HOME;
		
		invalidateOptionsMenu();
	}
	
	
	
	/**
	 * Displays all the template groups
	 */
	private void displayTemplateGroups()
	{
		// Build View
		RelativeLayout groupsView = (RelativeLayout) buildGroupsView();
		
		// Store the userName from HOME
		saveUserName();
		
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
	 * Builds the home View of this Activity
	 * 
	 * @return	The View of the home layout of this Activity
	 */
	private View buildHomeView()
	{
		RelativeLayout.LayoutParams params;
		
		//Pre-declare layout Views/IDs for RelativeLayout
		TextView nameViewTitle = new TextView(this);
		nameViewTitle.setId(1);
		
		EditText nameView = new EditText(this);
		nameView.setId(2);
		
		Button beginButton = new Button(this);
		beginButton.setId(3);
		
		
		
		
		// Begin layout
		RelativeLayout layout = new RelativeLayout(this);
		layout.setLayoutParams(new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
		
		
		// Text above EditText for entering user name			
		nameViewTitle.setText("Current User:");
		nameViewTitle.setTextSize(30);			
		
		params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		nameViewTitle.setLayoutParams(params);
			
		
		
		// EditText for entering user name
		if (userName != null)
		{
			nameView.setText(userName);
		}
		else
		{
			nameView.setText("");
		}
				
		params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.BELOW, nameViewTitle.getId());
		nameView.setLayoutParams(params);
				
		
		
		// Begin Button
		beginButton.setText("Begin");
		beginButton.setTextSize(30);
		beginButton.setOnClickListener(	new View.OnClickListener()
										{
											public void onClick(View view)
											{
												Utility.hideSoftKeyboard(getInstance());
												displayTemplateGroups();											
											}
										}					
									);
		
		params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.BELOW, nameView.getId());
		beginButton.setLayoutParams(params);
	
		
		
		
		// Add the Views to the Layout in vertical order
		layout.addView(nameViewTitle);
		layout.addView(nameView);
		layout.addView(beginButton);
		
		return layout;
	}
	
	
	
	/**
	 * Builds a View of all the template groups
	 * 
	 * @return	The View of the template groups layout
	 */
	private View buildGroupsView()
	{	
		// Get the template information
		List<String> templateGroupList = new ArrayList<String>();
		
		database.open();
		templateGroupList = database.getAllTemplateGroups();
		database.close();
		
		// Add "All Groups" to beginning of list
		templateGroupList.add(0, "All Groups");
		
	
		
		// Pre-declare the Views/IDs for RelativeLayout
		TextView navText = new TextView(this);
		navText.setId(GROUPS);
		
		ScrollView scrollView = new ScrollView(this);
		scrollView.setId(2);
		
		RelativeLayout.LayoutParams relParams;
		
			
		
		// Begin the layout
		LinearLayout layoutOfGroups = new LinearLayout(this);
		layoutOfGroups.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
		layoutOfGroups.setOrientation(LinearLayout.VERTICAL);
		
		// Create a view for each template group
		for (final String entry : templateGroupList)
		{
			LinearLayout groupView = new LinearLayout(this);
			groupView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
			groupView.setPadding(25, 25, 25, 0);
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
			
			
			layoutOfGroups.addView(groupView);		
		}
		
		
				
		// Navigation text
		navText.setText(navString(1));
		navText.setTextSize(30);
		
		relParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		relParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		navText.setLayoutParams(relParams);

		
		// Put the layout of groupViews into a scrollView
		relParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		relParams.addRule(RelativeLayout.BELOW, navText.getId());
		scrollView.setLayoutParams(relParams);
		
		scrollView.addView(layoutOfGroups);
		
		
		// Put it all in one wrapping layout
		RelativeLayout wholeLayout = new RelativeLayout(this);		
		wholeLayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
		
		wholeLayout.addView(navText);
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

		
		
		
		
		
		// Pre-declare the Views/IDs for RelativeLayout
		TextView navText = new TextView(this);
		navText.setId(1);
		
		ScrollView scrollView = new ScrollView(this);
		scrollView.setId(2);
		
		RelativeLayout.LayoutParams relParams;

		
		
		// Begin the layout
		LinearLayout layoutOfTemplates = new LinearLayout(this);
		layoutOfTemplates.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
		layoutOfTemplates.setOrientation(LinearLayout.VERTICAL);
		
		
		// Create a View for each template
		for (final SpinnerData templateData : templateList)
		{			
			LinearLayout templateView = new LinearLayout(this);
			templateView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
			templateView.setPadding(25, 25, 25, 0);
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
														form.meta.form_id = formID;
														
														
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
						
			layoutOfTemplates.addView(templateView);	
		}
		

		
		// Navigation text
		navText.setText(navString(TEMPLATES));
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
		
		
		
		// Begin the layout
		LinearLayout layoutOfForms = new LinearLayout(this);
		layoutOfForms.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
		layoutOfForms.setOrientation(LinearLayout.VERTICAL);
		
		
		
		// Create a layout for each form
		for (final SpinnerData formData : formList)
		{
			LinearLayout formView = new LinearLayout(this);
			formView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
			formView.setPadding(25, 25, 25, 0);
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
			
			layoutOfForms.addView(formView);
		}
			
		
		// Navigation text
		navText.setText(navString(FORMS));
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*---------- BEGIN MENU BUTTON IMPLEMENTATIONS ----------*/
	
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
	
	
	// TODO javadoc
	private void saveUserName()
	{
		if (viewStack[HOME].findFocus() != null)
		{
			if (viewStack[HOME].findFocus() instanceof EditText)
			{
				userName = ((EditText) viewStack[HOME].findFocus()).getText().toString();
			}
		}
	}
	/*---------- END UNCATEGORIZED METHODS ----------*/
}



