package net.brord.plugins.fearfactions.scoreboard;

import net.brord.plugins.fearfactions.util.FactionsUtil;
import net.brord.plugins.fearfactions.FearFactions;
import net.brord.plugins.fearfactions.FearListener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.factions.event.FactionsEventCreate;
import com.massivecraft.factions.event.FactionsEventMembershipChange;


/**
 * The listener for the plugin
 * @author Brord
 *
 */
public class ScoreBoardListener extends FearListener implements Listener{

	private FearFactions plugin;
	
	public ScoreBoardListener(FearFactions plugin) {
		this.plugin = plugin;
	}
	
	/**
	 * Example listener function
	 * @param e
	 */
	@EventHandler
	public void login(PlayerLoginEvent e){
		final UPlayer p = FactionsUtil.getPlayer(e.getPlayer());
		if (p.getFaction().getOnlinePlayers().size() > 0){
			new BukkitRunnable(){
				@Override
				public void run() {
					if (p.getFaction().getOnlinePlayers().get(0) == p.getPlayer()){
						p.getPlayer().setScoreboard(p.getFaction().getOnlinePlayers().get(1).getScoreboard());
					} else {
						p.getPlayer().setScoreboard(p.getFaction().getOnlinePlayers().get(0).getScoreboard());
					}
				}
			}.runTask(plugin);
	}
			
		update(p);
	}

	@EventHandler
	public void kick(PlayerKickEvent e){
		update(e.getPlayer());
	}
	
	@EventHandler
	public void quit(PlayerQuitEvent e){
		update(e.getPlayer());
	}
	
	/**
	 * Check to update the old player his {@link Faction}
	 * @param e
	 */
	@EventHandler
	public void create(final FactionsEventCreate e){
		final Faction f = e.getUSender().getFaction();
		new BukkitRunnable(){

			@Override
			public void run() {
				if (f.attached() && f.getOnlinePlayers().size() > 0){
					update(f.getOnlinePlayers().get(0));
				}
			}
		}.runTask(plugin);
	}
	
	@EventHandler
	public void membership(final FactionsEventMembershipChange e){
		//clear first, so new scoreboard add will be detected
		if (e.getUPlayer().getPlayer() != null){
//			if (Bukkit.getScoreboardManager().getMainScoreboard().getObjective("FactionsStats") != null){
//				Bukkit.getScoreboardManager().getMainScoreboard().clearSlot(DisplaySlot.SIDEBAR);
//			}
			e.getUPlayer().getPlayer().setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
		}

		update(e.getUPlayer());
	}
	
	private void update(final Player p){
		update(FactionsUtil.getPlayer(p));
	}
	
	public void update(final UPlayer up){
		if (up == null) return;
		
		new BukkitRunnable(){
			@Override
			public void run() {
				if (up.getFaction().getOnlinePlayers().isEmpty()) return;
				
				Scoreboard board;
				if (up.getFaction().getOnlinePlayers().size() == 1 && up.getPlayer() != null){
					//new player joined, only one on
					board = getBoard(up.getFaction());
					up.getPlayer().setScoreboard(board);
				} else if (up.getFaction().getOnlinePlayers().size() > 0 && up.getPlayer() == null){
					//hes not on, but others are
					board = up.getFaction().getOnlinePlayers().get(0).getScoreboard();
				} else {
					//hes not the only one on
					if (up.getFaction().getOnlinePlayers().get(0).equals(up.getPlayer())){
						board = up.getFaction().getOnlinePlayers().get(1).getScoreboard();
					} else {
						board = up.getFaction().getOnlinePlayers().get(0).getScoreboard();
					}
					
					if (board.getObjective("FactionsStats") == null || board.getObjective(DisplaySlot.SIDEBAR) == null){
						board = getBoard(up.getFaction());
						up.getPlayer().setScoreboard(board);
					} else if (up.getPlayer().getScoreboard().getObjective("FactionsStats") == null || up.getPlayer().getScoreboard().getObjective(DisplaySlot.SIDEBAR) == null){
						up.getPlayer().setScoreboard(board);
					}
				}
				int total = up.getFaction().getUPlayers().size();
				int on = up.getFaction().getOnlinePlayers().size();
				int off = total - on;
				
				Objective o = board.getObjective(DisplaySlot.SIDEBAR);
				Score s;
				s = o.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN + "Online: "));
				s.setScore(on);
				
				s = o.getScore(Bukkit.getOfflinePlayer(ChatColor.DARK_RED + "Offline: "));
				if (off == 0){
					s.setScore(1);
				}
				s.setScore(off);
			}
			
			private Scoreboard getBoard(Faction f) {
				Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
				Objective o = board.registerNewObjective("FactionsStats", "stats");
				
				o.setDisplaySlot(DisplaySlot.SIDEBAR);
				o.setDisplayName(ChatColor.GREEN + f.getName());
				return board;
			}
		}.runTaskLater(plugin, 5);
	}
	
	/**
	 * 
	 */
	public void disable() {
		// TODO Auto-generated method stub
		
	}

}
