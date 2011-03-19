package com.zand.bukkit.areaguard.command;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.zand.areaguard.Config;
import com.zand.areaguard.area.Area;
import com.zand.areaguard.area.Cuboid;
import com.zand.areaguard.area.World;
import com.zand.areaguard.area.sql.SqlStorage;
import com.zand.bukkit.areaguard.AreaGuard;
import com.zand.bukkit.areaguard.Session;
import com.zand.bukkit.common.Messager;

public class AdminCommands implements CommandExecutor {
	private AreaGuard plugin;
	
	public AdminCommands(AreaGuard plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (args != null && args.length > 0) {
			if (args[0].equalsIgnoreCase("info")) {
				sender.sendMessage(ChatColor.YELLOW + "Storage: " + ChatColor.WHITE + Config.storage.getInfo());
				
				ArrayList<Area> areas = Config.storage.getAreas();
				sender.sendMessage(ChatColor.YELLOW + "Areas: " + ChatColor.WHITE + areas.size());
				sender.sendMessage(ChatColor.YELLOW + "Cubiods: ");
				ArrayList<World> worlds = Config.storage.getWorlds();
				for (World world : worlds) {
					ArrayList<Cuboid> cuboids = world.getCuboids();
					sender.sendMessage(ChatColor.YELLOW + "  " + world.getName() + ": " + ChatColor.WHITE + cuboids.size());
				}
			} 
			
			
			else if (args[0].equalsIgnoreCase("reconfig")) {
				// TODO Make this reload config
				if (Config.storage instanceof SqlStorage) {
					Messager.inform(sender, "Reloading config");
					((SqlStorage)Config.storage).loadConfig();
				}
			}
			
			else if (args[0].equalsIgnoreCase("alias")) {
				Session session = plugin.getSession(sender);
				if (args.length > 1) {
					session.setName(args[1]);
				} else session.setName(null);
				
				Messager.inform(sender, "You are now \"" + session.getName() + "\" to AreaGuard.");
			}
			
			else {
				showHelp(sender, label);
			}
		} else {
			showHelp(sender, label);
		}
		return true;
	}
	
	public void showHelp(CommandSender sender, String label) {
		sender.sendMessage(ChatColor.DARK_PURPLE + plugin.versionInfo + " Admin Help");
		sender.sendMessage(ChatColor.WHITE + label + " help" + ChatColor.GOLD + " - " + ChatColor.YELLOW + "Shows this.");
		sender.sendMessage(ChatColor.WHITE + label + " info" + ChatColor.GOLD + " - " + ChatColor.YELLOW + "Shows the plugin status.");
		sender.sendMessage(ChatColor.WHITE + label + " reconfig" + ChatColor.GOLD + " - " + ChatColor.YELLOW + "Reloads the plugins config.");
	}

}
