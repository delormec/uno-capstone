package com.example.habitathumanityapp;

import org.simpleframework.xml.Element;

public class Meta {
	@Element (name="formid")
	public int formid;
	
	@Element (name="name")
	public String name;
	
	@Element (name="url")
	public String url;
	
	@Element (name="autoupload")
	public String autoUpload;
	
	@Element (name="datecreated")
	public String dateCreated;
	
	public int getFormID(){
		return formid;
	}
	
	public String getFormName(){
		return name;
	}
}
