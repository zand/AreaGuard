package com.zand.bukkit.common;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Messager {
	public static void warn(CommandSender sender, String message) {
		sender.sendMessage(ChatColor.RED + message);
	}
	
	public static void error(CommandSender sender, String message) {
		sender.sendMessage(ChatColor.DARK_RED + "Error!: " + message);
	}
	
	public static void inform(CommandSender sender, String message) {
		sender.sendMessage(ChatColor.YELLOW + message);
	}
	
	public static void debug(CommandSender sender, String message) {
		sender.sendMessage(ChatColor.GOLD + "Debug!: " + message);
	}
}
