/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.hidden;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import java.util.Map;
import java.util.WeakHashMap;
import mods.railcraft.api.core.WorldCoordinate;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.MiscTools;
import mods.railcraft.common.util.misc.Timer;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.ForgeDirection;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TrailTicker {

    private static final int TICKS_PER_MARKER = 30;
    private final Map<EntityPlayer, WorldCoordinate> lastPosition = new WeakHashMap<EntityPlayer, WorldCoordinate>();
    private final Map<EntityPlayer, Timer> timers = new WeakHashMap<EntityPlayer, Timer>();

    @SubscribeEvent
    public void tick(TickEvent.PlayerTickEvent event) {
        if (event.side == Side.CLIENT)
            return;

        EntityPlayer player = event.player;
        if (player.worldObj == null)
            return;

        Timer timer = timers.get(player);
        if (timer == null) {
            timer = new Timer();
            timers.put(player, timer);
        }
        if (!timer.hasTriggered(player.worldObj, TICKS_PER_MARKER))
            return;

        String username = Railcraft.proxy.getPlayerUsername(player);
        if (username == null || username.startsWith("["))
            return;

        int x = MathHelper.floor_double(player.posX);
        int y = MathHelper.floor_double(player.posY);
        int z = MathHelper.floor_double(player.posZ);

        World world = DimensionManager.getWorld(player.worldObj.provider.dimensionId); // Because Lava Boats

        boolean success = trySetMarker(world, x, y, z, player);

        for (int i = 0; i < 8 && !success; i++) {
            ForgeDirection dir = ForgeDirection.getOrientation(MiscTools.RANDOM.nextInt(6));
            int newX = MiscTools.getXOnSide(x, dir);
            int newY = MiscTools.getYOnSide(y, dir);
            int newZ = MiscTools.getZOnSide(z, dir);
            success = trySetMarker(world, newX, newY, newZ, player);
        }
    }

    private boolean trySetMarker(World world, int x, int y, int z, EntityPlayer player) {
        Block block = WorldPlugin.getBlock(world, x, y, z);
        if (block == Blocks.air) {
            world.setBlock(x, y, z, BlockHidden.getBlock(), 0, 6);
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile instanceof TileHidden) {
                TileHidden hidden = (TileHidden) tile;
                hidden.timestamp = System.currentTimeMillis();
                hidden.colorSeed = Railcraft.proxy.getPlayerUsername(player).hashCode() * 50021L;
                WorldCoordinate last = lastPosition.get(player);
                if (last != null)
                    hidden.lastMarker = last;
                hidden.sendUpdateToClient();
                lastPosition.put(player, new WorldCoordinate(world.provider.dimensionId, x, y, z));
                return true;
            }
        }
        return block == BlockHidden.getBlock();
    }

}
