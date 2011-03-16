package com.zand.bukkit.areaguard;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.zand.areaguard.area.Area;

public class PlayerSession extends Session {
	public Location lastLoc;
	public Area lastArea;
	
	public PlayerSession(Player player) {
		super(player);
		lastLoc = player.getLocation();
	}
	
	@Override
	public String getRealName() {
		return ((Player)sender).getName();
	}
}
