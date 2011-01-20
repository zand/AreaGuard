package com.zand.areaguard;

public class Session{
	public String name = null;
	private int[] loc1 = new int[3];
	private int[] loc2 = new int[3];
	public int selected = -1;
	public boolean ignoreOwnership = false;
	
	public void setPoint(int x, int y, int z) {
		loc2 = loc1;
		loc1 = new int[] {x, y, z};
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
