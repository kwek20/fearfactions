/**
 * 
 */
package net.brord.plugins.fearfactions.commands;

import net.brord.plugins.fearfactions.util.FactionsUtil;

import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.cmd.req.ReqFactionsEnabled;
import com.massivecraft.factions.cmd.req.ReqHasFaction;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.UConf;
import com.massivecraft.factions.event.FactionsEventHomeChange;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;
import com.massivecraft.mcore.cmd.req.ReqIsPlayer;
import com.massivecraft.mcore.ps.PS;

/**
 * @author Brord
 *
 */
public class CmdFactionSetBossCommand extends FCommand{
	 // -------------------------------------------- //
    // CONSTRUCT
    // -------------------------------------------- //
    
    public CmdFactionSetBossCommand()
    {
            // Aliases
            this.addAliases("setbosshome", "setboshome");

            // Args
            this.addOptionalArg("faction", "you");

            // Requirements
            this.addRequirements(ReqFactionsEnabled.get());
            this.addRequirements(ReqIsPlayer.get());
            this.addRequirements(ReqHasFaction.get());
            
            this.addRequirements(ReqHasPerm.get("fearfactions.boss"));
    }

    // -------------------------------------------- //
    // OVERRIDE
    // -------------------------------------------- //
    
    @Override
    public void perform()
    {
            // Args
            Faction faction = this.arg(0, ARFaction.get(usenderFaction), usenderFaction);
            if (faction == null) return;
            
            PS newHome = PS.valueOf(me.getLocation());
            
            // Validate
            if ( ! UConf.get(faction).homesEnabled)
            {
                    usender.msg("<b>Sorry, boss homes are disabled on this server.");
                    return;
            }
            
            // FPerm
            //if ( ! FPerm.SETHOME.has(usender, faction, true)) return;
            
            // Verify
            if (!usender.isUsingAdminMode() && !faction.isValidHome(newHome))
            {
                    usender.msg("<b>Sorry, your faction home can only be set inside your own claimed territory.");
                    return;
            }
            
            // Event
            FactionsEventHomeChange event = new FactionsEventHomeChange(sender, faction, newHome);
            event.run();
            if (event.isCancelled()) return;
            newHome = event.getNewHome();

            // Apply
            FactionsUtil.setBossHome(faction, newHome);
            
            // Inform
            faction.msg("%s<i> set the boss home for your faction. Bosses can now use: /f bosshome", usender.describeTo(usenderFaction, true));
            if (faction != usenderFaction){
                    usender.msg("<b>You have set the home for the "+faction.getName(usender)+"<i> faction.");
            }
    }
}
