package org.habitatomaha.HOST.Model;

import java.io.Serializable;

// A class for logging errors that occur during the running of the app
public class Error implements Serializable
{
	private static final long serialVersionUID = 6996105422714197425L;


	// Indicates level of severity of an error
	public enum Severity 
	{
		Critical, 
		Normal, 
		Minor
	};
	
	
	public String errorText;	// What the error is
	public Severity severity;	// How big of a deal this error is
	public String moreInfo;		// Extra info about the error(How to avoid this error in the future, etc.)
	
	public String timeStamp;	// timeStamp is set by ErrorLog when the Error is logged.

	
	// Constructor
	public Error(String errorText)
	{
		this.errorText = errorText;
		this.moreInfo = "No extra information is available.";
		this.severity = Severity.Normal;
		this.timeStamp = "";
	}
	
	// Constructor
	public Error(String errorText, String moreInfo)
	{
		this.errorText = errorText;
		this.moreInfo = moreInfo;
		this.severity = Severity.Normal;
		this.timeStamp = "";
	}
	
	// Constructor
	public Error(String errorText, Severity severity)
	{
		this.errorText = errorText;
		this.moreInfo = "No extra information is available.";
		this.severity = severity;
		this.timeStamp = "";
	}
	
	// Constructor
	public Error(String errorText, String moreInfo, Severity severity)
	{
		this.errorText = errorText;
		this.moreInfo = moreInfo;
		this.severity = severity;
		this.timeStamp = "";
	}
	
	
	@Override
	public boolean equals(Object other)
	{
		if (other == null)	return false;
		if (other == this)	return true;
		
		if (other instanceof Error)
		{
			// Cast other object as Error
			Error otherAsError = (Error) other;
			
			if (otherAsError.errorText.compareTo(this.errorText) == 0 &&
				otherAsError.moreInfo.compareTo(this.moreInfo) == 0 &&
				otherAsError.timeStamp.compareTo(this.timeStamp) == 0 &&
				otherAsError.severity == this.severity)
				
			{
				return true;
			}		
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}
}
