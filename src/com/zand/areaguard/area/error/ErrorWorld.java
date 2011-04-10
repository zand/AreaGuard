package com.zand.areaguard.area.error;

import java.util.ArrayList;

import com.zand.areaguard.area.Cuboid;
import com.zand.areaguard.area.World;

public class ErrorWorld extends World {
	public static final ErrorWorld
		NOT_FOUND = new ErrorWorld("WORLD NOT FOUND");
	
	private final String name;
	
	public ErrorWorld(String name) {
		super(-2);
		this.name = name;
	}

	@Override
	public boolean deleteCuboids() {
		return false;
	}

	@Override
	public Cuboid getCuboid(int x, int y, int z) {
		return ErrorCuboid.NOT_FOUND;
	}

	@Override
	public ArrayList<Cuboid> getCuboids() {
		return new ArrayList<Cuboid>();
	}

	@Override
	public ArrayList<Cuboid> getCuboids(int x, int y, int z) {
		return new ArrayList<Cuboid>();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean exsists() {
		return false;
	}

	@Override
	public Cuboid getCuboid(boolean active, int x, int y, int z) {
		return ErrorCuboid.NOT_FOUND;
	}

}
