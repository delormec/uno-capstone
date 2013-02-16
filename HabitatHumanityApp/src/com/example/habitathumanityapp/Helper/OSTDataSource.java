package com.example.habitathumanityapp.Helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.example.habitathumanityapp.ChoiceQuestion;
import com.example.habitathumanityapp.Form;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Base64;
import android.util.Log;


public class OSTDataSource {
	  /** Read the object from Base64 string. */
	  public static Object fromString(String s) throws IOException , ClassNotFoundException 
	  {
		  byte [] data = Base64.decode(s, Base64.DEFAULT);
		  ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
		  Object o  = ois.readObject();
		  ois.close();
		  return o;
	  }
	  /** Write the object to a Base64 string. */
	private static String toString( Serializable o ) throws IOException {
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    ObjectOutputStream oos = new ObjectOutputStream( baos );
	    oos.writeObject( o );
	    oos.close();
	    return new String( Base64.encodeToString( baos.toByteArray(), Base64.DEFAULT ) );
	}

	  // Database fields
	  private SQLiteDatabase database;

	  private OSTDatabaseHelper dbHelper;

	  public OSTDataSource(Context context) {
	    dbHelper = new OSTDatabaseHelper(context);
	  }
  
	  /* adds a form to the database */
	  //template_id NUMERIC, key_field TEXT, form BLOB
	  public void addForm(Form form)
	  {
		  ContentValues values = new ContentValues(); 
		  
		  try {
			  //we let _id auto increment
			  //values.put("_id", form.meta.formid);
			  
			  //this is really the template id
			  values.put("template_id", form.meta.formid);
			  
			  //TODO, the keyfield logic needs to be built in
			  values.put("key_field", "KF here");
			  values.put("form", toString(form));
		  } catch (IOException e) {
			// TODO Auto-generated catch block
			  e.printStackTrace();
		  }

		  database.insert("Forms", null, values);
	  }
	  
	  /* adds a template to the database */
	  //template_name TEXT, template_description TEXT, template BLOB
	  public void addTemplate(Form form)
	  {
		  ContentValues values = new ContentValues(); 
		  
		  try {
			  values.put("_id", form.meta.formid);
			  values.put("template_name", form.meta.name);
			  values.put("template", toString(form));
		  } catch (IOException e) {
			// TODO Auto-generated catch block
			  e.printStackTrace();
		  }

		  database.insert("Templates", null, values);
	  }
	  
	  /* close the db */
	  public void close() {
	    dbHelper.close();
	  }
	  
	  /* returns template_id and template_name for all templates in the db */
	  public List<String[]> getAllTemplateInfo()
	  {
		  List<String[]> template_info = new ArrayList<String[]>();
		  Cursor cursor = database.query("Templates", new String[] {"_id", "template_name"},null,null,null,null,null);
		  
		  cursor.moveToFirst();
		  while (!cursor.isAfterLast()) 
		  {
			  String[] temp = new String[2];
			  temp[0] = String.valueOf(cursor.getInt(0));
			  temp[1] = cursor.getString(1);
			  template_info.add(temp);
			  cursor.moveToNext();
		  }
		  
		  cursor.close();
		  return template_info;
	  }
	  
	  /* returns form_id and key_field for all templates in the db */
	  public List<String[]> getAllFormInfoByTemplateId(int template_id)
	  {
		  List<String[]> form_info = new ArrayList<String[]>();
		  Cursor cursor = database.query("Forms", new String[] {"_id", "key_field"}, "_id ==" + String.valueOf(template_id),null,null,null,null);
		  
		  cursor.moveToFirst();
		  while (!cursor.isAfterLast()) 
		  {
			  String[] temp = new String[2];
			  temp[0] = String.valueOf(cursor.getInt(0));
			  temp[1] = cursor.getString(1);
			  form_info.add(temp);
			  cursor.moveToNext();
		  }
		  
		  cursor.close();
		  return form_info;
	  }
	  
	  public Form getFormById(int form_id)
	  {
		  Form form = null;
		  Cursor cursor = database.query("Forms", new String[] {"form"}, "_id ==" + String.valueOf(form_id),null,null,null,null);
		  cursor.moveToFirst();
		  
		  try {
			form = (Form)fromString(cursor.getString(0));
		  } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		  } catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		  }
		  
		  cursor.close();
		  return form;
	  }
	  
	  public Form getTemplateById(int template_id)
	  {
		  Form form = null;
		  Cursor cursor = database.query("Templates", new String[] {"template"}, "_id ==" + String.valueOf(template_id),null,null,null,null);
		  cursor.moveToFirst();
		  
		  try {
			form = (Form)fromString(cursor.getString(0));
		  } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		  } catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		  }
		  
		  cursor.close();
		  return form;
	  }
	  
	  /* open the db */
	  public void open() throws SQLException {
	    database = dbHelper.getWritableDatabase();
	  }
	  
	  /* deletes all the forms in the db */
	  public void removeAllForms()
	  {
		  database.execSQL("DELETE FROM Forms");
	  }
	  
	  /* deletes all the templates in the db */
	  public void removeAllTemplates()
	  {
		  database.execSQL("DELETE FROM Templates");
	  }
	  
	  public void updateFormById(Form form, int form_id)
	  {
		  ContentValues values = new ContentValues(); 
		  
		  try {
			  //TODO, the keyfield logic needs to be built in
			  values.put("key_field", "KF here");
			  values.put("form", toString(form));
		  } catch (IOException e) {
			// TODO Auto-generated catch block
			  e.printStackTrace();
		  }
		  
		  database.update("Forms", values, "_id ==" + String.valueOf(form_id), null);
	  }
}
