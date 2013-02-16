package com.example.habitathumanityapp.Helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class OSTDatabaseHelper extends SQLiteOpenHelper
{
	private static final String DATABASE_NAME = "OST";
	private static final int DATABASE_VERSION = 1;
	
	private static final String template_table = "CREATE TABLE Templates (_id INTEGER PRIMARY KEY, template_name TEXT, template_description TEXT, template BLOB);";
	private static final String form_table = "CREATE TABLE Forms (_id INTEGER PRIMARY KEY autoincrement, template_id INTEGER, key_field TEXT, form BLOB);";
	

	public OSTDatabaseHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	public OSTDatabaseHelper(Context context) {
		// TODO Auto-generated constructor stub
		super(context,DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		// TODO Auto-generated method stub
		
		database.execSQL(template_table);
		database.execSQL(form_table);	
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    Log.w(OSTDatabaseHelper.class.getName(),
	            "Upgrading database from version " + oldVersion + " to "
	                + newVersion + ", which will destroy all old data");
	    	db.execSQL("DROP TABLE IF EXISTS Template");
	    	db.execSQL("DROP TABLE IF EXISTS Form");
	        db.execSQL("DROP TABLE IF EXISTS Forms");
	        db.execSQL("DROP TABLE IF EXISTS Templates");
	    
	        onCreate(db);

	}

}

