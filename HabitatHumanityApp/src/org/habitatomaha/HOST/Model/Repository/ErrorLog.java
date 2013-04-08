package org.habitatomaha.HOST.Model.Repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.Calendar;
import java.util.LinkedList;

import org.habitatomaha.HOST.Model.Error;

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
	 * Saves the error log to the file "error_log.ser"
	 * 
	 * @param context	The context that called this method
	 */
	private static void closeLog(Context context)
	{
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
	
	
	
	/**
	 * Returns a LinkedList of type Error that contains the error log
	 * 
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

		
		// Timestamp the Error
		Calendar calendar = Calendar.getInstance();
		error.timeStamp = calendar.getTime().toString();
		
		
		// Attempt to add the Error to the log
		if (log.size() < 50)
		{
			log.addLast(error);
		}
		else
		{
			// Error log full, remove oldest one first
			log.removeFirst();
			log.addLast(error);
		}
		
		closeLog(context);
	}
	
	
	
	/**
	 * Removes an entry from the error log
	 * 
	 * @param context	The Context that called this method
	 * @param err		The Error to remove from the log
	 */
	public static boolean remove(Context context, Error err)
	{
		boolean result;
		
		openLog(context);
		result = log.remove(err);
		closeLog(context);
		
		return result;
	}
	
}

