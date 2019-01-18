/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.gui.slots;

import mods.railcraft.api.items.IMinecartItem;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.filters.StackFilters;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Created by CovertJaguar on 12/29/2018 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class SlotDispensableCart extends SlotRailcraft {

    public SlotDispensableCart(IInventory iinventory, int slotIndex, int posX, int posY) {
        super(iinventory, slotIndex, posX, posY);
    }

    @Override
    public boolean isItemValid(@Nullable ItemStack stack) {
        if (InvTools.isEmpty(stack))
            return false;
        if (stack.getItem() instanceof IMinecartItem)
            return ((IMinecartItem) stack.getItem()).canBePlacedByNonPlayer(stack);
        return StackFilters.MINECART.test(stack);
    }

}
