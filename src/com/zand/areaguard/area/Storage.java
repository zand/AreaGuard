package com.zand.areaguard.area;

import java.util.ArrayList;

/**
 * A Class for managing data storage.
 * 
 * @author zand
 *
 */
public interface Storage {
	
	/**
	 * Gets the storage info.
	 * @return A human readable string.
	 */
	public String getInfo();
	
	/**
	 * Gets a list of all the {@link World}s.
	 * @return An Array list of the worlds.
	 */
	public ArrayList<World> getWorlds();
	
	/**
	 * Gets the {@link World} from Id.
	 * @param worldId The id of the world to look up.
	 * @return The World and -1 on error.
	 */
	public World getWorld(int worldId);
	
	/**
	 * Gets the {@link World} from name and creates it if it doesn't exist.
	 * @param name The name of the world.
	 * @return The World.
	 */
	public World getWorld(String name);
	
	/**
	 * Gets a list of all the {@link Area}s.
	 * @return An Array list of the Areas.
	 */
	public ArrayList<Area> getAreas();
	
	/**
	 * Gets a list of {@link Area}s by name.
	 * @param name The name of the area.
	 * @return An Array list of the Areas.
	 */
	public ArrayList<Area> getAreas(String name);
	
	/**
	 * Gets a list of {@link Area}s owned by owner with name.
	 * @param name The name of the area.
	 * @param owner The name of the owner.
	 * @return An Array list of the Areas.
	 */
	public ArrayList<Area> getAreas(String name, String owner);
	
	/**
	 * Gets a list of all {@link Area}s owned by owner.
	 * @param owner The name of the owner.
	 * @return An Array list of the Areas.
	 */
	public ArrayList<Area> getAreasOwned(String owner);
	
	/**
	 * Gets a list of all {@link Area}s created by creator.
	 * @param creator The name of the creator.
	 * @return An Array list of the Areas.
	 */
	public ArrayList<Area> getAreasCreated(String creator);
	
	/**
	 * Gets a list of all {@link Cuboid}s created by creator.
	 * @param creator The name of the creator.
	 * @return An Array list of the Cuboids.
	 */
	public ArrayList<Cuboid> getCuboidsCreated(String creator);
	
	/**
	 * Gets a the {@link Area} by Id.
	 * @param areaId The id of the area.
	 * @return The Area.
	 */
	public Area getArea(int areaId);
	
	/**
	 * Gets a the {@link Area} by Id.
	 * @param areaId The id of the area.
	 * @return The Area.
	 */
	public Cuboid getCuboid(int cuboidId);
	
	/**
	 * Creates a new {@link Area}.
	 * @param The name of the creator.
	 * @param The name of the area.
	 * @return The Area.
	 */
	public Area newArea(String creator, String name);
	
	/**
	 * Creates a new {@link Cuboid} in this World.
	 * @param The name of the creator.
	 * @param area The area that the cuboid is a part of.
	 * @param world The world that the cuboid is in.
	 * @param coords The coords for the cuboid.
	 * @return The new Cuboid.
	 */
	public Cuboid newCuboid(String creator, Area area, World world, int[] coords);
}
