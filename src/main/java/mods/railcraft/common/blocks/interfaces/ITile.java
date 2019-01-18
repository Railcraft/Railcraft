/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.interfaces;

import mods.railcraft.api.core.IOwnable;
import mods.railcraft.common.blocks.TileRailcraft;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

/**
 * Created by CovertJaguar on 9/12/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface ITile extends IOwnable {
    default TileRailcraft tile() {
        return (TileRailcraft) this;
    }

    void markBlockForUpdate();

    void onBlockPlacedBy(IBlockState state, @Nullable EntityLivingBase placer, ItemStack stack);

    void onNeighborBlockChange(IBlockState state, Block neighborBlock, BlockPos neighborPos);

    default void notifyBlocksOfNeighborChange() {
        if (tile().hasWorld())
            WorldPlugin.notifyBlocksOfNeighborChange(tile().getWorld(), tile().getPos(), tile().getBlockType());
    }

}
