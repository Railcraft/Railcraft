/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.interfaces;

import mods.railcraft.common.blocks.TileRailcraft;
import mods.railcraft.common.util.inventory.IInventoryImplementor;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

/**
 * Created by cover on 10/8/2018 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface ITileInventory extends ITileCompare, ICapabilityProvider, ITile, IInventoryImplementor {

    @Override
    default boolean isUsableByPlayer(EntityPlayer player) {
        return TileRailcraft.isUsableByPlayerHelper(tile(), player);
    }

    default void dropItem(ItemStack stack) {
        TileEntity te = tile();
        InvTools.dropItem(stack, te.getWorld(), te.getPos());
    }

    @Override
    default int getComparatorInputOverride() {
        return Container.calcRedstoneFromInventory(this);
    }
}
