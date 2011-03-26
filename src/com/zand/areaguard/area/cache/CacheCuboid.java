package com.zand.areaguard.area.cache;

import com.zand.areaguard.area.Area;
import com.zand.areaguard.area.Cuboid;
import com.zand.areaguard.area.Storage;
import com.zand.areaguard.area.World;

public class CacheCuboid extends Cuboid implements CacheData {
	final private Storage storage;
	final private Cuboid cuboid;
	static private int updateTime = 15000;
	private long lastUpdate = 0;
	
	// Cached data
	private boolean exsists;
	private String creator;
	private boolean active;
	private World world;
	private Area area;
	private int priority;
	private int coords[];
	
	public CacheCuboid(Storage storage, Cuboid cuboid) {
		super(cuboid.getId());
		this.storage = storage;
		this.cuboid = cuboid;
	}
	
	@Override
	public boolean update() {
		long time = System.currentTimeMillis();
		
		if (time - lastUpdate > updateTime) {
			lastUpdate = time;
			
			exsists = cuboid.exsists();
			world = storage.getWorld(cuboid.getWorld().getId());
			area = storage.getArea(cuboid.getArea().getId());
			priority = cuboid.getPriority();
			creator = cuboid.getCreator();
			coords = cuboid.getCoords();
			active = cuboid.isActive();
			
			System.out.println("Updated Cuboid " + getId());
		}
		
		return true;
	}

	@Override
	public boolean delete() {
		if (cuboid.delete()) {
			exsists = false;
			return true;
		}
		return false;
	}

	@Override
	public Area getArea() {
		update();
		return area;
	}

	@Override
	public int[] getCoords() {
		update();
		return coords;
	}

	@Override
	public String getCreator() {
		update();
		return creator;
	}

	@Override
	public int getPriority() {
		update();
		return priority;
	}

	@Override
	public World getWorld() {
		update();
		return world;
	}

	@Override
	public boolean isActive() {
		update();
		return active;
	}

	@Override
	public boolean setActive(boolean active) {
		if (cuboid.setActive(active)) {
			this.active = active;
			return true;
		}
		return false;
	}

	@Override
	public boolean setArea(Area area) {
		if (cuboid.setArea(area)) {
			this.area = area;
			return true;
		}
		return false;
	}

	@Override
	public boolean setLocation(World world, int[] coords) {
		if (cuboid.setLocation(world, coords)) {
			this.world = world;
			this.coords = coords;
			return true;
		}
		return false;
	}

	@Override
	public boolean setPriority(int priority) {
		if (cuboid.setPriority(priority)) {
			this.priority = priority;
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
