package com.zand.bukkit.areaguard;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.zand.areaguard.Area;

/**
 * Handle events for all Player related events
 * 
 * @author zand
 */
public class AreaGuardPlayerListener extends PlayerListener {
	public final AreaGuard plugin;

	public AreaGuardPlayerListener(AreaGuard instance) {
		plugin = instance;
	}
    
    public void onPlayerMove(PlayerMoveEvent event) {
    	if (event.isCancelled()) return;
    	Location loc = event.getTo();
    	
    	// Check if in area
    	Player player = event.getPlayer();
    	PlayerSession ps = plugin.getSession(player);
    	Area to = Area.getArea(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    	if (to == null) { 
    		ps.lastIn = -1;
    		return;
    	}
    	
    	// Where they in it
    	if (ps.lastIn != to.getId()) {
    		// if its not >= -1 it was never set
        	if (ps.lastIn >= -1)
        		plugin.checkEvent(event, player, "enter", loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    		ps.lastIn = to.getId();
    	}
    	
    }
}
