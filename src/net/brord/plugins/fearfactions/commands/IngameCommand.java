/**
 * 
 */
package net.brord.plugins.fearfactions.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Abstract class meaning the command is for ingame players only
 * @author Brord
 *
 */
public abstract class IngameCommand extends CommandBase {

	/**
	 * @param sender
	 * @param command
	 * @param args
	 * @throws NotIngameException if the sender is not ingame
	 */
	public IngameCommand(CommandSender sender, Command command, String[] args) throws NotIngameException {
		super(sender, command, args);
		if (!(sender instanceof Player) || sender == null){
			throw new NotIngameException();
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
