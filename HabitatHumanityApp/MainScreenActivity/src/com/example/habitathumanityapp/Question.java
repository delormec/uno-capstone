package com.example.habitathumanityapp;


import org.simpleframework.xml.Element;

public class Question {
	@Element ( name= "text")
	public String Text;
	
	@Element (name= "id")
	public int QuestionID;
	
	@Element (name= "helptext", required=false)
	public String HelpText;
	
	@Element (name= "fieldname")
	public String FieldName;
	
	@Element (name= "fieldtype")
	public String FieldType;
	
	@Element (name="answer")
	public String Answer;	
}
