/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.logic;

import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.util.inventory.IInvSlot;
import mods.railcraft.common.util.inventory.InvOp;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The logic behind the void chest.
 */
public class VoidChestLogic extends InventoryLogic {
    private static final int TICK_PER_VOID = 8;

    public VoidChestLogic(Adapter adapter) {
        super(adapter, 27);
    }

    @Override
    public void updateServer() {
        clock().onInterval(TICK_PER_VOID, () -> {
            if (streamSlots().filter(IInvSlot::hasStack).count() >= getSizeInventory() * 0.5F) {
                final double fullness = InvTools.calculateFullness(this);
                streamSlots().findFirst().ifPresent(slot -> {
                    int remove = (int) Math.round(slot.getMaxStackSize() * fullness);
                    slot.removeFromSlot(Math.max(remove, 1), InvOp.EXECUTE);
                });
                List<ItemStack> stacks = streamSlots().map(IInvSlot::getStack).collect(Collectors.toList());
                clear();
                stacks.forEach(this::addStack);
            }
        });
    }

    @Override
    public @Nullable EnumGui getGUI() {
        return EnumGui.CHEST;
    }
}
