package com.deaboy.whois.users;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import com.deaboy.whois.Whois;
import com.deaboy.whois.users.User.Field;
import com.deaboy.whois.users.User.Stat;

public class UserFile extends Properties implements Closeable
{
	private static final long serialVersionUID = 1L;
	private static final File directory = new File(Whois.getDirectory() + "/users");
	
	private FileInputStream input = null;
	private FileOutputStream output;
	
	private User user;
	private String name;
	
	/**
	 * Automatically loads the user's data from file.
	 * @param user
	 */
	public UserFile(User user)
	{
		this.user = user;
		this.name = user.getName().toLowerCase();
		loadUser();
	}
	
	public void loadUser()
	{
		loadFile();
		
		for (Field f : Field.values())
		{
			user.setField(f, getProperty(f.toString()));
		}
		
		for (Stat s : Stat.values())
		{
			Long l;
			try
			{
				l = Long.parseLong(getProperty(s.toString()));
			}
			catch (NumberFormatException e)
			{
				l = 0L;
			}
			user.setStat(s, l);
		}
		
	}
	
	public void saveUser()
	{
		clear();
		
		for (Field f : Field.values())
		{
			put(f.toString(), user.getField(f));
		}
		
		for (Stat s : Stat.values())
		{
			put(s.toString(), user.getStat(s).toString());
		}
		
		saveFile();
	}
	
	public void saveField(Field f)
	{
		clear();
		put(f, user.getField(f));
		saveFile();
		loadFile();
	}
	
	public void saveStat(Stat s)
	{
		clear();
		put(s, user.getStat(s));
		saveFile();
		loadFile();
	}
	
	public void loadFile()
	{
		if (input == null)
		{
			File file = new File(directory + "/" + name.toLowerCase() + ".who");
			if (!directory.exists()) {
				directory.mkdirs();
			}
			if (!file.exists())
			{
				if (Whois.getSettings().getBoolean("whitelist", false))
				{
					Bukkit.getLogger().log(Level.INFO, "Whois file for " + user.getName() + " was cancelled.");
					return;
				}
				else
				{
					try {
						file.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
						return;
					}
				}
			}
			try {
				input = new FileInputStream(file);
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
		try {
			load(input);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
	
	public void saveFile()
	{
		if (output == null)
		{
			File file = new File(directory + "/" + name.toLowerCase() + ".who");
			if (!directory.exists()) {
				directory.mkdirs();
			}
			if (!file.exists())
			{
				if (Whois.getSettings().getBoolean("whitelist", false))
				{
					Bukkit.getLogger().log(Level.INFO, "Whois file for " + user.getName() + " was cancelled.");
					return;
				}
				else
				{
					try {
						file.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
						return;
					}
				}
			}
			try {
				output = new FileOutputStream(file);
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}

		try {
			store(output, "- Whois: " + user.getName() + "'s Info File -");
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
	
	public static boolean exists(String user)
	{
		return new File(directory + "/" + user.toLowerCase() + ".who").exists();
	}
	
	public void close()
	{
		saveUser();
		user = null;
		try
		{
			if (input != null) input.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		input = null;
		try
		{
			if (output != null) output.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		output = null;
	}
}
