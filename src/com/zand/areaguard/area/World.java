package com.zand.areaguard.area;

import java.util.ArrayList;


public interface World {
	
	/**
	 * Gets a {@link Cuboid} at location in the world.
	 * @param x The x coord of the location
	 * @param y The y coord of the location
	 * @param z The z coord of the location
	 * @return The cuboid at that location
	 */
	public Cuboid getCuboid(long x, long y, long z);
	
	/**
	 * Gets the {@link Cuboid}s in the world.
	 * @return A ArrayList of the cuboid in the world.
	 */
	public ArrayList<Cuboid> getCuboids();
	
	/**
	 * Gets the {@link Cuboid}s at location in the world.
	 * @param x The x coord of the location
	 * @param y The y coord of the location
	 * @param z The z coord of the location
	 * @return A ArrayList cuboid at that location
	 */
	public ArrayList<Cuboid> getCuboids(long x, long y, long z);
	
	/**
	 * Deletes all the {@link Cuboid}s in the world.
	 * @return If success.
	 */
	public boolean deleteCuboids();
	
	/**
	 * Gets the Id of the world
	 * @return The worlds Id
	 */
	public int getId();
	
	/**
	 * Gets the Name of the world
	 * @return The worlds Name
	 */
	public String getName();
}
