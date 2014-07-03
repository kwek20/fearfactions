package net.brord.plugins.fearfactions.deaths;

import java.util.List;

import net.brord.plugins.fearfactions.util.FactionsUtil;
import net.brord.plugins.fearfactions.FearFactions;
import net.brord.plugins.fearfactions.FearListener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.massivecraft.factions.entity.UPlayer;


/**
 * The listener for the plugin
 * @author Brord
 *
 */
public class FactionDeathListener extends FearListener implements Listener {

	//private FearFactions plugin;
	
	public FactionDeathListener(FearFactions plugin) {
		//this.plugin = plugin;
	}
	
	/**
	 * Example listener function
	 * @param e
	 */
	@EventHandler
	public void login(PlayerDeathEvent e){
		UPlayer p  = FactionsUtil.getPlayer(e.getEntity());
		
		//no faction? remove the message
		if (!p.hasFaction()) {
			e.setDeathMessage(null);
			return;
		}
		
		List<Player> receivers = p.getFaction().getOnlinePlayers();
		
		if (e.getEntity().getKiller() != null){
			receivers.addAll(FactionsUtil.getPlayerFaction(e.getEntity().getKiller()).getOnlinePlayers());
		}
		
		for (Player fp : receivers){
			fp.sendMessage(e.getDeathMessage());
		}
		
		e.setDeathMessage(null);
	}
}
