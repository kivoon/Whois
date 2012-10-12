package com.deaboy.whois.users;

import java.io.Closeable;
import java.util.Date;
import java.util.EnumMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.deaboy.whois.Whois;

public class User implements Closeable
{
	private UserFile file;
	
	private EnumMap<Field, String> fields = new EnumMap<Field, String>(Field.class);
	private EnumMap<Stat, Long> stats = new EnumMap<Stat, Long>(Stat.class);
	
	int sched_save, sched_time;
	long last_time;
	
	
	// -------- CONSTRUCTORS -------- //
	
	public User(String player_name)
	{
		setField(Field.PLAYER_NAME, player_name);
		file = new UserFile(this);
		startTimers();
	}
	
	public User(Player player)
	{
		this(player.getName());
	}
	
	private void startTimers()
	{
		last_time = new Date().getTime();
		sched_save = Bukkit.getScheduler().scheduleAsyncRepeatingTask(Whois.getInstance(), new Runnable()
				{
					@Override
					public void run()
					{
						file.saveUser();
					}
				}, 200L, 200L);
		sched_time = Bukkit.getScheduler().scheduleAsyncRepeatingTask(Whois.getInstance(), new Runnable()
				{
					@Override
					public void run()
					{
						long current_time = new Date().getTime();
						incrementStat(Stat.SESSION_TIME, current_time - last_time);
						incrementStat(Stat.TOTAL_TIME, current_time - last_time);
						last_time = current_time;
					}
				}, 1L, 200L);
	}
	
	
	// -------- GETTERS -------- //
	
	public String getField(Field f)
	{
		if (fields.containsKey(f))
		{
			return fields.get(f);
		}
		else
		{
			fields.put(f, new String());
			return new String();
		}
	}
	
	public String getName()
	{
		return getField(Field.PLAYER_NAME);
	}
	
	public Long getStat(Stat s)
	{
		if (stats.containsKey(s))
		{
			return stats.get(s);
		}
		else
		{
			stats.put(s, 0L);
			return 0L;
		}
	}
	
	
	// -------- SETTERS -------- //
	
	public void setField(Field f, String val)
	{
		fields.put(f, val);
	}
	
	public void setStat(Stat s, Long val)
	{
		if (val >= 0)
			stats.put(s, val);
	}
	
	public void incrementStat(Stat s)
	{
		incrementStat(s, 1L);
	}
	
	public void incrementStat(Stat s, Long val)
	{
		if (val >= 1)
			stats.put(s, getStat(s) + val);
	}
	
	
	// -------- CLOSER -------- //
	
	public void close()
	{
		
		
		file.close();
		file = null;
		
		Bukkit.getScheduler().cancelTask(sched_save);
		Bukkit.getScheduler().cancelTask(sched_time);
	}
	
	
	// -------- ENUMS -------- //
	
	public enum Field
	{
		PLAYER_NAME,
		PLAYER_NAME_COLOR,
		PREFIX_TEXT,
		PREFIX_COLOR,
		REAL_NAME,
		EMAIL,
		PHONE,
		LOCATION,
		TIME_JOINED;
		
		public String toString()
		{
			switch (this)
			{
			case PLAYER_NAME:
				return "player_name";
			case PLAYER_NAME_COLOR:
				return "player_name_color";
			case PREFIX_TEXT:
				return "prefix_text";
			case PREFIX_COLOR:
				return "prefix_color";
			case REAL_NAME:
				return "real_name";
			case EMAIL:
				return "email";
			case PHONE:
				return "phone";
			case LOCATION:
				return "location";
			case TIME_JOINED:
				return "join_time";
			default:
				return null;
			}
		}
	}
	
	public enum Stat
	{
		TOTAL_TIME,
		SESSION_TIME,
		DIAMONDS,
		DAMAGE_TAKEN,
		DAMAGE_DEALT,
		KILLS_HOSTILE,
		KILLS_PASSIVE,
		KILLS_PLAYERS,
		DEATH_COUNT,
		LOG_INS;
		
		public String toString()
		{
			switch (this)
			{
			case TOTAL_TIME:
				return "total_time";
			case SESSION_TIME:
				return "session_time";
			case DEATH_COUNT:
				return "deaths";
			case DAMAGE_TAKEN:
				return "damage_taken";
			case DAMAGE_DEALT:
				return "damage_dealt";
			case KILLS_HOSTILE:
				return "kills_hostile";
			case KILLS_PASSIVE:
				return "kills_passive";
			case KILLS_PLAYERS:
				return "kills_players";
			case LOG_INS:
				return "logins";
			case DIAMONDS:
				return "collected_diamonds";
			default:
				return null;
			}
		}
	}
}
