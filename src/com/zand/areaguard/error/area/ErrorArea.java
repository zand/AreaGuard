package com.zand.areaguard.error.area;

import java.util.ArrayList;
import java.util.HashMap;

import com.zand.areaguard.area.Cuboid;
import com.zand.areaguard.area.List;
import com.zand.areaguard.area.Msg;
import com.zand.areaguard.area.World;


public class ErrorArea implements com.zand.areaguard.area.Area {
	private final String error;
	ArrayList<Cuboid> cubiods = new ArrayList<Cuboid>();
	ArrayList<List> lists = new ArrayList<List>();
	HashMap<String, Msg> msgs = new HashMap<String, Msg>();

	public ErrorArea(String error) {
		this.error = error;
		cubiods.add(new ErrorCuboid());
		lists.add(new ErrorList(this, "restrict", new String[] {"build", "open"}));
		msgs.put("error", new ErrorMsg(this, "error", error));
		}

	@Override
	public ArrayList<Cuboid> getCubiods() {
		return cubiods;
	}

	@Override
	public int getId() {
		return -2;
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
	public boolean isOwner(String player) {
		return false;
	}

	@Override
	public boolean playerCan(String player, String[] lists) { return false; }

	@Override
	public boolean hasOwner(String player) {
		return false;
	}

	@Override
	public boolean pointInside(World world, long x, long y, long z) {
		return true;
	}

	@Override
	public boolean pointInside(String world, long x, long y, long z) {
		return true;
	}
}
