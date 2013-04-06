package com.example.habitathumanityapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.LinkedList;

import android.content.Context;

public class ErrorLog
{
	public static LinkedList<Error> log;
	
	
	/**
	 * Attempts to open the ErrorLog from the "error_log.ser" file <br>
	 * Instantiates the log as new LinkedList if no log is read from the file
	 * 
	 * @param context	The Context that called this method
	 */
	private static void openLog(Context context)
	{
		File test = context.getFileStreamPath("error_log.ser");
		
		// Test if the file exists
		if (test.exists())
		{
			// Attempt to open the file and read the ErrorLog from it.
			try 
			{
				FileInputStream logFile = context.openFileInput("error_log.ser");
				ObjectInputStream ois = new ObjectInputStream(logFile);
				
				log = (LinkedList<Error>) ois.readObject();
				
				if (log == null)
				{
					log = new LinkedList<Error>();
				}
				
				ois.close();
				logFile.close();
			} 
			catch (FileNotFoundException e) 
			{
				e.printStackTrace();
			} 
			catch (StreamCorruptedException e) 
			{
				e.printStackTrace();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			} 
			catch (ClassNotFoundException e) 
			{
				e.printStackTrace();
			}
		}
		else
		{
			log = new LinkedList<Error>();
		}
	}
	
	
	
	/**
	 * Returns a LinkedList of type Error that contains the error log
	 * @param context	The Context that called this method
	 * @return	The list of Errors stored in the error log
	 */
	public static LinkedList<Error> getLog(Context context)
	{
		openLog(context);		
		return log;
	}
	
	
	
	/**
	 * Logs an error and then stores the log to the "error_log.ser" file
	 * 
	 * @param context	The context that called this method
	 * @param error		The Error to log
	 */
	public static void log(Context context, Error error)
	{	
		openLog(context);

		if (log == null)
		{
			log = new LinkedList<Error>();
		}

		// Attempt to add the Error to the log
		if (log.size() < 50)
		{
			log.addLast(error);
		}
		else
		{
			// Error log full, remove oldest one
			log.removeFirst();
			log.addLast(error);
		}


		// Write the error log to the file
		try 
		{
			FileOutputStream fos = context.openFileOutput("error_log.ser", Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			
			oos.writeObject(log);			
			oos.close();
			fos.close();
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
}

