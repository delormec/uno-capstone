package com.example.habitathumanityapp;


import java.util.List;

import org.simpleframework.xml.ElementList;

public class ChoiceQuestion extends Question {
	@ElementList
	public List<String> options;
}