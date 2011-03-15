package com.zand.areaguard.area;

public interface Cubiod {
	
	/**
	 * Gets the {@link Area} that this cubiod is a part of.
	 * @return The Area that this cubiod belongs to. 
	 */
	public Area getArea();
	
	/**
	 * Gets the coords of the cubiod.
	 * @return An Array of the cubiod's coords.
	 */
	public long[] getCoords();
	
	/**
	 * Gets the cubiod's id
	 * @return The cubiod's id
	 */
	public int getId();
	
	/**
	 * Gets the Priority for the cubiod.
	 * @return The Priority
	 */
	public int getPriority();
	
	/**
	 * Gets the {@link World} that this cubiod is a part of.
	 * @return The World that this cubiod belongs to. 
	 */
	public World getWorld();

	/**
	 * Tests if a point is in the cubiod.
	 * @param world The world that the point is in.
	 * @param x The X vector.
	 * @param y The Y vector.
	 * @param z The Z vector.
	 * @return If the point is in the cubiod
	 */
	public boolean pointInside(World world, long x, long y, long z);
	
	/**
	 * Sets the Priority for the cubiod.
	 * @param priority The Priority to set it to.
	 * @return If success.
	 */
	public boolean setPriority(int priority);
	
	/**
	 * Sets the {@link Area} that the cubiod is a part of.
	 * @param priority The Priority to set it to.
	 * @return If success.
	 */
	public boolean setArea(Area area);
	
	/**
	 * Sets the location for the cubiod.
	 * @param world The world to set it to.
	 * @param coords The coords to set it to.
	 * @return If success.
	 */
	public boolean setLocation(World world, long coords[]);
}
