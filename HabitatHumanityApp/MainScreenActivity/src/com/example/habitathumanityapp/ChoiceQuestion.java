package com.example.habitathumanityapp;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.ElementList;

public class ChoiceQuestion extends Question implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3551249012662043472L;
	@ElementList
	public List<String> options;
	
	public ChoiceQuestion()
	{
		options = new ArrayList<String>();
	}
}
