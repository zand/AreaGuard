package com.zand.areaguard.area.cache;

import java.util.ArrayList;

import com.zand.areaguard.area.Area;
import com.zand.areaguard.area.Cuboid;
import com.zand.areaguard.area.Storage;
import com.zand.areaguard.area.World;

public class CacheStorage implements Storage, CacheData {
	final private Storage storage;
	static private int updateTime = 30000;
	private long lastUpdate = 0;
	
	// Cache data
	final private ArrayList<Area> areas = new ArrayList<Area>();
	final private ArrayList<Cuboid> cuboids = new ArrayList<Cuboid>();
	final private ArrayList<World> worlds = new ArrayList<World>();
	
	public CacheStorage(Storage storage) {
		this.storage = storage;
	}
	
	@Override
	public Area getArea(int areaId) {
		for (Area area : getAreas()) {
			if (area.getId() == areaId)
				return area;
		}
		
		Area area = new CacheArea(this, storage.getArea(areaId));
		if (area.getId() == areaId && area.exsists()) {
			areas.add(area);
		}
		return area;
	}

	@Override
	public ArrayList<Area> getAreas() {
		update();
		return areas;
	}

	@Override
	public ArrayList<Area> getAreas(String name) {
		ArrayList<Area> ret = new ArrayList<Area>();
		for (Area area : getAreas()) {
			if (area.getName().equalsIgnoreCase(name))
				ret.add(area);
		}
		return ret;
	}

	@Override
	public ArrayList<Area> getAreas(String name, String owner) {
		ArrayList<Area> ret = new ArrayList<Area>();
		for (Area area : getAreas(name)) {
			if (area.isOwner(owner))
				ret.add(area);
		}
		return ret;
	}

	@Override
	public ArrayList<Area> getAreasCreated(String creator) {
		ArrayList<Area> ret = new ArrayList<Area>();
		for (Area area : getAreas()) {
			if (area.getCreator().equals(creator))
				ret.add(area);
		}
		return ret;
	}

	@Override
	public ArrayList<Area> getAreasOwned(String owner) {
		ArrayList<Area> ret = new ArrayList<Area>();
		for (Area area : getAreas()) {
			if (area.isOwner(owner))
				ret.add(area);
		}
		return ret;
	}

	@Override
	public Cuboid getCuboid(int cuboidId) {
		for (Cuboid cuboid : getCuboids()) {
			if (cuboid.getId() == cuboidId)
				return cuboid;
		}
		
		Cuboid cuboid = new CacheCuboid(this, storage.getCuboid(cuboidId));
		if (cuboid.getId() == cuboidId && cuboid.exsists()) {
			cuboids.add(cuboid);
		}
		return cuboid;
	}
	
	public ArrayList<Cuboid> getCuboids() {
		update();
		return cuboids;
	}

	@Override
	public ArrayList<Cuboid> getCuboidsCreated(String creator) {
		ArrayList<Cuboid> ret = new ArrayList<Cuboid>();
		for (Cuboid cuboid : getCuboids()) {
			if (cuboid.getCreator().equals(creator))
				ret.add(cuboid);
		}
		return ret;
	}

	@Override
	public String getInfo() {
		return "Cache " + storage.getInfo();
	}

	@Override
	public World getWorld(int worldId) {
		for (World world : getWorlds()) {
			if (world.getId() == worldId)
				return world;
		}
		
		World world = new CacheWorld(this, storage.getWorld(worldId));
		if (world.getId() == worldId && world.exsists()) {
			worlds.add(world);
		}
		return world;
	}

	@Override
	public World getWorld(String name) {
		for (World world : getWorlds()) {
			if (world.getName().equals(name))
				return world;
		}
		
		World world = new CacheWorld(this, storage.getWorld(name));
		if (world.getName().equals(name) && world.exsists()) {
			worlds.add(world);
		}
		return world;
	}

	@Override
	public ArrayList<World> getWorlds() {
		update();
		return worlds;
	}

	@Override
	public Area newArea(String creator, String name) {
		Area area = new CacheArea(this, storage.newArea(creator, name));
		if (area.exsists()) areas.add(area);
		return area;
	}

	@Override
	public Cuboid newCuboid(String creator, Area area, World world, int[] coords) {
		world = getWorld(world.getId());
		area = getArea(area.getId());
		Cuboid cuboid = new CacheCuboid(this, storage.newCuboid(creator, area, world, coords));
		if (cuboid.exsists()) {
			cuboids.add(cuboid);
			if (world instanceof CacheWorld) 
				((CacheWorld)world).cuboids.add(cuboid);
			if (area instanceof CacheArea) 
				((CacheArea)area).cuboids.add(cuboid);
		}
		return cuboid;
	}

	@Override
	public boolean update() {
		long time = System.currentTimeMillis();
		
		if (time - lastUpdate > updateTime) {
			for (World world : worlds)
				if (!world.exsists()) worlds.remove(world);
			for (World world : storage.getWorlds())
				getWorld(world.getId());
			
			for (Area area : areas)
				if (!area.exsists()) areas.remove(area);
			for (Area area : storage.getAreas())
				getArea(area.getId());
			
			for (Cuboid cuboid : cuboids)
				if (!cuboid.exsists()) cuboids.remove(cuboid);
			
			lastUpdate = time;
		}
		return true;
	}

}
