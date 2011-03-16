package com.zand.areaguard.area;

import java.util.ArrayList;


public interface Area {
	
	/**
	 * Gets a list of Cubiods that are a part of that area.
	 * @return
	 */
	public ArrayList<Cubiod> getCubiods();
	
	public boolean isOwner(String player);
	
	/**
	 * Tests if a point is in the area.
	 * @param world The world that the point is in.
	 * @param x The X vector.
	 * @param y The Y vector.
	 * @param z The Z vector.
	 * @return If the point is in the area
	 */
	public boolean pointInside(World world, long x, long y, long z);
	
	/**
	 * Tests if a point is in the area.
	 * @param world The name of the world that the point is in.
	 * @param x The X vector.
	 * @param y The Y vector.
	 * @param z The Z vector.
	 * @return If the point is in the area
	 */
	public boolean pointInside(String world, long x, long y, long z);
	
	public boolean hasOwner(String player);
	
	public boolean playerCan(String player, String[] lists);
	
	public List getList(String name);
	
	public ArrayList<List> getLists();
	
	public Msg getMsg(String name);
	
	public ArrayList<Msg> getMsgs();
	
	public int getId();
	
	public String getName();
	
	public boolean setName(String name);
}
