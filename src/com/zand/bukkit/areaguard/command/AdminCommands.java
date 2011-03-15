package com.zand.bukkit.areaguard.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AdminCommands implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (args != null && args.length > 0) {
			if (args[0].toLowerCase().startsWith("ver")) {
				// TODO Print version code.
			} else {
				// TODO Print Help
			}
		} else {
			// TODO Print Help
		}
		return false;
	}

}
