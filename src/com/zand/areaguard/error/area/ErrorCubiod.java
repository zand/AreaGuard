package com.zand.areaguard.error.area;

import com.zand.areaguard.area.*;

public class ErrorCubiod implements Cubiod {
	World world;
	Area area;
	
	@Override
	public int getPriority() {return Integer.MAX_VALUE;}
	@Override
	public Area getArea() {
	
		return area;
	}
	@Override
	public long[] getCoords() {
		return new long[] {
				Long.MIN_VALUE, Long.MIN_VALUE, Long.MIN_VALUE, 
				Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE, 
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
