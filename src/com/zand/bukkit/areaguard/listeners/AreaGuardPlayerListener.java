package com.zand.bukkit.areaguard.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.zand.areaguard.Area;
import com.zand.bukkit.areaguard.AreaGuard;
import com.zand.bukkit.areaguard.HealJob;
import com.zand.bukkit.areaguard.PlayerSession;

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
	
	public void onPlayerQuit(PlayerEvent event) {
		plugin.getSession(event.getPlayer()).onQuit();
    }
    
    public void onPlayerMove(PlayerMoveEvent event) {
    	if (event.isCancelled()) return;
    	Location loc = event.getTo();
    	
    	// Check if in area
    	Player player = event.getPlayer();
    	PlayerSession ps = plugin.getSession(player);
    	
    	// if they moved to a new block
    	if (loc.getBlockX() != ps.lastLoc.getBlockX() ||
    		loc.getBlockY() != ps.lastLoc.getBlockY() ||
    		loc.getBlockZ() != ps.lastLoc.getBlockZ()) {
    		Area to = Area.getArea(ps.getWorldId(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    		
    		// if they entered an area
    		if (to != null)
    			if (ps.lastArea == null || ps.lastArea.getId() != to.getId()) {
    				if (plugin.checkEvent(event, player, new String[] {"owners", "allow", "enter"}, to) &&
    					to.playerCan(player.getName(), new String[] {"heal"})) // can they auto heal
    						new HealJob(player, to); // start a new HealJob
    			}
    		
    		// if they left an area
    		if (ps.lastArea != null) 
    			if (to == null || ps.lastArea.getId() != to.getId()) {
    			String msg = ps.lastArea.getMsg("leave");
    			if (!msg.isEmpty()) player.sendMessage(ChatColor.YELLOW + msg);
    		}
    		
    		if (event.isCancelled()) {
    			player.teleportTo(ps.lastLoc);
    		}
    		else {
    			ps.lastArea = to;
    			ps.lastLoc = loc;
    			ps.onMove();
    		}
    	}
    }
}
