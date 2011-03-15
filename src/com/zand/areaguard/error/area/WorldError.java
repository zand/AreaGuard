package com.zand.areaguard.error.area;

import java.util.ArrayList;

import com.zand.areaguard.area.Cubiod;
import com.zand.areaguard.area.World;

public class WorldError implements World {

	@Override
	public boolean deleteCubiods() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Cubiod getCubiod(long x, long y, long z) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Cubiod> getCubiods() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Cubiod> getCubiods(long x, long y, long z) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

}
