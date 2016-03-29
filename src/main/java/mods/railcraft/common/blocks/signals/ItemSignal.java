/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.signals;

import mods.railcraft.common.blocks.ItemBlockRailcraftMultiType;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemSignal extends ItemBlockRailcraftMultiType {

    public ItemSignal(Block block) {
        super(block);
        setUnlocalizedName("railcraft.signal");
    }

    public ISignalTileDefinition getStructureType(ItemStack stack) {
        return EnumSignal.fromOrdinal(stack.getItemDamage());
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return getStructureType(stack).getTag();
    }
    
    @Override
    public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack) {
        Block oldBlock = world.getBlockState(pos).getBlock();

        if (oldBlock == Blocks.snow_layer)
            side = EnumFacing.UP;
        else if (oldBlock != Blocks.vine && oldBlock != Blocks.tallgrass && oldBlock != Blocks.deadbush && !oldBlock.isReplaceable(world, pos)) {
            pos = pos.offset(side);
        }
        
        return world.canBlockBePlaced(block, pos, false, side, (Entity) null, stack) && (!getStructureType(stack).needsSupport() || world.isSideSolid(pos.down(), EnumFacing.UP));
    }
}
