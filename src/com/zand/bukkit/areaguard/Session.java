package com.zand.bukkit.areaguard;

import java.util.HashSet;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import com.zand.areaguard.area.Area;
import com.zand.areaguard.area.Cuboid;
import com.zand.areaguard.area.World;

public class Session {
	protected final CommandSender sender;
	
	private String name; 
	private Area selectedArea;
	private Cuboid selectedCubiod;
	private World selectedWorld;
	private int lastSelectedPoint = 0;
	private int selectedPoints[][] = new int[2][3];
	private boolean bypass = false;
	HashSet<String> debugFlags = new HashSet<String>();
	
	public enum Selected { Area, Cubiod, World, Points, None }
	
	Selected draw;
	
	public Session(CommandSender sender) {
		this.sender = sender;
		setName(null);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		if (name == null || name.isEmpty())
			name = getRealName();
		this.name = name;
	}
	
	public String getRealName() {
		if (sender instanceof ConsoleCommandSender)
			return "@Console";
		return "@Unkown";
	}
	
	public void select(Area area) {
		selectedArea = area;
	}
	
	public void select(World world) {
		selectedWorld = world;
	}
	
	public void select(Cuboid cubiod) {
		select(cubiod.getWorld());
		select(cubiod.getArea());
		
		int coords[] = cubiod.getCoords();
		selectRight(coords[0], coords[1], coords[2]);
		select(coords[3], coords[4], coords[5]);
	}
	
	public void selectRight(Location loc) {
		selectRight(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	}
	
	public void selectRight(int x, int y, int z) {
		lastSelectedPoint = 1;
		select(x, y, z);
	}
	
	public void selectLeft(int x, int y, int z) {
		lastSelectedPoint = 0;
		select(x, y, z);
	}
	
	public void select(int x, int y, int z) {
		if (lastSelectedPoint == 0)
			lastSelectedPoint = 1;
		else lastSelectedPoint = 0;
		selectedPoints[0] = new int[] {x, y, z};
	}
	
	public void selectLeft(Location loc) {
		selectRight(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	}
	
	public void select(Location loc) {
		select(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	}
	
	public Area getSelectedArea() {
		return selectedArea;
	}
	
	public World getSelectedWorld() {
		return selectedWorld;
	}
	
	public Cuboid getSelectedCubiod() {
		return selectedCubiod;
	}
	
	public int[] getSelectedPoint() {
		return selectedPoints[lastSelectedPoint];
	}
	
	public int[] getSelectedPointRight() {
		return selectedPoints[0];
	}
	
	public int[] getSelectedPointLeft() {
		return selectedPoints[1];
	}
	
	public boolean isBypassing() {
		return bypass;
	}
	
	public void setBypassing(boolean bypass) {
		this.bypass = bypass;
	}
	
	public boolean isDebuging(String flag) {
		return 
		debugFlags.contains(flag) ||
		debugFlags.contains("all");
	}
	
	public void setDebuging(String flag, boolean debug) {
		if (flag.equals("all")) debugFlags.clear();
		if (debug) debugFlags.add(flag);
		else debugFlags.remove(flag);
	}
}
