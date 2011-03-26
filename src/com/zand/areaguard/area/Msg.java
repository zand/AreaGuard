package com.zand.areaguard.area;

/**
 * A interface for handling Messages for AreaGuard.
 * 
 * @author zand
 *
 */
public abstract class Msg implements Data {
	final private Area area;
	final private String name;
	
	public Msg(Area area, String name) {
		this.area = area;
		this.name = name;
	}
	
	@Override public boolean equals(Object o) {
		if (o instanceof Msg)
			return area == ((Msg)o).area && name == ((Msg)o).name;
		return false;
	}
	
	@Override public int hashCode() {
		return (name + "@" + area.getId()).hashCode();
	}
	
	/**
	 * Gets the set of list values.
	 * @return a set of values.
	 */
	public abstract String getMsg();
	
	/**
	 * Gets the Area for the Message.
	 * @return The Area.
	 */
	public Area getArea() {
		return area;
	}
	
	/**
	 * Gets the Name of the Message.
	 * @return The name of the Message.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the message.
	 * @param creator The messages creator.
	 * @param msg what to set the message to.
	 * @return False if there was an error.
	 */
	public abstract boolean setMsg(String creator, String msg);
}
