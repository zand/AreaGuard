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
import com.zand.bukkit.util.Messager;

public class AreaCommands implements CommandExecutor {
	private CommandHelp help;
	private AreaGuard plugin;
	
	public AreaCommands(AreaGuard plugin) {
		this.plugin = plugin;
		
		help = new CommandHelp(plugin, "Area");
		help.add("create", 	0,	"[name]", 				"Creates a new area.", "");
		help.add("delete", 	0,	"", 					"Deletes the selected area.", "areaguard.area.modify");
		help.add("owned", 	3,	"[by <player>]", 		"Gets areas owned by player.", "");
		help.add("made", 		0,	"[by <player>]",	"Lists the areas that where created.", "");
		help.add("select", 	3,	"[<player>|my] <name>",	"Selects an area by name.", "");
		help.add("select", 	3,	"<id>", 				"Selects an area by id.", "");
		help.add("select", 	3,	"<x> <y> <z>", 			"Selects an area at that position.", "");
		help.add("add", 	0,	"<list> [values...]", 	"Adds the values to the list.", "areaguard.area.modify");
		help.add("remove", 	3,	"<list> [values...]", 	"Removes from the list.", "areaguard.area.modify");
		help.add("clear", 	0,	"<list>", 				"Removes the list.", "areaguard.area.modify");
		help.add("msg", 	0,	"<event> [msg...]", 	"Sets the message for that event.", "areaguard.area.modify");
		help.add("show", 	0,	"<event>", 				"Shows the message for that event.", "");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		Session session = plugin.getSession(sender);
		
		if (args != null && args.length > 0) {
			
			//Create
			if (args[0].equalsIgnoreCase("create")) {
				if (!canCreate(sender))
					return true;
				
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
					if (!area.getList("restrict").addValues(session.getRealName(), Config.defaultRestict))
						Messager.error(sender, "Failed to add the default restricts.");
				
				}
				else Messager.error(sender, "Failed to create \"" + name + "\".");
				return true;
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
							if (cuboid != null && cuboid.exsists()) area = cuboid.getArea();
							if (area != null && area.exsists()) {
								session.select(area);
								Messager.inform(sender, "Selected the \"" + area.getName() + "\" area at your current location.");
								return true;
							}
						}
						
						// [id]
						try {
							area = Config.storage.getArea(Integer.valueOf(args[1]));
							if (area != null && area.exsists()) {
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
							if (area != null && area.exsists()) {
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
				else help.show(sender, label);
				
				return true;
			}
			
			// Owned
			else if (args[0].toLowerCase().startsWith("own")) {
				String player = session.getName();
				if (args.length > 2 && args[1].equalsIgnoreCase("by")) {
					player = args[2];
				}
				
				ArrayList<Area> areas = Config.storage.getAreasOwned(player);
				
				if (player == session.getName()) Messager.inform(sender, "Areas you own:" + ChatColor.WHITE + " " + areas.size());
				else Messager.inform(sender, "Areas owned by " + player + ":" + ChatColor.WHITE + " " + areas.size());
				
				show(sender, areas);
				
				return true;
			}
			
			// Made
			else if (args[0].equalsIgnoreCase("made")) {
				String player = session.getName();
				if (args.length > 2 && args[1].equalsIgnoreCase("by")) {
					player = args[2];
				}
				
				ArrayList<Area> areas = Config.storage.getAreasCreated(player);
				World world = session.getSelectedWorld();
				int cap = -1;
				if (world != null) cap = plugin.Security.getPermissionInteger(world.getName(), player, "areaguard-area-create");
				
				if (player == session.getName()) Messager.inform(sender, "Areas you created:" + ChatColor.WHITE + " " + areas.size() + "/" + cap + " Area(s)");
				else Messager.inform(sender, "Areas created by " + player + ":" + ChatColor.WHITE + " " + areas.size() + " Area(s)");
				
				show(sender, areas);
				
				return true;
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
				
				return true;
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
				sender.sendMessage(ChatColor.YELLOW + "Lists: " + ChatColor.WHITE + lists.size() + " Lists(s)");
				names = "";
				for (List list : lists) names += list.getName() + " ";
				if (!names.isEmpty()) sender.sendMessage(names);
				sender.sendMessage(ChatColor.YELLOW + "Msgs: " + ChatColor.WHITE + msgs.size() + " Msg(s)");
				names = "";
				for (Msg msg : msgs) names += msg.getName() + " ";
				if (!names.isEmpty()) sender.sendMessage(names);
				
				return true;
			}
			
			// Delete
			else if (args[0].equalsIgnoreCase("delete")) {
				Area area = session.getSelectedArea();
				if (area == null)
					Messager.warn(sender, "You have no area selected");
				if (!canModify(area, sender)) return true;
				if (area.delete()) Messager.inform(sender, "Selected Area Deleted.");
				else Messager.error(sender, "Faild to delete area!");
				
				return true;
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
					if (name == null || name.isEmpty())
						name = getValidListName(args[1]);
					
					if (name == null || name.isEmpty()) {
						Messager.warn(sender, "\"" + args[1] + "\" is not a valid msg or list name.");
						return true; }
					
					// Show the message
					Msg msg = area.getMsg(name);
					if (msg == null) Messager.error(sender, "Could not access the msg \"" + name + "\".");
					else Messager.inform(sender, name + ":" + ChatColor.WHITE + " " + msg.getMsg());
					
					// Show the list
					List list = area.getList(name);
					if (list == null) Messager.error(sender, "Could not access the list \"" + name + "\".");
					else {
						String text = ""; 
						for (String value : list.getValues()) text += value + " ";
						Messager.inform(sender, ChatColor.WHITE + text + " ");
					}
					
				} else Messager.warn(sender, "No message or list name given.");
				
				return true;
			}
			
			// Msg
			else if (args[0].equalsIgnoreCase("msg")) {
				Area area = session.getSelectedArea();
				if (area == null) {
					Messager.warn(sender, "You have no area selected");
					return true; }
				if (!canModify(area, sender)) return true;
				
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
				if (!canModify(area, sender)) return true;
				
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
					
					if (name.equals("restrict")) {
						for (int i = 2; i < args.length; i++) {
							String restrict = args[i];
							args[i] = getValidListName(restrict);
							if (args[i] == null || args[i].isEmpty()) {
								Messager.warn(sender, "\"" + restrict + "\" is not a valid restriction.");
							}
								
						}
					}
					
					if (list.addValues(session.getRealName(), Java15Compat.Arrays_copyOfRange(args, 2, args.length)))
						Messager.inform(sender, "Adding to " + name + ":");
					else Messager.error(sender, "Could not set the msg \"" + name + "\".");
					return true;
				} else Messager.warn(sender, "No list name given.");
				
				return true;
			}
			
			// Remove
			else if (args[0].toLowerCase().startsWith("rem")) {
				Area area = session.getSelectedArea();
				if (area == null) {
					Messager.warn(sender, "You have no area selected");
					return true; }
				if (!canModify(area, sender)) return true;
				
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
			
			// Reverse Arg Pairs
			if (args.length > 1) {
				if (args[1].equalsIgnoreCase("msg") ||
					args[1].equalsIgnoreCase("show") ||
					args[1].equalsIgnoreCase("add") ||
					args[1].toLowerCase().startsWith("rem")) {
					
					String temp = args[1];
					args[1] = args[0];
					args[0] = temp;
					return onCommand(sender, command, label, args);
				}
			}
			
			
			help.show(sender, label);
		} else {
			help.show(sender, label);
		}
		return true;
	}
	
