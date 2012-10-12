package com.deaboy.whois;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.deaboy.whois.users.User;
import com.deaboy.whois.users.UserFile;
import com.deaboy.whois.users.User.Field;

public class Commands implements CommandExecutor
{
	public Commands()
	{
		Bukkit.getPluginCommand("whois").setExecutor(this);
		// Bukkit.getPluginCommand("info").setExecutor(this);
	}
	
	public boolean onCommand(CommandSender sender, Command c, String cmd, String[] args)
	{
		if (cmd.equalsIgnoreCase("whois"))
		{
			if (args.length == 1)
			{
				sendWhoisInfo(sender, args[0]);
			}
			else
			{
				sender.sendMessage(ChatColor.RED + "Proper syntax is: /whois <player>");
			}
			return true;
		}
		
		return false;
	}
	
	public static void sendWhoisInfo(CommandSender sender, String user)
	{
		if (Whois.getSettings().getBoolean("opsonly", false) && !sender.isOp())
		{
			sender.sendMessage(ChatColor.RED + "Only server operators have access to this command.");
			return;
		}
		if (!UserFile.exists(user))
		{
			sender.sendMessage(ChatColor.RED + "That user does not exist!");
			return;
		}
		
		User u = new User(user);
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
		if (!u.getField(Field.PHONE).isEmpty() && sender.isOp())
		{
			sender.sendMessage(pre + "Phone:  " + u.getField(Field.PHONE));
			empty = false;
		}
		if (!u.getField(Field.EMAIL).isEmpty() && sender.isOp())
		{
			sender.sendMessage(pre + "Email:  " + u.getField(Field.EMAIL));
			empty = false;
		}
		if (!u.getField(Field.LOCATION).isEmpty() && sender.isOp())
		{
			sender.sendMessage(pre + "Location:  " + u.getField(Field.LOCATION));
			empty = false;
		}
		if (empty)
		{
			sender.sendMessage(pre + ChatColor.RED + "I have no information on " + u.getName() + ".  :(");
		}
		
		u.close();
	}
}
