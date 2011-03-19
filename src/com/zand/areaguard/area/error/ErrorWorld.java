package com.zand.areaguard.area.error;

import java.util.ArrayList;

import com.zand.areaguard.area.Cuboid;
import com.zand.areaguard.area.World;

public class ErrorWorld implements World {
	final static public ErrorWorld
		NOT_FOUND = new ErrorWorld("WORLD NOT FOUND");
	
	final private String name;
	
	public ErrorWorld(String name) {
		this.name = name;
	}

	@Override
	public boolean deleteCuboids() {
		return false;
	}

	@Override
	public Cuboid getCuboid(int x, int y, int z) {
		return null;
	}

	@Override
	public ArrayList<Cuboid> getCuboids() {
		return null;
	}

	@Override
	public ArrayList<Cuboid> getCuboids(long x, long y, long z) {
		return null;
	}

	@Override
	public int getId() {
		return -2;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean exsists() {
		return false;
	}

}
