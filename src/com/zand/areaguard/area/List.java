package com.zand.areaguard.area;

import java.util.Set;


/**
 * AreaList for managing lists in AreaGuard
 * 
 * @author zand
 *
 */
public interface List extends Data {
	
	/**
	 * Gets the set of list values.
	 * @return a set of values.
	 */
	public Set<String> getValues();
	
	/**
	 * Gets the Area Id for the list.
	 * @return The Area Id.
	 */
	public Area getArea();
	
	/**
	 * Gets the Name of the list.
	 * @return The name of the list.
	 */
	public String getName();
	
	/**
	 * Test if the list has a value.
	 * @param value The value to test for.
	 * @return If the value is in the list.
	 */
	public boolean hasValue(String value);
	
	/**
	 * Adds values to the List.
	 * @param values The Set of values to add.
	 * @return False if there was an error.
	 */
	public boolean addValues(String values[]);
	
	/**
	 * Adds a value to the List.
	 * @param values The value to add.
	 * @return False if there was an error.
	 */
	public boolean addValue(String value);
	
	/**
	 * Removes values from the List.
	 * @param values The Set of values to remove.
	 * @return False if there was an error.
	 */
	public boolean removeValues(String values[]);
	
	/**
	 * Removes a value from the List.
	 * @param value The value to remove.
	 * @return False if there was an error.
	 */
	public boolean removeValue(String value);
	
	/**
	 * Clears the List
	 * @return False if there was an error.
	 */
	public boolean clear();
}
