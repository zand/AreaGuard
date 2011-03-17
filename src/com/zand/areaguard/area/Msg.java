package com.zand.areaguard.area;

/**
 * A interface for handling Messages for AreaGuard.
 * 
 * @author zand
 *
 */
public interface Msg extends Data {
	/**
	 * Gets the set of list values.
	 * @return a set of values.
	 */
	public String getMsg();
	
	/**
	 * Gets the Area for the Message.
	 * @return The Area.
	 */
	public Area getArea();
	
	/**
	 * Gets the Name of the Message.
	 * @return The name of the Message.
	 */
	public String getName();
	
	/**
	 * Sets the message.
	 * @param creator The messages creator.
	 * @param msg what to set the message to.
	 * @return False if there was an error.
	 */
	public boolean setMsg(String creator, String msg);
}
