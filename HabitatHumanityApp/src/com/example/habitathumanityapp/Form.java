package com.example.habitathumanityapp;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.*;


@Root (name="form", strict=false)
public class Form implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1800436925959126021L;

	@Element (name="meta")
	public Meta meta = new Meta();
	
	@ElementList
	public List<Question> questions;
	
	public String getFormName(){
		return meta.getFormName();
	}
	
	public long getFormId(){
		return meta.getFormID();
	}

	public Form()
	{
		meta = new Meta();
		questions = new ArrayList<Question>();
	}
}
