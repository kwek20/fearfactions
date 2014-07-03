/**
 * 
 */
package net.brord.plugins.fearfactions.privatebeacon;

import net.brord.plugins.fearfactions.util.FactionsUtil;
import net.minecraft.server.v1_7_R1.AxisAlignedBB;
import net.minecraft.server.v1_7_R1.EntityPlayer;
import net.minecraft.server.v1_7_R1.MobEffect;
import net.minecraft.server.v1_7_R1.TileEntity;
import net.minecraft.server.v1_7_R1.TileEntityBeacon;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;

import com.massivecraft.factions.entity.BoardColls;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.mcore.ps.PS;
 
public class InjectedTileEntityBeacon extends TileEntityBeacon {
    private static Method UPDATE_STATE;
    private static Field BEACON_ACTIVE;
 
    public InjectedTileEntityBeacon() {
    	super();
    }
 
    @Override
    @SuppressWarnings("unchecked")
    public void h() {
        try {
            if (this.world.getTime() % 80L == 0L) {
                UPDATE_STATE.invoke(this);
                boolean result = BEACON_ACTIVE.getBoolean(this);
                
                if (result && this.l() > 0 /* levels > 0 */ && !this.world.isStatic && this.j() > 0 /* effect count > 0 */) {
                	
                	double doubleThing = (double) (this.l() /* levels */ * 10 + 10);
                    byte byteThing = 0;
 
                    if (this.l() >= 4 /* levels >= 4 */ &&
                            this.j() == this.k() /* primary effect == secondary effect */) {
                        byteThing = 1;
                    }
                    
                    AxisAlignedBB aaBB = AxisAlignedBB.a().a(x, y, z, x + 1, y + 1, z + 1).grow(doubleThing, doubleThing, doubleThing);
                    aaBB.e /* max y */ = this.world.getHeight();
                    List<EntityPlayer> players = this.world.a(EntityPlayer.class, aaBB); // Get entities within radius??
                    
                    Faction f = BoardColls.get().getFactionAt(PS.valueOf(new Location(world.getWorld(), x, y, z)));
                    UPlayer up;
                    
                    for (Iterator<EntityPlayer> itr = players.iterator(); itr.hasNext();) {
                        EntityPlayer player = itr.next();
                        
                        // Check something here
                        up = FactionsUtil.getPlayer(player.getBukkitEntity());
                        if (up == null || (f != null && !up.hasFaction()) || (f != null && !f.getName().equals(up.getFactionName()))) {
                            //beacon stands in a faction, and player doesn't have a faction
                        	//beacon stands in a faction and the player is not in that faction
                        	//player doesn't exist
                        	
                        	// Remove the player from the list
                            itr.remove();
                        } else {
                        	//beacon doesn't stand in a faction
                        	//beacon stands in the faction of the receiving player
                        	
                            // Add the effect
                            player.addEffect(new MobEffect(this.j(), 480, byteThing, true));
                        }
                    }
 
                    if (this.l() >= 4 /* levels >= 4 */ &&
                            this.j() != this.k() /* primary effect != secondary effect */ &&
                            this.k() > 0 /* secondary effect is set */) {
                        for (EntityPlayer player : players) {
                            // Add secondary effect
                            player.addEffect(new MobEffect(this.k(), 180, 0, true));
                        }
                    }
                }
            }
        } catch (Throwable e) {
        	e.printStackTrace();
            // Log error
        }
    }
 
    @SuppressWarnings("unchecked")
    public static void inject() throws Throwable {
        UPDATE_STATE = TileEntityBeacon.class.getDeclaredMethod("y"); UPDATE_STATE.setAccessible(true);
        BEACON_ACTIVE = TileEntityBeacon.class.getDeclaredField("k"); BEACON_ACTIVE.setAccessible(true);

        Field nameMapField = TileEntity.class.getDeclaredField("i"); nameMapField.setAccessible(true);
        Field classMapField = TileEntity.class.getDeclaredField("j"); classMapField.setAccessible(true);
 
        ((Map<String, Class<InjectedTileEntityBeacon>>) nameMapField.get(null)).put("Beacon", InjectedTileEntityBeacon.class);
        ((Map<Class<InjectedTileEntityBeacon>, String>) classMapField.get(null)).put(InjectedTileEntityBeacon.class, "Beacon");
    }
}