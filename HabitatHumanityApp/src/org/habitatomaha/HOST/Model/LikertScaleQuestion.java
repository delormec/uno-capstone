package org.habitatomaha.HOST.Model;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;


/**
 * This class serves as the model for Likert Scale questions within a form. 
 */
public class LikertScaleQuestion extends Question implements Serializable{
	
	private static final long serialVersionUID = -5849784343747975543L;

	/**
	 * This String designates the amount of steps (selections) are offered in the question.
	 */
	@Element(name= "steps")
	public String steps;

	/**
	 * This List represents the list of labels used to identify the value of particular steps.
	 */
	@ElementList
	public List<String> labels;
	
	/**
	 * A constructor.
	 * Creates a Likert Scale Question with an empty list of labels.
	 */
	public LikertScaleQuestion() {
		labels = new ArrayList<String>();
	}
}
