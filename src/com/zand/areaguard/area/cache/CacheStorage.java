package com.zand.areaguard.area.cache;

import java.util.ArrayList;
import java.util.HashMap;

import com.zand.areaguard.area.Area;
import com.zand.areaguard.area.Cuboid;
import com.zand.areaguard.area.Storage;
import com.zand.areaguard.area.World;

public class CacheStorage implements Storage, CacheData {
	final private HashMap<Integer, Area> areasById = new HashMap<Integer, Area>();
	final private HashMap<Integer, Cuboid> cuboidsById = new HashMap<Integer, Cuboid>();
	final private HashMap<Integer, World> worldsById = new HashMap<Integer, World>();
	final private HashMap<String, World> worldsByName = new HashMap<String, World>();

	@Override
	public Area getArea(int areaId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Area> getAreas() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Area> getAreas(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Area> getAreas(String name, String owner) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Area> getAreasCreated(String creator) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Area> getAreasOwned(String owner) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Cuboid getCuboid(int cuboidId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Cuboid> getCuboidsCreated(String creator) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public World getWorld(int worldId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public World getWorld(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<World> getWorlds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Area newArea(String creator, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Cuboid newCuboid(String creator, Area area, World world, int[] coords) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean update() {
		// TODO Auto-generated method stub
		return false;
	}

}
