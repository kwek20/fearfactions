/**
 * 
 */
package net.brord.plugins.fearfactions.commands;

import net.brord.plugins.fearfactions.util.FactionsUtil;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.massivecraft.factions.FFlag;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.cmd.req.ReqFactionsEnabled;
import com.massivecraft.factions.cmd.req.ReqHasFaction;
import com.massivecraft.factions.entity.BoardColls;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.UConf;
import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.factions.event.FactionsEventHomeTeleport;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;
import com.massivecraft.mcore.cmd.req.ReqIsPlayer;
import com.massivecraft.mcore.mixin.Mixin;
import com.massivecraft.mcore.mixin.TeleporterException;
import com.massivecraft.mcore.ps.PS;

/**
 * @author Brord
 *
 */
public class CmdFactionBossCommand extends FCommand {

	
	/**
	 * 
	 */
	public CmdFactionBossCommand() {
		this.addAliases("bosshome", "boshome");
		
		this.addRequirements(ReqHasPerm.get("fearfactions.boss"));
		this.addRequirements(ReqIsPlayer.get());
		this.addRequirements(ReqHasFaction.get());
		this.addRequirements(ReqFactionsEnabled.get());
	}

	@Override
	public void perform() {
		UConf uconf = UConf.get(sender);
        
        // TODO: Hide this command on help also.
        if ( ! uconf.homesEnabled)
        {
                usender.msg("<b>Sorry, boss homes are disabled on this server.");
                return;
        }

        if ( ! uconf.homesTeleportCommandEnabled)
        {
                usender.msg("<b>Sorry, the ability to teleport to boss homes is disabled on this server.");
                return;
        }
        
        if ( ! hasBossHome())
        {
                usender.msg("<b>Your faction does not have a boss home. Set one with /f setbosshome");
                return;
        }
        
        if ( ! uconf.homesTeleportAllowedFromEnemyTerritory && usender.isInEnemyTerritory())
        {
                usender.msg("<b>You cannot teleport to your boss home while in the territory of an enemy faction.");
                return;
        }
        
        if (!uconf.homesTeleportAllowedFromDifferentWorld && !me.getWorld().getName().equalsIgnoreCase(usenderFaction.getHome().getWorld()))
        {
                usender.msg("<b>You cannot teleport to your boss home while in a different world.");
                return;
        }
        
        
        Faction faction = BoardColls.get().getFactionAt(PS.valueOf(me));
        Location loc = me.getLocation().clone();
        
        // if player is not in a safe zone or their own faction territory, only allow teleport if no enemies are nearby
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
        )
        {
                World w = loc.getWorld();
                double x = loc.getX();
                double y = loc.getY();
                double z = loc.getZ();

                for (Player p : me.getServer().getOnlinePlayers())
                {
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

                        usender.msg("<b>You cannot teleport to your faction home while an enemy is within " + uconf.homesTeleportAllowedEnemyDistance + " blocks of you.");
                        return;
                }
        }

        // Event
        FactionsEventHomeTeleport event = new FactionsEventHomeTeleport(sender);
        event.run();
        if (event.isCancelled()) return;
        
        // Apply
        try
        {
                Mixin.teleport(me, PS.valueOf(getBossHome()), "your boss home", sender);
        }
        catch (TeleporterException e)
        {
                me.sendMessage(e.getMessage());
        }
	}
	
	private boolean hasBossHome(){
		return FactionsUtil.hasBossHome(usenderFaction);
	}
	
	private Location getBossHome(){
		return FactionsUtil.getBossHome(usenderFaction);
	}
}
