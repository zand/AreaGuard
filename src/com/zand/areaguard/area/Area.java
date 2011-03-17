package com.zand.areaguard.area;

import java.util.ArrayList;


public interface Area extends Data {
	
	/**
	 * Gets a list of Cubiods that are a part of that area.
	 * @return
	 */
	public ArrayList<Cuboid> getCubiods();
	
	/**
	 * Gets who created the area.
	 * @return The name of the person who created the area.
	 */
	public String getCreator();
	
	/**
	 * Gets weather the player is an owner of the Area. 
	 * @param player The player to check.
	 * @return True if they are
	 */
	public boolean isOwner(String player);
	
	/**
	 * Tests if a point is in the area.
	 * @param world The world that the point is in.
	 * @param x The X vector.
	 * @param y The Y vector.
	 * @param z The Z vector.
	 * @return If the point is in the area
	 */
	public boolean pointInside(World world, int x, int y, int z);
	
	/**
	 * Tests if a point is in the area.
	 * @param world The name of the world that the point is in.
	 * @param x The X vector.
	 * @param y The Y vector.
	 * @param z The Z vector.
	 * @return If the point is in the area
	 */
	public boolean pointInside(String world, int x, int y, int z);
	
	public boolean playerCan(String player, String[] lists);
	
	public List getList(String name);
	
	public Area getParrent();
	
	public boolean setParrent(Area parrent);
	
	public ArrayList<List> getLists();
	
	public Msg getMsg(String name);
	
	public ArrayList<Msg> getMsgs();
	
	public int getId();
	
	public String getName();
	
	public boolean setName(String name);
}
