package com.zand.bukkit.areaguard.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.*;

import com.zand.areaguard.Config;
import com.zand.areaguard.area.Area;
import com.zand.bukkit.areaguard.AreaGuard;

public class AreaGuardBlockListener extends BlockListener {
	
	public final AreaGuard plugin;

    public AreaGuardBlockListener(AreaGuard instance) {
    	plugin = instance;
    }
    
    public void onBlockPlace(BlockPlaceEvent event) {
    	if (event.isCancelled()) return;
    	
    	Player player = (Player)event.getPlayer();
    	Block block = event.getBlock();
    	
    	checkCanBuild(player, block, event);
    }
    
    public void onBlockDamage(BlockDamageEvent event) {
    	if (event.isCancelled()) return;
    	if (event.getDamageLevel() == BlockDamageLevel.BROKEN) {
    		
    		Player player = (Player)event.getPlayer();
    		Block block = event.getBlock();
    		if (!checkCanBuild(player, block, event))
    			return;
    	}
    }
    
    public void onBlockRightClick(BlockRightClickEvent event) {
    	Block block = event.getBlock();
    	Player player = event.getPlayer();
    	
    	// Set Point
    	if (event.getItemInHand().getTypeId() == Config.createTool) {
    		plugin.getSession(player).select(block.getX(), block.getY(), block.getZ());
    		player.sendMessage("Point (" + block.getX() + ", " + block.getY() + ", " + block.getZ() + ") set");
    	}
    	// Check Area
    	else if (event.getItemInHand().getTypeId() == Config.checkTool) {
    		Area area = Config.storage.getWorld(player.getWorld().getName())
        	.getCuboid(block.getX(), block.getY(), block.getZ())
        	.getArea();
    		if (area != null)
        		plugin.showAreaInfo(event.getPlayer(), area);
    		else player.sendMessage(ChatColor.YELLOW + "not an Area");
    	}
    }
	
	public void onBlockInteract(BlockInteractEvent event) {
		if (event.isCancelled()) return;
		if (!event.isPlayer()) return;
		
		Player player = (Player)event.getEntity();
		Block block = event.getBlock();
		
		checkCanUse(player, block, event);
	}
	
	/**
	 * Checks if a player is allowed to create or destroy a block.
	 * @param player	The player to check for
	 * @param block		The block to check
	 * @param event		The event to cancel if he can't
	 * @return			If the player can create or destroy
	 */
	public boolean checkCanBuild(Player player, Block block, Cancellable event) {
		// Check build
		if (!plugin.checkEvent(event, player, 
				new String[] {"owners", "allow", "build"}, 
				block.getX(), block.getY(), block.getZ()))
			return false;
		
		// Check can use
		if (checkCanUse(player, block, event))
			return false;
		
		
		return true;
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
		String type = "";
		
		// Check things that can be Opened
		switch (mat) {
		case CHEST: type = "chest"; break;
		case FURNACE: type = "furnace"; break;
		case DISPENSER: type = "dispenser"; break;
		case JUKEBOX: type = "jukebox"; break;
		}
		
		if (!type.isEmpty()) {
			return plugin.checkEvent(event, player, 
					new String[] {"owners", "allow", "open", type, "block-"+block.getTypeId()}, 
					block.getX(), block.getY(), block.getZ());
		}
		
		//Check things that can be operated
		switch (mat) {
		case WOOD_DOOR: type = "door"; break;
		case LEVER: type = "lever"; break;
		case STONE_BUTTON: type = "button"; break;
		}
		
		if (!type.isEmpty()) {
			return plugin.checkEvent(event, player, 
					new String[] {"owners", "allow", type, "block-"+block.getTypeId()}, 
					block.getX(), block.getY(), block.getZ());
		}
		
		return false;
	}
	
}
