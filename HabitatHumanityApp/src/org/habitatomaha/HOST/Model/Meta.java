package org.habitatomaha.HOST.Model;

import java.io.Serializable;

import org.simpleframework.xml.Element;

public class Meta implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3503635432336091621L;

	@Element (name="filledbyfieldname", required=false)
    public String filledByFieldName;
    
    @Element (name="filledbyfieldtype")
    public String filledByFieldType;
    
    @Element (name="filleddatefieldname", required=false)
    public String filledDateFieldName;
    
    @Element (name="filleddatefieldtype")
    public String filledDateFieldType;
    
    public String filledDate;
    public String filledBy;
	
	
	
	@Element (name="template_id")
	public long template_id;
	
	//doesn't come from admin tool
	public long form_id;
	
	//TODO name miss match
	@Element (name="formgroup", required=false)
	public String group;
	
	@Element (name="name")
	public String name;
	
	@Element (name="description", required=false)
	public String description;
	
	//TODO -- remove these from admin?
	@Element (name="user", required=false)
	public String user;
	
	//TODO -- remove these from admin?
	@Element (name="pass", required=false)
	public String pass;
	
	@Element (name="url")
	public String url;
	
	//TODO - this really shouldnt ever be blank
	@Element (name="listname", required=false)
	public String listname;
	
	//TODO - this really shouldnt ever be blank
	@Element (name="keyfield", required=false)
	public String keyfield;
	
	@Element (name="autoupload")
	public String autoUpload;
	
	@Element (name="datecreated")
	public String dateCreated;
	
	//TODO - remove/update
	public long getFormID(){
		return template_id;
	}

	public String getFormName(){
		return name;
	}
	
}
