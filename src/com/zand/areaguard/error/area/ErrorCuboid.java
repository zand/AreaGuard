package com.zand.areaguard.error.area;

import com.zand.areaguard.area.*;

public class ErrorCuboid implements Cuboid {
	World world;
	Area area;
	
	@Override
	public int getPriority() {return Integer.MAX_VALUE;}
	@Override
	public Area getArea() {
	
		return area;
	}
	@Override
	public int[] getCoords() {
		return new int[] {
				Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, 
				Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
		};
	}
	@Override
	public int getId() {
		return -2;
	}
	@Override
	public World getWorld() {
		return world;
	}
	@Override
	public boolean pointInside(World world, long x, long y, long z) {
		return true;
	}
	@Override
	public boolean setPriority(int priority) {
		return false;
	}
	@Override
	public boolean setArea(Area area) {
		return false;
	}
	@Override
	public boolean setLocation(World world, long[] coords) {
		return false;
	}

}
