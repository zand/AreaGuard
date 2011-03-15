package com.zand.areaguard.error.area;

import java.util.ArrayList;

import com.zand.areaguard.area.Cubiod;
import com.zand.areaguard.area.World;

public class ErrorWorld implements World {
	final private String name;
	
	public ErrorWorld(String name) {
		this.name = name;
	}

	@Override
	public boolean deleteCubiods() {
		return false;
	}

	@Override
	public Cubiod getCubiod(long x, long y, long z) {
		return null;
	}

	@Override
	public ArrayList<Cubiod> getCubiods() {
		return null;
	}

	@Override
	public ArrayList<Cubiod> getCubiods(long x, long y, long z) {
		return null;
	}

	@Override
	public int getId() {
		return -2;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean save() {
		return false;
	}

}
