package com.zand.bukkit.util;

import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijikokun.bukkit.Permissions.Permissions;

public class PermissionsWrapper {
	final public JavaPlugin plugin;
	public Permissions Permissions = null;
	private static Logger log = Logger.getLogger("Minecraft");
	
	public PermissionsWrapper(JavaPlugin plugin) {
		this.plugin = plugin;
		checkPlugin();
	}
	
	public void checkPlugin() {
		Plugin test = plugin.getServer().getPluginManager().getPlugin("Permissions");
    	if (this.Permissions == null) {
    		if(test != null) {
    			this.Permissions = (Permissions)test;
    	    	log.info("[" + plugin.getClass().getName() + "] Found Permissions plugin. Using it.");
    	    }
    	}
	}
	
	public boolean pluginFound() {
		return (this.Permissions != null);
	}
	
	@SuppressWarnings("static-access")
	public boolean permission(Player player, String nodes) {
		if (pluginFound())
    		return this.Permissions.Security.permission(player, nodes);
		return permissionHard(player, nodes);
	}
	
	@SuppressWarnings("static-access")
	public int getPermissionInteger(String world, String player, String nodes) {
		if (pluginFound())
			return this.Permissions.Security.getPermissionInteger(world, player, nodes);
		return permissionIntegerHard(world, player, nodes);
	}

	protected int permissionIntegerHard(String world, String player, String nodes) { return 0; }
	protected boolean permissionHard(Player player, String nodes) { return false; }
}
