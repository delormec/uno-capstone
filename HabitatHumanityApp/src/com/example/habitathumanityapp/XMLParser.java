package com.example.habitathumanityapp;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.content.Context;

public class XMLParser {
	
	public static Form getForm(String xml){
		Form f = null;
		
		Serializer serializer = new Persister();
		
		//Look into serialization/deserialization of objects
		try{
			f = serializer.read(Form.class, xml.toString());
			/*for( Question q: f.questions){
				System.out.println(q.getClass());
			}*/
		}catch(Exception e){
			e.printStackTrace();
		}

		return f;
	}
	
	//used to get list of form ids from admintool
	public static TemplateList getTemplateList(String xml){
		TemplateList tl = null;
		
		Serializer serializer = new Persister();
		
		try{
			tl = serializer.read(TemplateList.class, xml);

			/*for( Question q: f.questions){
				System.out.println(q.getClass());
			}*/
		}catch(Exception e){
			//ValueRequiredException
			//this thing was empty so were just gonna return null
			//e.printStackTrace();
			return null;
		}	
		return tl;
	}
	
	public static String getXML(Form f, Context ctx){
		FileOutputStream fos = null;
		FileInputStream fis = null;
		
		String FILENAME = "testForm";
		String fullXml = "Form not created.";
		StringBuilder sb = new StringBuilder();
		
		// Open private file to write XML.
		try{
			fos = ctx.openFileOutput(FILENAME, Context.MODE_PRIVATE	);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		// Create XML file using Simple XML Serializer.
		Serializer serializer = new Persister();
		String xmlChunk = "";
		try
		{
			serializer.write(f, fos);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		// Open file to read XML into a string.
		try{
			fis = ctx.openFileInput(FILENAME);
			
			BufferedReader br = new BufferedReader( new InputStreamReader(fis));
	
			while((xmlChunk = br.readLine()) != null){
				//Log.v("Parser", "Going through while loop with chunk" + xmlChunk);
				sb.append(xmlChunk);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		
		fullXml = sb.toString();
		
		return fullXml;
	}
	
}
