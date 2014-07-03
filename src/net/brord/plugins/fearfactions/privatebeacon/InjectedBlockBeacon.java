/**
 * 
 */
package net.brord.plugins.fearfactions.privatebeacon;

import net.minecraft.server.v1_7_R1.Block;
import net.minecraft.server.v1_7_R1.BlockBeacon;
import net.minecraft.server.v1_7_R1.TileEntity;
import net.minecraft.server.v1_7_R1.World;

/**
 * @author Brord
 *
 */
public class InjectedBlockBeacon extends BlockBeacon {
    @Override
    public TileEntity a(World world, int i) {
        // Make sure our injected beacon tile entity is used
        return new InjectedTileEntityBeacon();
    }
 
    public static void inject() throws Throwable {
        InjectedBlockBeacon block = new InjectedBlockBeacon();
        block.c("beacon"); block.a(1.0F); block.d("beacon");
        Block.REGISTRY.a(138, "beacon", block);
    }
}