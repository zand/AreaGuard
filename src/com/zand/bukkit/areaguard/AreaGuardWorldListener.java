
package com.zand.bukkit.areaguard;

import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldListener;

/**
 * Handles all World related events
 */
public class AreaGuardWorldListener extends WorldListener {
	
	public final AreaGuard plugin;

    public AreaGuardWorldListener(AreaGuard instance) {
    	plugin = instance;
    }
	
    /**
     * Called when a chunk is unloaded
     *
     * @param event Relevant event details
     */
    public void onChunkUnloaded(ChunkUnloadEvent event) {
    	TempBlocks.deleteChunk(event.getChunk());
    }
}
