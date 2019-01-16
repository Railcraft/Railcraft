/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.manipulator;

import mods.railcraft.api.carts.IEnergyTransfer;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.ic2.IC2Plugin;
import mods.railcraft.common.util.charge.Battery;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import java.io.IOException;

import static mods.railcraft.common.util.inventory.InvTools.sizeOf;

public abstract class TileIC2Manipulator extends TileManipulatorCart implements ISidedInventory {

    private static final int SLOT_CHARGE = 0;
    private static final int SLOT_BATTERY = 1;
    private static final int TIER = 2;
    private static final int CAPACITY = 100000;
    private static final int MAX_OVERCLOCKS = 10;
    private static final int MAX_LAPOTRON = 6;
    private static final int[] SLOTS = InvTools.buildSlotArray(0, 2);
    public double transferRate;
    public short storageUpgrades;
    public short lapotronUpgrades;
    protected final Battery battery = new Battery(CAPACITY) {
        @Override
        public double getCapacity() {
            int capacity = CAPACITY;
            capacity += storageUpgrades * 10000;
            capacity += lapotronUpgrades * 1000000;
            return capacity;
        }
    };
    protected short transformerUpgrades;
    protected short overclockerUpgrades;
    private boolean addedToIC2EnergyNet;

    protected TileIC2Manipulator() {
        setInventorySize(6);
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return SLOTS;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        switch (index) {
            case SLOT_CHARGE:
                return IC2Plugin.canCharge(itemStackIn, getTier());
            case SLOT_BATTERY:
                return IC2Plugin.canDischarge(itemStackIn, getTier());
            default:
                return false;
        }
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return true;
    }

    protected void countUpgrades() {
        ItemStack storage = IC2Plugin.getItem("upgrade#energy_storage");
        ItemStack overclocker = IC2Plugin.getItem("upgrade#overclocker");
        ItemStack transformer = IC2Plugin.getItem("upgrade#transformer");
        Item lapotron = RailcraftItems.LAPOTRON_UPGRADE.item();

        storageUpgrades = 0;
        overclockerUpgrades = 0;
        transformerUpgrades = 0;
        lapotronUpgrades = 0;

        for (int i = 2; i < 6; i++) {
            ItemStack stack = getStackInSlot(i);
            if (!InvTools.isEmpty(stack))
                if (!InvTools.isEmpty(storage) && stack.isItemEqual(storage))
                    storageUpgrades += sizeOf(stack);
                else if (!InvTools.isEmpty(overclocker) && stack.isItemEqual(overclocker))
                    overclockerUpgrades += sizeOf(stack);
                else if (!InvTools.isEmpty(transformer) && stack.isItemEqual(transformer))
                    transformerUpgrades += sizeOf(stack);
                else if (lapotron != null && stack.getItem() == lapotron)
                    lapotronUpgrades += sizeOf(stack);
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
    public boolean canHandleCart(EntityMinecart cart) {
        if (!(cart instanceof IEnergyTransfer))
            return false;
        IEnergyTransfer energyCart = (IEnergyTransfer) cart;
        return energyCart.getTier() <= getTier() && super.canHandleCart(cart);
    }

    public abstract TileEntity getIC2Delegate();

    @Override
    protected void upkeep() {
        super.upkeep();
        transferRate = 0;

        if (!addedToIC2EnergyNet) {
            IC2Plugin.addTileToNet(getIC2Delegate());
            addedToIC2EnergyNet = true;
        }

        double capacity = battery.getCapacity();
        if (battery.getCharge() > capacity)
            battery.setCharge(capacity);

        ItemStack input = getStackInSlot(SLOT_CHARGE);
        if (!InvTools.isEmpty(input))
            battery.removeCharge(IC2Plugin.chargeItem(input, battery.getAvailableCharge(), getTier()));

        ItemStack output = getStackInSlot(SLOT_BATTERY);
        if (!InvTools.isEmpty(output) && battery.needsCharging())
            battery.addCharge(IC2Plugin.dischargeItem(output, capacity - battery.getCharge(), getTier()));

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
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        battery.writeToNBT(data);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        battery.readFromNBT(data);
        countUpgrades();
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);

        data.writeShort(storageUpgrades);
        data.writeShort(lapotronUpgrades);
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);

        storageUpgrades = data.readShort();
        lapotronUpgrades = data.readShort();
    }

    public Battery getBattery() {
        return battery;
    }

    public int getTier() {
        return TIER + transformerUpgrades;
    }

    public double getTransferRate() {
        return transferRate;
    }
}
