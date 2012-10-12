package com.deaboy.whois;

import java.io.File;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.deaboy.whois.Settings.SettingBool;
import com.deaboy.whois.Settings.SettingString;
import com.deaboy.whois.users.User;
import com.deaboy.whois.users.UserFile;

public class Whois extends JavaPlugin
{
	private static Whois instance;
	private static EventListener listener;
	private static Settings settings;
	private final static File directory = new File("plugins/Whois");
	
	private HashMap<String, User> users = new HashMap<String, User>();
	
	
	// -------- INITIALIZER -------- //
	
	@Override
	public void onEnable()
	{
		instance = this;
		listener = new EventListener();
		settings = new Settings();
		new Commands();
		
		directory.mkdirs();
		
		// Load users, remove players who shouldn't be here
		boolean whitelist = settings.getSetting(SettingBool.WHITELIST);
		for (Player p : Bukkit.getOnlinePlayers())
		{
			if (whitelist && !UserFile.exists(p.getName()))
				p.kickPlayer(settings.getSetting(SettingString.MESSAGE_WHITELIST));
			else
				loadUser(p);
		}
	}
	
	@Override
	public void onDisable()
	{
		listener.close();
		settings.close();
		
		for (User u :users.values())
		{
			u.close();
		}
		users.clear();
	}
	
	
	// -------- STATIC USER FUNCTIONS -------- //
	
	public static User loadUser(Player p)
	{
		if (instance.users.containsKey(p.getName()))
			return instance.users.get(p.getName());
		
		User u = new User(p);
		instance.users.put(p.getName(), u);
		return u;
	}
	
	public static void unloadUser(Player p)
	{
		if (instance.users.containsKey(p.getName()))
		{
			instance.users.get(p.getName()).close();
			instance.users.remove(p.getName());
		}
	}
	
	public static User getUser(Player p)
	{
		if (instance.users.containsKey(p.getName()))
		{
			return instance.users.get(p.getName());
		}
		else
		{
			return null;
		}
	}
	
	
	// -------- STATIC GETTERS -------- //
	
	public static Whois getInstance()
	{
		return instance;
	}
	
	public static File getDirectory()
	{
		return directory;
	}
	
	public static Settings getSettings()
	{
		return settings;
	}
}
