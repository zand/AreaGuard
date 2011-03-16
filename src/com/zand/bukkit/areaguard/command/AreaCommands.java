package com.zand.bukkit.areaguard.command;

import java.util.EnumSet;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.CreatureType;
import org.bukkit.util.Java15Compat;

import com.zand.areaguard.Config;
import com.zand.areaguard.area.Area;
import com.zand.bukkit.areaguard.AreaGuard;
import com.zand.bukkit.areaguard.Session;
import com.zand.bukkit.common.Messager;

public class AreaCommands implements CommandExecutor {
	private AreaGuard plugin;
	
	public AreaCommands(AreaGuard plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (args != null && args.length > 0) {
			if (args[0].equalsIgnoreCase("create")) {
				String name = "New Area";
				if (args.length > 1) {
					name = "";
					
					for (String arg : Java15Compat.Arrays_copyOfRange(args, 1, args.length))
						name += arg + " ";
					name = name.trim();
				}
				
				Session session = plugin.getSession(sender);
				Area area = Config.storage.newArea(session.getRealName(), name);
				area.getList("owners").addValue(session.getRealName(), session.getName());
				session.select(area);
				Messager.inform(sender, "Added new area named \"" + name + "\".");
			} 
			else showHelp(sender, label);
		} else {
			showHelp(sender, label);
		}
		return true;
	}
	
	public void showHelp(CommandSender sender, String label) {
		sender.sendMessage(ChatColor.DARK_PURPLE + plugin.versionInfo + " Area Help");
		sender.sendMessage(ChatColor.WHITE + label + " help" + ChatColor.GOLD + " - " + ChatColor.YELLOW + "Shows this.");
	}
	
	public String getValidListName(String name) {
		name = name.toLowerCase();
		if (name.startsWith("own"))
			return "owners";
		
		if (name.equals("allow") ||
			name.equals("open") ||
			name.equals("enter") ||
			name.equals("use") ||
			name.equals("mobs"))
		return name;
		
		for (CreatureType type : EnumSet.allOf(CreatureType.class)) {
			String mob = type.getName().toLowerCase();
			if (name.equals(mob) || (name.length() > 3 && mob.startsWith(name)))
				return mob;
		}
		
		Material mat = Material.matchMaterial(name);
		if (mat != null && mat.getId() < 256) 
			return mat.name().toLowerCase().replaceAll("\\s+", "-").replaceAll("\\W", "");
		return null;
	}
}
