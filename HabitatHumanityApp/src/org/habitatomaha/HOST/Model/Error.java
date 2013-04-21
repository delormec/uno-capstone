package org.habitatomaha.HOST.Model;

import java.io.Serializable;

/**
 * A class for logging errors that occur during application runtime.
 */
public class Error implements Serializable
{
	private static final long serialVersionUID = 6996105422714197425L;


	/**
	 * An enumeration that documents the severity of an error.
	 */
	public enum Severity 
	{
		Critical, 
		Normal, 
		Minor
	};
	
	
	public String errorText;	/**< The actual error message */ 
	public Severity severity;	/**< The severity of the error message, how big a deal it is. */
	public String moreInfo;		/**< Any additional information about the error, (How to avoid, etc.) */	
	public String timeStamp;	/**< The time when the Error is logged. */

	
	/**
	 * A constructor.
	 * Instantiates an Error object given an ErrorText string. 
	 * @param errorText - Error Message displayed in the Error.
	 */
	public Error(String errorText)
	{
		this.errorText = errorText;
		this.moreInfo = "No extra information is available.";
		this.severity = Severity.Normal;
		this.timeStamp = "";
	}
	
	/**
	 * A constructor.
	 * Instantiates an Error object given an ErrorText string and additional information string.
	 * @param errorText - Error Message displayed in the Error.
	 * @param moreInfo - Any additional information about the error, (How to avoid, etc.) 
	 */
	public Error(String errorText, String moreInfo)
	{
		this.errorText = errorText;
		this.moreInfo = moreInfo;
		this.severity = Severity.Normal;
		this.timeStamp = "";
	}
	
	/** 
	 * A constructor.
	 * Instantiates an Error object given an ErrorText string and Severity designation.
	 * @param errorText - Error Message displayed in the Error.
	 * @param severity - Designated severity of the Error.
	 */
	public Error(String errorText, Severity severity)
	{
		this.errorText = errorText;
		this.moreInfo = "No extra information is available.";
		this.severity = severity;
		this.timeStamp = "";
	}
	
	/**
	 * A constructor.
	 * Instantiates an Error object given an ErrorText String, additional information string, and Severity designation.
	 * @param errorText - Error Message displayed in the Error.
	 * @param moreInfo - Any additional information about the error, (How to avoid, etc.)
	 * @param severity - Designated severity of the Error.
	 */
	public Error(String errorText, String moreInfo, Severity severity)
	{
		this.errorText = errorText;
		this.moreInfo = moreInfo;
		this.severity = severity;
		this.timeStamp = "";
	}
	
	
	@Override
	/**
	 * This method determines whether an object (typically an Error) is equal to the current Error.
	 * @param other - Object being compared to current Error
	 */
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
