/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.inventory.wrappers;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

/**
 * Created by CovertJaguar on 3/15/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class InventoryAdaptor implements IInventoryAdapter {
    private final Object inventory;

    private InventoryAdaptor(Object inventory) {
        this.inventory = inventory;
    }

    @Override
    public Object getBackingObject() {
        return inventory;
    }

    public static InventoryAdaptor get(final IInventory inventory) {
        Objects.requireNonNull(inventory);
        return new InventoryAdaptor(inventory) {

            @Override
            public int getNumSlots() {
                return inventory.getSizeInventory();
            }
        };
    }

    public static InventoryAdaptor get(final IItemHandler inventory) {
        Objects.requireNonNull(inventory);
        return new InventoryAdaptor(inventory) {

            @Override
            public int getNumSlots() {
                return inventory.getSlots();
            }
        };
    }

    public static Optional<IInventoryAdapter> get(@Nullable Object obj) {
        return get(obj, null);
    }

    // TODO: If we want to prefer IItemHandler, this is the place it needs to be done.
    public static Optional<IInventoryAdapter> get(@Nullable Object obj, @Nullable EnumFacing side) {
        IInventoryAdapter inv = null;
        if (obj instanceof IInventoryAdapter) {
            inv = (IInventoryAdapter) obj;
        } else if (obj instanceof TileEntityChest) {
            TileEntityChest chest = (TileEntityChest) obj;
            inv = new ChestWrapper(chest);
        } else if (side != null && obj instanceof ISidedInventory) {
            inv = new SidedInventoryDecorator((ISidedInventory) obj, side);
        } else if (obj instanceof IInventory) {
            inv = get((IInventory) obj);
        } else if (obj instanceof ICapabilityProvider && ((ICapabilityProvider) obj).hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)) {
            inv = get(
                    Objects.requireNonNull(
                            ((ICapabilityProvider) obj).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)
                    )
            );
        }
        return Optional.ofNullable(inv);
    }

}
