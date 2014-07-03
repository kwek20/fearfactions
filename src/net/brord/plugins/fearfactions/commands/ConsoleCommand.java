/**
 * 
 */
package net.brord.plugins.fearfactions.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * Abstract class meaning the command is for console only
 * @author Brord
 *
 */
public abstract class ConsoleCommand extends CommandBase {

	/**
	 * @param sender
	 * @param command
	 * @param args
	 * @throws NoConsoleException if the sender is not the console
	 */
	public ConsoleCommand(CommandSender sender, Command command, String[] args) throws NoConsoleException {
		super(sender, command, args);
		if (!(sender instanceof ConsoleCommandSender)){
			throw new NoConsoleException();
		}
	}
	
	/**
	 * Returns the player which used this command
	 * @return the {@link Player}
	 */
	public Player getPlayer(){
		return (Player) sender;
	}

}
