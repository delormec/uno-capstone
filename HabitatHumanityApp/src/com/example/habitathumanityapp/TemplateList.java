package com.example.habitathumanityapp;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Root;
import org.simpleframework.xml.ElementList;;


@Root(name="ArrayOfString")
public class TemplateList {

    @ElementList(inline=true)
    public List<String> ids = new ArrayList<String>();
	
	
}
