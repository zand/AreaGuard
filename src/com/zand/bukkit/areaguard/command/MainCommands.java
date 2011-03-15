package com.zand.bukkit.areaguard.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.util.Java15Compat;

public class MainCommands implements CommandExecutor {
	CommandExecutor admin = new AdminCommands();
	CommandExecutor area = new AreaCommands();

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (args != null && args.length > 0) {
			if (args[0].toLowerCase().startsWith("ver")) {
				// TODO Print version code.
			}
			else if (args[0].equalsIgnoreCase("admin")) {
				admin.onCommand(sender, new Command("admin"), args[0], Java15Compat.Arrays_copyOfRange(args, 1, args.length-1));
			}
			else {
				// TODO Print Main Help
			}
		} else {
			// TODO Print Main Help
		}
		return false;
	}

}
