package com.zand.bukkit.areaguard;

import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.entity.*;

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
    	Location loc = event.getEntity().getLocation();
		plugin.checkEvent(event, player, type, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    	
    }
    
    /*public void onEntityDamageByProjectile(EntityDamageByProjectileEvent event) {
    }
    
    public void onEntityCombust(EntityCombustEvent event) {
    }

    public void onEntityDamage(EntityDamageEvent event) {
    }

    public void onEntityExplode(EntityExplodeEvent event) {
    }*/
}
