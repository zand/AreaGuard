package com.zand.areaguard.area;

import java.util.ArrayList;


/**
 * AreaList for managing lists in AreaGuard
 * 
 * @author zand
 *
 */
public abstract class List implements Data {
	final private Area area;
	final private String name;
	
	public List(Area area, String name) {
		this.area = area;
		this.name = name;
	}
	
	@Override
	public String toString() {
		String ret = "";
		for (String value : getValues())
			ret += ", " + value;
		if (!ret.isEmpty())
			ret = ret.substring(2);
		return ret;
	}
	
	@Override public boolean equals(Object o) {
		if (o instanceof List)
			return area == ((List)o).area && name == ((List)o).name;
		return false;
	}
	
	@Override public int hashCode() {
		return (name + "@" + area.getId()).hashCode();
	}
	
	/**
	 * Gets the set of list values.
	 * @return a set of values.
	 */
	public abstract ArrayList<String> getValues();
	
	/**
	 * Gets the Area Id for the list.
	 * @return The Area Id.
	 */
	public Area getArea() {
		return area;
	}
	
	/**
	 * Gets the Name of the list.
	 * @return The name of the list.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Test if the list has a value.
	 * @param value The value to test for.
	 * @return If the value is in the list.
	 */
	public abstract boolean hasValue(String value);
	
	/**
	 * Adds values to the List.
	 * @param creator The values creator.
	 * @param values The Set of values to add.
	 * @return False if there was an error.
	 */
	public boolean addValues(String creator, String values[]) {
		for (String value : values)
			if (!addValue(creator, value)) return false;
		return true;
	}
	
	/**
	 * Adds a value to the List.
	 * @param creator The values creator.
	 * @param values The value to add.
	 * @return False if there was an error.
	 */
	public abstract boolean addValue(String creator, String value);
	
	/**
	 * Removes values from the List.
	 * @param values The Set of values to remove.
	 * @return False if there was an error.
	 */
	public boolean removeValues(String values[]) {
		for (String value : values)
			if (!removeValue(value)) return false;
		return true;
	}
	
	/**
	 * Removes a value from the List.
	 * @param value The value to remove.
	 * @return False if there was an error.
	 */
	public abstract boolean removeValue(String value);
	
	/**
	 * Clears the List
	 * @return False if there was an error.
	 */
	public boolean clear() {
		return removeValues((String[]) getValues().toArray());
	}
}
