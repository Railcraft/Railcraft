/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items.firestone;

import mods.railcraft.api.core.WorldCoordinate;
import mods.railcraft.common.blocks.RailcraftTileEntity;
import mods.railcraft.common.fluids.FluidHelper;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.effects.EffectManager;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class TileFirestoneRecharge extends RailcraftTileEntity {
    public static final int[] REBUILD_DELAY = new int[8];
    private final Deque<WorldCoordinate> queue = new LinkedList<WorldCoordinate>();
    private final Set<WorldCoordinate> visitedBlocks = new HashSet<WorldCoordinate>();
    public int charge = 0;
    public long rotationYaw, preRotationYaw;
    public float yOffset = -2, preYOffset = -2;
    private Deque<WorldCoordinate> lavaFound = new LinkedList<WorldCoordinate>();
    private int rebuildDelay;
    private String itemName;

    static {
        REBUILD_DELAY[0] = 128;
        REBUILD_DELAY[1] = 256;
        REBUILD_DELAY[2] = 512;
        REBUILD_DELAY[3] = 1024;
        REBUILD_DELAY[4] = 2048;
        REBUILD_DELAY[5] = 4096;
        REBUILD_DELAY[6] = 8192;
        REBUILD_DELAY[7] = 16384;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (Game.isNotHost(worldObj)) {
            preRotationYaw = rotationYaw;
            rotationYaw += 5;
            if (rotationYaw >= 360) {
                rotationYaw = 0;
                preRotationYaw = rotationYaw;
            }
            preYOffset = yOffset;
            if (yOffset < 0)
                yOffset += 0.0625F;
            return;
        }

        if (charge >= ItemFirestoneRefined.item.getMaxDamage())
            return;

//        if (clock % 4 == 0) {
        if (clock % REBUILD_DELAY[rebuildDelay] == 0) {
            rebuildDelay++;
            if (rebuildDelay >= REBUILD_DELAY.length)
                rebuildDelay = REBUILD_DELAY.length - 1;
            rebuildQueue();
        }
        WorldCoordinate index = getNextLavaBlock(true);

        if (index != null && coolLava(index.x, index.y, index.z)) {
            charge++;
            rebuildDelay = 0;
        }
//        }
    }

    private boolean coolLava(int x, int y, int z) {
        Block block = WorldPlugin.getBlock(worldObj, x, y, z);
        if (Fluids.LAVA.is(FluidHelper.getFluid(block))) {
            boolean placed = WorldPlugin.setBlock(worldObj, x, y, z, Blocks.obsidian);
            if (placed) {
                EffectManager.instance.fireSparkEffect(worldObj, x + 0.5, y + 0.5, z + 0.5, xCoord + 0.5, yCoord + 0.8, zCoord + 0.5);
                queueAdjacent(x, y, z);
                expandQueue();
                return true;
            }
        }
        return false;
    }

    private WorldCoordinate getNextLavaBlock(boolean remove) {
        if (queue.isEmpty())
            return null;

        if (remove) {
            WorldCoordinate index = queue.pollFirst();
            return index;
        }
        return queue.peekFirst();
    }

    /**
     * Nasty expensive function, don't call if you don't have to.
     */
    void rebuildQueue() {
        queue.clear();
        visitedBlocks.clear();
        lavaFound.clear();

        queueAdjacent(xCoord, yCoord, zCoord);

        expandQueue();
    }

    private void expandQueue() {
        while (!lavaFound.isEmpty()) {
            Deque<WorldCoordinate> blocksToExpand = lavaFound;
            lavaFound = new LinkedList<WorldCoordinate>();

            for (WorldCoordinate index : blocksToExpand) {
                queueAdjacent(index.x, index.y, index.z);
            }
        }
    }

    public void queueAdjacent(int x, int y, int z) {
        queueForFilling(x + 1, y, z);
        queueForFilling(x - 1, y, z);
        queueForFilling(x, y, z + 1);
        queueForFilling(x, y, z - 1);
        queueForFilling(x, y + 1, z);
        queueForFilling(x, y - 1, z);
    }

    public void queueForFilling(int x, int y, int z) {
        WorldCoordinate index = new WorldCoordinate(0, x, y, z);
        if (visitedBlocks.add(index)) {
            if ((x - xCoord) * (x - xCoord) + (z - zCoord) * (z - zCoord) > 64 * 64)
                return;

            Block block = WorldPlugin.getBlock(worldObj, x, y, z);
            if (block == Blocks.obsidian || Fluids.LAVA.is(FluidHelper.getFluid(block))) {
                lavaFound.add(index);
                if (FluidHelper.isFullFluidBlock(block, worldObj, x, y, z))
                    queue.addLast(index);
            }
        }
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setShort("charge", (short) charge);
        data.setByte("rebuildDelay", (byte) rebuildDelay);
        if (itemName != null)
            data.setString("itemName", itemName);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        charge = data.getShort("charge");
        rebuildDelay = data.getByte("rebuildDelay");
        if (data.hasKey(itemName))
            itemName = data.getString("itemName");
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);
    }

    @Override
    public String getLocalizationTag() {
        return "tile.railcraft.firestone.recharge.name";
    }

    @Override
    public short getId() {
        return 222;
    }
}
