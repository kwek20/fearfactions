/**
 * 
 */
package net.brord.plugins.fearfactions;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import net.brord.plugins.fearfactions.commands.CmdFactionRemoveWarpCommand;
import net.brord.plugins.fearfactions.commands.CmdFactionSetWarpCommand;
import net.brord.plugins.fearfactions.commands.CmdFactionBossCommand;
import net.brord.plugins.fearfactions.commands.CmdFactionSetBossCommand;
import net.brord.plugins.fearfactions.commands.CmdFactionWhoCommand;
import net.brord.plugins.fearfactions.commands.CommandHandler;
import net.brord.plugins.fearfactions.commands.CmdFactionWarpsCommand;
import net.brord.plugins.fearfactions.deaths.FactionDeathListener;
import net.brord.plugins.fearfactions.privatebeacon.InjectedBlockBeacon;
import net.brord.plugins.fearfactions.privatebeacon.InjectedTileEntityBeacon;
import net.brord.plugins.fearfactions.scoreboard.ScoreBoardListener;
import net.brord.plugins.fearfactions.util.FactionsUtil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.massivecraft.factions.Factions;

/**
 * @author Brord
 *
 */
public class FearFactions extends JavaPlugin {
	
	private static HashMap<String, Class<? extends FearListener> > listenerclasses = new HashMap<String, Class<? extends FearListener>>();
	static {
		listenerclasses.put("scoreboard", ScoreBoardListener.class);
		listenerclasses.put("deaths", FactionDeathListener.class);
	}
	
	
	private static FearFactions plugin;
	public String prefix = ChatColor.MAGIC + "|" + ChatColor.GRAY + "[" + ChatColor.GOLD + "Default" + ChatColor.GRAY + "]" + ChatColor.MAGIC + "| " + ChatColor.RESET;
	private CommandHandler handler;
	
	private List<FearListener> listeners = new LinkedList<FearListener>();
	

	@Override
	public void onLoad() {
		loadBeacons();
	}
	
	@Override
	public void onEnable(){
		super.onEnable();
		plugin = this;
		
		saveDefaultConfig();
		loadConfig();
		checkConfig();
		
		loadListeners();
		
		handler = new CommandHandler(this);
	}
	
	@Override
	public void onDisable(){
		for (FearListener l : listeners){
			l.stop();
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		return handler.handle(sender, command, args);
	}
	
	/**
	 * Loads all the listeners
	 */
	private void loadListeners() {
		ConfigurationSection c = getConfig().getConfigurationSection("features");
		for (String s : c.getKeys(false)){
			if (!c.getBoolean(s, false)) continue;
			
			if (listenerclasses.containsKey(s)){
				FearListener l;
				try {
					add(l = (FearListener) listenerclasses.get(s).getConstructor(this.getClass()).newInstance(this));
					
					if (l instanceof ScoreBoardListener){
						//update scoreboards
						ScoreBoardListener sl = (ScoreBoardListener)l;
						for (Player p : getServer().getOnlinePlayers()){
							sl.update(FactionsUtil.getPlayer(p));
						}
					}
				} catch (Exception e) {
				}
			} else {
				if (s.equalsIgnoreCase("bosshomes")){
					Factions.get().getOuterCmdFactions().addSubCommand(new CmdFactionBossCommand());
					Factions.get().getOuterCmdFactions().addSubCommand(new CmdFactionSetBossCommand());
				} else if (s.equals("warps")){
					Factions.get().getOuterCmdFactions().addSubCommand(new CmdFactionWarpsCommand());
					Factions.get().getOuterCmdFactions().addSubCommand(new CmdFactionSetWarpCommand());
					Factions.get().getOuterCmdFactions().addSubCommand(new CmdFactionRemoveWarpCommand());
				} else if (s.equals("who")){
					Factions.get().getOuterCmdFactions().addSubCommand(new CmdFactionWhoCommand());
				} 
				
				log("Enabled faction " + s);
			}
		}
	}

	/**
	 * Adds a listener
	 * @param listener
	 */
	private void add(FearListener listener) {
		Bukkit.getServer().getPluginManager().registerEvents(listener, this);
		log("Added listener " + listener.getClass().getSimpleName());
		listeners.add(listener);
	}

	/**
	 * 
	 */
	private void loadBeacons() {
		if (!getConfig().getBoolean("features.privatebeacons", false)) return;
		
		try {
			InjectedBlockBeacon.inject();
			InjectedTileEntityBeacon.inject();
			System.out.println("Custom beacon class injected! This error is normal, ignore it ;)");
		} catch (Throwable e) {
			//e.printStackTrace();
			System.out.println("Could not load the private beacons!");
		}
	}
	
	/**
	 * Checks the config for possible mistakes or needed updates.
	 */
	public void checkConfig(){
		YamlConfiguration config = YamlConfiguration.loadConfiguration(getResource("config.yml"));
		YamlConfiguration fconfig = YamlConfiguration.loadConfiguration(new File(getDataFolder() + File.separator + "config.yml"));
		for (String key : config.getKeys(true)){
			if (!fconfig.contains(key)){
				getConfig().set(key, config.get(key));
				log("Added the config value: " + key);
				saveConfig();
			}
		}
	}
	
	public void loadConfig(){
		prefix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("prefix"));
	}
	
	/**
	 * Logs to the console, attempts to use colors
	 * @param message The message to log
	 */
	public void log(String message) {
		try{
			Bukkit.getServer().getConsoleSender().sendMessage(prefix + message);
		} catch (Exception e){
			System.out.println(ChatColor.stripColor(prefix) + message);
		}
	}
	
	/**
	 * Sends the prefix used in this plugin
	 * @return a string containing the prefix
	 */
	public String getprefix() {
		return prefix;
	}
	
	/**
	 * Sends the main class instance
	 * @return The {@link JavaPlugin} instance of this plugin
	 */
	public static FearFactions getInstance(){
		return plugin;
	}
	
	/**
	 * @param sender
	 * @param string
	 */
	public void msg(CommandSender sender, String message) {
		if (sender instanceof Player && sender != null){
			((Player)sender).sendMessage(prefix + message);
		} else {
			log(message);
		}
	}

	/**
	 * @param string
	 * @return
	 */
	public String getString(String string) {
		return ChatColor.translateAlternateColorCodes('&', getConfig().getString(string, ""));
	}
}
