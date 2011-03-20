package com.zand.bukkit.areaguard.command;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CommandHelp {
	static public ChatColor headerColor = ChatColor.DARK_PURPLE;
	static public ChatColor cmdColor = ChatColor.WHITE;
	static public ChatColor sepColor = ChatColor.GOLD;
	static public ChatColor descColor = ChatColor.YELLOW;
	static public String program = "";
	final private String section;
	private ArrayList<String[]> commands = new ArrayList<String[]>();
	
	public CommandHelp(String section) {
		this.section = section;
		add("help", "", "Shows this", "");
	}
	
	public void add(String cmd, String use, String desc, String perm) {
		commands.add(new String[] {cmd, use, desc, perm});
	}
	
	public void show(CommandSender sender, String prefix) {
		sender.sendMessage(ChatColor.DARK_PURPLE + program + " " + section + " Help");
		for (String[] command : commands) {
			sender.sendMessage(cmdColor + prefix + " " + command[0] + " " + command[1] + sepColor + " -" + descColor + " " + command[2]);
		}
	}
	
}
