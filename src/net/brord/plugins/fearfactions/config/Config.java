/**
 * 
 */
package net.brord.plugins.fearfactions.config;

import java.io.File;
import java.io.IOException;

import net.brord.plugins.fearfactions.FearFactions;

import org.bukkit.configuration.file.YamlConfiguration;

/**
 * @author Brord
 *
 */
public final class Config {

	public static YamlConfiguration getConfig(String name){
		if (!name.endsWith(".yml")) name += ".yml";
		return YamlConfiguration.loadConfiguration(new File(getDir() + name));
		
	}
	
	public static boolean saveConfig(YamlConfiguration config, String name){
		if (!name.endsWith(".yml")) name += ".yml";
		try {
			config.save(getDir() + name);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	private static String getDir(){
		return FearFactions.getInstance().getDataFolder() + File.separator;
	}
}
