package com.zand.bukkit.areaguard.command;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Java15Compat;

import com.zand.bukkit.areaguard.AreaGuard;
import com.zand.bukkit.areaguard.Session;
import com.zand.bukkit.util.Messager;

public class PointCommands implements CommandExecutor {
	final AreaGuard plugin;
	private CommandHelp help;
	
	public PointCommands(AreaGuard plugin) {
		this.plugin = plugin;
		help = new CommandHelp(plugin, "Point");
		help.add("select", 3,	"[right,left] <x> <y> <z>", "Sellects a point.", "");
		help.add("select", 3,	"[right,left] here", "Sellects a point at your current location.", "");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		Session session = plugin.getSession(sender);
		
		if (args != null && args.length > 0) {
			if (args[0].toLowerCase().startsWith("sel")) {
				int point[];
				
				if (args.length > 1) {
					if (args[1].toLowerCase().startsWith("r")) {
						point = getPointFromArgs(session, sender, Java15Compat.Arrays_copyOfRange(args, 2, args.length));
						if (point != null) {
							session.selectRight(point[0], point[1], point[2]);
							Messager.inform(sender, "Right Point set:" + ChatColor.WHITE + " (" +
									point[0] + ", " +
									point[1] + ", " +
									point[2] + ")");
							return true;
						}
					}
					else if (args[1].toLowerCase().startsWith("l")) {
						point = getPointFromArgs(session, sender, Java15Compat.Arrays_copyOfRange(args, 2, args.length));
						if (point != null) {
							session.selectLeft(point[0], point[1], point[2]);
							Messager.inform(sender, "Left Point set:" + ChatColor.WHITE + " (" +
									point[0] + ", " +
									point[1] + ", " +
									point[2] + ")");
							return true;
						}
					}
					else {
						point = getPointFromArgs(session, sender, Java15Compat.Arrays_copyOfRange(args, 1, args.length));
						if (point != null) {
							session.select(point[0], point[1], point[2]);
							Messager.inform(sender, "Point set:" + ChatColor.WHITE + " (" +
									point[0] + ", " +
									point[1] + ", " +
									point[2] + ")");
							return true;
						}
					}
				}
			}
		}
		
		help.show(sender, label);
		
		return true;
	}
	
	public int[] getPointFromArgs(Session session, CommandSender sender, String args[]) {
		if (args != null && args.length > 0) {
			if (args[0].equalsIgnoreCase("here") && sender instanceof Player) {
				Location loc = ((Player)sender).getLocation();
				return new int[] {
						loc.getBlockX(), 
						loc.getBlockY(), 
						loc.getBlockZ()};
			}
			else if (args.length > 2) {
				try {
					return new int[] {
							Integer.valueOf(args[0]), 
							Integer.valueOf(args[1]),
							Integer.valueOf(args[2])};
				} catch (NumberFormatException e) {}
			} 
		}
		return null;
	}
}
