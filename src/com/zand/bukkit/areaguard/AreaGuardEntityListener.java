package com.zand.bukkit.areaguard;

import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.entity.*;

import com.zand.areaguard.Area;

/**
 * Handles all events fired in relation to entities
 */
public class AreaGuardEntityListener extends EntityListener {
	public final AreaGuard plugin;
	
    public AreaGuardEntityListener(AreaGuard instance) {
    	plugin = instance;
    }

    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
    	if (event.isCancelled()) return;
    	
    	Entity to = event.getEntity();
    	Entity from = event.getDamager();
    	String type = "";
    	
    	Player player = null;
    	
    	// if this is a player
    	if (from instanceof Player) {
    		player = (Player) from;
    		if (to instanceof Player) type = "pvp";
    		else type = "mobs";
    	}
    	else if (to instanceof Player) {
    		player = (Player) to;
    		type = "mobs";
    	}
    	
    	if(player == null) return;
    	Location loc = to.getLocation();
    	Area area = Area.getArea(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		if (plugin.checkEvent(event, player, type, area, false) &&
				to instanceof Player)
			onPlayerDamage((Player) to, area);
    }
    
    public void onEntityDamageByProjectile(EntityDamageByProjectileEvent event) {
    	if (event.isCancelled()) return;
    	
    	Entity to = event.getEntity();
    	Entity from = event.getDamager();
    	String type = "";
    	
    	Player player = null;
    	
    	// if this is a player
    	if (from instanceof Player) {
    		player = (Player) from;
    		if (to instanceof Player) type = "pvp";
    		else type = "mobs";
    	}
    	else if (to instanceof Player) {
    		player = (Player) to;
    		type = "mobs";
    	}
    	
    	if (player == null) return;
    	Location loc = to.getLocation();
    	Area area = Area.getArea(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		if (plugin.checkEvent(event, player, type, area, false) &&
				to instanceof Player)
			onPlayerDamage((Player) to, area);
    }
    
    public void onEntityDamageByBlock(EntityDamageByBlockEvent event) {
    	if (event.isCancelled()) return;
    	
    	Entity to = event.getEntity();
    	
    	if (to instanceof Player) {
    		Location loc = to.getLocation();
        	Area area = Area.getArea(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        	onPlayerDamage((Player) to, area);
    	}
    }
    
    private  void onPlayerDamage(Player player, Area area) {
    	// if they are in an area
		if (area != null)
			if (area.playerCan(player.getName(), "heal", false)) // can they auto heal
					new HealJob(player, area); // start a new HealJob
    }
    
    /*public void onEntityCombust(EntityCombustEvent event) {
    }

    public void onEntityExplode(EntityExplodeEvent event) {
    }*/
}
