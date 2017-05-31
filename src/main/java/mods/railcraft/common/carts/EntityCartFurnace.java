/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.carts;

import net.minecraft.entity.item.EntityMinecartFurnace;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

import mods.railcraft.common.plugins.forge.FuelPlugin;
import mods.railcraft.common.util.inventory.InvTools;

public class EntityCartFurnace extends EntityMinecartFurnace implements IRailcraftCart {

    public EntityCartFurnace(World world) {
        super(world);
    }

    public EntityCartFurnace(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public IRailcraftCartContainer getCartType() {
        return RailcraftCarts.FURNACE;
    }

    /**
     * Checks if the entity is in range to render.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public boolean isInRangeToRenderDist(double distance) {
        return CartTools.isInRangeToRenderDist(this, distance);
    }

    @Nullable
    @Override
    public ItemStack getCartItem() {
        return createCartItem(this);
    }

    @Override
    public void killMinecart(DamageSource par1DamageSource) {
        killAndDrop(this);
    }

//    public double getDrag() {
//        return DRAG_FACTOR;
//    }

    @Override
    public boolean processInitialInteract(EntityPlayer player, @Nullable ItemStack stack, EnumHand hand) {
        if (MinecraftForge.EVENT_BUS.post(new MinecartInteractEvent(this, player, stack, hand)))
            return true;
        Integer fuel = ReflectionHelper.getPrivateValue(EntityMinecartFurnace.class, this, 1);
        if (!InvTools.isEmpty(stack)) {
            int burnTime = FuelPlugin.getBurnTime(stack);

            if (burnTime > 0 && fuel + burnTime <= 32000) {
                if (!player.capabilities.isCreativeMode)
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, InvTools.depleteItem(stack));
                fuel += burnTime;
                ReflectionHelper.setPrivateValue(EntityMinecartFurnace.class, this, fuel, 1);

                pushX = posX - player.posX;
                pushZ = posZ - player.posZ;
            }
        }

        return true;
    }

    //    private static final double DRAG_FACTOR = 0.99;
    private static final double PUSH_FACTOR = 0.1D;
}
