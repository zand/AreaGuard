package com.zand.bukkit.areaguard;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.zand.areaguard.Config;
import com.zand.bukkit.util.PermissionsWrapper;

public class AreaGuardPermissionsWrapper extends PermissionsWrapper {

	public AreaGuardPermissionsWrapper(JavaPlugin plugin) {
		super(plugin);
	}
	
	@Override
	protected int permissionIntegerHard(String world, String player, String nodes) { 
		return -1;
	}
	
	@Override
	protected boolean permissionHard(Player player, String nodes) { 
		if (player.isOp()) return true;
		String allow[] = {
				"areaguard.area.create",
				"areaguard.area.modify.created",
				"areaguard.area.modify.owned",
				"areaguard.cuboid.create",
				"areaguard.cuboid.modify.created",
				"areaguard.cuboid.modify.owned",
		};
		for (String s : allow)
			if (nodes.startsWith(s))
				return true;
		
		if (Config.isCreator(player.getName())) {
			allow = new String[] {
					"areaguard.bypass",
					"areaguard.area.",
					"areaguard.cuboid.",
			};
			for (String s : allow)
				if (nodes.startsWith(s))
					return true;
		}
		
		return false;
	}
}
