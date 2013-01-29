package com.example.habitathumanityapp;
import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;


public class LikertScaleQuestion extends Question {
	
	@Element(name= "steps")
	public String steps;

	@ElementList
	public List<String> labels;
}
