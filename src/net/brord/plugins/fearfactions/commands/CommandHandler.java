package net.brord.plugins.fearfactions.commands;

import java.util.Arrays;

import net.brord.plugins.fearfactions.FearFactions;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;


public class CommandHandler {

	private FearFactions plugin;
	
	public CommandHandler(FearFactions plugin) {
		this.plugin = plugin;
	}
	
	public boolean handle(CommandSender sender, Command command, String[] args){
		
		CommandBase cmd = null;
		String label = command.getLabel();
		
		if (label.equalsIgnoreCase("fearfactions")){
			if (args.length > 0){
				label = args[0];
				try {
					command = Bukkit.getServer().getPluginCommand(label);
				} catch(Exception e){
					plugin.log("An error occured while handling command " + label + "(" + command.getName() + ") Exception:" + e.getClass().toString() + " " + e.getMessage());
					//whoups command didnt exist!
				}
				
				if (args.length > 1){
					args = Arrays.copyOfRange(args, 1, args.length);
				} else {
					args = new String[0];
				}
			} else {
				sender.sendMessage(ChatColor.GRAY + "---------------" + plugin.prefix + ChatColor.GRAY + "---------------");
				sender.sendMessage(ChatColor.GOLD + "Welcome to " + ChatColor.DARK_RED + "FearFactions!" + ChatColor.DARK_GREEN + "!");
				sender.sendMessage(ChatColor.GOLD + plugin.getDescription().getDescription());
				sender.sendMessage(ChatColor.GOLD + "Version: " + plugin.getDescription().getVersion());
				sender.sendMessage(ChatColor.GOLD + "Author: " + Arrays.toString(plugin.getDescription().getAuthors().toArray()).replace("[", "").replace("]", ""));
				return true;
			}
		}
		
		try {
			if (label.equalsIgnoreCase("help")){
				if (args.length > 0 && plugin.getConfig().contains("help." + args[0])){
					String[] messages = plugin.getConfig().getString("help." + args[0], "No description given!").split("\n");
					for (int i=0; i<messages.length; i++){
						String m = messages[i];
						m = ChatColor.translateAlternateColorCodes('&', m);
						m = m.replaceAll("<prefix>", plugin.prefix);
						m = m.replaceAll("<name>", sender.getName());
					}
					sender.sendMessage(messages);
				} else {
					sender.sendMessage(ChatColor.LIGHT_PURPLE + "Options: " + Arrays.toString(plugin.getConfig().getConfigurationSection("help").getKeys(false).toArray()).replace("[", "").replace("]", ""));
				}
				return true;
			} else if (label.equalsIgnoreCase("playercmd")){
				cmd = new PlayerCmd(sender, command, args);
			} else if (label.equalsIgnoreCase("consolecmd")){
				cmd = new ConsoleCmd(sender, command, args);
			}
		} catch (NoConsoleException e){
			plugin.msg(sender, "You must be the console to execute this command!");
			return true;
		} catch (NotIngameException e){
			plugin.msg(sender, "You must be ingame to execute this command!");
			return true;
		}
		
		if (cmd != null){
			if (!cmd.handle()){
				plugin.msg(sender, command.getUsage());
			}
			return true;
		} else {
			return false;
		}
	}

}
