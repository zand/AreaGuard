package com.zand.bukkit.util;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;

public class TextMapper {
	World world;
	int x;
	int z;
	int width;
	int height;
	String text[];
	
	public TextMapper(Chunk chunk) {
		this(chunk.getWorld(), chunk.getX(), chunk.getZ(), 16, 16);
	}
	
	public TextMapper(World world, int x, int z) {
		this(world, x, z, 26, 20);
	}
	
	public TextMapper(World world, int x, int z, int width, int height) {
		this.world = world;
		this.x = x;
		this.z = z;
		this.width = width;
		this.height = height;
		text = new String[height];
	}
	
	public void calcLines() {
		for (int i = 0; i < height; i++)
			calcLine(i);
	}
	
	public void calcLine(int line) {
		text[line] = "";
		for (int i=0; i < width; i++)
			text[line] += ChatColor.WHITE + " " + blockToString(
					getHighestBlock(world, x+width-i, z+height-line));
	}
	
	Block getHighestBlock(World world, int x, int z) {
		/*return world.getBlockAt(
				x,
				world.getHighestBlockYAt(x, z), 
				z); */
		
		Block block = null;
		for (int y = 127; y >= 0; y--) {
			block = world.getBlockAt(x, y, z);
			if (block.getType() != Material.AIR)
				break;
		}
		return block;
	}
	
	public String blockToString(Block block) {
		switch (block.getTypeId()) {
		case 0: return " "; // Air
		case 1: return ChatColor.GRAY + "S"; // Stone
		case 2: return ChatColor.GREEN + "G"; // Grass
		case 3: return ChatColor.GOLD + "D"; // Dirt
		case 4: return ChatColor.GRAY + "C"; // Cobble
		case 5: return ChatColor.GOLD + "="; // WoodPlanks
		case 6: return ChatColor.DARK_GREEN + "s"; // Sapling
		case 7: return ChatColor.BLACK + "B"; // Bedrock
		case 8:
		case 9: return ChatColor.AQUA + "~"; // Water
		case 10:
		case 11: return ChatColor.RED + "~"; // Lava
		case 12: return ChatColor.YELLOW + "S"; // Sand
		case 13: return ChatColor.DARK_GRAY + "G"; // Gravel
		case 14: return ChatColor.GOLD + "%"; // Gold
		case 15: return ChatColor.GRAY + "%"; // Iron
		case 16: return ChatColor.BLACK + "%"; // Coal
		case 17: return ChatColor.GOLD + "L"; // Log
		case 18: return ChatColor.DARK_GREEN + "#"; // Leaves
		case 19: return ChatColor.YELLOW + "%"; // Sponge
		case 20: return ChatColor.WHITE + "#"; // Glass
		case 21: return ChatColor.BLUE + "%"; // Lapus
		case 22: return ChatColor.BLUE + "$"; // Lapus Block
		case 23: return ChatColor.GRAY + "Y"; // Dispenser 
		case 24: return ChatColor.YELLOW + "S"; // SandStone
		
		default: return ChatColor.DARK_PURPLE + "?"; // Uknown
		}
	}
	
	public void show(CommandSender sender) {
		for (String line : text)
			sender.sendMessage(line);
	}
	
	
}
