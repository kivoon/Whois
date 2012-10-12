package com.deaboy.whois;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Settings extends Properties implements Closeable
{
	private static final long serialVersionUID = 1L;
	private static final File directory = Whois.getDirectory();
	
	public Settings()
	{
		loadFile();
	}
	
	public void saveProperty(String prop, String val)
	{
		put (prop, val);
		saveFile();
	}
	
	public String getString(String prop, String def)
	{
		if (containsKey(prop))
		{
			return getProperty(prop);
		}
		else
		{
			saveProperty(prop, def);
			return def;
		}
	}
	
	public boolean getBoolean(String prop, Boolean def)
	{
		if (containsKey(prop))
		{
			try
			{
				return Boolean.parseBoolean(getProperty(prop));
			}
			catch (NumberFormatException e)
			{
				saveProperty(prop, def.toString());
				return def;
			}
		}
		else
		{
			saveProperty(prop, def.toString());
			return def;
		}
	}
	
	public int getInteger(String prop, Integer def)
	{
		if (containsKey(prop))
		{
			try
			{
				return Integer.parseInt(getProperty(prop));
			}
			catch (NumberFormatException e)
			{
				saveProperty(prop, def.toString());
				return def;
			}
		}
		else
		{
			saveProperty(prop, def.toString());
			return def;
		}
	}
	
	public double getDouble(String prop, Double def)
	{
		if (containsKey(prop))
		{
			try
			{
				return Double.parseDouble(getProperty(prop));
			}
			catch (NumberFormatException e)
			{
				saveProperty(prop, def.toString());
				return def;
			}
		}
		else
		{
			saveProperty(prop, def.toString());
			return def;
		}
	}
	
	private void loadFile()
	{
		File file = new File(directory + "/config.properties");
		if (directory.exists()) {
			directory.mkdirs();
		}
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
		try {
			FileInputStream stream = new FileInputStream(file);
			load(stream);
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		saveFile();
	}
	
	private void saveFile()
	{
		File file = new File(directory + "/config.properties");
		if (!directory.exists()) {
			directory.mkdirs();
		}
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
		try {
			FileOutputStream stream = new FileOutputStream(file);
			store(stream, "- Whois Settings File -");
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
	
	public void close()
	{
		// Commented out because when the users reload, I don't want their settings to be overwritten.
		// saveFile();
	}
}
