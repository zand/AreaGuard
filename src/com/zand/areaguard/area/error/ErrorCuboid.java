package com.zand.areaguard.area.error;

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
	public boolean pointInside(World world, int x, int y, int z) {
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
	public boolean setLocation(World world, int[] coords) {
		return false;
	}
	@Override
	public boolean exsists() {
		return false;
	}
	@Override
	public long getBlockCount() {
		return Integer.MAX_VALUE;
	}
	@Override
	public boolean isActive() {
		return true;
	}
	@Override
	public boolean setActive(boolean active) {
		return false;
	}
	@Override
	public String getCreator() {
		return "@Error";
	}
	@Override
	public boolean delete() {
		return false;
	}

}
