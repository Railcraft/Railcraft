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
import mods.railcraft.common.blocks.RailcraftTileEntity;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.items.ItemGoggles;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.effects.EffectManager;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class TileHidden extends RailcraftTileEntity {
    private static final int DURATION_MIINUTES = 15;
    private static final long DURATION_MILLISECONDS = TimeUnit.MILLISECONDS.convert(DURATION_MIINUTES, TimeUnit.MINUTES);
    public WorldCoordinate lastMarker;
    public long colorSeed;
    public long timestamp = -1;

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (Game.isNotHost(worldObj)) {
            if (lastMarker != null && EffectManager.instance.isGoggleAuraActive(ItemGoggles.GoggleAura.TRACKING))
                EffectManager.instance.trailEffect(lastMarker.x, lastMarker.y, lastMarker.z, this, colorSeed);
            return;
        }
        if (timestamp == -1)
            return;

        if (clock % 64 == 0) {
            if (BlockHidden.getBlock() == null || !RailcraftConfig.isTrackingAuraEnabled()) {
                worldObj.setBlockToAir(xCoord, yCoord, zCoord);
                return;
            }

            Block block = WorldPlugin.getBlock(worldObj, xCoord, yCoord, zCoord);
            if (block != BlockHidden.getBlock()) {
                worldObj.removeTileEntity(xCoord, yCoord, zCoord);
                return;
            }

            boolean timeElapsed = System.currentTimeMillis() - timestamp >= DURATION_MILLISECONDS;
            if (timeElapsed)
                worldObj.setBlock(xCoord, yCoord, zCoord, Blocks.air, 0, 6);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        if (lastMarker != null) {
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setInteger("dim", lastMarker.dimension);
            nbt.setInteger("x", lastMarker.x);
            nbt.setInteger("y", lastMarker.y);
            nbt.setInteger("z", lastMarker.z);
            data.setTag("last", nbt);
        }
        data.setLong("seed", colorSeed);
        data.setLong("time", timestamp);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        if (data.hasKey("last")) {
            NBTTagCompound nbt = data.getCompoundTag("last");
            int dim = nbt.getInteger("dim");
            int x = nbt.getInteger("x");
            int y = nbt.getInteger("y");
            int z = nbt.getInteger("z");
            lastMarker = new WorldCoordinate(dim, x, y, z);
        }
        colorSeed = data.getLong("seed");
        timestamp = data.getLong("time");
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeLong(colorSeed);
        data.writeBoolean(lastMarker != null);
        if (lastMarker != null) {
            data.writeInt(lastMarker.dimension);
            data.writeInt(lastMarker.x);
            data.writeInt(lastMarker.y);
            data.writeInt(lastMarker.z);
        }
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);
        colorSeed = data.readLong();
        if (data.readBoolean()) {
            int dim = data.readInt();
            int x = data.readInt();
            int y = data.readInt();
            int z = data.readInt();
            lastMarker = new WorldCoordinate(dim, x, y, z);
        }
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public String getLocalizationTag() {
        return "";
    }

    @Override
    public short getId() {
        return 111;
    }
}
