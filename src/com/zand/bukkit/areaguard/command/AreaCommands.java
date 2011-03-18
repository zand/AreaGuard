package com.zand.bukkit.areaguard.command;

import java.util.ArrayList;
import java.util.EnumSet;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Player;
import org.bukkit.util.Java15Compat;

import com.zand.areaguard.Config;
import com.zand.areaguard.area.Area;
import com.zand.areaguard.area.Cuboid;
import com.zand.areaguard.area.List;
import com.zand.areaguard.area.Msg;
import com.zand.areaguard.area.World;
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
		Session session = plugin.getSession(sender);
		
		if (args != null && args.length > 0) {
			
			//Create
			if (args[0].equalsIgnoreCase("create")) {
				String name = "";
				if (args.length > 1) {					
					for (String arg : Java15Compat.Arrays_copyOfRange(args, 1, args.length))
						name += arg + " ";
					name = name.trim();
				}
				
				if (name.isEmpty()) name = "New Area";
				
				Area area = Config.storage.newArea(session.getRealName(), name);
				if (area != null) {
					session.select(area);
					Messager.inform(sender, "Added and selected new area named \"" + name + "\".");
					
					if (!area.getList("owners").addValue(session.getRealName(), session.getName()))
						Messager.error(sender, "Failed to add \"" + session.getName() + "\" to owners list.");
				}
				else Messager.error(sender, "Failed to create \"" + name + "\".");
			} 
			
			// Select
			else if (args[0].toLowerCase().startsWith("sel")) {
				if (args.length > 1) {
					Area area = null;
					if (args.length == 2) {
						// here
						if (args[1].equalsIgnoreCase("here") && sender instanceof Player) {
							Location loc = ((Player)sender).getLocation();
							Cuboid cuboid = Config.storage.getWorld(loc.getWorld().getName())
							.getCuboid(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
							if (cuboid != null) area = cuboid.getArea();
							if (area != null) {
								session.select(area);
								Messager.inform(sender, "Selected the \"" + area.getName() + "\" area at your current location.");
								return true;
							}
						}
						
						// [id]
						try {
							area = Config.storage.getArea(Integer.valueOf(args[1]));
							if (area != null) {
								session.select(area);
								Messager.inform(sender, "Selected the \"" + area.getName() + "\" area with id of \"" + args[1] + "\".");
								return true;
							}
						} catch (NumberFormatException e) {}
					}
					// [x] [y] [z]
					else if (args.length == 4) {
						World world = session.getSelectedWorld();
						if (world == null && session instanceof Player)
								world = Config.storage.getWorld(((Player)sender).getWorld().getName());
						if (world != null)
						try {
							Cuboid cuboid = world.getCuboid(
									Integer.valueOf(args[1]), 
									Integer.valueOf(args[2]), 
									Integer.valueOf(args[3]));
							if (cuboid != null) area = cuboid.getArea();
							if (area != null) {
								session.select(area);
								Messager.inform(sender, "Selected the \"" + area.getName() + 
										"\" area at (" + args[1] + ", " + args[2] + ", " + args[3] + ").");
								return true;
							}
						} catch (NumberFormatException e) {}
					} else if (args.length > 2) {
						String name = "";
						for (String arg : Java15Compat.Arrays_copyOfRange(args, 2, args.length))
							name += arg + " ";
						name = name.trim();
						
						// my [name...]
						if (args[1].equalsIgnoreCase("my")) {
							ArrayList<Area> areas = Config.storage.getAreas(name, session.getName());
							if (!areas.isEmpty()) {
								session.select(areas.get(0));
								Messager.inform(sender, "Selected your \"" + areas.get(0).getName() + 
										"\" area by name.");
								return true;
							}
						} else { // [player] [name...]
							ArrayList<Area> areas = Config.storage.getAreas(name, args[1]);
							if (!areas.isEmpty()) {
								session.select(areas.get(0));
								Messager.inform(sender, "Selected " + args[1] + "'s \"" + areas.get(0).getName() + 
										"\" area by name.");
								return true;
							}
						}
					}
					// [name...]
					String name = "";
					for (String arg : Java15Compat.Arrays_copyOfRange(args, 1, args.length))
						name += arg + " ";
					name = name.trim();
					ArrayList<Area> areas = Config.storage.getAreas(name, session.getName());
					if (!areas.isEmpty()) {
						session.select(areas.get(0));
						Messager.inform(sender, "Selected your \"" + areas.get(0).getName() + 
							"\" area by name.");
						return true;
					}
					areas = Config.storage.getAreas(name);
					if (!areas.isEmpty()) {
						session.select(areas.get(0));
						Messager.inform(sender, "Selected the \"" + areas.get(0).getName() + 
							"\" area by name.");
						return true;
					}
					
					Messager.warn(sender, "Could not find area \"" + name + "\"");
				}
				else showSelectHelp(sender, label + " " + args[0]);
			}
			
			// Owned
			else if (args[0].toLowerCase().startsWith("own")) {
				String msg = "";
				String player = session.getName();
				if (args.length > 2 && args[1].equalsIgnoreCase("by")) {
					player = args[2];
				}
				
				ArrayList<Area> areas = Config.storage.getAreasOwned(player);
				
				if (player == session.getName()) Messager.inform(sender, "Areas you own:" + ChatColor.WHITE + " " + areas.size());
				else Messager.inform(sender, "Areas owned by " + player + ":" + ChatColor.WHITE + " " + areas.size());
				
				for (Area area : areas) {
					msg += ", (" + area.getId() + ")" + area.getName();
				}
				if (msg.isEmpty()) msg = "None";
				else msg = msg.substring(2);
				sender.sendMessage(msg);
			}
			
			// Check
			else if (args[0].equalsIgnoreCase("check")) {
				if (args.length > 1) {
					String msg = "";
					
					for (String arg : Java15Compat.Arrays_copyOfRange(args, 1, args.length))
						msg += getValidListName(arg) + " ";
					msg = msg.trim();
					sender.sendMessage(msg);
				}
			}
			
			// Info
			else if (args[0].equalsIgnoreCase("info")) {
				Area area = session.getSelectedArea();
				if (area == null) {
					Messager.warn(sender, "You have no area selected");
					return true;
				}
				
				ArrayList<List> lists = area.getLists();
				ArrayList<Msg> msgs = area.getMsgs();
				String names;
				
				sender.sendMessage(ChatColor.DARK_PURPLE + "Area Info");
				sender.sendMessage(ChatColor.YELLOW + "ID: " + ChatColor.WHITE + area.getId() 
						+ ChatColor.YELLOW + " Name: " + ChatColor.WHITE + area.getName());
				sender.sendMessage(ChatColor.YELLOW + "Owners: " + ChatColor.WHITE + area.getList("owners").toString());
				sender.sendMessage(ChatColor.YELLOW + "Restrict: " + ChatColor.WHITE + area.getList("restrict").toString());
				sender.sendMessage(ChatColor.YELLOW + "Lists: " + ChatColor.WHITE + lists.size());
				names = "";
				for (List list : lists) names += list.getName() + " ";
				if (!names.isEmpty()) sender.sendMessage(names);
				sender.sendMessage(ChatColor.YELLOW + "Msgs: " + ChatColor.WHITE + msgs.size());
				names = "";
				for (Msg msg : msgs) names += msg.getName() + " ";
				if (!names.isEmpty()) sender.sendMessage(names);
			}
			
			// Delete
			else if (args[0].equalsIgnoreCase("delete")) {
				Area area = session.getSelectedArea();
				if (area == null)
					Messager.warn(sender, "You have no area selected");
				if (!area.isOwner(session.getName())) {
					// TODO Add area creators
					Messager.warn(sender, "Your not an Owner of the selected area.");
					return true; }
				if (area.delete()) Messager.inform(sender, "Selected Area Deleted.");
				else Messager.error(sender, "Faild to delete area!");
			}
			
			// Msg Operations
			// Show
			else if (args[0].equalsIgnoreCase("show")) {
				Area area = session.getSelectedArea();
				if (area == null) {
					Messager.warn(sender, "You have no area selected");
					return true; }
				
				if (args.length > 1) {
					String name = getValidMsgName(args[1]);
					
					if (name == null || name.isEmpty()) {
						Messager.warn(sender, "\"" + args[1] + "\" is not a valid msg name.");
						return true; }
					
					Msg msg = area.getMsg(name);
					if (msg == null) Messager.error(sender, "Could not access the msg \"" + name + "\".");
					else Messager.inform(sender, name + ":" + ChatColor.WHITE + " " + msg.getMsg());
				} else Messager.warn(sender, "No message name given.");
				return true;
			}
			
			// Msg
			else if (args[0].equalsIgnoreCase("msg")) {
				Area area = session.getSelectedArea();
				if (area == null) {
					Messager.warn(sender, "You have no area selected");
					return true; }
				if (!area.isOwner(session.getName())) {
					// TODO Add area creators
					Messager.warn(sender, "Your not an Owner of the selected area.");
					return true; }
				
				if (args.length > 1) {
					String name = getValidMsgName(args[1]);
					String text = "";
					
					if (name == null || name.isEmpty()) {
						Messager.warn(sender, "\"" + args[1] + "\" is not a valid msg name.");
						return true; }
					
					if (args.length > 2)
						for (String arg : Java15Compat.Arrays_copyOfRange(args, 2, args.length))
							text += arg + " ";
					
					Msg msg = area.getMsg(name);
					if (msg == null) {
						Messager.error(sender, "Could not access the msg \"" + name + "\".");
						return true; }
					
					if (msg.setMsg(session.getRealName(), text.trim()))
						Messager.inform(sender, "Set the msg \"" + name + "\" to:" + ChatColor.WHITE + " " + text);
					else Messager.error(sender, "Could not set the msg \"" + name + "\".");
					return true;
				} else Messager.warn(sender, "No message name given.");
				return true;
			}
			
			
			// List Operations
			// Add
			else if (args[0].equalsIgnoreCase("add")) {
				Area area = session.getSelectedArea();
				if (area == null) {
					Messager.warn(sender, "You have no area selected");
					return true; }
				if (!area.isOwner(session.getName())) {
					// TODO Add area creators
					Messager.warn(sender, "Your not an Owner of the selected area.");
					return true; }
				
				if (args.length > 1) {
					String name = getValidListName(args[1]);
					
					if (name == null || name.isEmpty()) {
						Messager.warn(sender, "\"" + args[1] + "\" is not a valid list name.");
						return true; }
					
					if (args.length <= 2)
						Messager.warn(sender, "No values given for the list.");
					
					List list = area.getList(name);
					if (list == null) {
						Messager.error(sender, "Could not access the list \"" + name + "\".");
						return true; }
					
					if (list.addValues(session.getRealName(), Java15Compat.Arrays_copyOfRange(args, 2, args.length)))
						Messager.inform(sender, "Adding to " + name + ":");
					else Messager.error(sender, "Could not set the msg \"" + name + "\".");
					return true;
				} else Messager.warn(sender, "No list name given.");
				return true;
			}
			
			// Remove
			else if (args[0].equalsIgnoreCase("remove")) {
				Area area = session.getSelectedArea();
				if (area == null) {
					Messager.warn(sender, "You have no area selected");
					return true; }
				if (!area.isOwner(session.getName())) {
					// TODO Add area creators
					Messager.warn(sender, "Your not an Owner of the selected area.");
					return true; }
				
				if (args.length > 1) {
					String name = getValidListName(args[1]);
					
					if (name == null || name.isEmpty()) {
						Messager.warn(sender, "\"" + args[1] + "\" is not a valid list name.");
						return true; }
					
					if (args.length <= 2)
						Messager.warn(sender, "No values given for the list.");
					
					List list = area.getList(name);
					if (list == null) {
						Messager.error(sender, "Could not access the list \"" + name + "\".");
						return true; }
					
					if (list.removeValues(Java15Compat.Arrays_copyOfRange(args, 2, args.length)))
						Messager.inform(sender, "Removing from " + name + ":");
					else Messager.error(sender, "Could not set the list \"" + name + "\".");
					return true;
				} else Messager.warn(sender, "No list name given.");
				return true;
			}
			// List
			
			else showHelp(sender, label);
		} else {
			showHelp(sender, label);
		}
		return true;
	}
	
	public void showHelp(CommandSender sender, String label) {
		sender.sendMessage(ChatColor.DARK_PURPLE + plugin.versionInfo + " Area Help");
		sender.sendMessage(ChatColor.WHITE + label + " help" + ChatColor.GOLD + " - " + ChatColor.YELLOW + "Shows this.");
		sender.sendMessage(ChatColor.WHITE + label + " create [name]" + ChatColor.GOLD + " - " + ChatColor.YELLOW + "Creates a new area.");
		sender.sendMessage(ChatColor.WHITE + label + " owned [by <player>]" + ChatColor.GOLD + " - " + ChatColor.YELLOW + "Gets areas owned by player.");
		sender.sendMessage(ChatColor.WHITE + label + " select [...]" + ChatColor.GOLD + " - " + ChatColor.YELLOW + "Selects an area. Run it without any arguments for mor info.");
	}
	
	public void showSelectHelp(CommandSender sender, String label) {
		sender.sendMessage(ChatColor.DARK_PURPLE + plugin.versionInfo + " Area Select Help");
		sender.sendMessage(ChatColor.WHITE + label + " <name>" + ChatColor.GOLD + " - " + ChatColor.YELLOW + "Selects an area with that name.");
		sender.sendMessage(ChatColor.WHITE + label + " <id>" + ChatColor.GOLD + " - " + ChatColor.YELLOW + "Selects an area with that id.");
		sender.sendMessage(ChatColor.WHITE + label + " <x> <y> <z>" + ChatColor.GOLD + " - " + ChatColor.YELLOW + "Selects an area at that position.");
		sender.sendMessage(ChatColor.WHITE + label + " my <name>" + ChatColor.GOLD + " - " + ChatColor.YELLOW + "Selects an area you own with that name.");
		sender.sendMessage(ChatColor.WHITE + label + " <player> <name>" + ChatColor.GOLD + " - " + ChatColor.YELLOW + "Selects an area owned by player with that name.");
	}
	
	public String getValidListName(String name) {
		name = name.toLowerCase();
		if (name.startsWith("own"))
			return "owners";
		
		String prefix = "";
		if (name.startsWith("no-") && name.length() > 3) {
			name = name.substring(3);
			prefix = "no-";
		}
		
		if (name.equals("allow") ||
			name.equals("open") ||
			name.equals("enter") ||
			name.equals("heal") ||
			name.equals("use") ||
			name.equals("mobs"))
		return prefix + name;
		
		for (CreatureType type : EnumSet.allOf(CreatureType.class)) {
			String mob = type.getName().toLowerCase();
			if (name.equals(mob) || (name.length() > 3 && mob.startsWith(name)))
				return prefix + mob;
		}
		
		Material mat = Material.matchMaterial(name);
		if (mat != null && mat.getId() < 256) 
			return prefix + mat.name().toLowerCase().replaceAll("\\W", "").replaceAll("_", "-");
		return null;
	}
	
	public String getValidMsgName(String name) {
		String prefix = "";
		if (name.startsWith("no-") && name.length() > 3) {
			name = name.substring(3);
			prefix = "no-";
		}
		
		if (name.equals("allow") ||
			name.equals("open") ||
			name.equals("enter") ||
			name.equals("leave") ||
			name.equals("use") ||
			name.equals("mobs"))
		return prefix + name;
		
		for (CreatureType type : EnumSet.allOf(CreatureType.class)) {
			String mob = type.getName().toLowerCase();
			if (name.equals(mob) || (name.length() > 3 && mob.startsWith(name)))
				return prefix + mob;
		}
		
		Material mat = Material.matchMaterial(name);
		if (mat != null && mat.getId() < 256) 
			return prefix + mat.name().toLowerCase().replaceAll("\\W", "").replaceAll("_", "-");
		return null;
	}
}
