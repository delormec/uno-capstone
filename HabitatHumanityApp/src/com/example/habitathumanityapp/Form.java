package com.example.habitathumanityapp;


import java.util.List;

import org.simpleframework.xml.*;


@Root (name="form", strict=false)
public class Form {
	
	@Element (name="meta")
	public Meta meta;
	
	@ElementList
	public List<Question> questions;
	
}