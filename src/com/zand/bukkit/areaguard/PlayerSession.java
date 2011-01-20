package com.zand.bukkit.areaguard;

import org.bukkit.entity.Player;
import com.zand.areaguard.Session;

public class PlayerSession extends Session {
	public final Player player;
	public int lastIn = -2;
	PlayerSession(Player player) {
		this.player = player;
		name = player.getName();
	}
}
