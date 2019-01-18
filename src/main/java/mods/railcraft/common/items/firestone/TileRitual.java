/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.items.firestone;

import mods.railcraft.api.core.CollectionToolsAPI;
import mods.railcraft.common.blocks.TileRailcraftTicking;
import mods.railcraft.common.fluids.FluidTools;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.effects.EffectManager;
import mods.railcraft.common.util.effects.HostEffects;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class TileRitual extends TileRailcraftTicking {
    public static final int[] REBUILD_DELAY = new int[8];
    private final Deque<BlockPos> queue = CollectionToolsAPI.blockPosDeque(ArrayDeque::new);
    private final Deque<BlockPos> lavaFound = CollectionToolsAPI.blockPosDeque(ArrayDeque::new);
    private final Set<BlockPos> visitedBlocks = CollectionToolsAPI.blockPosSet(HashSet::new);
    public int charge;
    public long rotationYaw, preRotationYaw;
    public float yOffset = -2, preYOffset = -2;
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
        if (Game.isClient(world)) {
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

        ItemStack firestone = RailcraftItems.FIRESTONE_REFINED.getStack();
        if (InvTools.isEmpty(firestone))
            return;

        if (charge >= firestone.getMaxDamage())
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

    // logical server
    private boolean coolLava(BlockPos pos) {
        Block block = WorldPlugin.getBlock(world, pos);
        if (Fluids.LAVA.is(block)) {
            boolean placed = WorldPlugin.setBlockState(world, pos, Blocks.OBSIDIAN.getDefaultState());
            if (placed) {
                Vec3d startPosition = new Vec3d(pos).add(0.5, 0.5, 0.5);
                Vec3d endPosition = new Vec3d(getPos()).add(0.5, 0.8, 0.5);
                HostEffects.INSTANCE.fireSparkEffect(world, startPosition, endPosition);
                queueAdjacent(pos);
                expandQueue();
                return true;
            }
        }
        return false;
    }

    private @Nullable BlockPos getNextLavaBlock(boolean remove) {
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
        BlockPos next;
        while ((next = lavaFound.poll()) != null) {
            queueAdjacent(next);
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

            IBlockState state = WorldPlugin.getBlockState(world, index);
            if (state.getBlock() == Blocks.OBSIDIAN || Fluids.LAVA.is(FluidTools.getFluid(state))) {
                lavaFound.add(index);
                if (FluidTools.isFullFluidBlock(state, world, index))
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
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setShort("charge", (short) charge);
        data.setByte("rebuildDelay", (byte) rebuildDelay);
        if (itemName != null)
            data.setString("itemName", itemName);
        return data;
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
    public String getLocalizationTag() {
        return "tile.railcraft.firestone.recharge.name";
    }

}
