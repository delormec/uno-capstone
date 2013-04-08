package org.habitatomaha.HOST.Activity;

import java.util.LinkedList;

import org.habitatomaha.HOST.Model.Error;
import org.habitatomaha.HOST.Model.Repository.ErrorLog;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * 
 * Displays the Errors currently stored in the ErrorLog.
 */
public class ErrorLogActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		ScrollView scrollView = new ScrollView(this);
		scrollView.setPadding(25, 25, 25, 25);
		
		// The LinearLayout child of scrollView
		LinearLayout linearLayout = new LinearLayout(this);	
		linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));		
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		
		
		// Open the ErrorLog
		LinkedList<Error> errorLog = ErrorLog.getLog(this);
		
		if (errorLog.size() > 0)
		{
			// Display each error
			for (Error err : errorLog)
			{
				int colorCode;
				switch(err.severity)
				{
					case Minor:
						colorCode = Color.parseColor("#0000CC");
						break;
					case Normal:
						colorCode = Color.parseColor("#CCCC00");
						break;
					case Critical:
						colorCode = Color.parseColor("#FF0000");
						break;
					default:
						colorCode = Color.parseColor("#000000");
				}
				
				// Create a view for the error
				LinearLayout errorLayout = new LinearLayout(this);
				errorLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
				errorLayout.setOrientation(LinearLayout.VERTICAL);
				
				
				// Display the error text
				TextView errorText = new TextView(this);
				errorText.setText(err.errorText);
				errorText.setTextSize(25);
				errorText.setTextColor(colorCode);
				errorText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));			
				errorLayout.addView(errorText);
				
				
				// Display timestamp
				TextView timeView = new TextView(this);
				timeView.setText(err.timeStamp);
				timeView.setTextSize(20);						
				timeView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));			
				errorLayout.addView(timeView);
				
				
				// Display additional info
				TextView infoView = new TextView(this);
				infoView.setText(err.moreInfo);
				infoView.setTextSize(15);
				infoView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
				errorLayout.addView(infoView);
				
				
				// Delete button
				Button deleteButton = new Button(this);
				deleteButton.setText("Dismiss");
				deleteButton.setTag(err);
				deleteButton.setTextSize(20);
				deleteButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
				deleteButton.setOnClickListener(new View.OnClickListener(){
														public void onClick(View view)
														{
															removeEntry(view);
														}
													}					
												);
				errorLayout.addView(deleteButton);
				
				
				// Add a horizontal rule between errors
				View lineBreak = new View(this);
				lineBreak.setBackgroundColor(Color.parseColor("#000000"));
				lineBreak.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2));
				errorLayout.addView(lineBreak);
				
				
				
				linearLayout.addView(errorLayout);
			}
			
			scrollView.addView(linearLayout);
		}
		else
		{
			// The log was empty
			TextView empty = new TextView(this);
			empty.setText("No errors to display.");
			empty.setTextSize(25);
			scrollView.addView(empty);
		}
		
		
		
		setContentView(scrollView);
		return;
	}
	
	
	/**
	 * Removes an entry from the ErrorLog.
	 * 
	 * @param view	The View that called this method
	 */
	public void removeEntry(View view)
	{
		// Attempt to remove the Error from ErrorLog
		if (ErrorLog.remove(this, (Error)view.getTag()))
		{
			// Remove the view of the Error from the layout if the Error was removed
			LinearLayout errorView = (LinearLayout) view.getParent();
			((LinearLayout) errorView.getParent()).removeView(errorView);		
		}
	}

}