package com.zand.bukkit.areaguard.listeners;

import org.bukkit.block.*;
import org.bukkit.entity.Player;
import org.bukkit.event.block.*;

import com.zand.bukkit.areaguard.AreaGuard;

public class AreaGuardBlockListener extends BlockListener {
	
	public final AreaGuard plugin;

    public AreaGuardBlockListener(AreaGuard instance) {
    	plugin = instance;
    }
    
    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
    	if (event.isCancelled()) return;
    	
    	Player player = (Player)event.getPlayer();
    	Block block = event.getBlock();
    	
    	plugin.checkCanBuild(player, block, event);
    }
    
    @Override
    public void onBlockDamage(BlockDamageEvent event) {
    	Player player = event.getPlayer();
    	Block block = event.getBlock();
    	
    	// Don't process cancelled events beyond this point
    	if (event.isCancelled()) return;
    	
    	if (!plugin.checkCanBuild(player, block, event)) return;
    }
}
