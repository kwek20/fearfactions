/**
 * 
 */
package net.brord.plugins.fearfactions.commands;

import org.bukkit.Bukkit;

import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.cmd.arg.ARUPlayer;
import com.massivecraft.factions.cmd.req.ReqFactionsEnabled;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;

/**
 * @author Brord
 *
 */
public class CmdFactionWhoCommand extends FCommand {

	
	/**
	 * 
	 */
	public CmdFactionWhoCommand() {
		this.addAliases("who");
		
		this.addRequirements(ReqHasPerm.get("fearfactions.who"));
		this.addRequirements(ReqFactionsEnabled.get());
		
		this.addOptionalArg("name", "you");
	}

	@Override
	public void perform() {
		Faction f = this.arg(0, ARFaction.get(usenderFaction), usenderFaction);
		UPlayer p = this.arg(0, ARUPlayer.getStartAny(sender), usender);
		boolean displayed = false;
		if (!(f.getComparisonName().equals("wilderness") || f.getComparisonName().equals("safezone") || f.getComparisonName().equals("warzone"))){
			if (f != null && !f.isDefault() && f.attached())
				displayed = true;
				Bukkit.getServer().dispatchCommand(sender, "f f " + f.getName());
		}
		
		if (p != null && !p.isDefault() && p.attached()){
			displayed = true;
			Bukkit.getServer().dispatchCommand(sender, "f p " + p.getName());
		}
		
		if (!displayed){
			usender.msg("<b>Sorry, Could not find anything for name: " + arg(0));
		}
	}
}
