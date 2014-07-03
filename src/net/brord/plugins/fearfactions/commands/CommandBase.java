package net.brord.plugins.fearfactions.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public abstract class CommandBase {

	CommandSender sender;
	Command cmd;
	String[] args;
	
	
	public CommandBase(CommandSender sender, Command command, String[] args) {
		this.sender = sender;
		this.cmd = command;
		this.args = args;
	}
	
	public abstract boolean handle();
	

}
