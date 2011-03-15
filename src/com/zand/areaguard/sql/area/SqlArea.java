package com.zand.areaguard.sql.area;

import java.util.ArrayList;

import com.zand.areaguard.area.Area;
import com.zand.areaguard.area.Cubiod;
import com.zand.areaguard.area.List;
import com.zand.areaguard.area.Msg;

public class SqlArea implements Area {
	final private SqlStorage storage;
	final private int id;
	
	protected SqlArea(SqlStorage storage, int id) {
		this.storage = storage;
		this.id = id;
	}

	@Override
	public ArrayList<Cubiod> getCubiods() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public List getList(String name) {
		// TODO Auto-generated method stub
		return new SqlList(storage, getId(), name);
	}

	@Override
	public ArrayList<List> getLists() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Msg getMsg(String name) {
		// TODO Auto-generated method stub
		return new SqlMsg(storage, getId(), name);
	}

	@Override
	public ArrayList<Msg> getMsgs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isOwner(String player) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean playerCan(String player, String[] lists) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setName(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean save() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasOwner(String player) {
		// TODO Auto-generated method stub
		return false;
	}

}
