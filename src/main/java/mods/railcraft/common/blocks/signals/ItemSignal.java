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
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import mods.railcraft.common.blocks.RailcraftBlocks;
import net.minecraft.init.Blocks;

public class ItemSignal extends ItemBlockRailcraftMultiType {

    public ItemSignal(Block block) {
        super(block);
        setUnlocalizedName("railcraft.signal");
    }

    public ISignalTileDefinition getStructureType(ItemStack stack) {
        return EnumSignal.fromId(stack.getItemDamage());
    }

    @Override
    public IIcon getIconFromDamage(int damage) {
        return RailcraftBlocks.getBlockSignal().getIcon(2, damage);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return getStructureType(stack).getTag();
    }

    @Override
    public boolean func_150936_a(World world, int x, int y, int z, int side, EntityPlayer player, ItemStack stack) {
        Block oldBlock = world.getBlock(x, y, z);

        if (oldBlock == Blocks.snow_layer)
            side = 1;
        else if (oldBlock != Blocks.vine && oldBlock != Blocks.tallgrass && oldBlock != Blocks.deadbush && !oldBlock.isReplaceable(world, x, y, z)) {
            if (side == 0)
                --y;

            if (side == 1)
                ++y;

            if (side == 2)
                --z;

            if (side == 3)
                ++z;

            if (side == 4)
                --x;

            if (side == 5)
                ++x;
        }

        return world.canPlaceEntityOnSide(field_150939_a, x, y, z, false, side, (Entity) null, stack) && (!getStructureType(stack).needsSupport() || world.isSideSolid(x, y - 1, z, ForgeDirection.UP));
    }

}
