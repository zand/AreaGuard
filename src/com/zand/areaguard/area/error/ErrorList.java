package com.zand.areaguard.area.error;

import java.util.ArrayList;

import com.zand.areaguard.area.Area;
import com.zand.areaguard.area.List;

public class ErrorList implements List {
	Area area;
	String name;
	ArrayList<String> values = new ArrayList<String>();
	String error;
	
	public ErrorList(Area area, String name, String values[]) {
		this.area = area;
		this.name = name;
		if (values != null)
			for (String value : values) this.values.add(value);
	}

	@Override
	public boolean addValue(String creator, String value) {
		return false;
	}

	@Override
	public boolean addValues(String creator, String[] values) {
		return false;
	}

	@Override
	public boolean clear() {
		return false;
	}

	@Override
	public Area getArea() {
		return area;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public ArrayList<String> getValues() {
		return values;
	}

	@Override
	public boolean hasValue(String value) {
		return false;
	}

	@Override
	public boolean removeValue(String value) {
		return false;
	}

	@Override
	public boolean removeValues(String[] values) {
		return false;
	}

	@Override
	public boolean exsists() {
		return false;
	}

}
