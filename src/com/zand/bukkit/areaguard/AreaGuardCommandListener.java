package com.zand.bukkit.areaguard;

import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;

import com.zand.areaguard.Area;
import com.zand.areaguard.Config;

/**
 * Handle events for all Player related events
 * 
 * @author zand
 */
public class AreaGuardCommandListener extends PlayerListener {
	public final AreaGuard plugin;
	private int index;
	private String args[];
	private Player player;

	public AreaGuardCommandListener(AreaGuard instance) {
		plugin = instance;
	}
    
	public void onPlayerCommand(PlayerChatEvent event) {
		if (event.isCancelled())
			return;

		// remove the / and split to args
		args = event.getMessage().toLowerCase().substring(1).split(" ");
		index = 0;
		
		// Is this for us
		if (args.length > index && plugin.isCommand(args[index])) {
			player = event.getPlayer();
			
			processCommand();

			event.setCancelled(true);
		}
	}
	
	private void processCommand() {
		index++;
		
		// Is there something after /ag
		if (args.length > index) {
			
			// Help
			if (args[index].equals("help")) { index++; showHelp(); return; }
			
			// Reconfig
			else if (args[index].equals("reconfig")) {
				if (player.isOp()) {
					Config.setup();
					Messager.inform(player, "Reloading the config file.");
				} else Messager.warn(player, "Your not allowed to use that command.");
				return;
			}
			
			// Debug
			else if (args[index].equals("debug")) {
				if (player.isOp()) {
					PlayerSession s = plugin.getSession(player);
					s.debug = !s.debug;
					Messager.inform(player, "Debug mode " + (s.debug ? "En" : "Dis") + "abled.");
				} else Messager.warn(player, "Your not allowed to use that command.");
				return;
			}
			
			// Bypass
			else if (args[index].equals("bypass") && plugin.checkPermission(player, "ag.bypass")) { 
				PlayerSession ps = plugin.getSession(player);
				ps.bypassArea = !ps.bypassArea;
				Messager.warn(player, "Bypassing " + (ps.bypassArea ? "enabled" : "disabled"));
			}
			
			// Draw
			else if (args[index].equals("outline")) {
				if (plugin.checkPermission(player, "ag.create")) {
					index++;
					if (args.length > index) {
						plugin.getSession(player).drawOutline(false);
						Messager.inform(player, "Outline removed.");
					} else {
						plugin.getSession(player).drawOutline(true);
						Messager.inform(player, "Drawing outline.");
					}
				} else Messager.warn(player, "Your not allowed to use that command.");
				return;
			}
			
			// Create
			else if (args[index].equals("create")) { 
				index++; if (args.length > index)  if (plugin.checkPermission(player, "ag.create")) {
					PlayerSession ps = plugin.getSession(player);
					Area area = new Area(args[index], ps.getCoords());
					if (area.getId() != -1) {
							Messager.inform(player, area.toString() + " Added and Selected");
							
							if (!area.addList("restrict", Config.defaultRestict))
								Messager.error(player, "Failed to add default restrict");
							
							HashSet<String> owners = new HashSet<String>();
							for (index++; index < args.length; index++)
								owners.add(args[index]);
							if (!owners.isEmpty()) {
								if (area.addList("owners", owners)) {
									// Get added values
									String values = "";
									for (String value : owners) values += " " + value;
									
									// Inform the player
									Messager.inform(player, "Added owners:" + ChatColor.WHITE + values); }
								else Messager.error(player, "Failed to add owners");
							}
							
							ps.selected = area.getId();
					}
					else Messager.error(player, "Faild to Add Area");
				}
			}
			
			// [area] [operation]
			else {
				Area area = getAreaFromArgs();
				if (area != null && args.length > index) {
					// All
					// Info/Show
					if (args[index].equals("show") || args[index].equals("info")) plugin.showAreaInfo(player, area);
					
					// Select
					else if (args[index].equals("select")) {
						plugin.getSession(player).selected = area.getId();
						Messager.inform(player, "Area Selected"); }
					
					// Owners only
					// Add|Remove|Clear List
					else if ((args[index].equals("add") ||
							args[index].equals("remove") ||
							args[index].equals("clear")) && plugin.canModify(area, player)) 
						list(area);
					
					// Msg
					else if (args[index].equals("msg") && plugin.canModify(area, player)) 
					{ index++; setMsg(area); }
					
					// Rename
					else if (args[index].equals("rename") && plugin.canModify(area, player)) 
					{ index++; if (args.length > index) if (area.setName(args[index])) 
						Messager.inform(player, "Area Renamed");
					else Messager.error(player, "Faild to Rename Area");
					}

					// Creators only
					// Priority 
					else if (args[index].startsWith("prio") && plugin.canModify(area, player)) { 
						index++; 
						
						if (args.length > index) {
							int i;
							try {
								i = Integer.valueOf(args[index]);
							} catch (NumberFormatException e) {
								Messager.warn(player, args[index] +" is not a number");
								return;
							}
						
							if (area.setPriority(i)) 
								Messager.inform(player, "Area priority set");
							else Messager.error(player, "Faild to set area priority");
						}
					}
					
					// Delete
					else if (args[index].equals("delete") && plugin.checkPermission(player, "ag.create")) {
						if (area.remove())
							Messager.inform(player, "Area Deleted");
						else Messager.error(player, "Faild to Delete Area");
					}
					
					// Move
					else if (args[index].equals("move") && plugin.checkPermission(player, "ag.create")) { 
						if (area.setCoords(plugin.getSession(player).getCoords()))
							Messager.inform(player, "Area Moved");
						else Messager.error(player, "Faild to Move Area");
					}
					
					// Extend 
					else if (args[index].equals("extend") && plugin.checkPermission(player, "ag.create")) {
						int coords[] = area.getCoords();
						int point[] = plugin.getSession(player).getPoint();
						
						for (int i=0; i<3; i++) // Extend the coords to include the point
							if 		(coords[i] > point[i]) 	coords[i] = point[i];
							else if (coords[i+3] < point[i]) coords[i+3] = point[i];
						
						if (area.setCoords(coords))
							Messager.inform(player, "Area Extended");
						else Messager.error(player, "Faild to Extend Area");
					}
					
					// We have reached the end
					// now its time we look at the next arg
					else if (args.length > index+1) {
						index++;
						
						// Alias for Msg or List
						if (args[index].equals("msg") || 
							args[index].equals("add") ||
							args[index].equals("remove") ||
							args[index].equals("clear")) {
							if (args[index].equals(args[index-1])) return; // I would hate to get an endless loop here
							
							// Swap the args
							String temp   = args[index];
							args[index]   = args[index-1];
							args[index-1] = temp;
							
							// re-process the command
							index = 0;
							processCommand();
						}
					}
				}
				else Messager.warn(player, "Could not find Area");
			}
		}
		else showHelp();
	}

