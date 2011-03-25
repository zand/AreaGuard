package com.zand.areaguard.area.error;

import java.util.ArrayList;
import java.util.HashMap;

import com.zand.areaguard.area.Area;
import com.zand.areaguard.area.Cuboid;
import com.zand.areaguard.area.List;
import com.zand.areaguard.area.Msg;


public class ErrorArea extends com.zand.areaguard.area.Area {
	final static public ErrorArea
	NOT_FOUND = new ErrorArea("AREA NOT FOUND");
	private final String error;
	ArrayList<Cuboid> cubiods = new ArrayList<Cuboid>();
	ArrayList<List> lists = new ArrayList<List>();
	HashMap<String, Msg> msgs = new HashMap<String, Msg>();

	public ErrorArea(String error) {
		super(-2);
		this.error = error;
		cubiods.add(new ErrorCuboid());
		lists.add(new ErrorList(this, "restrict", new String[] {"build", "open"}));
		msgs.put("error", new ErrorMsg(this, "error", error));
		}

	@Override
	public ArrayList<Cuboid> getCuboids() {
		return cubiods;
	}

	@Override
	public List getList(String name) {
		return lists.get(0);
	}

	@Override
	public ArrayList<List> getLists() {
		return lists;
	}

	@Override
	public Msg getMsg(String name) {
		return msgs.get(name);
	}

	@Override
	public ArrayList<Msg> getMsgs() {
		return (ArrayList<Msg>) msgs.values();
	}

	@Override
	public String getName() {
		return "ERROR: " + error;
	}

	@Override
	public boolean setName(String name) {
		return false;
	}

	@Override
	public boolean exsists() {
		return false;
	}

	@Override
	public Area getParrent() {
		return null;
	}

	@Override
	public boolean setParrent(Area parrent) {
		return false;
	}

	@Override
	public String getCreator() {
		return "@Error";
	}

	@Override
	public boolean delete() {
		return false;
	}
}
