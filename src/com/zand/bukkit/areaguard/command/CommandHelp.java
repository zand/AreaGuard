package com.zand.bukkit.areaguard.command;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.zand.bukkit.areaguard.AreaGuard;

public class CommandHelp {
	private final AreaGuard plugin;
	static public ChatColor headerColor = ChatColor.DARK_PURPLE;
	static public ChatColor cmdColor = ChatColor.GRAY;
	static public ChatColor minColor = ChatColor.WHITE;
	static public ChatColor useColor = minColor;
	static public ChatColor sepColor = ChatColor.GOLD;
	static public ChatColor descColor = ChatColor.YELLOW;
	static public String program = "";
	final private String section;
	
	private class CommandHelpLine {
		final AreaGuard plugin;
		String cmd;
		String use;
		String subfix = "";
		String desc;
		String perm;
		
		CommandHelpLine(AreaGuard plugin, String cmd, int min, String use, String desc, String perm) {
			this.plugin = plugin;
			if (min > 0) {
				this.cmd = cmd.substring(0, min);
				this.subfix = cmd.substring(min);
			} else this.cmd = cmd;
			this.use = use;
			this.desc = desc;
			this.perm = perm;
		}
		
		boolean canUse(CommandSender sender) {
			if (perm == null || perm.isEmpty()) return true;
			
			if (sender instanceof Player) {
				Player player = (Player)sender;
				return plugin.Security.permission(player, perm);
			}
			return true;
		}
		
		void show(CommandSender sender, String prefix) {
			sender.sendMessage(
					prefix + " "
					+ CommandHelp.minColor + cmd
					+ CommandHelp.cmdColor + subfix +  " " 
					+ CommandHelp.useColor + use
					+ CommandHelp.sepColor + " -" 
					+ CommandHelp.descColor + " " + desc);
		}
	}
	
	private ArrayList<CommandHelpLine> commands = new ArrayList<CommandHelpLine>();
	
	public CommandHelp(AreaGuard plugin, String section) {
		this.plugin = plugin;
		this.section = section;
		add("help", 0, "", "Shows this", "");
	}
	
	public void add(String cmd, int min, String use, String desc, String perm) {
		commands.add(new CommandHelpLine(plugin, cmd, min, use, desc, perm));
	}
	
	public void show(CommandSender sender, String prefix) {
		sender.sendMessage(ChatColor.DARK_PURPLE + program + " " + section + " Help");
		for (CommandHelpLine command : commands) {
			if (command.canUse(sender))
				command.show(sender, prefix);
		}
	}
	
}
