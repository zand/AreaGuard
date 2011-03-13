package com.zand.areaguard;

public class Session{
	public String name = null;
	protected int[] loc1 = new int[3];
	protected int[] loc2 = new int[3];
	public int selected = -1;
	protected String worldName = "ERROR";
	protected int worldId = -1;
	public boolean ignoreOwnership = false;
	public boolean debug = false;
	
	public void setWorld(int id) {
		worldId = id;
		worldName = AreaDatabase.getInstance().getWorldName(id);
	}
	
	public void setWorld(String name) {
		worldName = name;
		worldId = AreaDatabase.getInstance().getWorldId(name);
	}
	
	public int getWorldId() {
		return worldId;
	}
	
	public String getWorldName() {
		return worldName;
	}
	
	public void setPoint(int x, int y, int z) {
		loc2 = loc1;
		loc1 = new int[] {x, y, z};
		coordsUpdated();
	}
	
	public boolean setCoords(int[]coords) {
		if (coords.length != 6) return false;
		
		for (int i=0; i < 3; i++) loc1[i] = coords[i];
		for (int i=0; i < 3; i++) loc2[i] = coords[i+3];
		coordsUpdated();
		return true;
	}
	
	public void coordsUpdated() {
		// stub
	}
	
	public int[] getPoint() {
		return loc1;
	}
	
	public int[] getCoords() {
		int[] ret = new int[6];
		for (int i=0; i < 3; i++) ret[i]   = (loc1[i] < loc2[i] ? loc1[i] : loc2[i]);
		for (int i=0; i < 3; i++) ret[i+3] = (loc1[i] > loc2[i] ? loc1[i] : loc2[i]);
		return ret;
	}
}
