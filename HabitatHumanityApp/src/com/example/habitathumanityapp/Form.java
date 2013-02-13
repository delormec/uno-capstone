package com.example.habitathumanityapp;


import java.util.List;

import org.simpleframework.xml.*;


@Root (name="form", strict=false)
public class Form {
	
	@Element (name="meta")
	public Meta meta;
	
	@ElementList
	public List<Question> questions;
	
	public String getFormName(){
		return meta.getFormName();
	}
	
	public int getFormId(){
		return meta.getFormID();
	}
}
