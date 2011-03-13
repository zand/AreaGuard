package com.zand.areaguard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Area {
	private static AreaDatabase ad = AreaDatabase.getInstance();
	public static Area getArea(int id) {
		return ad.getArea(id);
	}
	
	public static Area getArea(String world, int x, int y, int z) {
		return getArea(ad.getWorldId(world), x, y, z);
	}
	
	public static Area getArea(int world, int x, int y, int z) {
		return ad.getArea(ad.getAreaId(world, x, y, z));
	}
	
	public static Area getArea(String world, String name) {
		return getArea(ad.getWorldId(world), name);
	}
	
	public static Area getArea(int world, String name) {
		return ad.getArea(ad.getAreaId(world, name));
	}
	
	public static Area getOwnedArea(String owner, String name) {
		for (int id : ad.getAreaIdsFromListValues("owners", owner)) {
			Area area = ad.getArea(id);
			if (area != null)
				if (area.getName().equals(name))
					return area;
		}
		return null;
	}
	public static boolean remove(int id) {
		return ad.removeArea(id);
	}

	private Integer id = -1;
	private Integer parrentId = -1;
	@SuppressWarnings("unused")
	private Integer worldId = -1;
	@SuppressWarnings("unused")
	private String worldName = "NOT FOUND";
	
	// Cache
	private String name = "NOT FOUND";
	private int priority = 0;
	
	private int[] coords = new int[6];
	
	protected Area(int id, int worldId, String name, int priority, int[] coords) {
		this.id = id;
		this.worldId = worldId;
		this.worldName = ad.getWorldName(worldId);
		this.name = name;
		this.priority = priority;
		if (coords.length == 6)
			this.coords = coords;
	}
	
	public Area(String world, String name, int[] coords) {
		this(ad.getWorldId(world), name, coords);
	}
	
	public Area(int world, String name, int[] coords) {
		this.name = name;
		if (coords.length == 6)
			this.coords = coords;
		this.id = ad.addArea(world, name, coords);
	}
	
	public boolean setMsg(String name, String msg) {
		return ad.setMsg(id, name, msg); 
	}

	public boolean addList(String list, HashSet<String> values) {
		return ad.addList(id, list, values);
	}

	public int[] getCoords() {
		return coords;
	}

	public int getId() {
		return id;
	}
	
	public String getMsg(String name) {
		return ad.getMsg(id, name);
	}
	
	public boolean isInside(int x, int y, int z) {
		return (x >= coords[0] && x <= coords[3] &&
				y >= coords[1] && y <= coords[4] &&
				z >= coords[2] && z <= coords[5]);
	}
	
	public Set<String> getLists() {
		return ad.getLists(id);
	}
	
	public ArrayList<String> getList(String list) {
		return ad.getList(id, list);
	}
	
	public String getName() {
		if (parrentId >= 0)
			return getArea(parrentId).getName();
		return name;
	}
	
	public boolean remove() {
		return remove(id);
	}
	
	public boolean removeList(String list) {
		if (parrentId >= 0)
			return ad.removeList(parrentId, list);
		return ad.removeList(id, list);
	}
	
	public boolean removeList(String list, HashSet<String> values) {
		return ad.removeList(id, list, values);
	}
	
	public boolean setCoords(int[] coords) {
		if (id == -1) return false;
		if (coords.length != 6) return false;
		this.coords = coords;
		return ad.updateArea(this);
	}

	public boolean setName(String name) {
		if (id == -1) return false;
		this.name = name;
		return ad.updateArea(this);
	}
	
	public String toString() {
		return priority + ": [" + id + "] \t" + name + " \t@ (" + 
		coords[0] + ", " + coords[1] + ", " + coords[2] + ")-(" +
		coords[3] + ", " + coords[4] + ", " + coords[5] + ")"; 
	}
	
	public boolean listHas(String list, String value) {
		return ad.listHas(id, list, value);
	}
	
	/**
	 * Checks if the player can do an action.
	 * @param player	The player to check for
	 * @param lists		An array of lists to check
	 * @return			If they can
	 */
	public boolean playerCan(String player, String[] lists) {
		boolean ret = true;
		
		// Find out if any of the events are restricted
		for (String list : lists) if (listHas("restrict", list)) {
			ret = false;
			break;
		}
		
		// Check the lists switching between them as seen fit
		for (String list : lists)
			if (ret) {
				if (listHas("no-"+list, player)) ret = false;
			} else if (listHas(list, player)) ret = true;
		
		return ret;
	}
	
	/**
	 * Gets all the messages for this area.
	 * @return A HashMap of event names to messages.
	 */
	public HashMap<String, String> getMsgs() {
		return ad.getMsgs(id);
	}

	public int getPriority() {
		return priority;
	}

	public boolean setPriority(int priority) {
		if (id == -1) return false;
		this.priority = priority;
		return ad.updateArea(this);
	}
}
