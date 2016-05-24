/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.hidden;

import mods.railcraft.api.core.WorldCoordinate;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.MiscTools;
import mods.railcraft.common.util.misc.Timer;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Map;
import java.util.WeakHashMap;

/**
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

        World world = DimensionManager.getWorld(player.worldObj.provider.getDimensionId()); // Because Lava Boats

        boolean success = trySetMarker(world, player.getPosition(), player);

        for (int i = 0; i < 8 && !success; i++) {
            success = trySetMarker(world, player.getPosition().offset(EnumFacing.random(MiscTools.RANDOM)), player);
        }
    }

    private boolean trySetMarker(World world, BlockPos pos, EntityPlayer player) {
        Block block = WorldPlugin.getBlock(world, pos);
        if (block == Blocks.air) {
            WorldPlugin.setBlockState(world, pos, BlockHidden.getBlock().getDefaultState(), 6);
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof TileHidden) {
                TileHidden hidden = (TileHidden) tile;
                hidden.timestamp = System.currentTimeMillis();
                hidden.colorSeed = Railcraft.proxy.getPlayerUsername(player).hashCode() * 50021L;
                WorldCoordinate last = lastPosition.get(player);
                if (last != null)
                    hidden.lastMarker = last;
                hidden.sendUpdateToClient();
                lastPosition.put(player, new WorldCoordinate(world.provider.getDimensionId(), pos));
                return true;
            }
        }
        return block == BlockHidden.getBlock();
    }
}
