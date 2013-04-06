package com.example.habitathumanityapp;

import java.util.LinkedList;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Displays the Errors currently stored in the ErrorLog.
 */
public class DisplayErrorLogActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		ScrollView scrollView = new ScrollView(this);
		
		LinearLayout linearLayout = new LinearLayout(this);
		
		linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));		
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		
		
		// Open the ErrorLog
		LinkedList<Error> errorLog = ErrorLog.getLog(this);
		
		if (errorLog.size() > 0)
		{
			// Display each error
			for (Error err : errorLog)
			{
				// Display the error text
				TextView errorView = new TextView(this);
				errorView.setText(err.errorText);
				errorView.setTextSize(25);						
				errorView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));			
				linearLayout.addView(errorView);
				
				// Display additional info
				TextView infoView = new TextView(this);
				infoView.setText(err.moreInfo);
				infoView.setTextSize(15);
				infoView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
				linearLayout.addView(infoView);
				
				// Add a horizontal rule between errors
				View lineBreak = new View(this);
				lineBreak.setBackgroundColor(Color.parseColor("#000000"));
				lineBreak.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2));
				linearLayout.addView(lineBreak);
			}
		}
		else
		{
			// The log was empty
			TextView empty = new TextView(this);
			empty.setText("No errors to display.");
			empty.setTextSize(25);
			linearLayout.addView(empty);
		}
		
		scrollView.addView(linearLayout);
		
		setContentView(scrollView);
		return;
	}

}