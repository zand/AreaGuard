package com.zand.bukkit.areaguard.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.util.Java15Compat;

import com.zand.bukkit.areaguard.AreaGuard;
import com.zand.bukkit.common.Messager;
import com.zand.bukkit.common.TextMapper;

public class DebugCommands implements CommandExecutor {
	private AreaGuard plugin;
	
	public DebugCommands(AreaGuard plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (args != null && args.length > 0) {
			if (args[0].equalsIgnoreCase("map")) {
				TextMapper map = new TextMapper(plugin.getServer().getWorlds().get(0).getChunkAt(0, 0));
				map.calcLines();
				map.show(sender);
			} else if (args[0].equalsIgnoreCase("start")) {
				String flags[] = Java15Compat.Arrays_copyOfRange(args, 1, args.length);
				for (String flag : flags) {
					plugin.getSession(sender).setDebuging(flag, true);
					Messager.debug(sender, "Starting " + flag);
				}
			} else if (args[0].equalsIgnoreCase("stop")) {
				String flags[] = Java15Compat.Arrays_copyOfRange(args, 1, args.length);
				for (String flag : flags) {
					plugin.getSession(sender).setDebuging(flag, true);
					Messager.debug(sender, "Stopping " + flag);
				}
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
		sender.sendMessage(ChatColor.DARK_PURPLE + plugin.versionInfo + " Debug Help");
		sender.sendMessage(ChatColor.WHITE + label + " help" + ChatColor.GOLD + " - " + ChatColor.YELLOW + "Shows this.");
		sender.sendMessage(ChatColor.WHITE + label + " map" + ChatColor.GOLD + " - " + ChatColor.YELLOW + "Text Map Testing.");
	}

}
