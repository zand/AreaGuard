package com.zand.bukkit.areaguard;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

import com.zand.areaguard.Area;
import com.zand.areaguard.Config;

//import com.nijikokun.bukkit.General.General;

/**
 * AreaGuard for Bukkit
 *
 * @author zand
 */
public class AreaGuard extends JavaPlugin {
	private final AreaGuardCommandListener commandListener = new AreaGuardCommandListener(this);
	private final AreaGuardPlayerListener playerListener = new AreaGuardPlayerListener(this);
    private final AreaGuardBlockListener blockListener = new AreaGuardBlockListener(this);
    private final AreaGuardEntityListener entityListener = new AreaGuardEntityListener(this);
    private final List<String> commands = new ArrayList<String>();
    protected final HashMap<String, PlayerSession> playerSessions = new HashMap<String, PlayerSession>();
    public final String versionInfo;
    Config man = new Config();

    public AreaGuard(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder, File plugin, ClassLoader cLoader) {
        super(pluginLoader, instance, desc, folder, plugin, cLoader);

        versionInfo = desc.getName() + " version " + desc.getVersion() + " by zand";
        commands.add(desc.getName().toLowerCase());
        commands.add("ag");
    }

    

    public void onEnable() {
    	registerEvents();
        System.out.println( versionInfo + " is enabled!" );
    }
    
    public void onDisable() {
    }
    
    public boolean isCommand(String command) {
    	return commands.contains(command);
    }
    
    private void registerEvents() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_COMMAND, commandListener, Event.Priority.Low, this);
		pm.registerEvent(Event.Type.ENTITY_DAMAGEDBY_ENTITY, entityListener, Event.Priority.Low, this);
        pm.registerEvent(Event.Type.BLOCK_PLACED, blockListener, Event.Priority.Low, this);
        pm.registerEvent(Event.Type.BLOCK_DAMAGED, blockListener, Event.Priority.Low, this);
        pm.registerEvent(Event.Type.BLOCK_RIGHTCLICKED, blockListener, Event.Priority.Low, this);
        pm.registerEvent(Event.Type.BLOCK_INTERACT, blockListener, Event.Priority.Low, this);
    }
    
    public PlayerSession getSession(Player player) {
    	String name = player.getName();
    	if (!playerSessions.containsKey(name))
    		playerSessions.put(name, new PlayerSession(player));
    	return playerSessions.get(name);
    }
    
    public void showAreaInfo(Player player, Area area) {
    	player.sendMessage(ChatColor.YELLOW + area.toString());
    	ArrayList<String> values;
    	String[] first = {"owners", "restrict", "allow", "no-allow"};
    	
    	// Show what comes first
    	for (String list : first) {
    		values = area.getList(list);
    		if (!values.isEmpty()) {
    			String msg = ChatColor.YELLOW + "  " + list + ":" + ChatColor.WHITE;
    			for (String value: values) 
    				msg += " " + value;
    			player.sendMessage(msg);
    		}
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
    
    public boolean canCreate(Player player) {
    	if (Config.creators.contains(player.getName())) return true;
    	player.sendMessage("your not a area creator but, I will allow for debug.");
    	return true;
    }
    
    public boolean canModify(Area area, Player player) {
    	return (area.listHas("owners", player.getName()) || canCreate(player));
    }
    
    public boolean checkEvent(Cancellable event, Player player, String type, int x, int y, int z) {
    	Area area = Area.getArea(x, y, z);
		if (area != null) {
			if (area.playerCan(player.getName(), type)) {
				String msg = area.getMsg(type);
				if (!msg.isEmpty()) player.sendMessage(ChatColor.YELLOW + msg);
			}
			else {
				event.setCancelled(true);
				String msg = area.getMsg("no-"+type);
				if (!msg.isEmpty()) player.sendMessage(ChatColor.DARK_RED + msg);
				return false;
			}	
		}
		return true;
    }
}
