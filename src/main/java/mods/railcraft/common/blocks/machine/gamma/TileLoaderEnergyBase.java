/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.gamma;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mods.railcraft.api.carts.IEnergyTransfer;
import mods.railcraft.common.carts.CartUtils;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import mods.railcraft.common.modules.ModuleIC2;
import mods.railcraft.common.plugins.ic2.IC2Plugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.tileentity.TileEntity;

public abstract class TileLoaderEnergyBase extends TileLoaderBase implements ISidedInventory {
    private static final int SLOT_CHARGE = 0;
    private static final int SLOT_BATTERY = 1;
    private static final int TIER = 2;
    private static final int CAPACITY = 100000;
    private static final int MAX_OVERCLOCKS = 10;
    private static final int MAX_LAPOTRON = 6;
    private static final int[] SLOTS = InvTools.buildSlotArray(0, 2);
    public int transferRate;
    public short storageUpgrades;
    public short lapotronUpgrades;
    protected int energy;
    protected short transformerUpgrades;
    protected short overclockerUpgrades;
    protected ForgeDirection direction = ForgeDirection.NORTH;
    protected boolean transferredEnergy;
    private boolean addedToIC2EnergyNet;

    public TileLoaderEnergyBase() {
        super();
        setInventorySize(6);
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        return SLOTS;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side) {
        switch (slot) {
            case SLOT_CHARGE:
                return IC2Plugin.canCharge(stack, getTier());
            case SLOT_BATTERY:
                return IC2Plugin.canDischarge(stack, getTier());
            default:
                return false;
        }
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side) {
        return true;
    }

    @Override
    public void onBlockPlacedBy(EntityLivingBase entityliving, ItemStack stack) {
        super.onBlockPlacedBy(entityliving, stack);
        direction = MiscTools.getSideFacingTrack(worldObj, xCoord, yCoord, zCoord);
        if (direction == ForgeDirection.UNKNOWN)
            direction = MiscTools.getSideClosestToPlayer(worldObj, xCoord, yCoord, zCoord, entityliving);
    }

    protected void countUpgrades() {
        ItemStack storage = IC2Plugin.getItem("energyStorageUpgrade");
        ItemStack overclocker = IC2Plugin.getItem("overclockerUpgrade");
        ItemStack transformer = IC2Plugin.getItem("transformerUpgrade");
        Item lapotron = ModuleIC2.lapotronUpgrade;

        storageUpgrades = 0;
        overclockerUpgrades = 0;
        transformerUpgrades = 0;
        lapotronUpgrades = 0;

        for (int i = 2; i < 6; i++) {
            ItemStack stack = getStackInSlot(i);
            if (stack != null)
                if (storage != null && stack.isItemEqual(storage))
                    storageUpgrades += stack.stackSize;
                else if (overclocker != null && stack.isItemEqual(overclocker))
                    overclockerUpgrades += stack.stackSize;
                else if (transformer != null && stack.isItemEqual(transformer))
                    transformerUpgrades += stack.stackSize;
                else if (lapotron != null && stack.getItem() == lapotron)
                    lapotronUpgrades += stack.stackSize;
        }
        if (overclockerUpgrades > MAX_OVERCLOCKS)
            overclockerUpgrades = MAX_OVERCLOCKS;
        if (lapotronUpgrades > MAX_LAPOTRON)
            lapotronUpgrades = MAX_LAPOTRON;
    }

    @Override
    public void markDirty() {
        super.markDirty();
        countUpgrades();
    }

    @Override
    public boolean isManualMode() {
        return false;
    }

    @Override
    public boolean canHandleCart(EntityMinecart cart) {
        if (isSendCartGateAction())
            return false;
        if (!(cart instanceof IEnergyTransfer))
            return false;
        IEnergyTransfer energyCart = (IEnergyTransfer) cart;
        if (energyCart.getTier() > getTier())
            return false;
//        ItemStack minecartSlot1 = getCartFilters().getStackInSlot(0);
//        ItemStack minecartSlot2 = getCartFilters().getStackInSlot(1);
//        if (minecartSlot1 != null || minecartSlot2 != null)
//            if (!CartUtils.doesCartMatchFilter(minecartSlot1, cart) && !CartUtils.doesCartMatchFilter(minecartSlot2, cart))
//                return false;
        return true;
    }

    @Override
    public boolean isProcessing() {
        return transferredEnergy;
    }

    public abstract TileEntity getIC2Delegate();

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (Game.isNotHost(getWorld()))
            return;

        if (!addedToIC2EnergyNet) {
            IC2Plugin.addTileToNet(getIC2Delegate());
            addedToIC2EnergyNet = true;
        }

        int capacity = getCapacity();
        if (energy > capacity)
            energy = capacity;

        ItemStack charge = getStackInSlot(SLOT_CHARGE);
        if (charge != null)
            energy -= IC2Plugin.chargeItem(charge, energy, getTier());

        ItemStack battery = getStackInSlot(SLOT_BATTERY);
        if (battery != null && energy < capacity)
            energy += IC2Plugin.dischargeItem(battery, capacity - energy, getTier());

        if (clock % 64 == 0)
            countUpgrades();
    }

    private void dropFromNet() {
        if (addedToIC2EnergyNet)
            IC2Plugin.removeTileFromNet(getIC2Delegate());
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        dropFromNet();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        dropFromNet();
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setInteger("energy", energy);
        data.setByte("direction", (byte) direction.ordinal());
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        energy = data.getInteger("energy");
        direction = ForgeDirection.getOrientation(data.getByte("direction"));

        countUpgrades();
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);

        data.writeByte(direction.ordinal());
        data.writeShort(storageUpgrades);
        data.writeShort(lapotronUpgrades);
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);

        direction = ForgeDirection.getOrientation(data.readByte());
        storageUpgrades = data.readShort();
        lapotronUpgrades = data.readShort();
    }

    @Override
    public boolean rotateBlock(ForgeDirection axis) {
        if (direction == axis)
            direction = axis.getOpposite();
        else
            direction = axis;
        markBlockForUpdate();
        return true;
    }

    public double getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public int getCapacity() {
        int capacity = CAPACITY;
        capacity += storageUpgrades * 10000;
        capacity += lapotronUpgrades * 5000000;
        return capacity;
    }

    public int getTier() {
        return TIER + transformerUpgrades;
    }

    public int getTransferRate() {
        return transferRate;
    }

    public int getEnergyBarScaled(int scale) {
        return ((int) getEnergy() * scale) / getCapacity();
    }
}
