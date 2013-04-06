package com.example.habitathumanityapp;

import java.io.Serializable;

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

	
	
	public Error(String errorText)
	{
		this.errorText = errorText;
		this.moreInfo = "No extra information is available.";
		this.severity = Severity.Normal;
	}
	
	
	public Error (String errorText, String moreInfo)
	{
		this.errorText = errorText;
		this.moreInfo = moreInfo;
		this.severity = Severity.Normal;
	}
	
	
	public Error (String errorText, Severity severity)
	{
		this.errorText = errorText;
		this.moreInfo = "No extra information is available.";
		this.severity = severity;
	}
	
	
	public Error (String errorText, String moreInfo, Severity severity)
	{
		this.errorText = errorText;
		this.moreInfo = moreInfo;
		this.severity = severity;
	}		
}
