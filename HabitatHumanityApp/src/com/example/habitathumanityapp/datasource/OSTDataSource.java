package com.example.habitathumanityapp.datasource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.example.habitathumanityapp.Form;
import com.example.habitathumanityapp.MyData;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Base64;


/**
 * Provides access to SQLite DB<br>
 *    OSTDataSource oDS = new OSTDataSource(this); // this is current 'Context'<br>
 *    oDS.open();
 * @author Cody Delorme
 *
 */
public class OSTDataSource {

	/** Read the object from Base64 string.
	 * @param s
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private static Object fromString(String s) throws IOException , ClassNotFoundException 
	{
		byte [] data = Base64.decode(s, Base64.DEFAULT);
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
		Object o  = ois.readObject();
		ois.close();
		return o;
	}

	/** Write the object to a Base64 string.
	 * @param o
	 * @return
	 * @throws IOException
	 */
	private static String toString( Serializable o ) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream( baos );
		oos.writeObject( o );
		oos.close();
		return new String( Base64.encodeToString( baos.toByteArray(), Base64.DEFAULT ) );
	}

	/**
	 * Database reference
	 */
	private SQLiteDatabase database;
	private OSTDatabaseHelper dbHelper;


	/** Constructor that accepts current context in activity
	 * @param context
	 */
	public OSTDataSource(Context context) 
	{
		dbHelper = new OSTDatabaseHelper(context);
	}

	/* adds a form to the database */
	//template_id NUMERIC, key_field TEXT, form BLOB

	/** Add a form to the database
	 * @param form
	 * @return returns id of inserted form; -1 on failure
	 */
	public long addForm(Form form)
	{
		ContentValues values = new ContentValues(); 

		try {
			values.put("template_id", form.meta.template_id);  
			//TODO, the keyfield logic needs to be built in, right now well just take the answer to first question
			if (form.questions.get(0).Answer != null)
				values.put("key_field", form.questions.get(0).Answer);
			else
				values.put("key_field", "-none-");
			values.put("form", toString(form));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return database.insert("Forms", null, values);
	}

	/** Add template to the database, uses form class.
	 * @param form
	 */
	public void addTemplate(Form form)
	{
		ContentValues values = new ContentValues(); 

		try {
			values.put("_id", form.meta.template_id);
			values.put("group_name", form.meta.group);
			values.put("template_name", form.meta.name);
			values.put("template", toString(form));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		database.insert("Templates", null, values);
	}

	/** close the database
	 * 
	 */
	public void close() 
	{
		dbHelper.close();
	}

	/* returns form_id and key_field for all templates in the db */
	/**	Returns a List of 2 element string arrays that represent all forms with template_id == param <br>
	 *    string[0] = form_id <br>
	 *    string[1] = value of key_field, if empty returns -none-
	 * @param template_id
	 * @return List<String[]>; List of two element string arrays
	 */
	/*
	public List<String[]> getAllFormInfoByTemplateId(int template_id)
	{
		List<String[]> form_info = new ArrayList<String[]>();
		Cursor cursor = database.query("Forms", new String[] {"_id", "key_field"}, "template_id ==" + String.valueOf(template_id),null,null,null,null);


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
*/
	public List<MyData> getAllFormInfoByTemplateId(int template_id)
	{
		List<MyData> form_info = new ArrayList<MyData>();
		Cursor cursor = database.query("Forms", new String[] {"_id", "key_field"}, "template_id ==" + String.valueOf(template_id),null,null,null,null);


		cursor.moveToFirst();
		while (!cursor.isAfterLast()) 
		{
			String key = cursor.getString(1);
			//String value = String.valueOf(cursor.getInt(0));
			int value = cursor.getInt(0);
			MyData temp = new MyData(key,value);

			form_info.add(temp);
			cursor.moveToNext();
		}

		cursor.close();
		return form_info;
	}
	
	/** Returns a List containing all the unique template groups in the database
	 * @return List of unique template groups
	 */
	public List<String> getAllTemplateGroups()
	{
		List<String> groups = new ArrayList<String>();
		Cursor cursor = database.query(true, "Templates", new String[] {"group_name"}, null, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) 
		{
			groups.add(cursor.getString(0));
			cursor.moveToNext();
		}

		cursor.close();
		return groups;
	}

	/** Returns a List of 2 element string arrays that represent all templates in the database <br>
	 *    string[0] = template_id <br>
	 *    string[1] = template_name
	 * @return List<String[]>; List of two element string arrays
	 */
	/*
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
	*/
	public List<MyData> getAllTemplateInfo()
	{
		List<MyData> template_info = new ArrayList<MyData>();
		Cursor cursor = database.query("Templates", new String[] {"_id", "template_name"},null,null,null,null,null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) 
		{
			String key = cursor.getString(1);
			//String value = String.valueOf(cursor.getInt(0));
			int value = cursor.getInt(0);
			MyData temp = new MyData(key,value);
			
			template_info.add(temp);
			cursor.moveToNext();
		}

		cursor.close();
		return template_info;
	}


	/** Returns a List of 2 element string arrays where group == param<br>
	 *    string[0] = template_id <br>
	 *    string[1] = template_name
	 * @param group
	 * @return List<String[]>; List of two element string arrays
	 */
	/*
	public List<String[]> getAllTemplateInfoByGroup(String group)
	{
		List<String[]> template_info = new ArrayList<String[]>();
		Cursor cursor = database.query("Templates", new String[] {"_id", "template_name"},"group_name ==\"" + group + "\"",null,null,null,null);

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
	*/
	
	/** Returns a List of MyData objects where group == param<br>
	 *    string[0] = template_id <br>
	 *    string[1] = template_name
	 * @param group
	 * @return List<MyData>; List of two element string arrays
	 */
	public List<MyData> getAllTemplateInfoByGroupMyData(String group)
	{
		List<MyData> template_info = new ArrayList<MyData>();
		Cursor cursor = database.query("Templates", new String[] {"_id", "template_name"},"group_name ==\"" + group + "\"",null,null,null,null);
		
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) 
		{
			String key = cursor.getString(1);
			//String value = String.valueOf(cursor.getInt(0));
			int value = cursor.getInt(0);
			MyData temp = new MyData(key,value);
			
			template_info.add(temp);
			cursor.moveToNext();
		}

		cursor.close();
		return template_info;
	}

	/** Return form where form_id == param
	 * @param form_id
	 * @return Form
	 */
	public Form getFormById(long form_id)
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

	/** Return template where template_id == param
	 * @param template_id
	 * @return Template (Form class)
	 */
	public Form getTemplateById(long template_id)
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

	/** Open the database, has to be run
	 * @throws SQLException
	 */
	public void open() throws SQLException 
	{
		database = dbHelper.getWritableDatabase();
	}

	/** deletes all the forms in the db
	 * 
	 */
	public void removeAllForms()
	{
		database.execSQL("DELETE FROM Forms");
	}

	/** deletes all the templates in the db
	 * 
	 */
	public void removeAllTemplates()
	{
		database.execSQL("DELETE FROM Templates");
	}


	/** Delete form where form_id = param
	 * @param form_id
	 */
	public void removeFormById(long form_id)
	{
		database.delete("Forms", "_id =" + String.valueOf(form_id), null);
	}


	/** Update a form in the database, uses the form.meta.form_id to know which form to update<br>
	 *    form_id MUST be set for it to work
	 * @param form
	 */
	public void updateForm(Form form)
	{
		ContentValues values = new ContentValues(); 

		try {
			//TODO, the keyfield logic needs to be built in, right now well just use answer to first quesiton
			if (form.questions.get(0).Answer != null)
				values.put("key_field", form.questions.get(0).Answer);
			else
				values.put("key_field", "-none-");
			values.put("form", toString(form));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		database.update("Forms", values, "_id ==" + String.valueOf(form.meta.form_id), null);
	}
}
