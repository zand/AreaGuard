package com.zand.areaguard.area;

import java.util.ArrayList;


public interface Area extends Data {
	public ArrayList<Cubiod> getCubiods();
	
	public boolean isOwner(String player);
	
	public boolean hasOwner(String player);
	
	public boolean playerCan(String player, String[] lists);
	
	public List getList(String name);
	
	public ArrayList<List> getLists();
	
	public Msg getMsg(String name);
	
	public ArrayList<Msg> getMsgs();
	
	public int getId();
	
	public String getName();
	
	public boolean setName(String name);
}
