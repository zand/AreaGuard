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

		if (args.length > index && plugin.isCommand(args[index])) {
			index++;
			player = event.getPlayer();

			if (args.length > index) {
				if (args[index].equals("help")) { index++; showHelp(); }
				if (args[index].equals("add")) { 
					index++; if (args.length > index)  if (plugin.canCreate(player)) {
						PlayerSession ps = plugin.getSession(player);
						Area area = new Area(args[index], ps.getCoords());
						if (area.getId() != -1) {
								player.sendMessage(ChatColor.YELLOW + area.toString() + " Added and Selected");
								area.addList("restrict", Config.defaultRestict);
								ps.selected = area.getId();
						}
						else player.sendMessage(ChatColor.DARK_RED + "Faild to Add Area");
					}
				}
				else {
					Area area = getAreaFromArgs();
					if (area != null && args.length > index) {
						
						// [area] [operation]
						if (args[index].equals("show") || args[index].equals("info")) plugin.showAreaInfo(player, area);
						else if (args[index].equals("select")) 
							plugin.getSession(player).selected = area.getId();
						
						// Owners only
						else if (args[index].equals("list") && plugin.canModify(area, player)) 
						{ index++; list(area); }
						else if (args[index].equals("msg") && plugin.canModify(area, player)) 
						{ index++; setMsg(area); }
						
						// Creators only
						else if (args[index].equals("rename") && plugin.canCreate(player)) 
						{ index++; if (args.length > index) if (area.setName(args[index])) 
							player.sendMessage(ChatColor.YELLOW + "Area Renamed");
						else player.sendMessage(ChatColor.DARK_RED + "Faild to Rename Area");
						}
						else if (args[index].equals("remove") && plugin.canCreate(player)) {
							if (area.remove())
								player.sendMessage(ChatColor.YELLOW + "Area Removed");
							else player.sendMessage(ChatColor.DARK_RED + "Faild to Remove Area");
						}
						else if (args[index].equals("move") && plugin.canCreate(player)) { 
							if (area.setCoords(plugin.getSession(player).getCoords()))
								player.sendMessage(ChatColor.YELLOW + "Area Moved");
							else player.sendMessage(ChatColor.DARK_RED + "Faild to Move Area");
						}
					}
					else player.sendMessage("Could not find Area");
				}
			}
			else showHelp();

			event.setCancelled(true);
		}
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
		index++; // Skip list name for now
		if (args.length > index) {
			String list = args[index-1]; // the list name
			if (args[index].equals("add")) {
				index++;
				HashSet<String> values = new HashSet<String>();
				for (; index < args.length; index++) values.add(args[index]);
				area.addList(list, values);
				player.sendMessage("Added to " + list);
			}
			else if (args[index].equals("remove")) {
				index++;
				HashSet<String> values = new HashSet<String>();
				for (; index < args.length; index++) values.add(args[index]);
				area.removeList(list, values);
				player.sendMessage("Removed from " + list);
			}
			else if (args[index].equals("delete")) {
				area.removeList(list);
				player.sendMessage("Deleted " + list);
			}
		}
	}

	private Area getAreaFromArgs() {
		if (args.length > index) {
			// Selected
			if (args[index].equals("selected")) 
			{ index++; return Area.getArea(plugin.getSession(player).selected); }
			
			// Name
			else if (args[index].equals("name")) {
				index += 2;
				if (args.length >= index)
					return Area.getArea(args[index-1]);
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
		player.sendMessage("id [area_id] - Area with ID number");
		player.sendMessage("at [x] [y] [z] - Area at location");
		player.sendMessage("here - Area that you are in");
		return null;
	}

	private void showHelp() {
		String prefix = "/" + args[0];
		String[][] help = {
				{"[area] info|show",					"Shows info on a area"},
				{"[area] select",						"Selects an a area"},
				{"[area] list [name] add [values]",		"Adds to a list"},
				{"[area] list [name] remove [values]",	"Removes from a list"},
				{"[area] list [name] delete",			"Deletes the list"},
				{"[area] msg [name] [message]",			"Sets a message"},
				{"[area] move",							"Move the area to the last 2 selected points"},
				{"[area] rename [name]",				"Renames the area"},
				{"[area] remove",						"Removes the area"}};
		
		if (args.length > index) {
			if (args[index].equals("lists")) {
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
			else if (args[index].equals("events")) {
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
