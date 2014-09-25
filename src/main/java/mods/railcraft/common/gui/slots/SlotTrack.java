/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.gui.slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import mods.railcraft.api.core.items.ITrackItem;
import mods.railcraft.common.blocks.tracks.TrackTools;

public class SlotTrack extends SlotRailcraft {

    public SlotTrack(IInventory iinventory, int slotIndex, int posX, int posY) {
        super(iinventory, slotIndex, posX, posY);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return stack != null && (stack.getItem() instanceof ITrackItem || TrackTools.isRailBlock(stack));
    }

}