	public String getValidListName(String name) {
		name = name.toLowerCase();
		if (name.startsWith("own"))
			return "owners";
		
		if (name.equals("restrict"))
			return name;
		
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
	
	public boolean canModify(Area area, CommandSender sender) {
		// areaguard.area.modify.owned
		// areaguard.area.modify.created
		// areaguard.area.modify.all
		
		if (sender instanceof Player) {
			Player player = (Player)sender;
			
			if (plugin.Security.permission(player, "areaguard.area.modify.all")) return true;
			if (area.isOwner(player.getName()) &&
					plugin.Security.permission(player, "areaguard.area.modify.owned")) return true;
			if (area.getCreator().equalsIgnoreCase(player.getName()) &&
					plugin.Security.permission(player, "areaguard.area.modify.created")) return true;
		} else if (sender.isOp()) return true;
		Messager.warn(sender, "Your not allowed to modify the selected area.");
		return false;
	}
	
	public boolean canCreate(CommandSender sender) {
		// areaguard-area-create
		
		if (sender instanceof Player) {
			Player player = (Player)sender;
			int cap = plugin.Security.getPermissionInteger(player.getWorld().getName(), player.getName(), "areaguard-area-create");
			int created = Config.storage.getAreasCreated(player.getName()).size();
			if (cap > created) return true;
			else if (cap >= 0) Messager.warn(sender, "You have reached your created area cap of " + cap + ".");
		} else if (sender.isOp()) return true;
		Messager.warn(sender, "Your not allowed to create new areas.");
		return false;
	}
	
	public void show(CommandSender sender, ArrayList<Area> areas) {
		String msg = "";
		for (Area area : areas) {
			msg += ", (" + area.getId() + ")" + area.getName();
		}
		if (msg.isEmpty()) msg = "None";
		else msg = msg.substring(2);
		sender.sendMessage(msg);
	}
}
