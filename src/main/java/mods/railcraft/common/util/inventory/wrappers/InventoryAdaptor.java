/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.inventory.wrappers;

import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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

    public static Optional<IInventoryAdapter> get(World world, BlockPos pos, EnumFacing side, final @Nullable Class<? extends TileEntity> type, final @Nullable Class<? extends TileEntity> exclude) {
        return get(world, pos, side, tile -> {
            //noinspection SimplifiableIfStatement
            if (type != null && !type.isAssignableFrom(tile.getClass()))
                return false;
            return exclude == null || !exclude.isAssignableFrom(tile.getClass());
        });
    }

    public static Optional<IInventoryAdapter> get(World world, BlockPos pos, EnumFacing side, java.util.function.Predicate<TileEntity> filter) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos.offset(side));
        if (!(tile instanceof IInventory) || !filter.test(tile))
            return Optional.empty();
        return get(tile, side.getOpposite());
    }

    public static Optional<IInventoryAdapter> get(@Nullable Object obj, EnumFacing side) {
        IInventoryAdapter inv = null;
        if (obj instanceof TileEntityChest) {
            TileEntityChest chest = (TileEntityChest) obj;
            inv = new ChestWrapper(chest);
        } else if (obj instanceof ISidedInventory) {
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

    public static Optional<IInventoryAdapter> get(@Nullable Object obj) {
        IInventoryAdapter inv = null;
        if (obj instanceof IInventoryAdapter) {
            inv = (IInventoryAdapter) obj;
        } else if (obj instanceof TileEntityChest) {
            TileEntityChest chest = (TileEntityChest) obj;
            inv = new ChestWrapper(chest);
        } else if (obj instanceof IInventory) {
            inv = get((IInventory) obj);
        } else if (obj instanceof ICapabilityProvider && ((ICapabilityProvider) obj).hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
            inv = get(
                    Objects.requireNonNull(
                            ((ICapabilityProvider) obj).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)
                    )
            );
        }
        return Optional.ofNullable(inv);
    }

}
