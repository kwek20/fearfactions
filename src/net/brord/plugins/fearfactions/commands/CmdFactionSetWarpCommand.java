/**
 * 
 */
package net.brord.plugins.fearfactions.commands;

import net.brord.plugins.fearfactions.FearFactions;
import net.brord.plugins.fearfactions.util.FactionsUtil;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.cmd.req.ReqFactionsEnabled;
import com.massivecraft.factions.cmd.req.ReqHasFaction;
import com.massivecraft.factions.entity.UConf;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;
import com.massivecraft.mcore.cmd.req.ReqIsPlayer;
import com.massivecraft.mcore.ps.PS;

/**
 * @author Brord
 *
 */
public class CmdFactionSetWarpCommand extends FCommand{
	/**
	 * 
	 */
	public CmdFactionSetWarpCommand() {
		 // Aliases
        this.addAliases("setwarp");

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
        PS newWarp = PS.valueOf(me.getLocation());
        
        // Validate
        if ( ! UConf.get(usenderFaction).homesEnabled){
                usender.msg("<b>Sorry, warps are disabled on this server.");
                return;
        }
        
        int numwarps = FearFactions.getInstance().getConfig().getInt("numwarps", 1);
        if (FactionsUtil.getNumWarps(usenderFaction) >= numwarps && !usender.getPlayer().hasPermission("fearfactions.warps.unlimited")){
        	usender.msg("<b>Sorry, you are not allowed to have more than " + numwarps + " warps.");
            return;
        }
        
        String warpname = args.get(0);
        if (warpname.equalsIgnoreCase("")){
        	usender.msg("<b>Please supply a name with atleast 1 character");
            return;
        }
        
        // Verify
        if (!usender.isUsingAdminMode() && !usenderFaction.isValidHome(newWarp))
        {
                usender.msg("<b>Sorry, warps can only be set inside your own claimed territory.");
                return;
        }

        // Apply
        FactionsUtil.setWarp(usenderFaction, warpname, newWarp.asBukkitLocation());
        
        // Inform
        usenderFaction.msg("%s<i> added warp %s for your faction. You can now use: /f warp %s", usender.describeTo(usenderFaction, true), warpname, warpname);
	}
}
