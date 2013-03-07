package com.example.habitathumanityapp;

import java.io.Serializable;


import org.simpleframework.xml.Element;

public class Question implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3967170648257931694L;

	@Element ( name= "text")
	public String Text;
	
	//TODO wrong name here
	@Element (name= "formid",required=false)
	public int formid;
	
	@Element (name="sortorder")
	public int QuestionID;
	
	@Element (name= "helptext", required=false)
	public String HelpText;
	
	
	//TODO shouldnt be false
	@Element (name= "fieldname", required=false)
	public String FieldName;
	
	//TODO shouldnt be false
	@Element (name= "fieldtype", required=false)
	public String FieldType;
	
	@Element (name="answer", required=false)
	public String Answer;
}
