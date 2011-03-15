package com.zand.areaguard.error.area;

import java.util.HashSet;
import java.util.Set;

import com.zand.areaguard.area.Area;
import com.zand.areaguard.area.List;

public class ErrorList implements List {
	Area area;
	String name;
	HashSet<String> values = new HashSet<String>();
	String error;
	
	public ErrorList(Area area, String name, String values[]) {
		this.area = area;
		this.name = name;
		if (values != null)
			for (String value : values) this.values.add(value);
	}

	@Override
	public boolean addValue(String value) {
		return false;
	}

	@Override
	public boolean addValues(String[] values) {
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
	public Set<String> getValues() {
		return values;
	}

	@Override
	public boolean hasValue(String value) {
		// TODO Auto-generated method stub
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
	public boolean save() {
		return false;
	}

}
