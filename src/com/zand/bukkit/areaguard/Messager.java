package com.zand.bukkit.areaguard;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Messager {
	public static void warn(Player player, String message) {
		player.sendMessage(ChatColor.RED + message);
	}
	
	public static void error(Player player, String message) {
		player.sendMessage(ChatColor.DARK_RED + "Error!: " + message);
	}
	
	public static void inform(Player player, String message) {
		player.sendMessage(ChatColor.YELLOW + message);
	}
}