	private void setMsg(Area area) {
		if (args.length > index) {
			String name = args[index];
			String msg = "";
			index++;
			for (;index < args.length; index++) msg += " " + args[index];
			area.setMsg(name, msg.trim());
			player.sendMessage("Message " + name + " set:" + msg);
		}
	}

	private void list(Area area) {
		if (args.length > index) {
			String list = args[index+1]; // the list name
			if (args[index].equals("add")) {
				index+=2;
				
				// Get the values
				HashSet<String> values = new HashSet<String>();
				for (; index < args.length; index++) values.add(args[index]);
				if (values.isEmpty()) Messager.warn(player, "No values given");
				else if (area.addList(list, values)) {
					// Get added values
					String s = "";
					for (String value : values) s += " " + value;
					
					// Inform the player
					Messager.inform(player, "Added to " + list + ":" + ChatColor.WHITE + s); }
				else Messager.error(player, "Failed to add to " + list);
			}
			else if (args[index].equals("remove")) {
				index+=2;
				HashSet<String> values = new HashSet<String>();
				for (; index < args.length; index++) values.add(args[index]);
				if (values.isEmpty()) Messager.warn(player, "No values given");
				else if (area.removeList(list, values)) {
					// Get added values
					String s = "";
					for (String value : values) s += " " + value;
					
					// Inform the player
					Messager.inform(player, "Removed from " + list + ":" + ChatColor.WHITE + s); }
				else Messager.error(player, "Failed to remove from " + list);
			}
			else if (args[index].equals("clear")) {
				if (area.removeList(list))
					Messager.inform(player, "Cleared " + list);
				else Messager.error(player, "Failed to clear " + list);
			}
		}
	}

