/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.wayobjects;

import mods.railcraft.common.blocks.ItemBlockRailcraftSubtyped;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemWayObject extends ItemBlockRailcraftSubtyped {

    public ItemWayObject(Block block) {
        super(block);
    }

    public IWayObjectDefinition getStructureType(ItemStack stack) {
        return EnumWayObject.fromOrdinal(stack.getItemDamage());
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return getStructureType(stack).getTag();
    }

    @Override
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack) {
        Block block = worldIn.getBlockState(pos).getBlock();

        if (block == Blocks.SNOW_LAYER && block.isReplaceable(worldIn, pos))
        {
            side = EnumFacing.UP;
        }
        else if (!block.isReplaceable(worldIn, pos))
        {
            pos = pos.offset(side);
        }

        return worldIn.canBlockBePlaced(getBlock(), pos, false, side, (Entity) null, stack) && (!getStructureType(stack).needsSupport() || worldIn.isSideSolid(pos.down(), EnumFacing.UP));
    }
}
