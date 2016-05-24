/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items.firestone;

import mods.railcraft.common.blocks.RailcraftTileEntity;
import mods.railcraft.common.fluids.FluidHelper;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.effects.EffectManager;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;
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
    private final Deque<BlockPos> queue = new LinkedList<BlockPos>();
    private final Set<BlockPos> visitedBlocks = new HashSet<BlockPos>();
    public int charge;
    public long rotationYaw, preRotationYaw;
    public float yOffset = -2, preYOffset = -2;
    private Deque<BlockPos> lavaFound = new LinkedList<BlockPos>();
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
    public void update() {
        super.update();
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
        BlockPos index = getNextLavaBlock(true);

        if (index != null && coolLava(index)) {
            charge++;
            rebuildDelay = 0;
        }
//        }
    }

    private boolean coolLava(BlockPos pos) {
        Block block = WorldPlugin.getBlock(worldObj, pos);
        if (Fluids.LAVA.is(FluidHelper.getFluid(block))) {
            boolean placed = WorldPlugin.setBlockState(worldObj, pos, Blocks.obsidian.getDefaultState());
            if (placed) {
                Vec3d startPosition = new Vec3d(pos).addVector(0.5, 0.5, 0.5);
                Vec3d endPosition = new Vec3d(getPos()).addVector(0.5, 0.8, 0.5);
                EffectManager.instance.fireSparkEffect(worldObj, startPosition, endPosition);
                queueAdjacent(pos);
                expandQueue();
                return true;
            }
        }
        return false;
    }

    private BlockPos getNextLavaBlock(boolean remove) {
        if (queue.isEmpty())
            return null;

        if (remove) return queue.pollFirst();
        return queue.peekFirst();
    }

    /**
     * Nasty expensive function, don't call if you don't have to.
     */
    void rebuildQueue() {
        queue.clear();
        visitedBlocks.clear();
        lavaFound.clear();

        queueAdjacent(getPos());

        expandQueue();
    }

    private void expandQueue() {
        while (!lavaFound.isEmpty()) {
            Deque<BlockPos> blocksToExpand = lavaFound;
            lavaFound = new LinkedList<BlockPos>();

            for (BlockPos index : blocksToExpand) {
                queueAdjacent(index);
            }
        }
    }

    public void queueAdjacent(BlockPos pos) {
        // No idea if it matters which order these are added,
        // but I figured it best to keep them in the same order.
        for (EnumFacing side : EnumFacing.HORIZONTALS) {
            queueForFilling(pos.offset(side));
        }
        queueForFilling(pos.up());
        queueForFilling(pos.down());
    }

    //TODO: test
    public void queueForFilling(BlockPos index) {
        if (visitedBlocks.add(index)) {
            if ((index.getX() - pos.getX()) * (index.getX() - pos.getX()) + (index.getZ() - pos.getZ()) * (index.getZ() - pos.getZ()) > 64 * 64)
                return;

            IBlockState state = WorldPlugin.getBlockState(worldObj, index);
            if (state.getBlock() == Blocks.obsidian || Fluids.LAVA.is(FluidHelper.getFluid(state))) {
                lavaFound.add(index);
                if (FluidHelper.isFullFluidBlock(state, worldObj, index))
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

    @Nonnull
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
