package com.zand.bukkit.areaguard;

import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import com.zand.areaguard.Area;
import com.zand.areaguard.AreaDatabase;
import com.zand.areaguard.Config;

public class CommandHandler {
	public final AreaGuard plugin;

	public CommandHandler(AreaGuard instance) {
		plugin = instance;
	}
	
	public String[] subargs(String args[], int start) {
		if (args == null || args.length == 0) return null;
		if (start <= 0) start += args.length;
		if (start >= args.length) return null;
		String ret[] = new String[args.length - start];
		for (int i = 0; i<args.length-start; i++)
			ret[i] = args[i+start];
		return ret;
	}
    
	/**
	 * the start of all /ag commands
	 * @param ps	The PlayerSession for the calling player
	 * @param args	The Arguments for the command
	 */
	public void ag(PlayerSession ps, String args[]) {
		if (args == null || args.length == 0) help(ps, null); // Help
		else if (args[0].equalsIgnoreCase("help")) help(ps, subargs(args, 1)); 				// Help
		else if (args[0].equalsIgnoreCase("reconfig")) reconfig(ps, subargs(args, 1)); 	// Reconfig
		else if (args[0].equalsIgnoreCase("debug")) debug(ps, subargs(args, 1)); 		// Debug
		else if (args[0].equalsIgnoreCase("bypass")) bypass(ps, subargs(args, 1)); 		// Bypass
		else if (args[0].equalsIgnoreCase("outline")) outline(ps, subargs(args, 1)); 	// Outline
		else if (args[0].equals("create")) create(ps, subargs(args, 1));				// Create
		else getArea(ps, args);
	}
	
