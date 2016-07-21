/*******************************************************************************
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.blocks.detector;

import mods.railcraft.common.blocks.ItemBlockRailcraftSubtyped;
import mods.railcraft.common.core.IVariantEnum;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ItemDetector extends ItemBlockRailcraftSubtyped {

    public ItemDetector(Block block) {
        super(block);
    }

    @Override
    public IBlockState getState(@Nullable IVariantEnum variant) {
        IBlockState state = block.getDefaultState();
        if (variant != null) {
            checkVariant(variant);
            state = state.withProperty(BlockDetector.VARIANT, (EnumDetector) variant);
        }
        return state;
    }

    /**
     * Called to actually place the block, after the location is determined and
     * all permission checks have been made.
     *
     * @param stack  The item stack that was used to place the block. This can be
     *               changed inside the method.
     * @param player The player who is placing the block. Can be null if the
     *               block is not being placed by a player.
     * @param side   The side the player (or machine) right-clicked on.
     */
    //TODO: do we even need a special item for this?
    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        if (!world.setBlockState(pos, newState, 3))
            return false;

        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileDetector)
            ((TileDetector) tile).setDetector(EnumDetector.fromOrdinal(stack.getItemDamage()));

        if (world.getBlockState(pos).getBlock() == block) {
            block.onBlockPlacedBy(world, pos, newState, player, stack);
        }

        return true;
    }
}
