/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine;

import mods.railcraft.common.blocks.ItemBlockRailcraftSubtyped;
import mods.railcraft.common.util.collections.ArrayTools;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemMachine extends ItemBlockRailcraftSubtyped {

    private final BlockMachine<? extends IEnumMachine<?>> machineBlock;

    public ItemMachine(Block block) {
        super(block);
        this.machineBlock = (BlockMachine<? extends IEnumMachine<?>>) block;
    }

    public IEnumMachine<?> getMachine(ItemStack stack) {
        int meta = stack.getMetadata();
        if (!ArrayTools.indexInBounds(machineBlock.getVariants().length, meta))
            meta = 0;
        return machineBlock.getVariants()[meta];
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        if (!world.setBlockState(pos, newState, 3))
            return false;

        if (world.getBlockState(pos).getBlock() == machineBlock) {
            machineBlock.onBlockPlacedBy(world, pos, newState, player, stack);
            machineBlock.initFromItem(world, pos, stack);
        }

        return true;
    }

    @Override
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack) {
        if (!super.canPlaceBlockOnSide(worldIn, pos, side, player, stack))
            return false;
        if (!machineBlock.needsSupport())
            return true;
        Block block = worldIn.getBlockState(pos).getBlock();
        if (!block.isReplaceable(worldIn, pos)) {
            pos = pos.offset(side);
        }
        return worldIn.isSideSolid(pos.down(), EnumFacing.UP);
    }
}
