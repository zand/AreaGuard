package com.zand.areaguard.area;

import java.util.ArrayList;


public abstract class Area extends IdData {
	
	public Area(int id) {
		super(id);
	}

	/**
	 * Gets a list of Cubiods that are a part of that area.
	 * @return
	 */
	public abstract ArrayList<Cuboid> getCuboids();
	
	/**
	 * Gets a list of Cubiods that are a part of that area.
	 * @param active Whether to get active or inactive.
	 * @return
	 */
	public ArrayList<Cuboid> getCuboids(boolean active) {
		ArrayList<Cuboid> ret = new ArrayList<Cuboid>();
		for (Cuboid cuboid : getCuboids())
			if (cuboid.isActive() == active) ret.add(cuboid);
		return ret;
	}
	
	/**
	 * Gets who created the area.
	 * @return The name of the person who created the area.
	 */
	public abstract String getCreator();
	
	/**
	 * Gets weather the player is an owner of the Area. 
	 * @param player The player to check.
	 * @return True if they are
	 */
	public boolean isOwner(String player) {
		return getList("owners").hasValue(player);
	}
	
	/**
	 * Tests if a point is in the area.
	 * @param world The world that the point is in.
	 * @param x The X vector.
	 * @param y The Y vector.
	 * @param z The Z vector.
	 * @return If the point is in the area
	 */
	public boolean pointInside(World world, int x, int y, int z) {
		for (Cuboid cubiod : getCuboids(true))
			if (cubiod.pointInside(world, x, y, z))
				return true;
		return false;
	}
	
	/**
	 * Tests if a point is in the area.
	 * @param world The name of the world that the point is in.
	 * @param x The X vector.
	 * @param y The Y vector.
	 * @param z The Z vector.
	 * @return If the point is in the area
	 */
	
	public abstract List getList(String name);
	
	public abstract Area getParrent();
	
	public abstract boolean setParrent(Area parrent);
	
	public abstract ArrayList<List> getLists();
	
	public abstract Msg getMsg(String name);
	
	public abstract ArrayList<Msg> getMsgs();
	
	/**
	 * Gets the name for this Area.
	 * @return The name for this area.
	 */
	public abstract String getName();
	
	/**
	 * Sets the name for this area.
	 * @param name The name to set it to.
	 * @return True if success.
	 */
	public abstract boolean setName(String name);
	
	/**
	 * Deletes this Area and attached {@link List}s, {@link Msg}s, and {@link Cuboid}s.
	 * @return True if success.
	 */
	public abstract boolean delete();
}
