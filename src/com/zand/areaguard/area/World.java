package com.zand.areaguard.area;

import java.util.ArrayList;


public interface World extends Data {
	
	/**
	 * Gets a {@link Cubiod} at location in the world.
	 * @param x The x coord of the location
	 * @param y The y coord of the location
	 * @param z The z coord of the location
	 * @return The cubiod at that location
	 */
	public Cubiod getCubiod(long x, long y, long z);
	
	/**
	 * Gets the {@link Cubiod}s in the world.
	 * @return A ArrayList of the cubiod in the world.
	 */
	public ArrayList<Cubiod> getCubiods();
	
	/**
	 * Gets the {@link Cubiod}s at location in the world.
	 * @param x The x coord of the location
	 * @param y The y coord of the location
	 * @param z The z coord of the location
	 * @return A ArrayList cubiod at that location
	 */
	public ArrayList<Cubiod> getCubiods(long x, long y, long z);
	
	/**
	 * Deletes all the {@link Cubiod}s in the world.
	 * @return If success.
	 */
	public boolean deleteCubiods();
	
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
