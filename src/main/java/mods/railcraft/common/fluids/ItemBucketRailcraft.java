/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.fluids;

import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.BlockFluidFinite;
import net.minecraftforge.fluids.Fluid;

public class ItemBucketRailcraft extends Item {

    private final Fluid fluid;
    private ItemStack container = new ItemStack(Items.bucket);

    public ItemBucketRailcraft(Fluid fluid) {
        this.fluid = fluid;
        setMaxStackSize(1);
        setContainerItem(Items.bucket);
        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
    }

    @Override
    public Item setUnlocalizedName(String name) {
        return super.setUnlocalizedName(name);
    }

    public ItemBucketRailcraft setContainerItemStack(ItemStack stack) {
        container = stack;
        return this;
    }

    @Override
    public ItemStack getContainerItem(ItemStack stack) {
        return container.copy();
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        MovingObjectPosition mop = getMovingObjectPositionFromPlayer(world, player, false);

        if (mop != null && mop.typeOfHit == MovingObjectType.BLOCK) {
            BlockPos pos = mop.getBlockPos();

            if (!world.isBlockModifiable(player, pos))
                return stack;

            pos = pos.offset(mop.sideHit);

            if (!player.canPlayerEdit(pos, mop.sideHit, stack))
                return stack;

            if (tryPlaceContainedLiquid(world, pos) && !player.capabilities.isCreativeMode)
                return getContainerItem(stack);
        }

        return stack;
    }

    private boolean tryPlaceContainedLiquid(World world, BlockPos pos) {
        if (fluid.getBlock() == null)
            return false;

        Material material = WorldPlugin.getBlockMaterial(world, pos);

        if (!world.isAirBlock(pos) && material.isSolid())
            return false;

        if (!world.isRemote && !material.isSolid() && !material.isLiquid())
            world.destroyBlock(pos, true);

        Block block = fluid.getBlock();
        world.setBlockState(pos, block.getDefaultState().withProperty(BlockFluidBase.LEVEL, block instanceof BlockFluidFinite ? 15 : 0));
        return true;
    }

}
