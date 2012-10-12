package com.deaboy.whois;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.deaboy.whois.Settings.SettingBool;
import com.deaboy.whois.users.User;
import com.deaboy.whois.users.UserFile;
import com.deaboy.whois.users.User.Field;
import com.deaboy.whois.users.User.Stat;

public class Commands implements CommandExecutor
{
	public Commands()
	{
		Bukkit.getPluginCommand("whois").setExecutor(this);
		Bukkit.getPluginCommand("stats").setExecutor(this);
	}
	
	public boolean onCommand(CommandSender sender, Command c, String cmd, String[] args)
	{
		if (cmd.equalsIgnoreCase("whois"))
		{
			if (args.length == 1)
			{
				sendWhoisInfo(sender, args[0]);
				return true;
			}
			else
			{
				return false;
			}
		}
		
		if (cmd.equalsIgnoreCase("stats"))
		{
			if (args.length == 1)
			{
				sendWhoisStats(sender, args[0]);
				return true;
			}
			else
			{
				return false;
			}
		}
		
		return false;
	}
	
	public static void sendWhoisInfo(CommandSender sender, String user)
	{
		if (!Whois.getSettings().getSetting(SettingBool.PUBLIC_WHOIS) && !sender.isOp())
		{
			sender.sendMessage(ChatColor.RED + "Only server operators have access to this command.");
			return;
		}
		if (!UserFile.exists(user))
		{
			sender.sendMessage(ChatColor.RED + "That user does not exist!");
			return;
		}

		User u = Bukkit.getPlayer(user) != null ? Whois.getUser(Bukkit.getPlayer(user)) : new User(user);
		
		ChatColor c = ChatColor.YELLOW;
		String pre = c + "   ";
		boolean empty = true;
		
		
		sender.sendMessage(c + "---------- Who Is " + u.getName() + " ----------");
		
		if (!u.getField(Field.REAL_NAME).isEmpty())
		{
			sender.sendMessage(pre + "Real Name:  " + u.getField(Field.REAL_NAME));
			empty = false;
		}
		if (!u.getField(Field.TIME_JOINED).isEmpty())
		{
			sender.sendMessage(pre + "Date Joined:  " + u.getField(Field.TIME_JOINED));
			empty = false;
		}
		if (!u.getField(Field.PHONE).isEmpty() && (sender.isOp() || Whois.getSettings().getSetting(SettingBool.PUBLIC_WHOIS_SENSITIVE)))
		{
			sender.sendMessage(pre + "Phone:  " + u.getField(Field.PHONE));
			empty = false;
		}
		if (!u.getField(Field.EMAIL).isEmpty() && (sender.isOp() || Whois.getSettings().getSetting(SettingBool.PUBLIC_WHOIS_SENSITIVE)))
		{
			sender.sendMessage(pre + "Email:  " + u.getField(Field.EMAIL));
			empty = false;
		}
		if (!u.getField(Field.LOCATION).isEmpty() && (sender.isOp() || Whois.getSettings().getSetting(SettingBool.PUBLIC_WHOIS_SENSITIVE)))
		{
			sender.sendMessage(pre + "Location:  " + u.getField(Field.LOCATION));
			empty = false;
		}
		if (empty)
		{
			sender.sendMessage(pre + ChatColor.RED + "I have no information on " + u.getName() + ".  :(");
		}
		
		if (Bukkit.getPlayer(user) == null)
		{
			u.close();
		}
	}
	
	public static void sendWhoisStats(CommandSender sender, String user)
	{
		if (!Whois.getSettings().getSetting(SettingBool.PUBLIC_STATS) && !sender.isOp())
		{
			sender.sendMessage(ChatColor.RED + "Only server operators have access to this command.");
			return;
		}
		if (!UserFile.exists(user))
		{
			sender.sendMessage(ChatColor.RED + "That user does not exist!");
			return;
		}
		
		User u = Bukkit.getPlayer(user) != null ? Whois.getUser(Bukkit.getPlayer(user)) : new User(user);
		
		ChatColor c = ChatColor.YELLOW;
		String pre = c + "   ";
		
		
		sender.sendMessage(c + "---------- Who Is " + u.getName() + " ----------");
		
		for (Stat s : Stat.values())
		{
			String m = (s == Stat.SESSION_TIME || s == Stat.TOTAL_TIME ? formatTime(u.getStat(s)) : u.getStat(s).toString());
			sender.sendMessage(pre + s.getLabel() + ":  " + m);
		}
		
		if (Bukkit.getPlayer(user) == null)
		{
			u.close();
		}
	}
	
	public static String formatTime(Long l)
	{
		String time = new String();
		
		// Days
		if (l / 86400000 > 0)
			time += (l / 86400000) + " Days, ";
		l %= 86400000;
		
		// Hours
		if (l / 3600000 < 10)
			time += "0";
		time += l / 3600000;
		l %= 3600000;
		time += ":";
		
		// Minutes
		if (l / 60000 < 10)
			time += "0";
		time += l / 60000;
		l %= 60000;
		time += ":";
		
		// Seconds
		if (l / 1000 < 10)
			time += "0";
		time += l / 1000;
		l %= 1000;
		
		return time;
	}
}
