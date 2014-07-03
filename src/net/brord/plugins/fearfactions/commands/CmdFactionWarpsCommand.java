/**
 * 
 */
package net.brord.plugins.fearfactions.commands;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import net.brord.plugins.fearfactions.util.FactionsUtil;

import com.massivecraft.factions.FFlag;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.cmd.req.ReqFactionsEnabled;
import com.massivecraft.factions.cmd.req.ReqHasFaction;
import com.massivecraft.factions.entity.BoardColls;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.UConf;
import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;
import com.massivecraft.mcore.cmd.req.ReqIsPlayer;
import com.massivecraft.mcore.mixin.Mixin;
import com.massivecraft.mcore.mixin.TeleporterException;
import com.massivecraft.mcore.ps.PS;

/**
 * @author Brord
 *
 */
public class CmdFactionWarpsCommand extends FCommand {
	/**
	 * 
	 */
	public CmdFactionWarpsCommand() {
		 // Aliases
        this.addAliases("warp");

        // Args
        this.addOptionalArg("name", "");

        // Requirements
        this.addRequirements(ReqFactionsEnabled.get());
        this.addRequirements(ReqIsPlayer.get());
        this.addRequirements(ReqHasFaction.get());
        
        this.addRequirements(ReqHasPerm.get("fearfactions.warps"));
        //this.addRequirements(ReqHasPerm.get(Perm.OFFICER.node));
	}
	
	 @Override
	    public void perform()
	    {
	            UConf uconf = UConf.get(sender);
	            
	            // Validate
	            if ( ! UConf.get(usenderFaction).homesEnabled){
	                    usender.msg("<b>Sorry, warps are disabled on this server.");
	                    return;
	            }
	            
	            if (args.isEmpty() || args.get(0).equals("")){
	            	//list warps
	            	usender.msg("<b>Optional warps: ");
	            	usender.msg(FactionsUtil.getWarps(usenderFaction));
	            	return;
	            }
	            
	            String warp = args.get(0);
	            
	            if (!FactionsUtil.hasWarp(usenderFaction, warp)){
	            	 usender.msg("<b>Sorry, warp %s does not exist.", warp);
	            	 return;
	            }
	            
	            Faction faction = BoardColls.get().getFactionAt(PS.valueOf(me));
	            Location loc = me.getLocation().clone();
	            
	            if
	            (
	                            uconf.homesTeleportAllowedEnemyDistance > 0
	                    &&
	                    faction.getFlag(FFlag.PVP)
	                    &&
	                    (
	                            ! usender.isInOwnTerritory()
	                            ||
	                            (
	                                    usender.isInOwnTerritory()
	                                    &&
	                                    ! uconf.homesTeleportIgnoreEnemiesIfInOwnTerritory
	                            )
	                    )
	            ){
	                    World w = loc.getWorld();
	                    double x = loc.getX();
	                    double y = loc.getY();
	                    double z = loc.getZ();

	                    for (Player p : me.getServer().getOnlinePlayers()){
	                            if (p == null || !p.isOnline() || p.isDead() || p == me || p.getWorld() != w)
	                                    continue;

	                            UPlayer fp = UPlayer.get(p);
	                            if (usender.getRelationTo(fp) != Rel.ENEMY)
	                                    continue;

	                            Location l = p.getLocation();
	                            double dx = Math.abs(x - l.getX());
	                            double dy = Math.abs(y - l.getY());
	                            double dz = Math.abs(z - l.getZ());
	                            double max = uconf.homesTeleportAllowedEnemyDistance;

	                            // box-shaped distance check
	                            if (dx > max || dy > max || dz > max)
	                                    continue;

	                            usender.msg("<b>You cannot warp while an enemy is within " + uconf.homesTeleportAllowedEnemyDistance + " blocks of you.");
	                            return;
	                    }
	            }

	            try {
	                    Mixin.teleport(me, PS.valueOf(FactionsUtil.getWarp(usenderFaction, warp)), "warp " + warp + ".", sender);
	            } catch (TeleporterException e) {
	                    me.sendMessage(e.getMessage());
	            }
	    }
}
