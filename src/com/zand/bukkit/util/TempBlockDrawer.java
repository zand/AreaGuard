package com.zand.bukkit.util;

import org.bukkit.World;
import org.bukkit.block.Block;

public class TempBlockDrawer extends TempBlocks {
	
	public boolean addLine(World world, int point1[], int point2[], int type) {
		if (point1.length != 3) return false;
		if (point2.length != 3) return false;
		
		return addLine(world, new int[] {
			point1[0],
			point1[2],
			point1[3],
			point2[0],
			point2[2],
			point2[3],
			}, type);
	}
	
	public boolean addLine(World world, int coords[], int type) {
		if (coords.length != 6) return false;
		
		int[] delta = new int[3];
		int[] step = {1, 1, 1};
		int axis = 0;
		
		// Get the length and steps 
		for (int i=0; i < delta.length; i++) {
			delta[i] = coords[i+3] - coords[i];
			if (delta[i] < 0) {
				delta[i] = -delta[i];
				step[i] = -1;
			}
		}
		
		// get the axis we are to draw on
		if (delta[1] > delta[0])
			if (delta[1] > delta[2]) axis = 1;
			else axis = 2;
		else if (delta[2] > delta[0]) axis = 2;
		
		// Draw the line
		Block block = world.getBlockAt(coords[0], coords[1], coords[2]);
		if (block.getTypeId() == 0)
			addBlock(world.getBlockAt(coords[0], coords[1], coords[2]), type);
		while (coords[axis] != coords[axis+3]) {
			// only draw strait lines for now
			coords[axis] += step[axis];
			block = world.getBlockAt(coords[0], coords[1], coords[2]);
			if (block.getTypeId() == 0)
				addBlock(world.getBlockAt(coords[0], coords[1], coords[2]), type);
		}
		
		return true;
	}
	
	public boolean drawCubeOutline(World world, int coords[], int type) {
		if (coords.length != 6) return false;
		
		addLine(world, new int[] {coords[0], coords[1], coords[2], coords[3], coords[1], coords[2]}, type);
		addLine(world, new int[] {coords[0], coords[4], coords[2], coords[3], coords[4], coords[2]}, type);
		addLine(world, new int[] {coords[0], coords[1], coords[5], coords[3], coords[1], coords[5]}, type);
		addLine(world, new int[] {coords[0], coords[4], coords[5], coords[3], coords[4], coords[5]}, type);
		
		addLine(world, new int[] {coords[0], coords[1], coords[2], coords[0], coords[4], coords[2]}, type);
		addLine(world, new int[] {coords[3], coords[1], coords[2], coords[3], coords[4], coords[2]}, type);
		addLine(world, new int[] {coords[0], coords[1], coords[5], coords[0], coords[4], coords[5]}, type);
		addLine(world, new int[] {coords[3], coords[1], coords[5], coords[3], coords[4], coords[5]}, type);
		
		addLine(world, new int[] {coords[0], coords[1], coords[2], coords[0], coords[1], coords[5]}, type);
		addLine(world, new int[] {coords[3], coords[1], coords[2], coords[3], coords[1], coords[5]}, type);
		addLine(world, new int[] {coords[0], coords[4], coords[2], coords[0], coords[4], coords[5]}, type);
		addLine(world, new int[] {coords[3], coords[4], coords[2], coords[3], coords[4], coords[5]}, type);
		return true;
	}

}
