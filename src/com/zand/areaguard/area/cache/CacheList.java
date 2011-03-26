package com.zand.areaguard.area.cache;

import java.util.ArrayList;
import com.zand.areaguard.area.List;
import com.zand.areaguard.area.Storage;

public class CacheList extends List implements CacheData {
	final private List list;
	static private int updateTime = 15000;
	private long lastUpdate = 0;
	
	// Cached Data
	private boolean exsists;
	private ArrayList<String> values = new ArrayList<String>();
	
	public CacheList(Storage storage, List list) {
		super(storage.getArea(list.getArea().getId()), list.getName());
		this.list = list;
	}

	@Override
	public boolean addValue(String creator, String value) {
		if (list.addValue(creator, value)) {
			if (!values.contains(value))
				values.add(value);
			return true;
		}
		return false;
	}

	@Override
	public ArrayList<String> getValues() {
		update();
		return values;
	}

	@Override
	public boolean hasValue(String value) {
		return getValues().contains(value);
	}

	@Override
	public boolean removeValue(String value) {
		if (list.removeValue(value)) {
			values.remove(value);
			return true;
		}
		return false;
	}

	@Override
	public boolean update() {
		long time = System.currentTimeMillis();
		
		if (time - lastUpdate > updateTime) {
			lastUpdate = time;
			
			exsists = list.exsists();
			values = list.getValues();
			
			if (CacheStorage.debug) System.out.println("Updated List " + getName() + "@" + getArea().getId());
		}
		
		return true;
	}

	@Override
	public boolean exsists() {
		update();
		return exsists;
	}

}
