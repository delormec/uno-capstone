package com.example.habitathumanityapp;

import java.io.Serializable;

import org.simpleframework.xml.Element;

public class Meta implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3503635432336091621L;

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
