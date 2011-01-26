package com.zand.bukkit.areaguard;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

import com.nijikokun.bukkit.Permissions.Permissions;
import com.zand.areaguard.Area;
import com.zand.areaguard.Config;

//import com.nijikokun.bukkit.General.General;

/**
 * AreaGuard for Bukkit
 *
 * @author zand
 */
public class AreaGuard extends JavaPlugin {
	private final String name;
	public final String versionInfo;
	private static Logger log = Logger.getLogger("Minecraft");
	
	// Are Listeners
	private final AreaGuardCommandListener commandListener = new AreaGuardCommandListener(this);
	private final AreaGuardWorldListener worldListener = new AreaGuardWorldListener(this);
	private final AreaGuardPlayerListener playerListener = new AreaGuardPlayerListener(this);
    private final AreaGuardBlockListener blockListener = new AreaGuardBlockListener(this);
    private final AreaGuardEntityListener entityListener = new AreaGuardEntityListener(this);
    
    // Outside Plugins
    public Permissions Permissions = null;
    
    // Are command prefixes
    private final List<String> commands = new ArrayList<String>();
    
    // Are data on each player
    protected final HashMap<String, PlayerSession> playerSessions = new HashMap<String, PlayerSession>();

    public AreaGuard(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder, File plugin, ClassLoader cLoader) {
        super(pluginLoader, instance, desc, folder, plugin, cLoader);
        
        Config.setup();
        name = desc.getName();
        String authors = "";
        for (String author : desc.getAuthors()) authors += ", " + author;
        versionInfo = name + " version " + desc.getVersion() + 
        	(authors.isEmpty() ? "" : " by" + authors.substring(1));
        commands.add(name.toLowerCase());
        commands.add("ag");
    }

    public void onEnable() {
    	registerEvents();
    	setupOtherPlugins();
    	log.info( versionInfo + " is enabled!" );
    }
    
    public void onDisable() {
    	TempBlocks.deleteAll();
    }
    
    public boolean isCommand(String command) {
    	return commands.contains(command);
    }
    
    private void registerEvents() {
		PluginManager pm = getServer().getPluginManager();
		Priority preventPriority = Event.Priority.High;
		
		pm.registerEvent(Event.Type.PLAYER_COMMAND, commandListener, Event.Priority.Low, this);
		
		pm.registerEvent(Event.Type.CHUNK_UNLOADED, worldListener, preventPriority, this);
		pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, preventPriority, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, preventPriority, this);
		pm.registerEvent(Event.Type.ENTITY_DAMAGEDBY_ENTITY, entityListener, preventPriority, this);
		pm.registerEvent(Event.Type.ENTITY_DAMAGEDBY_PROJECTILE, entityListener, preventPriority, this);
		pm.registerEvent(Event.Type.ENTITY_DAMAGEDBY_BLOCK, entityListener, preventPriority, this);
        pm.registerEvent(Event.Type.BLOCK_PLACED, blockListener, preventPriority, this);
        pm.registerEvent(Event.Type.BLOCK_DAMAGED, blockListener, preventPriority, this);
        pm.registerEvent(Event.Type.BLOCK_RIGHTCLICKED, blockListener, preventPriority, this);
        pm.registerEvent(Event.Type.BLOCK_INTERACT, blockListener, preventPriority, this);
    }
    
    private void setupOtherPlugins() {
    	
    	// Permissions
    	Plugin test = this.getServer().getPluginManager().getPlugin("Permissions");
    	if (this.Permissions == null) {
    		if(test != null) {
    			this.Permissions = (Permissions)test;
    	    	log.info("[" + name + "] Found Permissions plugin. Using it.");
    	    }
    	}
    }
    
    public PlayerSession getSession(Player player) {
    	String name = player.getName();
    	if (!playerSessions.containsKey(name))
    		playerSessions.put(name, new PlayerSession(player));
    	return playerSessions.get(name);
    }
    
    public void showAreaInfo(Player player, Area area) {
    	player.sendMessage(ChatColor.YELLOW + area.toString());
    	String[] first = {"owners", "restrict", "allow", "no-allow"};
    	
    	// list the set msgs
    	HashMap<String, String> msgs = area.getMsgs();
		if (!msgs.isEmpty()) {
			String msg = ChatColor.YELLOW + "  msg on:" + ChatColor.WHITE;
			for (String value: msgs.keySet()) 
				msg += " " + value;
			player.sendMessage(msg);
		}
    	
    	// Show the lists that come first
		ArrayList<String> values;
    	for (String list : first) {
    		values = area.getList(list);
    		if (!values.isEmpty()) {
    			String msg = ChatColor.YELLOW + "  " + list + ":" + ChatColor.WHITE;
    			for (String value: values) 
    				msg += " " + value;
    			player.sendMessage(msg);
    		}
    		else player.sendMessage(ChatColor.YELLOW + "  " + list + ":");
    	}
    	int i;
    	for (String list : area.getLists()) {
    		// Go though the list and break when we find a match
    		for (i=0; i < first.length; i++) 
    			if (list.equals(first[i])) break;
    		
    		// if we diden't reach the end then there was a match
    		if (i != first.length) continue;
    		
    		values = area.getList(list);
    		if (!values.isEmpty()) {
    			String msg = ChatColor.YELLOW + "  " + list + ":" + ChatColor.WHITE;
    			for (String value: values) 
    				msg += " " + value;
    			player.sendMessage(msg);
    		}
    	}
    }
    
    @SuppressWarnings("static-access")
	public boolean canCreate(Player player) {
    	if (this.Permissions != null)
    		if (this.Permissions.Security.permission(player, "areaguard.create")) return true;
    	if (player.isOp() || Config.isCreator(player.getName())) return true;
    	Messager.warn(player, "Your not allowed to use that command.");
    	return false;
    }
    
    public boolean canModify(Area area, Player player) {
    	return (area.listHas("owners", player.getName()) || canCreate(player));
    }
    
    public boolean checkEvent(Cancellable event, Player player, String type, int x, int y, int z, boolean checkAllow) {
    	Area area = Area.getArea(x, y, z);
		return checkEvent(event, player, type, area, checkAllow);
    }
    
	public boolean checkEvent(Cancellable event, Player player, String type, Area area, boolean checkAllow) {
		if (area != null) {
			if (area.playerCan(player.getName(), type, checkAllow)) {
				String msg = area.getMsg(type);
				if (!msg.isEmpty()) Messager.inform(player, msg);
			}
			else if (checkAllow && getSession(player).bypassArea) {
				Messager.warn(player, "Bypassing area permissions");
				String msg = area.getMsg("no-"+type);
				if (!msg.isEmpty()) Messager.warn(player, msg);
				return true;
			} else {
				event.setCancelled(true);
				String msg = area.getMsg("no-"+type);
				if (!msg.isEmpty()) Messager.warn(player, msg);
				return false;
			}	
		}
		return true;
	}
}
