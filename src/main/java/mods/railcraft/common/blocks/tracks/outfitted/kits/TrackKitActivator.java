/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.tracks.outfitted.kits;

import mods.railcraft.common.blocks.tracks.outfitted.TrackKits;
import mods.railcraft.common.carts.CartTools;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.util.inventory.InventoryAdvanced;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.math.BlockPos;

import java.util.function.Predicate;

public class TrackKitActivator extends TrackKitPowered {

    private static final int POWER_PROPAGATION = 8;
    private final InventoryAdvanced cartFilter = new InventoryAdvanced(3).setInventoryStackLimit(1).callback(this).phantom();
    private final Predicate<EntityMinecart> cartMatcher = CartTools.cartFilterMatcher(cartFilter);

    public IInventory getCartFilter() {
        return cartFilter;
    }

    @Override
    public TrackKits getTrackKitContainer() {
        return TrackKits.ACTIVATOR;
    }

    @Override
    protected EnumGui getGUI() {
        return EnumGui.TRACK_ACTIVATOR;
    }

    @Override
    public void onMinecartPass(EntityMinecart cart) {
        if (!cartMatcher.test(cart)) {
            return;
        }

        BlockPos pos = getPos();
        cart.onActivatorRailPass(pos.getX(), pos.getY(), pos.getZ(), isPowered());
    }

    @Override
    public int getPowerPropagation() {
        return POWER_PROPAGATION;
    }
}
