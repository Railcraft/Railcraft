/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.fluids;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.BlockFluidFinite;
import net.minecraftforge.fluids.Fluid;

public class ItemBucketRailcraft extends Item {

    private final Fluid fluid;
    private String iconName;
    private ItemStack container = new ItemStack(Items.bucket);

    public ItemBucketRailcraft(Fluid fluid) {
        this.fluid = fluid;
        setMaxStackSize(1);
        setContainerItem(Items.bucket);
        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
    }

    @Override
    public Item setUnlocalizedName(String name) {
        iconName = MiscTools.cleanTag(name);
        return super.setUnlocalizedName(name);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister) {
        this.itemIcon = par1IconRegister.registerIcon("railcraft:" + iconName);
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
        MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(world, player, false);

        if (mop != null && mop.typeOfHit == MovingObjectType.BLOCK) {
            int x = mop.blockX;
            int y = mop.blockY;
            int z = mop.blockZ;

            if (!world.canMineBlock(player, x, y, z))
                return stack;

            ForgeDirection sideHit = ForgeDirection.getOrientation(mop.sideHit);

            x += sideHit.offsetX;
            y += sideHit.offsetY;
            z += sideHit.offsetZ;

            if (!player.canPlayerEdit(x, y, z, mop.sideHit, stack))
                return stack;

            if (this.tryPlaceContainedLiquid(world, x, y, z) && !player.capabilities.isCreativeMode)
                return getContainerItem(stack);
        }

        return stack;
    }

    private boolean tryPlaceContainedLiquid(World world, int x, int y, int z) {
        if (fluid.getBlock() == null)
            return false;

        Material material = world.getBlock(x, y, z).getMaterial();

        if (!world.isAirBlock(x, y, z) && material.isSolid())
            return false;

        if (!world.isRemote && !material.isSolid() && !material.isLiquid())
            world.func_147480_a(x, y, z, true);

        Block block = fluid.getBlock();
        world.setBlock(x, y, z, block, block instanceof BlockFluidFinite ? 15 : 0, 3);
        return true;
    }

}
