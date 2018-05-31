/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBlockEntityDelegate extends ItemBlockRailcraftSubtyped {

    protected final BlockEntityDelegate entityDelegate;

    public ItemBlockEntityDelegate(Block block) {
        super(block);
        this.entityDelegate = (BlockEntityDelegate) block;
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        if (!world.setBlockState(pos, newState, 3))
            return false;

        if (world.getBlockState(pos).getBlock() == entityDelegate) {
            entityDelegate.onBlockPlacedBy(world, pos, newState, player, stack);
            entityDelegate.initFromItem(world, pos, stack);
        }

        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack) {
        if (!super.canPlaceBlockOnSide(worldIn, pos, side, player, stack))
            return false;
        if (!entityDelegate.needsSupport())
            return true;
        Block block = worldIn.getBlockState(pos).getBlock();
        if (!block.isReplaceable(worldIn, pos)) {
            pos = pos.offset(side);
        }
        return worldIn.isSideSolid(pos.down(), EnumFacing.UP);
    }
}
