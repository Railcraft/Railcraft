/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.carts;

import mods.railcraft.common.util.inventory.filters.StackFilters;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class EntityCartChest extends CartBaseChest {
    public EntityCartChest(World world) {
        super(world);
    }

    public EntityCartChest(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public Type getType() {
        return Type.CHEST;
    }

    @Override
    public IRailcraftCartContainer getCartType() {
        return RailcraftCarts.CHEST;
    }

    @Override
    public IBlockState getDefaultDisplayTile() {
        return Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.NORTH);
    }

    @Override
    public int getSizeInventory() {
        return 27;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return StackFilters.CARGO.test(stack);
    }


}
