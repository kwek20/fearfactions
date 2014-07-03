/**
 * 
 */
package net.brord.plugins.fearfactions.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Brord
 *
 */
public class ConsoleCmd extends ConsoleCommand {

	/**
	 * 
	 * @param sender
	 * @param command
	 * @param args
	 * @throws NoConsoleException
	 */
	public ConsoleCmd(CommandSender sender, Command command, String[] args) throws NoConsoleException {
		super(sender, command, args);
	}

	@Override
	public boolean handle() {
		if (args.length < 3){
			return false;
		}
		
		Player p = Bukkit.getServer().getPlayer(args[0]);
		if (p == null){
			return false;
		}
		
		int amount;
		try {
			amount = Integer.parseInt(args[1]);
		} catch (NumberFormatException e){
			return false;
		}
		
		return true;
	}

}
