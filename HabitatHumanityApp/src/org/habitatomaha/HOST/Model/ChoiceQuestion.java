package org.habitatomaha.HOST.Model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

/**
 * This class serves as the model for choice questions within a form. 
 */
public class ChoiceQuestion extends Question implements Serializable{

	/**TODO WHat does this do? */
	private static final long serialVersionUID = -3551249012662043472L;
	
	/**
	 * This option determines whether the Choice Question is multiple or single select.
	 */
	@Element(name="multipleselect")
	public String multipleselect;
	
	
	@Element(name="other")
	public String other;
	
	/**
	 * This list stores each option available in a Choice Question.
	 */
	@ElementList
	public List<String> options;
	
	/**
	 * A constructor.
	 * Creates a new Choice Question with an empty array of options. 
	 */
	public ChoiceQuestion()
	{
		options = new ArrayList<String>();
	}
}
