package com.zand.areaguard.area;

import java.util.ArrayList;


public abstract class World extends IdData {
	
	public World(int id) {
		super(id);
	}

	/**
	 * Gets a {@link Cuboid} at location in the world.
	 * @param x The x coord of the location
	 * @param y The y coord of the location
	 * @param z The z coord of the location
	 * @return The cuboid at that location
	 */
	public abstract Cuboid getCuboid(int x, int y, int z);
	
	/**
	 * Gets a {@link Cuboid} at location in the world.
	 * @param active Whether to get active or inactive.
	 * @param x The x coord of the location
	 * @param y The y coord of the location
	 * @param z The z coord of the location
	 * @return The cuboid at that location
	 */
	public abstract Cuboid getCuboid(boolean active, int x, int y, int z);
	
	/**
	 * Gets the {@link Cuboid}s in the world.
	 * @return A ArrayList of the cuboid in the world.
	 */
	public abstract ArrayList<Cuboid> getCuboids();
	
	/**
	 * Gets the {@link Cuboid}s at location in the world.
	 * @param x The x coord of the location
	 * @param y The y coord of the location
	 * @param z The z coord of the location
	 * @return A ArrayList cuboid at that location
	 */
	public abstract ArrayList<Cuboid> getCuboids(int x, int y, int z);
	
	/**
	 * Deletes all the {@link Cuboid}s in the world.
	 * @return If success.
	 */
	public abstract boolean deleteCuboids();
	
	/**
	 * Gets the Name of the world
	 * @return The worlds Name
	 */
	public abstract String getName();
}
