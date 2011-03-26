package com.zand.areaguard.area.cache;

import java.util.ArrayList;

import com.zand.areaguard.area.Area;
import com.zand.areaguard.area.Cuboid;
import com.zand.areaguard.area.List;
import com.zand.areaguard.area.Msg;

public class CacheArea extends Area implements CacheData {
	final private CacheStorage storage;
	final private Area area;
	static private int updateTime = 15000;
	private long lastUpdate = 0;
	
	// Cached data
	private boolean exsists;
	private String name;
	private String creator;
	private Area parrent;
	final protected ArrayList<Cuboid> cuboids = new ArrayList<Cuboid>();
	final private ArrayList<List> lists = new ArrayList<List>();
	final private ArrayList<Msg> msgs = new ArrayList<Msg>();
	
	protected CacheArea(CacheStorage storage, Area area) {
		super(area.getId());
		this.storage = storage;
		this.area = area;
	}
	
	@Override
	public boolean update() {
		long time = System.currentTimeMillis();
		
		if (time - lastUpdate > updateTime) {
			lastUpdate = time;
			
			exsists = area.exsists();
			name = area.getName();
			creator = area.getCreator();
			parrent = area.getParrent();
			if (parrent != null) 
				parrent = storage.getArea(parrent.getId());
			cuboids.clear();
			for (Cuboid cuboid : storage.getCuboids())
				if (cuboid.getArea().getId() == getId() && !cuboids.contains(cuboid)) 
					cuboids.add(cuboid);
			for (List list : lists)
				if (!list.exsists()) 
					lists.remove(list);
			for (List list : area.getLists())
				if (!lists.contains(list))
					lists.add(new CacheList(storage, list));
			for (Msg msg : msgs)
				if (!msg.exsists()) 
					msgs.remove(msg);
			for (Msg msg : area.getMsgs())
				if (!msgs.contains(msg))
					msgs.add(new CacheMsg(storage, msg));
			
			System.out.println("Updated Area " + getId());
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
	public ArrayList<Cuboid> getCuboids() {
		update();
		return cuboids;
	}

	@Override
	public List getList(String name) {
		for (List list : getLists())
			if (list.getName().equals(name)) 
				return list;
		
		List list = new CacheList(storage, area.getList(name));
		if (list.exsists())
			lists.add(list);
		return list;
	}

	@Override
	public ArrayList<List> getLists() {
		update();
		return lists;
	}

	@Override
	public Msg getMsg(String name) {
		for (Msg msg : getMsgs())
			if (msg.getName().equals(name)) 
				return msg;
		
		Msg msg = new CacheMsg(storage, area.getMsg(name));
		if (msg.exsists())
			msgs.add(msg);
		return msg;
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
