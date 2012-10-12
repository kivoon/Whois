package com.deaboy.whois;

import java.io.Closeable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.deaboy.whois.users.User;
import com.deaboy.whois.users.UserFile;
import com.deaboy.whois.users.User.Field;
import com.deaboy.whois.users.User.Stat;

public class EventListener implements Listener, Closeable
{
	public EventListener()
	{
		Bukkit.getPluginManager().registerEvents(this, Whois.getInstance());
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		if (!UserFile.exists(e.getPlayer().getName()) && Whois.getSettings().getBoolean("whitelist", false))
		{
			e.getPlayer().kickPlayer(Whois.getSettings().getString("deniedmessage", "You are not whitelisted on this server!"));
			e.setJoinMessage(null);
			Bukkit.getLogger().log(Level.INFO, "Oops, nevermind. Whois is in whitelist mode.");
			return;
		}
		
		User u = Whois.loadUser(e.getPlayer());
		
		String message = new String();
		message += ChatColor.YELLOW;
		message += e.getPlayer().getName() + " ";
		
		if (!u.getField(Field.REAL_NAME).isEmpty())
		{
			message += "(" + u.getField(Field.REAL_NAME) + ") ";
			e.getPlayer().setPlayerListName((e.getPlayer().getName() + " (" + u.getField(Field.REAL_NAME) + ")").substring(0, 16));
		}
		message += "has joined the game";
		
		if (u.getField(Field.TIME_JOINED).isEmpty())
		{
			message += " for the first time";
			
			u.setField(Field.TIME_JOINED, new SimpleDateFormat().format(new Date()));
		}
		e.setJoinMessage(message + ".");
		
		String display_name = new String();
		
		if (!u.getField(Field.PREFIX_TEXT).isEmpty())
		{
			try
			{
				display_name += ChatColor.getByChar(u.getField(Field.PREFIX_COLOR));
			}
			catch (IllegalArgumentException ex)
			{ }
			
			display_name += "[" + u.getField(Field.PREFIX_TEXT) + "]";
		}
		
		try
		{
			display_name += ChatColor.getByChar(u.getField(Field.PLAYER_NAME_COLOR));
		}
		catch (IllegalArgumentException ex)
		{ }
		
		display_name += e.getPlayer().getName();
		
		e.getPlayer().setDisplayName(display_name);
		
		u.incrementStat(Stat.LOG_INS);
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e)
	{
		if (!UserFile.exists(e.getPlayer().getName()) && Whois.getSettings().getBoolean("whitelist", false))
		{
			e.setQuitMessage(null);
			return;
		}
		else
		{
			User u = Whois.getUser(e.getPlayer());
			
			String message = new String();
			
			message += ChatColor.YELLOW;
			message += e.getPlayer().getName();
			
			if (!u.getField(Field.REAL_NAME).isEmpty())
			{
				message += "(" + u.getField(Field.REAL_NAME) + ")";
			}
			
			message += " has left the game.";
			
			e.setQuitMessage(message);
			
			Whois.unloadUser(e.getPlayer());
		}
	}
	
	public void onPlayerKick(PlayerKickEvent e)
	{
		if (!UserFile.exists(e.getPlayer().getName()) && Whois.getSettings().getBoolean("whitelist", false))
		{
			e.setLeaveMessage(null);
			return;
		}
		else
		{
			User u = new User(e.getPlayer());
			
			String message = new String();
			
			message += ChatColor.YELLOW;
			message += e.getPlayer().getName();
			
			if (!u.getField(Field.REAL_NAME).isEmpty())
			{
				message += "(" + u.getField(Field.REAL_NAME) + ")";
			}
			
			message += " has left the game.";
			
			e.setLeaveMessage(message);
			
			u.close();
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEntityEvent e)
	{
		if (!Whois.getSettings().getBoolean("stick", true))
		{
			return;
		}
		
		if (e.getRightClicked().getType() == EntityType.PLAYER)
		{
			Commands.sendWhoisInfo(e.getPlayer(), ((Player) e.getRightClicked()).getName());
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e)
	{
		if (!Whois.getSettings().getBoolean("track_stats", true) || e.isCancelled())
		{
			return;
		}
		
		User u = Whois.getUser(e.getPlayer());
		
		if (e.getBlock().getType().equals(Material.DIAMOND_ORE))
		{
			u.incrementStat(Stat.DIAMONDS);
		}
	}
	
	public void close()
	{
		HandlerList.unregisterAll(this);
	}
}
