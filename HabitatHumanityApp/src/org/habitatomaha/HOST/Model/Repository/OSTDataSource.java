package org.habitatomaha.HOST.Model.Repository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.habitatomaha.HOST.Model.Form;
import org.habitatomaha.HOST.Model.SpinnerData;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Base64;
import android.util.Log;


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
			
			//If the key field question is answered then insert it.
			//Otherwise inserts a generic placeholder
			if (form.questions.size() >= Integer.parseInt(form.meta.keyfield) && form.questions.get(Integer.parseInt(form.meta.keyfield)-1).Answer != null && form.questions.get(Integer.parseInt(form.meta.keyfield)-1).Answer.compareTo("") != 0)
				values.put("key_field", form.questions.get(Integer.parseInt(form.meta.keyfield)-1).Answer);
			else
				values.put("key_field", "__Key Question " + form.meta.keyfield +  " Unanswered.");
			
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

	/** Returns a List of MyData which contains the value of a form's key field <br>
	 *  and it's form_id.
	 * @return List of MyData used to populate spinners
	 */
	public List<SpinnerData> getAllFormInfo()
	{
		List<SpinnerData> form_info = new ArrayList<SpinnerData>();
		Cursor cursor = database.query("Forms", new String[] {"_id", "key_field"}, null,null,null,null,"key_field");


		cursor.moveToFirst();
		while (!cursor.isAfterLast()) 
		{
			String key = cursor.getString(1);
			//String value = String.valueOf(cursor.getInt(0));
			int value = cursor.getInt(0);
			SpinnerData temp = new SpinnerData(key,value);

			form_info.add(temp);
			cursor.moveToNext();
		}

		cursor.close();
		return form_info;
	}
	
	/**	Returns a List of MyData which contains the value of a form's key field <br>
	 *  and it's form_id WHERE template_id == param.
	 * @param template_id template id in form table
	 * @return List of MyData used to populate spinners
	 */
	public List<SpinnerData> getAllFormInfoByTemplateId(int template_id)
	{
		List<SpinnerData> form_info = new ArrayList<SpinnerData>();
		Cursor cursor = database.query("Forms", new String[] {"_id", "key_field"}, "template_id ==" + String.valueOf(template_id),null,null,null,"key_field");


		cursor.moveToFirst();
		while (!cursor.isAfterLast()) 
		{
			String key = cursor.getString(1);
			//String value = String.valueOf(cursor.getInt(0));
			int value = cursor.getInt(0);
			SpinnerData temp = new SpinnerData(key,value);

			form_info.add(temp);
			cursor.moveToNext();
		}

		cursor.close();
		return form_info;
	}
	
	/**	Returns a List of MyData which contains the value of a form's key field <br>
	 *  and it's form_id WHERE form.template_id == template.id and template.group_name = param
	 * @param group group name
	 * @return List of MyData used to populate spinners
	 */
	public List<SpinnerData> getAllFormInfoByTemplateGroup(String group)
	{
		List<SpinnerData> form_info = new ArrayList<SpinnerData>();
		List<SpinnerData> templates = getAllTemplateInfoByGroup(group);
		
		for (SpinnerData template : templates)
		{
			Cursor cursor = database.query("Forms", new String[] {"_id", "key_field"}, "template_id ==" + String.valueOf(template.getValue()),null,null,null,"key_field");

			cursor.moveToFirst();
			while (!cursor.isAfterLast()) 
			{
				String key = cursor.getString(1);
				int value = cursor.getInt(0);
				SpinnerData temp = new SpinnerData(key,value);
				form_info.add(temp);
				cursor.moveToNext();
			}
			
			cursor.close();
		}
		
		return form_info;
	}
	
	
	/** Returns a List containing all the unique template groups in the database
	 * @return List of unique template groups
	 */
	public List<String> getAllTemplateGroups()
	{
		List<String> groups = new ArrayList<String>();
		Cursor cursor = database.query(true, "Templates", new String[] {"group_name"}, null, null, null, null, "group_name", null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) 
		{
			groups.add(cursor.getString(0));
			cursor.moveToNext();
		}

		cursor.close();
		return groups;
	}


	/**	Returns a List of MyData which contains the all the template names and their template_id.
	 * @return List of MyData used to populate spinners
	 */
	public List<SpinnerData> getAllTemplateInfo()
	{
		List<SpinnerData> template_info = new ArrayList<SpinnerData>();
		Cursor cursor = database.query("Templates", new String[] {"_id", "template_name"},null,null,null,null,"template_name");

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) 
		{
			String key = cursor.getString(1);
			//String value = String.valueOf(cursor.getInt(0));
			int value = cursor.getInt(0);
			SpinnerData temp = new SpinnerData(key,value);
			
			template_info.add(temp);
			cursor.moveToNext();
		}

		cursor.close();
		return template_info;
	}
	
	/** Returns a List of MyData objects where group == param<br>
	 *    string[0] = template_id <br>
	 *    string[1] = template_name
	 * @param group
	 * @return List<MyData>
	 */
	public List<SpinnerData> getAllTemplateInfoByGroup(String group)
	{
		List<SpinnerData> template_info = new ArrayList<SpinnerData>();
		Cursor cursor = database.query("Templates", new String[] {"_id", "template_name"},"group_name ==\"" + group + "\"",null,null,null,"template_name");
		
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) 
		{
			String key = cursor.getString(1);
			//String value = String.valueOf(cursor.getInt(0));
			int value = cursor.getInt(0);
			SpinnerData temp = new SpinnerData(key,value);
			
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
			
			
			//If the key field question is answered then insert it.
			//Otherwise inserts a generic placeholder
			if (form.questions.size() >= Integer.parseInt(form.meta.keyfield) && form.questions.get(Integer.parseInt(form.meta.keyfield)-1).Answer != null && form.questions.get(Integer.parseInt(form.meta.keyfield)-1).Answer.compareTo("") != 0)
				values.put("key_field", form.questions.get(Integer.parseInt(form.meta.keyfield)-1).Answer);
			else
				values.put("key_field", "__Key Question " + form.meta.keyfield +  " Unanswered.");
			
			values.put("form", toString(form));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		database.update("Forms", values, "_id ==" + String.valueOf(form.meta.form_id), null);
	}
}
