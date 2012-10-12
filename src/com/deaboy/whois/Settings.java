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
		loadValues();
		saveFile();
	}
	
	
	// -------- SETTERS -------- //
	
	public void saveSetting(SettingBool setting, Boolean val)
	{
		put(setting.toString(), val.toString());
		saveFile();
	}
	
	public void saveSetting(SettingString setting, String val)
	{
		put(setting.toString(), val);
		saveFile();
	}
	
	
	// -------- GETTERS -------- //
	
	public boolean getSetting(SettingBool setting)
	{
		try
		{
			return Boolean.parseBoolean(getProperty(setting.toString()));
		}
		catch (NumberFormatException e)
		{
			return setting.getDefault();
		}
	}
	
	public String getSetting(SettingString setting)
	{
		try
		{
			return getProperty(setting.toString());
		}
		catch (NumberFormatException e)
		{
			return setting.getDefault();
		}
	}
	
	
	// -------- FILE SAVING/LOADING -------- //
	
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
	
	private void loadValues()
	{
		for (SettingBool s : SettingBool.values())
		{
			if (!containsKey(s.toString()))
				put(s.toString(), s.getDefault().toString());
		}
		for (SettingString s : SettingString.values())
		{
			if (!containsKey(s.toString()))
				put(s.toString(), s.getDefault());
		}
	}
	
	
	// -------- ENUMERATORS -------- //
	
	public enum SettingBool
	{
		WHITELIST,
		REAL_NAMES,
		NAME_COLORS,
		TRACK_STATS,
		WHOIS_STICK,
		PUBLIC_WHOIS,
		PUBLIC_STATS,
		PUBLIC_WHOIS_SENSITIVE;
		
		public String toString()
		{
			switch (this)
			{
			case WHITELIST:
				return "enable_whitelist";
			case REAL_NAMES:
				return "enable_real_names";
			case NAME_COLORS:
				return "enable_name_colors";
			case TRACK_STATS:
				return "enable_stat_tracking";
			case WHOIS_STICK:
				return "enable_whois_stick";
			case PUBLIC_WHOIS:
				return "enable_public_whois";
			case PUBLIC_STATS:
				return "enable_public_stats";
			case PUBLIC_WHOIS_SENSITIVE:
				return "enable_public_whois_sensitive";
			default:
				return null;
			}
		}
		
		public Boolean getDefault()
		{
			switch (this)
			{
			case WHITELIST:
				return false;
			case REAL_NAMES:
				return true;
			case NAME_COLORS:
				return true;
			case TRACK_STATS:
				return true;
			case WHOIS_STICK:
				return true;
			case PUBLIC_WHOIS:
				return true;
			case PUBLIC_STATS:
				return true;
			case PUBLIC_WHOIS_SENSITIVE:
				return false;
			default:
				return false;
			}
		}
	}
	
	public enum SettingString
	{
		MESSAGE_WHITELIST;
		
		public String toString()
		{
			switch (this)
			{
			case MESSAGE_WHITELIST:
				return "message_whitelist";
			default:
				return null;
			}
		}
		
		public String getDefault()
		{
			switch (this)
			{
			case MESSAGE_WHITELIST:
				return "You are not whitelisted on this server";
			default:
				return null;
			}
		}
	}
	
	
	// -------- CLOSER -------- //
	
	public void close()
	{
		// Commented out because when the users reload, I don't want their settings to be overwritten.
		// saveFile();
	}
}
