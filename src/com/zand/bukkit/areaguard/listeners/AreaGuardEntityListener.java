package com.zand.bukkit.areaguard.listeners;

import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.entity.*;

import com.zand.areaguard.Config;
import com.zand.areaguard.area.Area;
import com.zand.areaguard.area.Cuboid;
import com.zand.bukkit.areaguard.AreaGuard;
import com.zand.bukkit.areaguard.HealJob;

/**
 * Handles all events fired in relation to entities
 */
public class AreaGuardEntityListener extends EntityListener {
	public final AreaGuard plugin;
	
    public AreaGuardEntityListener(AreaGuard instance) {
    	plugin = instance;
    }
    
    @Override
    public void onEntityDamage(EntityDamageEvent event) {
    	if (event.isCancelled()) return;
    	
    	Entity entity = event.getEntity();
    	Location loc = entity.getLocation();
    	Cuboid cuboid = Config.storage.getWorld(entity.getWorld().getName())
    	.getCuboid(true, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    	if (cuboid == null) return; // TODO fix
    	Area area = cuboid.getArea();
    	if (area == null) return; // TODO fix
    	
    	if (event instanceof EntityDamageByEntityEvent) {
    		Entity from = ((EntityDamageByEntityEvent)event).getDamager();
    		
    		Player player = null;
    		
    		// if this is a player
        	if (from instanceof Player) {
        		player = (Player) from;
        		if (entity instanceof Player) { // player attacks player
        			plugin.checkEvent(event, player, new String[] {"pvp"}, area);
        		}
        		else { // player attacks mobs
        			plugin.checkEvent(event, player, new String[] {"mobs"}, area);
        		}
        	}
        	else if (entity instanceof Player) { // mobs attack player
        		player = (Player) entity;
        		plugin.checkEvent(event, player, new String[] {"mobs"}, area);
        	}
    	}
    	
    	if (entity instanceof Player) {
    		Player player = (Player)entity;
    		
    		// if they are in an area
    		if (area != null)
    			if (plugin.playerCan(area, player, new String[] {"heal"})) // can they auto heal
    					new HealJob(player, area); // start a new HealJob
    	}
    }
    
    @Override
    public void onEntityInteract(EntityInteractEvent event) {
    }
}
