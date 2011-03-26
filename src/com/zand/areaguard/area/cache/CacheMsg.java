package com.zand.areaguard.area.cache;

import com.zand.areaguard.area.Msg;
import com.zand.areaguard.area.Storage;

public class CacheMsg extends Msg implements CacheData {
	final private Msg msg;
	static private int updateTime = 15000;
	private long lastUpdate = 0;
	
	// Cached Data
	private boolean exsists;
	private String value;

	public CacheMsg(Storage storage, Msg msg) {
		super(storage.getArea(msg.getArea().getId()), msg.getName());
		this.msg = msg;
	}

	@Override
	public String getMsg() {
		update();
		return value;
	}

	@Override
	public boolean setMsg(String creator, String msg) {
		if (this.msg.setMsg(creator, msg)) {
			value = msg;
			return true;
		}
		return false;
	}

	@Override
	public boolean update() {
		long time = System.currentTimeMillis();
		
		if (time - lastUpdate > updateTime) {
			lastUpdate = time;
			
			exsists = msg.exsists();
			value = msg.getMsg();
			
			System.out.println("Updated Msg " + getName() + "@" + getArea().getId());
		}
		
		return true;
	}

	@Override
	public boolean exsists() {
		update();
		return exsists;
	}

}
