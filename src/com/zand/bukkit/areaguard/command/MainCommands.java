package com.zand.bukkit.areaguard.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.util.Java15Compat;

import com.zand.areaguard.area.Area;
import com.zand.areaguard.area.Cuboid;
import com.zand.areaguard.area.World;
import com.zand.bukkit.areaguard.AreaGuard;
import com.zand.bukkit.areaguard.Session;

public class MainCommands implements CommandExecutor {
	private AreaGuard plugin;
	
	final CommandExecutor admin;
	final CommandExecutor area;
	final CommandExecutor cuboid;
	final CommandExecutor debug;
	final CommandExecutor point;
	
	public MainCommands(AreaGuard plugin) {
		this.plugin = plugin;
		
		admin = new AdminCommands(plugin);
		area = new AreaCommands(plugin);
		cuboid = new CuboidCommands(plugin);
		debug = new DebugCommands(plugin, this);
		point = new PointCommands(plugin);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (args != null && args.length > 0) {
			if (args[0].toLowerCase().startsWith("ver")) {
				sender.sendMessage(plugin.versionInfo);
			}
			else if (args[0].equalsIgnoreCase("admin")) {
				return admin.onCommand(sender, command, label + " " + args[0], 
						Java15Compat.Arrays_copyOfRange(args, 1, args.length));
			}
			else if (args[0].equalsIgnoreCase("area")) {
				return area.onCommand(sender, command, label + " " + args[0], 
						Java15Compat.Arrays_copyOfRange(args, 1, args.length));
			}
			else if (args[0].equalsIgnoreCase("cuboid")) {
				return cuboid.onCommand(sender, command, label + " " + args[0], 
						Java15Compat.Arrays_copyOfRange(args, 1, args.length));
			}
			else if (args[0].equalsIgnoreCase("debug")) {
				return debug.onCommand(sender, command, label + " " + args[0], 
						Java15Compat.Arrays_copyOfRange(args, 1, args.length));
			} 
			else if (args[0].equalsIgnoreCase("point")) {
				return point.onCommand(sender, command, label + " " + args[0], 
						Java15Compat.Arrays_copyOfRange(args, 1, args.length));
			} 
			else if (args[0].equalsIgnoreCase("info")) {
				showInfo(sender);
				return true;
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
		sender.sendMessage(ChatColor.DARK_PURPLE + plugin.versionInfo + " Help");
		sender.sendMessage(ChatColor.WHITE + label + " help" + ChatColor.GOLD + " - " + ChatColor.YELLOW + "Shows this.");
		sender.sendMessage(ChatColor.WHITE + label + " ver" + ChatColor.GOLD + " - " + ChatColor.YELLOW + "Shows the version info.");
		sender.sendMessage(ChatColor.WHITE + label + " admin [...]" + ChatColor.GOLD + " - " + ChatColor.YELLOW + "Admin Commands.");
		sender.sendMessage(ChatColor.WHITE + label + " area [...]" + ChatColor.GOLD + " - " + ChatColor.YELLOW + "Area Commands.");
		sender.sendMessage(ChatColor.WHITE + label + " debug [...]" + ChatColor.GOLD + " - " + ChatColor.YELLOW + "Debug Commands.");
		sender.sendMessage(ChatColor.WHITE + label + " info" + ChatColor.GOLD + " - " + ChatColor.YELLOW + "Displays the current Session info.");
	}
	
	public void showInfo(CommandSender sender) {
		Session s = plugin.getSession(sender);
		World w = s.getSelectedWorld();
		Area a = s.getSelectedArea();
		Cuboid c = s.getSelectedCuboid();
		int l[] = s.getSelectedPointLeft();
		int r[] = s.getSelectedPointRight();
		
		sender.sendMessage(ChatColor.DARK_PURPLE + "Session Info");
		sender.sendMessage(ChatColor.YELLOW + "User: "  + ChatColor.WHITE + s.getName());
		sender.sendMessage(ChatColor.YELLOW + "Selected: ");
		sender.sendMessage(ChatColor.YELLOW + "    World:" + ChatColor.WHITE + " " + (w == null ? "none" : w.getName()));
		sender.sendMessage(ChatColor.YELLOW + "    Area:" + ChatColor.WHITE + " " + (a == null ? "none" :  "(" + a.getId() + ") " + a.getName()));
		sender.sendMessage(ChatColor.YELLOW + "    Cuboid:" + ChatColor.WHITE + " " + (c == null ? "none" :  c.getId()));
		sender.sendMessage(ChatColor.YELLOW + "    Points:" + ChatColor.WHITE + 
				" (" + l[0] + ", " + l[1] + ", " + l[2] + 
				") (" + r[0] + ", " + r[1] + ", " + r[2] + ")");
	}
}
