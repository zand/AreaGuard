package com.zand.bukkit.areaguard;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.zand.areaguard.Area;
import com.zand.areaguard.Session;

public class PlayerSession extends Session {
	public final Player player;
	public Area lastArea = null;
	public Location lastLoc;
	PlayerSession(Player player) {
		this.player = player;
		name = player.getName();
		lastLoc = player.getLocation();
		lastArea = Area.getArea(lastLoc.getBlockX(), 
								lastLoc.getBlockY(), 
								lastLoc.getBlockZ());
	}
}
