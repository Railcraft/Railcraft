/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.inventory;

import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.inventory.wrappers.ChestWrapper;
import mods.railcraft.common.util.inventory.wrappers.IInventoryObject;
import mods.railcraft.common.util.inventory.wrappers.InventoryAdaptor;
import mods.railcraft.common.util.inventory.wrappers.SidedInventoryDecorator;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

/**
 * Created by CovertJaguar on 5/28/2017 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class InventoryFactory {
    private InventoryFactory() {
    }

    public static Optional<IInventoryObject> get(World world, BlockPos pos, EnumFacing side, final @Nullable Class<? extends TileEntity> type, final @Nullable Class<? extends TileEntity> exclude) {
        return get(world, pos, side, tile -> {
            //noinspection SimplifiableIfStatement
            if (type != null && !type.isAssignableFrom(tile.getClass()))
                return false;
            return exclude == null || !exclude.isAssignableFrom(tile.getClass());
        });
    }

    public static Optional<IInventoryObject> get(World world, BlockPos pos, EnumFacing side, java.util.function.Predicate<TileEntity> filter) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos.offset(side));
        if (!(tile instanceof IInventory) || !filter.test(tile))
            return Optional.empty();
        return get(tile, side.getOpposite());
    }

    public static Optional<IInventoryObject> get(@Nullable Object obj, EnumFacing side) {
        IInventoryObject inv = null;
        if (obj instanceof TileEntityChest) {
            TileEntityChest chest = (TileEntityChest) obj;
            inv = new ChestWrapper(chest);
        } else if (obj instanceof ISidedInventory) {
            inv = new SidedInventoryDecorator((ISidedInventory) obj, side);
        } else if (obj instanceof IInventory) {
            inv = InventoryAdaptor.get((IInventory) obj);
        } else if (obj instanceof ICapabilityProvider && ((ICapabilityProvider) obj).hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)) {
            inv = InventoryAdaptor.get(
                    Objects.requireNonNull(
                            ((ICapabilityProvider) obj).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)
                    )
            );
        }
        return Optional.ofNullable(inv);
    }

    public static Optional<IInventoryObject> get(@Nullable Object obj) {
        IInventoryObject inv = null;
        if (obj instanceof IInventoryObject) {
            inv = (IInventoryObject) obj;
        } else if (obj instanceof TileEntityChest) {
            TileEntityChest chest = (TileEntityChest) obj;
            inv = new ChestWrapper(chest);
        } else if (obj instanceof IInventory) {
            inv = InventoryAdaptor.get((IInventory) obj);
        } else if (obj instanceof ICapabilityProvider && ((ICapabilityProvider) obj).hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
            inv = InventoryAdaptor.get(
                    Objects.requireNonNull(
                            ((ICapabilityProvider) obj).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)
                    )
            );
        }
        return Optional.ofNullable(inv);
    }
}
