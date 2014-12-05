/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.detector.types;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import mods.railcraft.common.blocks.detector.DetectorFilter;
import mods.railcraft.common.blocks.detector.EnumDetector;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.slots.ISlotController;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.inventory.InvTools;

import static mods.railcraft.common.plugins.forge.PowerPlugin.*;

import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import net.minecraft.nbt.NBTTagCompound;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class DetectorItem extends DetectorFilter {

    public enum PrimaryMode {

        EMPTY, FULL, ANYTHING, FILTERED, NOT_EMPTY, ANALOG;

        @Override
        public String toString() {
            return LocalizationPlugin.translate("railcraft.gui.detector.item." + name().toLowerCase(Locale.ENGLISH));
        }

    };

    public enum FilterMode {

        AT_LEAST, AT_MOST, EXACTLY, LESS_THAN, GREATER_THAN;

        @Override
        public String toString() {
            return LocalizationPlugin.translate("railcraft.gui.detector.item." + name().toLowerCase(Locale.ENGLISH));
        }

    };
    private PrimaryMode primaryMode = PrimaryMode.ANYTHING;
    private FilterMode filterMode = FilterMode.AT_LEAST;

    public DetectorItem() {
        super(9);
    }

    @Override
    public int testCarts(List<EntityMinecart> carts) {
        for (EntityMinecart cart : carts) {
            IInventory cartInv = null;
            if (cart instanceof IInventory)
                cartInv = (IInventory) cart;
            if (cartInv != null && cartInv.getSizeInventory() > 0)
                switch (primaryMode) {
                    case ANYTHING:
                        return FULL_POWER;
                    case EMPTY:
                        if (InvTools.isInventoryEmpty(cartInv))
                            return FULL_POWER;
                        continue;
                    case FULL:
                        if (InvTools.isInventoryFull(cartInv))
                            return FULL_POWER;
                        continue;
                    case FILTERED:
                        if (matchesFilter(cartInv))
                            return FULL_POWER;
                        continue;
                    case NOT_EMPTY:
                        if (!InvTools.isInventoryEmpty(cartInv))
                            return FULL_POWER;
                        continue;
                    case ANALOG:
                        return Container.calcRedstoneFromInventory(cartInv);
                }
        }
        return NO_POWER;
    }

    private boolean matchesFilter(IInventory cartInv) {
        for (int i = 0; i < getFilters().getSizeInventory(); i++) {
            ItemStack filter = getFilters().getStackInSlot(i);
            if (filter == null)
                continue;
            int amountFilter = InvTools.countItems(getFilters(), filter);
            int amountCart = InvTools.countItems(cartInv, filter);

            switch (filterMode) {
                case EXACTLY:
                    if (amountCart != amountFilter)
                        return false;
                    break;
                case AT_LEAST:
                    if (amountCart < amountFilter)
                        return false;
                    break;
                case AT_MOST:
                    if (amountCart > amountFilter)
                        return false;
                    break;
                case GREATER_THAN:
                    if (amountCart <= amountFilter)
                        return false;
                    break;
                case LESS_THAN:
                    if (amountCart >= amountFilter)
                        return false;
                    break;
            }
        }
        return true;
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);

        data.setByte("primaryMode", (byte) primaryMode.ordinal());
        data.setByte("filterMode", (byte) filterMode.ordinal());
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);

        primaryMode = PrimaryMode.values()[data.getByte("primaryMode")];
        filterMode = FilterMode.values()[data.getByte("filterMode")];
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);

        data.writeByte((byte) primaryMode.ordinal());
        data.writeByte((byte) filterMode.ordinal());
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);

        primaryMode = PrimaryMode.values()[data.readByte()];
        filterMode = FilterMode.values()[data.readByte()];
    }

    @Override
    public void writeGuiData(DataOutputStream data) throws IOException {
        data.writeByte((byte) primaryMode.ordinal());
        data.writeByte((byte) filterMode.ordinal());
    }

    @Override
    public void readGuiData(DataInputStream data, EntityPlayer sender) throws IOException {
        primaryMode = PrimaryMode.values()[data.readByte()];
        filterMode = FilterMode.values()[data.readByte()];
    }

    @Override
    public boolean blockActivated(EntityPlayer player) {
        openGui(EnumGui.DETECTOR_ITEM, player);
        return true;
    }

    public PrimaryMode getPrimaryMode() {
        return primaryMode;
    }

    public FilterMode getFilterMode() {
        return filterMode;
    }

    public void setPrimaryMode(PrimaryMode primaryMode) {
        this.primaryMode = primaryMode;
    }

    public void setFilterMode(FilterMode filterMode) {
        this.filterMode = filterMode;
    }

    public ISlotController getSlotController() {
        return new ISlotController() {
            @Override
            public boolean isSlotEnabled() {
                return getPrimaryMode() == PrimaryMode.FILTERED;
            }

        };
    }

    @Override
    public EnumDetector getType() {
        return EnumDetector.ITEM;
    }

}
