package org.habitatomaha.HOST.Model;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;


public class LikertScaleQuestion extends Question implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5849784343747975543L;

	@Element(name= "steps")
	public String steps;

	@ElementList
	public List<String> labels;
	
	public LikertScaleQuestion() {
		labels = new ArrayList<String>();
	}
}
