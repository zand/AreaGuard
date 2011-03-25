package com.zand.areaguard.area.cache;

import java.util.ArrayList;

import com.zand.areaguard.area.Area;
import com.zand.areaguard.area.Cuboid;
import com.zand.areaguard.area.List;
import com.zand.areaguard.area.Msg;
import com.zand.areaguard.area.World;

public class CacheArea implements Area {
	final private Area area;
	final private int areaId;
	static private int updateTime = 1500;
	private long lastUpdate = 0;
	
	// Cached data
	private boolean exsists;
	private String name;
	private String creator;
	private Area parrent;
	private ArrayList<Cuboid> cuboids;
	private ArrayList<List> lists;
	private ArrayList<Msg> msgs;
	
	protected CacheArea(Area area) {
		this.area = area;
		areaId = area.getId();
		update();
	}
	
	public boolean update() {
		long time = System.currentTimeMillis();
		
		if (time - lastUpdate > updateTime) {
			exsists = area.exsists();
			name = area.getName();
			creator = area.getCreator();
			cuboids = area.getCubiods();
			lists = area.getLists();
			msgs = area.getMsgs();
			
			lastUpdate = time;
		}
		return true;
	}

	@Override
	public boolean delete() {
		if (area.delete()) {
			exsists = false;
			return true;
		}
		return false;
	}

	@Override
	public String getCreator() {
		update();
		return creator;
	}

	@Override
	public ArrayList<Cuboid> getCubiods() {
		update();
		return cuboids;
	}

	@Override
	public ArrayList<Cuboid> getCubiods(boolean active) {
		ArrayList<Cuboid> ret = new ArrayList<Cuboid>();
		for (Cuboid cuboid : getCubiods())
			if (cuboid.isActive() == active) ret.add(cuboid);
		
		return ret;
	}

	@Override
	public int getId() {
		return areaId;
	}

	@Override
	public List getList(String name) {
		for (List list : getLists())
			if (list.getName().equals(name)) 
				return list;
		// TODO make new list
		return null;
	}

	@Override
	public ArrayList<List> getLists() {
		update();
		return lists;
	}

	@Override
	public Msg getMsg(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Msg> getMsgs() {
		update();
		return msgs;
	}

	@Override
	public String getName() {
		update();
		return name;
	}

	@Override
	public Area getParrent() {
		update();
		return parrent;
	}

	@Override
	public boolean isOwner(String player) {
		return getList("owners").hasValue(player);
	}

	@Override
	public boolean pointInside(World world, int x, int y, int z) {
		for (Cuboid cubiod : getCubiods(true))
			if (cubiod.pointInside(world, x, y, z))
				return true;
		return false;
	}

	@Override
	public boolean pointInside(String world, int x, int y, int z) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setName(String name) {
		if (area.setName(name)) {
			this.name = name;
			return true;
		}
		return false;
	}

	@Override
	public boolean setParrent(Area parrent) {
		if (area.setParrent(parrent)) {
			this.parrent = parrent;
			return true;
		}
		return false;
	}

	@Override
	public boolean exsists() {
		update();
		return exsists;
	}

}
