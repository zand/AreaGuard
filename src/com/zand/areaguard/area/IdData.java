package com.zand.areaguard.area;

public abstract class IdData implements Data {
	final private int id;
	
	public IdData(int id) {
		this.id = id;
	}
	
	/**
	 * Gets the id for the data
	 * @return The id for the data
	 */
	public int getId() {
		return id;
	}
	
	@Override public boolean equals(Object o) {
		if (o instanceof IdData)
			return id == ((IdData)o).id;
		if (o instanceof Integer)
			return id == ((Integer)o).intValue();
		return false;
	}
	
	@Override public int hashCode() {
		return id;
	}
}
