/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.detector.types;

import mods.railcraft.common.blocks.detector.DetectorFilter;
import mods.railcraft.common.blocks.detector.EnumDetector;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.InventoryComposite;
import mods.railcraft.common.util.inventory.filters.StackFilters;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

import static mods.railcraft.common.plugins.forge.PowerPlugin.FULL_POWER;
import static mods.railcraft.common.plugins.forge.PowerPlugin.NO_POWER;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class DetectorItem extends DetectorFilter {

    private PrimaryMode primaryMode = PrimaryMode.ANYTHING;
    private FilterMode filterMode = FilterMode.AT_LEAST;

    public DetectorItem() {
        super(9);
    }

    @Override
    public int testCarts(List<EntityMinecart> carts) {
        for (EntityMinecart cart : carts) {
            InventoryComposite cartInv = null;
            if (cart instanceof IInventory)
                cartInv = InventoryComposite.of(cart);
            if (cartInv != null && cartInv.slotCount() > 0)
                switch (primaryMode) {
                    case ANYTHING:
                        return FULL_POWER;
                    case EMPTY:
                        if (cartInv.hasNoItems())
                            return FULL_POWER;
                        continue;
                    case FULL:
                        if (cartInv.isFull())
                            return FULL_POWER;
                        continue;
                    case FILTERED:
                        if (matchesFilter(cartInv))
                            return FULL_POWER;
                        continue;
                    case NOT_EMPTY:
                        if (cartInv.hasItems())
                            return FULL_POWER;
                        continue;
                    case ANALOG:
                        return cartInv.calcRedstone();
                }
        }
        return NO_POWER;
    }

    private boolean matchesFilter(InventoryComposite cartInv) {
        for (int i = 0; i < getFilters().getSizeInventory(); i++) {
            ItemStack filter = getFilters().getStackInSlot(i);
            if (InvTools.isEmpty(filter))
                continue;
            Predicate<ItemStack> stackFilter = StackFilters.anyMatch(filter);
            int amountFilter = getFilters().countItems(stackFilter);
            int amountCart = cartInv.countItems(stackFilter);

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
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);

        data.writeByte((byte) primaryMode.ordinal());
        data.writeByte((byte) filterMode.ordinal());
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);

        primaryMode = PrimaryMode.values()[data.readByte()];
        filterMode = FilterMode.values()[data.readByte()];
    }

    @Override
    public void writeGuiData(RailcraftOutputStream data) throws IOException {
        data.writeByte((byte) primaryMode.ordinal());
        data.writeByte((byte) filterMode.ordinal());
    }

    @Override
    public void readGuiData(RailcraftInputStream data, EntityPlayer sender) throws IOException {
        primaryMode = PrimaryMode.values()[data.readByte()];
        filterMode = FilterMode.values()[data.readByte()];
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        openGui(EnumGui.DETECTOR_ITEM, player);
        return true;
    }

    public PrimaryMode getPrimaryMode() {
        return primaryMode;
    }

    public void setPrimaryMode(PrimaryMode primaryMode) {
        this.primaryMode = primaryMode;
    }

    public FilterMode getFilterMode() {
        return filterMode;
    }

    public void setFilterMode(FilterMode filterMode) {
        this.filterMode = filterMode;
    }

    @Override
    public EnumDetector getType() {
        return EnumDetector.ITEM;
    }

    public enum PrimaryMode {

        EMPTY, FULL, ANYTHING, FILTERED, NOT_EMPTY, ANALOG;

        @Override
        public String toString() {
            return LocalizationPlugin.translate("gui.railcraft.detector.item." + name().toLowerCase(Locale.ENGLISH));
        }

    }

    public enum FilterMode {

        AT_LEAST, AT_MOST, EXACTLY, LESS_THAN, GREATER_THAN;

        @Override
        public String toString() {
            return LocalizationPlugin.translate("gui.railcraft.detector.item." + name().toLowerCase(Locale.ENGLISH));
        }

    }

}
