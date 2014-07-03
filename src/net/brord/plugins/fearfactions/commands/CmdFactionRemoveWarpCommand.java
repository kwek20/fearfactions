/**
 * 
 */
package net.brord.plugins.fearfactions.commands;

import net.brord.plugins.fearfactions.util.FactionsUtil;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.cmd.req.ReqFactionsEnabled;
import com.massivecraft.factions.cmd.req.ReqHasFaction;
import com.massivecraft.factions.entity.UConf;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;
import com.massivecraft.mcore.cmd.req.ReqIsPlayer;

/**
 * @author Brord
 *
 */
public class CmdFactionRemoveWarpCommand extends FCommand{
	/**
	 * 
	 */
	public CmdFactionRemoveWarpCommand() {
		 // Aliases
        this.addAliases("delwarp", "deletewarp", "remwarp", "removewarp");

        // Args
        this.addRequiredArg("name");
        
        // Requirements
        this.addRequirements(ReqFactionsEnabled.get());
        this.addRequirements(ReqIsPlayer.get());
        this.addRequirements(ReqHasFaction.get());
        
        this.addRequirements(ReqHasPerm.get("fearfactions.warps"));
        this.addRequirements(ReqHasPerm.get(Perm.OFFICER.node));
	}
	

	@Override
	public void perform() {
        // Validate
        if ( ! UConf.get(usenderFaction).homesEnabled)
        {
                usender.msg("<b>Sorry, warps are disabled on this server.");
                return;
        }
        
        String warpname = args.get(0);
        if (warpname.equalsIgnoreCase("")){
        	usender.msg("<b>Please supply a name with atleast 1 character");
            return;
        }

        if (!FactionsUtil.hasWarp(usenderFaction, warpname)){
        	usender.msg("<b>This warp does not exist!");
            return;
        }
        
        FactionsUtil.removeWarp(usenderFaction, warpname);
        
        // Inform
        usenderFaction.msg("%s<i> removed warp %s.", usender.describeTo(usenderFaction, true), warpname);
	}
}
