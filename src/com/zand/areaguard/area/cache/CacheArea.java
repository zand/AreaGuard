package com.zand.areaguard.area.cache;

import java.util.ArrayList;
import java.util.HashMap;

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
	final private HashMap<String, CacheList> lists = new HashMap<String, CacheList>();
	final private HashMap<String, CacheMsg> msgs = new HashMap<String, CacheMsg>();
	
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
			
			// Update Lists
			for (CacheList list : lists.values())
				list.update();
			for (List list : area.getLists())
				if (!lists.containsKey(list.getName()))
					lists.put(list.getName(), new CacheList(storage, list));
			
			// Update Msgs
			for (CacheMsg msg : msgs.values())
				msg.update();
			for (Msg msg : area.getMsgs())
				if (!msgs.containsKey(msg.getName()))
					msgs.put(msg.getName(), new CacheMsg(storage, msg));
			
			if (CacheStorage.debug) System.out.println("Updated Area " + getId());
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
		update();
		if (lists.containsKey(name))
			return lists.get(name);
		
		CacheList list = new CacheList(storage, area.getList(name));
		lists.put(list.getName(), list);
		return list;
	}

	@Override
	public ArrayList<List> getLists() {
		update();
		ArrayList<List> ret = new ArrayList<List>();
		for (List list : lists.values())
			if (list.exsists()) ret.add(list);
		return ret;
	}

	@Override
	public Msg getMsg(String name) {
		update();
		if (msgs.containsKey(name))
			return msgs.get(name);
		
		CacheMsg msg = new CacheMsg(storage, area.getMsg(name));
		msgs.put(msg.getName(), msg);
		return msg;
	}

	@Override
	public ArrayList<Msg> getMsgs() {
		update();
		ArrayList<Msg> ret = new ArrayList<Msg>();
		for (Msg msg : msgs.values())
			if (msg.exsists()) ret.add(msg);
		return ret;
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