	private Area getAreaFromArgs() {
		if (args.length > index) {
			// Selected
			if (args[index].startsWith("sel")) 
			{ index++; return Area.getArea(plugin.getSession(player).selected); }
			
			// Name
			else if (args[index].equals("name")) {
				index += 2;
				if (args.length >= index)
					return Area.getArea(args[index-1]);
			} 
			// My
			else if (args[index].equals("my")) {
				index += 2;
				if (args.length >= index)
					return Area.getOwnedArea(player.getName(), args[index-1]);
			} 
			// Id
			else if (args[index].equals("id")) {
				index += 2;
				if (args.length >= index)
					return Area.getArea(Integer.valueOf(args[index-1]));
			} 
			// At
			else if (args[index].equals("at")) {
				index += 3;
				if (args.length >= index)
					return Area.getArea(Integer.valueOf(args[index-3]),
										Integer.valueOf(args[index-2]), 
										Integer.valueOf(args[index-1]));
			}
			// Here
			else if (args[index].equals("here")) {
				index++;
				Location loc = player.getLocation();
				return Area.getArea(loc.getBlockX(),
									loc.getBlockY(), 
									loc.getBlockZ());
			}
		}
		player.sendMessage("name [area_name] - Area with name");
		player.sendMessage("my [area_name] - Area you own with name");
		player.sendMessage("id [area_id] - Area with ID number");
		player.sendMessage("at [x] [y] [z] - Area at location");
		player.sendMessage("here - Area that you are in");
		return null;
	}

	private void showHelp() {
		String prefix = "/" + args[0];
		String[][] help = {
				{"help create",	"Help for area creators"},
				{"help owner",	"Help for area owners"},
				{"help lists",	"Help on lists"},
				{"help events",	"Help on events"}};
		
		if (args.length > index) {
			if (args[index].startsWith("create")) {
				help = new String[][] {
					{"create [name] (owners)",				"Creates an area and adds the owners"},
					{"[area] move",							"Move the area to the last 2 selected points"},
					{"[area] extend",						"Exstend the area to the last selected point"},
					{"[area] delete",						"Deletes the area"}};
			}
			else if (args[index].startsWith("own")) {
				help = new String[][] {
					{"[area] info|show",				"Shows info on a area"},
					{"[area] select",					"Selects an a area"},
					{"[area] add [event] [values]",		"Adds to a list"},
					{"[area] remove [event] [values]",	"Removes from a list"},
					{"[area] clear [event]",			"clears the list"},
					{"[area] msg [event] [message]",	"Sets a message"},
					{"[area] rename [name]",			"Renames the area"}};
			}
			else if (args[index].startsWith("list")) {
				prefix = "";
				help = new String[][] {
				{"owners","The area's owners"},
				{"restict","What events are not allowed at default"},
				{"allow","Players that can do anything"},
				{"no-allow","Players that can't do anything"},
				{"","Any event has a list for it, the build event for example -"},
				{"buid","Players that can build"},
				{"no-buid","Players that can't build"},
				}; }
			else if (args[index].startsWith("event")) {
				prefix = "";
				help = new String[][] {
				{"build","Create/Destroy Blocks"},
				{"pvp","Harm to or from other players"},
				{"mobs","Harm to or from Mobs"},
				{"enter","Enter the area"},
				{"open","Open Anything"},
				{"chest","Open chest"},
				{"furnace","Open furnace"},
				{"dispenser","Open dispenser"},
				{"","Not Working -"},
				{"exsplode","Exsplotions"},
				{"leave","Leave the area, Cant be restricted"},
				}; }
		}
		
		player.sendMessage(ChatColor.LIGHT_PURPLE + "Help: " + ChatColor.WHITE + plugin.versionInfo);
		for (String[] pair : help)
			player.sendMessage(
					ChatColor.WHITE + prefix + " " + pair[0] + 
					ChatColor.GOLD + " - " + 
					ChatColor.YELLOW + pair[1]);
	}
}
