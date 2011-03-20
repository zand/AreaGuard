package com.zand.bukkit.areaguard.command;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.zand.areaguard.Config;
import com.zand.areaguard.area.Area;
import com.zand.areaguard.area.Cuboid;
import com.zand.areaguard.area.World;
import com.zand.bukkit.areaguard.AreaGuard;
import com.zand.bukkit.areaguard.Session;
import com.zand.bukkit.common.Messager;

public class CuboidCommands implements CommandExecutor {
	private AreaGuard plugin;
	private CommandHelp help = new CommandHelp("Cuboid");
	
	public CuboidCommands(AreaGuard plugin) {
		this.plugin = plugin;
		help.add("create", 		"", 	"Creates a new cuboid for the selected area.", "");
		help.add("activate", 	"", 	"Activates the selected cuboid.", "");
		help.add("deactivate", 	"", 	"Deactivates the selected cuboid.", "");
		help.add("delete", 		"", 	"Deletes the selected cuboid.", "");
		help.add("list", 		"", 	"Lists the cuboids in the selected area.", "");
		help.add("move", 		"", 	"Moves the selected area.", "");
		help.add("select", 		"<id>", "Selects a cuboid by ID.", "");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		Session session = plugin.getSession(sender);
		if (args != null && args.length > 0) {
			// Create
			if (args[0].equalsIgnoreCase("create")) {
				World world = session.getSelectedWorld();
				Area area = session.getSelectedArea();
				
				if (world == null) {
					Messager.warn(sender, "You have no world selected");
					return true; }
				
				if (area == null) {
					Messager.warn(sender, "You have no area selected");
					return true; }
				if (!canModify(area, sender, true)) return true;
				
				Cuboid cuboid = Config.storage.newCuboid(session.getRealName(), area, world, session.getCoords());
				if (cuboid == null || !cuboid.exsists()) Messager.error(sender, "Failed to create Cuboid!");
				else {
					session.select(cuboid);
					Messager.inform(sender, "Cuboid created and selected for \"" + area.getName() + "\".");
					if (canActivate(cuboid, sender, false)) {
						if (!cuboid.setActive(true))
							Messager.warn(sender, "Failed to activate cuboid.");
					}
				}
				System.out.println("[]");
				return true;
			}
			
			// Select
			else if (args[0].toLowerCase().startsWith("sel")) {
				if (args.length > 1) {
					try {
						Cuboid cuboid = Config.storage.getCuboid(Integer.valueOf(args[1]));
						if (cuboid != null && cuboid.exsists()) {
							session.select(cuboid);
							Messager.inform(sender, "Selected cuboid ID " + args[1]);
						} else Messager.warn(sender, "Could not find cuboid with ID " + args[1]);
					} catch (NumberFormatException e) {
						Messager.warn(sender, "\"" + args[1] + "\" is not a number.");
					}
				}
				return true;
			}
			
			// Move
			else if (args[0].equalsIgnoreCase("move")) {
				World world = session.getSelectedWorld();
				Cuboid cuboid = session.getSelectedCuboid();
				if (cuboid == null) {
					Messager.warn(sender, "You have no cuboid selected.");
					return true; }
				if (!canModify(cuboid, sender, true)) return true;
				if (cuboid.isActive() && !canActivate(cuboid, sender, true)) {
					Messager.warn(sender, "You can deactivate this cuboid to move it, but it will need to be activated agen.");
					return true; }
				if (world == null) {
					Messager.warn(sender, "You have no world selected.");
					return true; }
				
				if (cuboid.setLocation(world, session.getCoords()))
					Messager.inform(sender, "The selected cuboid was deleted.");
				else Messager.error(sender, "Faild to delete cuboid!");
				
				return true;
			}
			
			// Activate
			else if (args[0].toLowerCase().startsWith("acti")) {
				Cuboid cuboid = session.getSelectedCuboid();
				if (cuboid == null) {
					Messager.warn(sender, "You have no cuboid selected.");
					return true; }
				if (!canActivate(cuboid, sender, true)) return true;
				if (cuboid.isActive()) {
					Messager.inform(sender, "The selected cuboid is already active."); 
					return true; }
				
				if (cuboid.setActive(true))
					Messager.inform(sender, "The selected cuboid is now active.");
				else Messager.error(sender, "Faild to activate cuboid!");
				
				return true;
			}
			
			// Deactivate
			else if (args[0].toLowerCase().startsWith("deacti")) {
				Cuboid cuboid = session.getSelectedCuboid();
				if (cuboid == null) {
					Messager.warn(sender, "You have no cuboid selected.");
					return true; }
				if (!canModify(cuboid, sender, true)) return true;
				if (!cuboid.isActive()) {
					Messager.inform(sender, "The selected cuboid is already inactive."); 
					return true; }
				
				if (cuboid.setActive(false))
					Messager.inform(sender, "The selected cuboid is now inactive.");
				else Messager.error(sender, "Faild to deactivate cuboid!");
				
				return true;
			}
			
			// List
			else if (args[0].equalsIgnoreCase("list")) {
				
				Area area = session.getSelectedArea();
				if (area == null) {
					Messager.warn(sender, "You have no area selected");
					return true; }
				
				ArrayList<Cuboid> cuboids = area.getCubiods();
				Messager.inform(sender, "Cuboids in \"" + area.getName() + "\":" + ChatColor.WHITE + " " + cuboids.size());
				for (Cuboid cuboid : cuboids) {
					String line = "";
					line += "ID: \t" + cuboid.getId();
					line += " Priority: " + cuboid.getPriority();
					int coords[] = cuboid.getCoords();
					line += " \t@(" + coords[0] + ", " + coords[1] + ", " + coords[2] + ")";
					line += "-(" + coords[3] + ", " + coords[4] + ", " + coords[5] + ")";
					
					// Active:   
					// Inactive: 
					if (cuboid.isActive()) 
						sender.sendMessage(ChatColor.GREEN + line);
					else sender.sendMessage(ChatColor.RED + line);
				}
				return true;
			}
			
			// Info
			else if (args[0].equalsIgnoreCase("info")) {
				
				Cuboid cuboid = session.getSelectedCuboid();
				if (cuboid == null) {
					Messager.warn(sender, "You have no cuboid selected.");
					return true; }
				
				sender.sendMessage(ChatColor.DARK_PURPLE + "Selected Cuboid Info:");
				sender.sendMessage(ChatColor.YELLOW + "ID:" + ChatColor.WHITE + " " + cuboid.getId());
				if (cuboid.isActive()) 
					sender.sendMessage(ChatColor.YELLOW + "Status:" + ChatColor.GREEN + " Active");
					else sender.sendMessage(ChatColor.YELLOW + "Status:" + ChatColor.RED + " Inactive");
				
				sender.sendMessage(ChatColor.YELLOW + "Area:" + ChatColor.WHITE + " " + cuboid.getArea().getName());
				sender.sendMessage(ChatColor.YELLOW + "World:" + ChatColor.WHITE + " " + cuboid.getWorld().getName());
				int coords[] = cuboid.getCoords();
				sender.sendMessage(ChatColor.YELLOW + "Coords:" + ChatColor.WHITE + " "
				+ "(" + coords[0] + ", " + coords[1] + ", " + coords[2] + ")"
				+ "-(" + coords[3] + ", " + coords[4] + ", " + coords[5] + ")");
				
				return true;
			}
			
			// Delete
			else if (args[0].equalsIgnoreCase("delete")) {
				
				Cuboid cuboid = session.getSelectedCuboid();
				if (cuboid == null) {
					Messager.warn(sender, "You have no cuboid selected.");
					return true; }
				if (!canModify(cuboid, sender, true)) return true;
				
				if (cuboid.delete())
					Messager.inform(sender, "The selected cuboid was deleted.");
				else Messager.error(sender, "Faild to delete cuboid!");
				
				return true;
			}
		}
		help.show(sender, label);
		return true;
	}
	
	public boolean canActivate(Cuboid cuboid, CommandSender sender, boolean warn) {
		// areaguard.cuboid.activate.owned
		// areaguard.cuboid.activate.created
		// areaguard.cuboid.activate.all
		
		if (sender.isOp()) return true;
		if (warn) Messager.warn(sender, "You are not a allowed to activate this cuboid.");
		return false;
	}
	
	public boolean canModify(Cuboid cuboid, CommandSender sender, boolean warn) {
		return canModify(cuboid.getArea(), sender, warn);
	}
	
	public boolean canModify(Area area, CommandSender sender, boolean warn) {
		// areaguard.cuboid.modify.owned
		// areaguard.cuboid.modify.created
		// areaguard.cuboid.modify.all
		
		if (sender.isOp()) return true;
		if (sender instanceof Player) {
			Player player = (Player)sender;
			if (area.isOwner(player.getName())) return true;
		}
		if (warn) Messager.warn(sender, "You are not a owner of the area that this cuboid belongs to.");
		return false;
	}
}
