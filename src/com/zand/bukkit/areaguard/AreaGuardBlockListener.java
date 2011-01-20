package com.zand.bukkit.areaguard;

import org.bukkit.block.*;
import org.bukkit.entity.Player;
import org.bukkit.event.block.*;

import com.zand.areaguard.Area;
import com.zand.areaguard.Config;

public class AreaGuardBlockListener extends BlockListener {
	
	public final AreaGuard plugin;

    public AreaGuardBlockListener(AreaGuard instance) {
    	plugin = instance;
    }
    
    public void onBlockPlace(BlockPlaceEvent event) {
    	if (event.isCancelled()) return;
    	
    	Player player = (Player)event.getPlayer();
    	Block block = event.getBlock();
    	plugin.checkEvent(event, player, "build", block.getX(), block.getY(), block.getZ());
    }
    
    public void onBlockDamage(BlockDamageEvent event) {
    	if (event.isCancelled()) return;
    	if (event.getDamageLevel() == BlockDamageLevel.BROKEN) {
    		
    		Player player = (Player)event.getPlayer();
    		Block block = event.getBlock();
    		if (!plugin.checkEvent(event, player, "build", block.getX(), block.getY(), block.getZ()))
    			return;
    		
    		String type;
    		switch (block.getType()) {
    			case CHEST: type = "chest"; break;
    			case FURNACE: type = "furnace"; break;
    			case DISPENSER: type = "dispenser"; break;
    			case JUKEBOX: type = "jukebox"; break;
    			case LEVER: type = "lever"; break;
    			case STONE_BUTTON: type = "button"; break;
    			case WOOD_DOOR: type = "door"; break;
    			default: return;
    		}
    		
    		if (plugin.checkEvent(event, player, type, block.getX(), block.getY(), block.getZ()))
    			plugin.checkEvent(event, player, "open", block.getX(), block.getY(), block.getZ());
    	}
    }
    
    public void onBlockRightClick(BlockRightClickEvent event) {
    	Block block = event.getBlock();
    	Player player = event.getPlayer();
    	
    	if (event.getItemInHand().getTypeId() == Config.createTool) {
    		plugin.getSession(player).setPoint(block.getX(), block.getY(), block.getZ());
    		player.sendMessage("Point (" + block.getX() + ", " + block.getY() + ", " + block.getZ() + ") set");
    	}
    	else if (event.getItemInHand().getTypeId() == Config.checkTool) {
    		Area area = Area.getArea(block.getX(), block.getY(), block.getZ());
    		if (area != null)
        		plugin.showAreaInfo(event.getPlayer(), area);
    	}
    }
	
	public void onBlockInteract(BlockInteractEvent event) {
		if (event.isCancelled()) return;
		if (!event.isPlayer()) return;
		
		Player player = (Player)event.getEntity();
		Block block = event.getBlock();
		String type;
		switch (block.getType()) {
			case CHEST: type = "chest"; break;
			case FURNACE: type = "furnace"; break;
			case DISPENSER: type = "dispenser"; break;
			case JUKEBOX: type = "jukebox"; break;
			case LEVER: type = "lever"; break;
			case STONE_BUTTON: type = "button"; break;
			case WOOD_DOOR: type = "door"; break;
			default: return;
		}
		
		if (plugin.checkEvent(event, player, type, block.getX(), block.getY(), block.getZ()))
			plugin.checkEvent(event, player, "open", block.getX(), block.getY(), block.getZ());
	}
	

}
