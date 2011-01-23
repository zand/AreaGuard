package com.zand.bukkit.areaguard;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.zand.areaguard.Area;
import com.zand.areaguard.Session;

public class PlayerSession extends Session {
	public final Player player;
	public Area lastArea = null;
	public Location lastLoc;
	protected boolean firstPoint = true;
	private TempBlockDrawer drawer = new TempBlockDrawer();
	boolean drawOutline = false;
	protected boolean bypassArea = false;
	
	PlayerSession(Player player) {
		this.player = player;
		name = player.getName();
		lastLoc = player.getLocation();
		lastArea = Area.getArea(lastLoc.getBlockX(), 
								lastLoc.getBlockY(), 
								lastLoc.getBlockZ());
	}
	
	public void drawOutline(boolean b) {
		drawOutline = b;
		if (b) coordsUpdated();
		else drawer.delete();
	}
	
	public void coordsUpdated() {
		if (drawOutline) {
			drawer.delete();
			
			// Get the size
			int sx = loc1[0] - loc2[0];
			int sz = loc1[2] - loc2[2];
			if (sx < 0) sx = -sx;
			if (sz < 0) sz = -sz;
			
			if (firstPoint) firstPoint = false;
			else if (sx < (4<<4) && sz < (4<<4)) 
				drawer.drawCubeOutline(player.getWorld(), getCoords(), 20);
		}
	}
	
	public void onMove() {
		if (drawOutline) {
			int sx = (loc1[0] + loc2[0])/2 - lastLoc.getBlockX();
			int sz = (loc1[2] + loc2[2])/2 - lastLoc.getBlockZ();
			if (sx < 0) sx = -sx;
			if (sz < 0) sz = -sz;
			if (sx > (3<<4) || sz > (3<<4)) {
				drawOutline = false;
				drawer.delete();
			}
		}
	}
	
	public void onQuit() {
		drawer.delete();
	} 
}
