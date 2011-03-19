package com.zand.bukkit.areaguard;

import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

import com.nijikokun.bukkit.Permissions.Permissions;
import com.zand.areaguard.*;
import com.zand.areaguard.area.Area;
import com.zand.bukkit.areaguard.command.AreaCommands;
import com.zand.bukkit.areaguard.command.CuboidCommands;
import com.zand.bukkit.areaguard.command.MainCommands;
import com.zand.bukkit.areaguard.listeners.AreaGuardBlockListener;
import com.zand.bukkit.areaguard.listeners.AreaGuardEntityListener;
import com.zand.bukkit.areaguard.listeners.AreaGuardPlayerListener;
import com.zand.bukkit.areaguard.listeners.AreaGuardWorldListener;
import com.zand.bukkit.common.Messager;
import com.zand.bukkit.common.TempBlocks;

/**
 * AreaGuard for Bukkit
 *
 * @author zand
 */
public class AreaGuard extends JavaPlugin {
	static final private HashMap<CommandSender, Session> sessions = new HashMap<CommandSender, Session>();
	
	/**
	* Gets the senders current session.
	* @param sender	The sender to get the session for
	* @return 		The current session for that sender
	*/
	public Session getSession(CommandSender sender) {
		if (!sessions.containsKey(sender)) {
			Session session;
			if (sender instanceof Player) {
				session = new PlayerSession((Player)sender);
				session.select(Config.storage.getWorld(((Player)sender).getWorld().getName()));
			}
			else {
				session = new Session(sender);
				session.select(Config.storage.getWorld(getServer().getWorlds().get(0).getName()));
			}
			sessions.put(sender, session);
		}
		return sessions.get(sender);
	}
	
	private String name;
	public String versionInfo;
	private static Logger log = Logger.getLogger("Minecraft");
	
	// Are Listeners
	private final AreaGuardWorldListener worldListener = new AreaGuardWorldListener(this);
	private final AreaGuardPlayerListener playerListener = new AreaGuardPlayerListener(this);
    private final AreaGuardBlockListener blockListener = new AreaGuardBlockListener(this);
    private final AreaGuardEntityListener entityListener = new AreaGuardEntityListener(this);
    
    // Outside Plugins
    public Permissions Permissions = null;
    
    // Are data on each player
    protected final HashMap<String, PlayerSession> playerSessions = new HashMap<String, PlayerSession>();

    public void onEnable() {
    	Config.setup();
    	PluginDescriptionFile desc = getDescription();
        name = desc.getName();
        String authors = "";
        for (String author : desc.getAuthors()) authors += ", " + author;
        versionInfo = name + " version " + desc.getVersion() + 
        	(authors.isEmpty() ? "" : " by" + authors.substring(1));
        
        getCommand("ag").setExecutor(new MainCommands(this));
        getCommand("area").setExecutor(new AreaCommands(this));
        getCommand("cuboid").setExecutor(new CuboidCommands(this));
    	
    	registerEvents();
    	setupOtherPlugins();
    	log.info( versionInfo + " is enabled!" );
    }
    
    public void onDisable() {
    	TempBlocks.deleteAll();
    }
    
    private void registerEvents() {
		PluginManager pm = getServer().getPluginManager();
		Priority preventPriority = Event.Priority.High;
		
		pm.registerEvent(Event.Type.CHUNK_UNLOADED, worldListener, preventPriority, this);
		pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, preventPriority, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, preventPriority, this);
		pm.registerEvent(Event.Type.ENTITY_DAMAGED, entityListener, preventPriority, this);
        pm.registerEvent(Event.Type.BLOCK_PLACED, blockListener, preventPriority, this);
        pm.registerEvent(Event.Type.BLOCK_DAMAGED, blockListener, preventPriority, this);
        pm.registerEvent(Event.Type.BLOCK_RIGHTCLICKED, blockListener, preventPriority, this);
        pm.registerEvent(Event.Type.BLOCK_INTERACT, blockListener, preventPriority, this);
    }
    
    /**
	* Detects and sets up the other plugins for use
	*/
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
    
    /**
	* Sends the player a message containing the area info.
	* @param player	The player to send to
	* @param area	The area to show
	*/
    public void showAreaInfo(CommandSender sender, Area area) {
    	// TODO area info
    	sender.sendMessage(ChatColor.YELLOW + area.toString());
    }
    
    /**
	* Checks if the player can modify the area settings.
	* @param event	The area to check
	* @param player	The player to check for
	* @return if they can modify the area
	*/
    public boolean canModify(Area area, Player player) {
    	return (area.isOwner(player.getName()) || checkPermission(player, "ag.create"));
    }
    
    /**
     * Checks the permission nodes via the AreaGuard config and Permissions plugin if available.
     * @param player	The player to check
     * @param nodes		The nodes to test
     * @return			If the player has permission
     */
    @SuppressWarnings("static-access")
	public boolean checkPermission(Player player, String nodes) {
    	if (player.isOp()) return true;
    	if (Config.isCreator(player.getName()) &&
    		nodes.startsWith("ag.create") ||
    		nodes.startsWith("ag.bypass"))
    	if (this.Permissions != null)
    		if (this.Permissions.Security.permission(player, nodes)) return true;
    	return false;
    }
    
    /**
	* Checks if the player can do the event and Cancels it he can't.
	* It also sends	the player the Msgs for that event.
	* @param event	The event to be canceled
	* @param player	The player to check for
	* @param lists	An array of list names for that event
	* @param x		The x location of area to check
	* @param y		The y location of area to check
	* @param z		The z location of area to check
	* @return if the event was canceled
	*/
    public boolean checkEvent(Cancellable event, Player player, String[] lists, int x, int y, int z) {
    	// TODO fix
    	Area area = null;
		return checkEvent(event, player, lists, area);
    }
	
	/**
	* Checks if the player can do the event and Cancels it he can't.
	* It also sends the player the Msgs for that event.
	* @param event	The event to be canceled
	* @param player	The player to check for
	* @param lists	An array of list names for that event
	* @param area	The area to check
	* @return		If the event was canceled
	*/
	public boolean checkEvent(Cancellable event, Player player, String[] lists, Area area) {
		if (area != null) {
			
			// In the event of an error
			if (area instanceof Area) 
				if (checkPermission(player, "ag.bypass.error")) {
					Messager.error(player, "AreaGuard Failed! Bypassing error protection");
					return false;
				} else {
					Messager.error(player, "AreaGuard Failed!");
					event.setCancelled(true);
					return false;
				}
			
			// Check if the player can do the events 
			if (area.playerCan(player.getName(), lists)) {
				
				// Send the allowed messages
				for (String list : lists) {
					String msg = area.getMsg(list).getMsg();
					if (getSession(player).isDebuging("lists")) 
						Messager.debug(player, "Checking " + list + " " 
								+ (area.getList(list).hasValue(player.getName())));
					if (!msg.isEmpty()) Messager.inform(player, msg); }
			} else {
				
				// Bypass or Cancel the event
				if (getSession(player).isBypassing())
					Messager.warn(player, "Bypassing area permissions");
				else event.setCancelled(true);
				
				// Send the not allowed messages
				for (String list : lists) {
					String msg = area.getMsg("no-"+list).getMsg();
					if (getSession(player).isDebuging("lists")) 
						Messager.debug(player, "Checking no-" + list + " " 
								+ (area.getList("no-"+list).hasValue(player.getName())));
					if (!msg.isEmpty()) Messager.warn(player, msg); }
				return false;
			}	
		}
		return true;
	}

	@Override
	public void onLoad() {
		// TODO Auto-generated method stub
	}
}
