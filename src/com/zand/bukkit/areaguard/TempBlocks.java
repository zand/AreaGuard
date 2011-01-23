package com.zand.bukkit.areaguard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Chunk;
import org.bukkit.block.Block;

public class TempBlocks {
	private class TempBlock {
		private Block block;
		private int oldType, tempType;
		
		public TempBlock(Block block, int type) {
			this.block = block;
			tempType = type;
			oldType = block.getTypeId();
			if (tempType != oldType)
				block.setTypeId(tempType);
		}
		
		public void delete() {
			if (block.getTypeId() == tempType && 
				tempType != oldType)
				block.setTypeId(oldType);
		}
	}
	
	private static HashMap<TempBlock, Chunk> allBlocks = new HashMap<TempBlock, Chunk>();
	private ArrayList<TempBlock> blocks = new ArrayList<TempBlock>();
	
	public void addBlock(Block block, int type) {
		TempBlock t = new TempBlock(block, type);
		allBlocks.put(t, block.getChunk());
		blocks.add(t);
	}
	
	public void delete() {
		for (TempBlock t : blocks) {
			t.delete();
			allBlocks.remove(t);
		}
		blocks.clear();
	}
	
	public static void deleteChunk(Chunk chunk) {
		for (Entry<TempBlock, Chunk> set : allBlocks.entrySet()) {
			if (set.getValue().equals(chunk)) {
				TempBlock t = set.getKey();
				t.delete();
				allBlocks.remove(t);
			}
		}
	}
	
	public static void deleteAll() {
		for (TempBlock t : allBlocks.keySet()) {
			t.delete();
		}
		allBlocks.clear();
	}
}
