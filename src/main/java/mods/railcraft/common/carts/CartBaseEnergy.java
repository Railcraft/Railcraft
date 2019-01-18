/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.carts;

import mods.railcraft.api.carts.CartToolsAPI;
import mods.railcraft.api.carts.IEnergyTransfer;
import mods.railcraft.api.carts.ILinkageManager;
import mods.railcraft.api.charge.CapabilitiesCharge;
import mods.railcraft.api.charge.IBatteryCart;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.plugins.ic2.IC2Plugin;
import mods.railcraft.common.util.charge.CartBattery;
import mods.railcraft.common.util.misc.APIErrorHandler;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import org.jetbrains.annotations.Nullable;

public abstract class CartBaseEnergy extends CartBaseContainer implements IEnergyTransfer, IWeightedCart {

    private final IBatteryCart cartBattery = new CartBattery(CartBattery.Type.STORAGE, getCapacity());

    protected CartBaseEnergy(World world) {
        super(world);
    }

    protected CartBaseEnergy(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilitiesCharge.CART_BATTERY || super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilitiesCharge.CART_BATTERY)
            return (T) cartBattery;
        return super.getCapability(capability, facing);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (Game.isClient(world))
            return;

        if (getEnergy() > getCapacity())
            cartBattery.setCharge(getCapacity());

        ItemStack stack = getStackInSlot(0);
        if (IC2Plugin.isEnergyItem(stack) && getEnergy() > 0)
            cartBattery.removeCharge(IC2Plugin.chargeItem(stack, getEnergy(), getTier()));

        stack = getStackInSlot(1);
        if (IC2Plugin.isEnergyItem(stack) && getEnergy() < getCapacity())
            cartBattery.addCharge(IC2Plugin.dischargeItem(stack, getCapacity() - getEnergy(), getTier()));
    }

    @Override
    public abstract int getTier();

    @Override
    public boolean canBeRidden() {
        return false;
    }

    @Override
    public final float softMaxSpeed() {
        return 0.18F - 0.05F * getTier();
    }

    @Override
    public int getSizeInventory() {
        return 2;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound data) {
        super.readEntityFromNBT(data);
        cartBattery.readFromNBT(data);
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound data) {
        super.writeEntityToNBT(data);
        cartBattery.writeToNBT(data);
    }

    @Override
    public double injectEnergy(Object source, double amount, int tier, boolean ignoreTransferLimit, boolean simulate, boolean passAlong) {
        if (tier < getTier())
            return amount;
        double extra = 0;
        if (!ignoreTransferLimit) {
            extra = Math.max(amount - getTransferLimit(), 0);
            amount = Math.min(amount, getTransferLimit());
        }
        double e = getEnergy() + amount;
        int capacity = getCapacity();
        if (e > capacity) {
            extra += e - capacity;
            e = capacity;
        }
        if (!simulate)
            cartBattery.setCharge(e);

        if (!passAlong)
            return extra;

        try {
            ILinkageManager lm = CartToolsAPI.linkageManager();

            EntityMinecart linkedCart = lm.getLinkedCartA(this);
            if (extra > 0 && linkedCart != source && linkedCart instanceof IEnergyTransfer)
                extra = ((IEnergyTransfer) linkedCart).injectEnergy(this, extra, tier, ignoreTransferLimit, simulate, true);

            linkedCart = lm.getLinkedCartB(this);
            if (extra > 0 && linkedCart != source && linkedCart instanceof IEnergyTransfer)
                extra = ((IEnergyTransfer) linkedCart).injectEnergy(this, extra, tier, ignoreTransferLimit, simulate, true);
        } catch (Throwable t) {
            APIErrorHandler.versionMismatch(IEnergyTransfer.class);
        }

        return extra;
    }

    @Override
    public double extractEnergy(Object source, double amount, int tier, boolean ignoreTransferLimit, boolean simulate, boolean passAlong) {
        if (tier < getTier())
            return 0;
        if (!ignoreTransferLimit)
            amount = Math.min(amount, getTransferLimit());
        double e = getEnergy();
        double provide = Math.min(amount, e);
        e -= provide;
        if (e < 0)
            e = 0;
        if (!simulate)
            cartBattery.setCharge(e);

        if (!passAlong)
            return provide;

        ILinkageManager lm = CartToolsAPI.linkageManager();

        EntityMinecart linkedCart = lm.getLinkedCartA(this);
        if (provide < amount && linkedCart != source && linkedCart instanceof IEnergyTransfer)
            provide += ((IEnergyTransfer) linkedCart).extractEnergy(this, amount - provide, tier, ignoreTransferLimit, simulate, true);

        linkedCart = lm.getLinkedCartB(this);
        if (provide < amount && linkedCart != source && linkedCart instanceof IEnergyTransfer)
            provide += ((IEnergyTransfer) linkedCart).extractEnergy(this, amount - provide, tier, ignoreTransferLimit, simulate, true);

        return provide;
    }

    @Override
    public double getEnergy() {
        return cartBattery.getCharge();
    }

    @Override
    public boolean canExtractEnergy() {
        return true;
    }

    @Override
    public boolean canInjectEnergy() {
        return true;
    }

    @Override
    protected EnumGui getGuiType() {
        return EnumGui.CART_ENERGY;
    }
}
