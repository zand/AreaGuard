package com.zand.bukkit.areaguard.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.ContainerBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;

import org.bukkit.event.block.Action;
import com.zand.areaguard.Config;
import com.zand.areaguard.area.Area;
import com.zand.areaguard.area.Cuboid;
import com.zand.bukkit.areaguard.AreaGuard;
import com.zand.bukkit.areaguard.HealJob;
import com.zand.bukkit.areaguard.PlayerSession;
import com.zand.bukkit.areaguard.Session;

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
    
	@Override
    public void onPlayerMove(PlayerMoveEvent event) {
    	if (event.isCancelled()) return;
    	Location loc = event.getTo();
    	
    	// Check if in area
    	Player player = event.getPlayer();
    	PlayerSession ps = (PlayerSession)plugin.getSession(player);
    	
    	// if they moved to a new block
    	if (loc.getBlockX() != ps.lastLoc.getBlockX() ||
    		loc.getBlockY() != ps.lastLoc.getBlockY() ||
    		loc.getBlockZ() != ps.lastLoc.getBlockZ()) {
    		Cuboid cuboid = Config.storage.getWorld(player.getWorld().getName())
    		.getCuboid(true, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    		Area to = null;
    		cuboid = Config.storage.getWorld(player.getWorld().getName())
    		.getCuboid(true, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    		
    		if (cuboid != null) to = cuboid.getArea();
    		
    		// if they entered an area
    		if (to != null)
    			if (ps.lastArea == null || ps.lastArea.getId() != to.getId()) {
    				if (plugin.checkEvent(event, player, new String[] {"owners", "allow", "enter"}, to) &&
    					plugin.playerCan(to, player, new String[] {"heal"})) // can they auto heal
    						new HealJob(player, to); // start a new HealJob
    			}
    		
    		// if they left an area
    		if (ps.lastArea != null) 
    			if (to == null || ps.lastArea.getId() != to.getId()) {
    			String msg = ps.lastArea.getMsg("leave").getMsg();
    			if (!msg.isEmpty()) player.sendMessage(ChatColor.YELLOW + msg);
    		}
    		
    		if (event.isCancelled()) {
    			player.teleport(ps.lastLoc);
    		}
    		else {
    			ps.lastArea = to;
    			ps.lastLoc = loc;
    		}
    	}
    }
	
	public void onPlayerInteract(PlayerInteractEvent event) {
		Action action = event.getAction();
		Block block = event.getClickedBlock();
    	Player player = event.getPlayer();
    	
    	// Left Click
    	if  (action == Action.LEFT_CLICK_BLOCK) {
    		// Set Point
        	if (player.getItemInHand().getTypeId() == Config.createTool) {
        		Session ps = plugin.getSession(player);
        		ps.select(Config.storage.getWorld(player.getWorld().getName()));
        		ps.selectLeft(block.getX(), block.getY(), block.getZ());
        		player.sendMessage("Left Point Selected (" + block.getX() + ", " + block.getY() + ", " + block.getZ() + ") set");
        	}
    	}
    	
    	// Right Click
    	if  (action == Action.RIGHT_CLICK_BLOCK) {
	    	// Set Point
	    	if (player.getItemInHand().getTypeId() == Config.createTool) {
	    		Session ps = plugin.getSession(player);
	    		ps.select(Config.storage.getWorld(player.getWorld().getName()));
	    		ps.selectRight(block.getX(), block.getY(), block.getZ());
	    		player.sendMessage("Right Point Selected (" + block.getX() + ", " + block.getY() + ", " + block.getZ() + ") set");
	    	}
	    	
	    	// Check Area
	    	else if (player.getItemInHand().getTypeId() == Config.checkTool) {
	    		Cuboid cuboid = Config.storage.getWorld(player.getWorld().getName())
	        		.getCuboid(block.getX(), block.getY(), block.getZ());
	    		if (cuboid != null && cuboid.exsists())
	        		plugin.showCuboidInfo(event.getPlayer(), cuboid);
	    		else player.sendMessage(ChatColor.YELLOW + "not an Area");
	    	}
    	}
		
		if (event.isCancelled()) return;
		
		checkCanUse(player, block, event);
    }
	
	/**
	 * Checks if a player is allowed to use a block.
	 * @param player	The player to check for
	 * @param block		The block to check
	 * @param event		The event to cancel if he can't
	 * @return			If the player can use the block
	 */
	public boolean checkCanUse(Player player, Block block, Cancellable event) {
		Material mat = block.getType();
		String list = mat.name().toLowerCase().replaceAll("\\W", "").replaceAll("_", "-");
		
		// If it can be opened
		if (block.getState() instanceof ContainerBlock) {
			if (!plugin.checkEvent(event, player, 
				new String[] {"owners", "allow", "open", list}, 
				block.getX(), block.getY(), block.getZ())) return false;
		}
		
		return plugin.checkEvent(event, player, 
				new String[] {"owners", "allow", "use", list}, 
				block.getX(), block.getY(), block.getZ());
	}
}
