/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.multi;

import mods.railcraft.common.blocks.ItemBlockEntityDelegate;
import mods.railcraft.common.blocks.ItemBlockRailcraft;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemMultiBlock extends ItemBlockEntityDelegate {

    protected final BlockMultiBlock multiBlock;

    public ItemMultiBlock(Block block) {
        super(block);
        this.multiBlock = (BlockMultiBlock) block;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack) {
        if (!super.canPlaceBlockOnSide(worldIn, pos, side, player, stack))
            return false;
        if (!multiBlock.needsSupport())
            return true;
        Block block = worldIn.getBlockState(pos).getBlock();
        if (!block.isReplaceable(worldIn, pos)) {
            pos = pos.offset(side);
        }
        return worldIn.isSideSolid(pos.down(), EnumFacing.UP);
    }
}