	private void help(PlayerSession ps, String args[]) {
		String prefix = "/ag";
		String[][] help = {
				{"help create",	"Help for area creators"},
				{"help owner",	"Help for area owners"},
				{"help lists",	"Help on lists"},
				{"help events",	"Help on events"}};
		
		if (args != null) {
			if (args[0].startsWith("create")) {
				help = new String[][] {
					{"create [name] (owners)",				"Creates an area and adds the owners"},
					{"[area] move",							"Move the area to the last 2 selected points"},
					{"[area] extend",						"Exstend the area to the last selected point"},
					{"[area] delete",						"Deletes the area"}};
			}
			else if (args[0].startsWith("own")) {
				help = new String[][] {
					{"[area] info|show",				"Shows info on a area"},
					{"[area] select",					"Selects an a area"},
					{"[area] add [event] [values]",		"Adds to a list"},
					{"[area] remove [event] [values]",	"Removes from a list"},
					{"[area] clear [event]",			"clears the list"},
					{"[area] msg [event] [message]",	"Sets a message"},
					{"[area] rename [name]",			"Renames the area"}};
			}
			else if (args[0].startsWith("list")) {
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
			else if (args[0].startsWith("event")) {
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
		
		ps.player.sendMessage(ChatColor.LIGHT_PURPLE + "Help: " + ChatColor.WHITE + plugin.versionInfo);
		for (String[] pair : help)
			ps.player.sendMessage(
					ChatColor.WHITE + prefix + " " + pair[0] + 
					ChatColor.GOLD + " - " + 
					ChatColor.YELLOW + pair[1]);
	}

	/**
	 * The Command /ag reconfig
	 * @param ps	The PlayerSession for the calling player
	 * @param args	The Arguments for the command
	 */
	public void reconfig(PlayerSession ps, String args[]) {
		if (ps.player.isOp()) {
			AreaDatabase.getInstance().disconnect(true);
			Config.setup();
			Messager.inform(ps.player, "Reloading the config file.");
		} else Messager.warn(ps.player, "Your not allowed to use that command.");
	}
	
	/**
	 * The Command /ag debug
	 * @param ps	The PlayerSession for the calling player
	 * @param args	The Arguments for the command
	 */
	public void debug(PlayerSession ps, String args[]) {
		if (ps.player.isOp()) {
			ps.debug = !ps.debug;
			Messager.inform(ps.player, "Debug mode " + (ps.debug ? "En" : "Dis") + "abled.");
		} else Messager.warn(ps.player, "Your not allowed to use that command.");
	}
	
	/**
	 * The Command /ag bypass
	 * @param ps	The PlayerSession for the calling player
	 * @param args	The Arguments for the command
	 */
	public void bypass(PlayerSession ps, String args[]) {
		if (plugin.checkPermission(ps.player, "ag.bypass")) {
			ps.bypassArea = !ps.bypassArea;
			Messager.warn(ps.player, "Bypassing " + (ps.bypassArea ? "enabled" : "disabled"));
		}  else Messager.warn(ps.player, "Your not allowed to use that command.");
	}
	
	/**
	 * The Command /ag outline
	 * @param ps	The PlayerSession for the calling player
	 * @param args	The Arguments for the command
	 */
	public void outline(PlayerSession ps, String args[]) {
		if (plugin.checkPermission(ps.player, "ag.create")) {
			if (args.length > 1) {
				ps.drawOutline(false);
				Messager.inform(ps.player, "Outline removed.");
			} else {
				plugin.getSession(ps.player).drawOutline(true);
				Messager.inform(ps.player, "Drawing outline.");
			}
		} else Messager.warn(ps.player, "Your not allowed to use that command.");
	}
	
	/**
	 * The Command /ag create [name] (owners...)
	 * @param ps	The PlayerSession for the calling player
	 * @param args	The Arguments for the command
	 */
	public void create(PlayerSession ps, String args[]) {
		if (plugin.checkPermission(ps.player, "ag.create")) {
			if (args == null || args.length == 0) { Messager.warn(ps.player, "No name was given."); return; }
			
			Area area = new Area(args[0], ps.getCoords());
			if (area.getId() != -1) {
				Messager.inform(ps.player, area.toString() + " Added and Selected");
			
				if (!area.addList("restrict", Config.defaultRestict))
					Messager.error(ps.player, "Failed to add default restrict");
			
				if (args.length > 1) {
					args[0] = "owners";
					areaList(ps, args, area); }
			
				HashSet<String> owners = new HashSet<String>();
				for (int i = 1; i < args.length; i++)
					owners.add(args[i]);
				if (!owners.isEmpty()) {
					if (area.addList("owners", owners)) {
						// Get added values
						String values = "";
						for (String value : owners) values += " " + value;
					
						// Inform the player
						Messager.inform(ps.player, "Added owners:" + ChatColor.WHITE + values); }
					else Messager.error(ps.player, "Failed to add owners");
				}
					
				ps.selected = area.getId();
			}
		} else Messager.warn(ps.player, "Your not allowed to use that command.");
	}
	
	/**
	 * The Command to get an area
	 * @param ps	The PlayerSession for the calling player
	 * @param args	The Arguments for the command
	 */
	public void getArea(PlayerSession ps, String args[]) {
		if (args[0].equalsIgnoreCase("id")) getAreaId(ps, subargs(args, 1));
		else if (args[0].equalsIgnoreCase("name")) getAreaNamed(ps, subargs(args, 1));
		else if (args[0].equalsIgnoreCase("my")) { args[0] = ps.player.getName(); getAreaOwned(ps, args); }
		else if (args[0].toLowerCase().startsWith("own")) getAreaOwned(ps, subargs(args, 1));
		else if  (args[0].toLowerCase().startsWith("sel")) getAreaSelected(ps, subargs(args, 1));
		else if (args[0].equalsIgnoreCase("at")) getAreaAt(ps, subargs(args, 1));
		else if (args[0].equalsIgnoreCase("here")) getAreaHere(ps, subargs(args, 1));
		else {
			try {
				Integer.valueOf(args[0]);
				getAreaId(ps, args);
			} catch (NumberFormatException e) {
				getAreaNamed(ps, args); }
		}
	}
	
	/**
	 * The Commands to get an area by id
	 * @param ps	The PlayerSession for the calling player
	 * @param args	The Arguments for the command
	 */
	public void getAreaId(PlayerSession ps, String args[]) {
		if (args == null) { Messager.warn(ps.player, " no Id given"); return; }
		try {
			withArea(ps, subargs(args, 1), Area.getArea(Integer.valueOf(args[0])));
		} catch (NumberFormatException e) {
			Messager.warn(ps.player, args[0] +" is not a number"); }
	}
	
	/**
	 * The Commands to get an area by name
	 * @param ps	The PlayerSession for the calling player
	 * @param args	The Arguments for the command
	 */
	public void getAreaNamed(PlayerSession ps, String args[]) {
		if (args == null) { Messager.warn(ps.player, " no Name given"); return; }
		withArea(ps, subargs(args, 1), Area.getArea(args[0]));
	}
	
	/**
	 * The Commands to get an area by owner
	 * @param ps	The PlayerSession for the calling player
	 * @param args	The Arguments for the command
	 */
	public void getAreaOwned(PlayerSession ps, String args[]) {
		if (args == null) { Messager.warn(ps.player, " no Player Name given"); return; }
		if (args.length == 1) { Messager.warn(ps.player, " no Player Name given"); return; }
		if (args.length < 2) { Messager.warn(ps.player, " no Area Name given"); return; }
		withArea(ps, subargs(args, 1), Area.getOwnedArea(args[0], args[1]));
	}
	
	/**
	 * The Commands to get an area by selected
	 * @param ps	The PlayerSession for the calling player
	 * @param args	The Arguments for the command
	 */
	public void getAreaSelected(PlayerSession ps, String args[]) {
		withArea(ps, args, Area.getArea(ps.selected));
	}
	
	/**
	 * The Commands to get an area at location
	 * @param ps	The PlayerSession for the calling player
	 * @param args	The Arguments for the command
	 */
	public void getAreaAt(PlayerSession ps, String args[]) {
		if (args == null || args.length < 3) { Messager.warn(ps.player, "no coords given"); return; }
		withArea(ps, subargs(args, 1), Area.getArea(
				Integer.valueOf(args[0]),
				Integer.valueOf(args[1]),
				Integer.valueOf(args[2])));
	}
	
	/**
	 * The Commands to get an area at players location
	 * @param ps	The PlayerSession for the calling player
	 * @param args	The Arguments for the command
	 */
	public void getAreaHere(PlayerSession ps, String args[]) {
		if (args == null || args.length < 3) { Messager.warn(ps.player, "no coords given"); return; }
		Location loc = ps.player.getLocation();
		withArea(ps, subargs(args, 1), Area.getArea(
				loc.getBlockX(),
				loc.getBlockY(), 
				loc.getBlockZ()));
	}
	
	/**
	 * The Commands to for an area by id
	 * @param ps	The PlayerSession for the calling player
	 * @param args	The Arguments for the command
	 * @param area	The area to perform an action on
	 */
	public void withArea(PlayerSession ps, String args[], Area area) {
		if (area == null) { Messager.warn(ps.player, " Area not Found"); return; }
		if (args[0].equalsIgnoreCase("show")) areaInfo(ps, subargs(args, 1), area);
		else if (args[0].equalsIgnoreCase("info")) areaInfo(ps, subargs(args, 1), area);
		else if (args[0].equalsIgnoreCase("select")) areaSelect(ps, subargs(args, 1), area);
		else if (args[0].equalsIgnoreCase("add")) areaList(ps, args, area);
		else if (args[0].equalsIgnoreCase("remove")) areaList(ps, args, area);
		else if (args[0].equalsIgnoreCase("clear")) areaList(ps, args, area);
		else if (args[0].equalsIgnoreCase("msg")) areaMsg(ps, subargs(args, 1), area);
		else if (args[0].toLowerCase().startsWith("prio")) areaPriority(ps, subargs(args, 1), area);
		else if (args[0].equalsIgnoreCase("move")) areaMove(ps, subargs(args, 1), area);
		else if (args[0].equalsIgnoreCase("extend")) areaExtend(ps, subargs(args, 1), area);
		else if (args[0].equalsIgnoreCase("delete")) areaDelete(ps, subargs(args, 1), area);
		else { areaSelect(ps, subargs(args, 1), area); areaInfo(ps, subargs(args, 1), area); }
	}
	
	/**
	 * The Commands to show info for an area
	 * @param ps	The PlayerSession for the calling player
	 * @param args	The Arguments for the command
	 * @param area	The area to perform an action on
	 */
	public void areaInfo(PlayerSession ps, String args[], Area area) {
		plugin.showAreaInfo(ps.player, area);
	}
	
	/**
	 * The Commands to select an area
	 * @param ps	The PlayerSession for the calling player
	 * @param args	The Arguments for the command
	 * @param area	The area to perform an action on
	 */
	public void areaSelect(PlayerSession ps, String args[], Area area) {
		ps.selected = area.getId();
		Messager.inform(ps.player, "Area Selected");
	}
	
	/**
	 * The Commands to manage lists for an area
	 * @param ps	The PlayerSession for the calling player
	 * @param args	The Arguments for the command
	 * @param area	The area to perform an action on
	 */
	public void areaList(PlayerSession ps, String args[], Area area) {
		if (!plugin.canModify(area, ps.player)) return;
		if (args == null || args.length == 1) { Messager.warn(ps.player, "No list name given"); return; }
		String list = args[1];
		if (args[0].equalsIgnoreCase("clear"))  {
			if (area.removeList(list))
				Messager.inform(ps.player, "Cleared " + list);
			else Messager.error(ps.player, "Failed to clear " + list);
		}
		else {
			String v[] = subargs(args, 2);
			HashSet<String> values = new HashSet<String>();
			if (v != null) for (String value : v) values.add(value);
			
			if (values.isEmpty()) Messager.warn(ps.player, "No values given");
			
			else if (args[0].equalsIgnoreCase("add")) { 
				
				if (area.addList(list, values)) {
					// Get added values
					String s = "";
					for (String value : values) s += " " + value;
					
					// Inform the player
					Messager.inform(ps.player, "Added to " + list + ":" + ChatColor.WHITE + s); }
				else Messager.error(ps.player, "Failed to add to " + list);}
			if (args[0].equalsIgnoreCase("remove")) { 
				
				if (area.removeList(list, values)) {
					// Get added values
					String s = "";
					for (String value : values) s += " " + value;
					
					// Inform the player
					Messager.inform(ps.player, "Removed from " + list + ":" + ChatColor.WHITE + s); }
				else Messager.error(ps.player, "Failed to remove from " + list);}
		}
	}
	
	/**
	 * The Commands to set the msg for an area event
	 * @param ps	The PlayerSession for the calling player
	 * @param args	The Arguments for the command
	 * @param area	The area to perform an action on
	 */
	public void areaMsg(PlayerSession ps, String args[], Area area) {
		if (args.length > 0) {
			String name = args[0];
			String msg = "";
			if (args.length > 1) for (String arg : subargs(args, 2)) msg += arg + " ";
			area.setMsg(name, msg.trim());
			ps.player.sendMessage("Message " + name + " set:" + msg);
		}
	}
	
	/**
	 * The Commands to set the priority for an area
	 * @param ps	The PlayerSession for the calling player
	 * @param args	The Arguments for the command
	 * @param area	The area to perform an action on
	 */
	public void areaPriority(PlayerSession ps, String args[], Area area) {
		if (args != null) {
			if (!plugin.checkPermission(ps.player, "ag.create")) return;
			int i;
			try {
				i = Integer.valueOf(args[0]);
			} catch (NumberFormatException e) {
				Messager.warn(ps.player, args[0] +" is not a number");
				return;
			}
		
			if (area.setPriority(i)) 
				Messager.inform(ps.player, "Area priority set to " + i);
			else Messager.error(ps.player, "Faild to set area priority");
		}
		else Messager.inform(ps.player, "The Area priority is " + area.getPriority());
	}
	
	/**
	 * The Commands to move an area
	 * @param ps	The PlayerSession for the calling player
	 * @param args	The Arguments for the command
	 * @param area	The area to perform an action on
	 */
	public void areaMove(PlayerSession ps, String args[], Area area) {
		if (!plugin.checkPermission(ps.player, "ag.create")) return;
		if (area.setCoords(ps.getCoords()))
			Messager.inform(ps.player, "Area Moved");
		else Messager.error(ps.player, "Faild to Move Area");
	}
	
	/**
	 * The Commands to extend an area
	 * @param ps	The PlayerSession for the calling player
	 * @param args	The Arguments for the command
	 * @param area	The area to perform an action on
	 */
	public void areaExtend(PlayerSession ps, String args[], Area area) {
		if (!plugin.checkPermission(ps.player, "ag.create")) return;
		int coords[] = area.getCoords();
		int point[] = ps.getPoint();
		
		for (int i=0; i<3; i++) // Extend the coords to include the point
			if 		(coords[i] > point[i]) 	coords[i] = point[i];
			else if (coords[i+3] < point[i]) coords[i+3] = point[i];
		
		if (area.setCoords(coords))
			Messager.inform(ps.player, "Area Extended");
		else Messager.error(ps.player, "Faild to Extend Area");
	}
	
	/**
	 * The Commands to delete an area
	 * @param ps	The PlayerSession for the calling player
	 * @param args	The Arguments for the command
	 * @param area	The area to perform an action on
	 */
	public void areaDelete(PlayerSession ps, String args[], Area area) {
		if (!plugin.checkPermission(ps.player, "ag.create")) return;
		if (area.remove())
			Messager.inform(ps.player, "Area Deleted");
		else Messager.error(ps.player, "Faild to Delete Area");
	}
}
