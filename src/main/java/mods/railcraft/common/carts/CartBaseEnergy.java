/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
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
import mods.railcraft.api.charge.ICartBattery;
import mods.railcraft.common.blocks.charge.CartBattery;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.plugins.ic2.IC2Plugin;
import mods.railcraft.common.util.misc.APIErrorHandler;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.SafeNBTWrapper;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nonnull;

abstract class CartBaseEnergy extends CartBaseContainer implements IEnergyTransfer, IIC2EnergyCart {

    private final ICartBattery cartBattery = new CartBattery(CartBattery.Type.STORAGE, getCapacity());

    protected CartBaseEnergy(World world) {
        super(world);
    }

    protected CartBaseEnergy(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CapabilitiesCharge.CART_BATTERY || super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilitiesCharge.CART_BATTERY)
            return (T) cartBattery;
        return super.getCapability(capability, facing);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (Game.isClient(worldObj))
            return;

        if (getEnergy() > getCapacity())
            setEnergy(getCapacity());

        ItemStack stack = getStackInSlot(0);
        if (IC2Plugin.isEnergyItem(stack) && getEnergy() > 0)
            setEnergy(getEnergy() - IC2Plugin.chargeItem(stack, getEnergy(), getTier()));

        stack = getStackInSlot(1);
        if (IC2Plugin.isEnergyItem(stack) && getEnergy() < getCapacity())
            setEnergy(getEnergy() + IC2Plugin.dischargeItem(stack, getCapacity() - getEnergy(), getTier()));
    }

    @Override
    public abstract int getTier();

    @Override
    public boolean doInteract(EntityPlayer player, ItemStack stack, EnumHand hand) {
        if (Game.isHost(worldObj))
            GuiHandler.openGui(EnumGui.CART_ENERGY, player, worldObj, this);
        return true;
    }

    @Override
    public boolean canBeRidden() {
        return false;
    }

    @Override
    public final float getMaxCartSpeedOnRail() {
        int numLocomotives = Train.getTrain(this).getNumRunningLocomotives();
        if (numLocomotives == 0)
            return super.getMaxCartSpeedOnRail();
        return Math.min(1.2F, 0.18F - 0.05F * getTier() + (numLocomotives - 1) * 0.075F);
    }

    @Override
    public int getSizeInventory() {
        return 2;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        SafeNBTWrapper safe = new SafeNBTWrapper(nbt);
        setEnergy(safe.getDouble("energy"));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {
        super.writeEntityToNBT(nbttagcompound);
        nbttagcompound.setDouble("energy", getEnergy());
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
            setEnergy(e);

        if (!passAlong)
            return extra;

        try {
            ILinkageManager lm = CartToolsAPI.getLinkageManager(worldObj);

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
            setEnergy(e);

        if (!passAlong)
            return provide;

        ILinkageManager lm = CartToolsAPI.getLinkageManager(worldObj);

        EntityMinecart linkedCart = lm.getLinkedCartA(this);
        if (provide < amount && linkedCart != source && linkedCart instanceof IEnergyTransfer)
            provide += ((IEnergyTransfer) linkedCart).extractEnergy(this, amount - provide, tier, ignoreTransferLimit, simulate, true);

        linkedCart = lm.getLinkedCartB(this);
        if (provide < amount && linkedCart != source && linkedCart instanceof IEnergyTransfer)
            provide += ((IEnergyTransfer) linkedCart).extractEnergy(this, amount - provide, tier, ignoreTransferLimit, simulate, true);

        return provide;
    }

    @Override
    public int getEnergyBarScaled(int scale) {
        return ((int) getEnergy() * scale) / getCapacity();
    }

    @Override
    public double getEnergy() {
        return cartBattery.getCharge();
    }

    @Override
    public void setEnergy(double energy) {
        cartBattery.setCharge(energy);
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
    public EntityMinecart getEntity() {
        return this;
    }

    @Nonnull
    @Override
    protected EnumGui getGuiType() {
        return EnumGui.CART_ENERGY;
    }
}
