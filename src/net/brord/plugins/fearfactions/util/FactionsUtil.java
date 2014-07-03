/**
 * 
 */
package net.brord.plugins.fearfactions.util;

import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.mcore.ps.PS;
import com.massivecraft.mcore.xlib.gson.JsonElement;
import com.massivecraft.mcore.xlib.gson.JsonObject;

/**
 * @author Brord
 *
 */
public class FactionsUtil {
	
	private static final String BOSSHOME = "bosshome";
	private static final String WARP = "warps";
	
	public static UPlayer getPlayer(Player p){
		return UPlayer.get(p);
	}
	
	public static Faction getPlayerFaction(Player p){
		UPlayer up = UPlayer.get(p);
		if (up == null) return null;
		
		return up.getFaction();
	}
	
	public static boolean hasFaction(Player p){
		UPlayer up = UPlayer.get(p);
		if (up == null) return false;
		
		return up.hasFaction();
	}
	
	public static boolean hasBossHome(Faction f){
		JsonObject data = f.getCustomData();
		return (data != null && data.get(BOSSHOME) != null);
	}
	
	public static void setBossHome(Faction f, PS newHome){
		JsonObject data = f.getCustomData();
		if (data == null) data = new JsonObject();
		
		data.add(BOSSHOME, locToJson(newHome));
		
		f.setCustomData(data);
	}
	
	public static Location getBossHome(Faction f){
		if (!hasBossHome(f)) return null;
		
		JsonObject data = f.getCustomData();
		return PS.valueOf(data.get(BOSSHOME)).asBukkitLocation();
	}
	
	public static Location getWarp(Faction f, String name){
		if (!hasWarp(f, name)) return null;
			
		JsonObject data = f.getCustomData();
		return PS.valueOf(data.get(WARP).getAsJsonObject().get(name)).asBukkitLocation();
	}
	
	public static void setWarp(Faction f, String name, Location warploc){
		JsonObject data = f.getCustomData();
		if (data == null) data = new JsonObject();
		if (!data.has(WARP))data.add(WARP, new JsonObject());
		
		JsonObject warp = data.get(WARP).getAsJsonObject();
		warp.add(name, locToJson(warploc));
		
		data.add(WARP, warp);
		
		f.setCustomData(data);
	}
	
	/**
	 * @param usenderFaction
	 * @param warpname
	 */
	public static void removeWarp(Faction f, String name) {
		if (!hasWarp(f, name)) return;
		
		JsonObject data = f.getCustomData();
		JsonObject warp = data.get(WARP).getAsJsonObject();
		warp.remove(name);
		
		data.add(WARP, warp);
		
		f.setCustomData(data);
	}
	
	/**
	 * @param usenderFaction
	 * @return
	 */
	public static int getNumWarps(Faction f) {
		JsonObject data = f.getCustomData();
		if (data == null) return 0;
		if (!data.has(WARP)) return 0;
		return data.get(WARP).getAsJsonObject().entrySet().size();
	}
	
	/**
	 * @param usenderFaction
	 * @return
	 */
	public static String getWarps(Faction f) {
		JsonObject data = f.getCustomData();
		JsonObject warps;
		
		if (data.get(WARP) == null || (warps = data.getAsJsonObject(WARP).getAsJsonObject()) == null || warps.entrySet().isEmpty()){
			return "Sorry, no warps found.";
		}
		
		StringBuilder b = new StringBuilder();
		for (Entry<String, JsonElement> entry : warps.entrySet()){
			b.append(entry.getKey());
			b.append(", ");
		}
		b.replace(b.length()-2, b.length(), ".");
		return b.toString();
	}
	
	/**
	 * @param usenderFaction
	 * @param string
	 * @return
	 */
	public static boolean hasWarp(Faction f, String string) {
		JsonObject data = f.getCustomData();
		return (data != null && data.get(WARP) != null && data.get(WARP).getAsJsonObject().get(string) != null);
	}
	
	public static JsonObject locToJson(PS l){
		return locToJson(l.asBukkitLocation());
	}
	
	public static JsonObject locToJson(Location l){
		JsonObject element = new JsonObject();
		
		element.addProperty("w", l.getWorld().getName());
		element.addProperty("lx", l.getX());
		element.addProperty("ly", l.getY());
		element.addProperty("lz", l.getZ());
		element.addProperty("p", l.getPitch());
		element.addProperty("y", l.getYaw());
		
		return element;
	}
}

