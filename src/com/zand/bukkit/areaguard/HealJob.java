package com.zand.bukkit.areaguard;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.zand.areaguard.Config;
import com.zand.areaguard.area.Area;

public class HealJob extends TimerTask {
	private static Timer healTimer = new Timer();
	public static int healPower = 1;
	public static int healDelay = 1000;
	private static HashSet<Player> players = new HashSet<Player>(); // Keeps only 1 job per player
	private Player player;
	private Area area;
	
	public HealJob(Player player, Area area) {
		this.player = player;
		this.area = area;
		
		if (players.add(player)) // Are we not healing this player
			healTimer.schedule(this, healDelay);
	}
	
	public void run() {
		players.remove(player);
		if (player.getHealth() < 20)
			new HealJob(player, area);
		else return;
		
		// Are they inside the area
		Location loc = player.getLocation();
		if (player.isOnline() &&
				area.pointInside(
				Config.storage.getWorld(player.getWorld().getName()),
				loc.getBlockX(), 
				loc.getBlockY(), 
				loc.getBlockZ())) {
			if (player.getHealth() > 0)
					player.setHealth(player.getHealth() + healPower);
		}
	}
}