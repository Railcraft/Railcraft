/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.containers;

import mods.railcraft.api.crafting.Crafters;
import mods.railcraft.common.gui.slots.SlotFluidContainerEmpty;
import mods.railcraft.common.gui.slots.SlotOutput;
import mods.railcraft.common.gui.slots.SlotRailcraft;
import mods.railcraft.common.gui.widgets.FluidGaugeWidget;
import mods.railcraft.common.blocks.logic.CokeOvenLogic;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerCokeOven extends RailcraftContainer {

    private int lastProgress, lastDuration;
    private final CokeOvenLogic logic;

    public ContainerCokeOven(InventoryPlayer inventoryplayer, CokeOvenLogic logic) {
        super(logic);
        this.logic = logic;

        addWidget(new FluidGaugeWidget(logic.getTankManager().get(0), 90, 24, 176, 0, 48, 47));

        addSlot(new SlotCokeOven(logic, 0, 16, 43));
        addSlot(new SlotOutput(logic, 1, 62, 43));
        addSlot(new SlotOutput(logic, 2, 149, 57));
        addSlot(new SlotFluidContainerEmpty(logic, 3, 149, 22));
        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 9; k++) {
                addSlot(new Slot(inventoryplayer, k + i * 9 + 9, 8 + k * 18, 84 + i * 18));
            }

        }

        for (int j = 0; j < 9; j++) {
            addSlot(new Slot(inventoryplayer, j, 8 + j * 18, 142));
        }

    }

    @Override
    public void sendUpdateToClient() {
        super.sendUpdateToClient();

        for (IContainerListener listener : listeners) {
            int progress = logic.getProgress();
            if (lastProgress != progress)
                listener.sendWindowProperty(this, 10, progress);

            int duration = logic.getDuration();
            if (lastDuration != duration)
                listener.sendWindowProperty(this, 11, duration);
        }

        lastProgress = logic.getProgress();
        lastDuration = logic.getDuration();
    }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);

        listener.sendWindowProperty(this, 10, logic.getProgress());
        listener.sendWindowProperty(this, 11, logic.getDuration());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data) {

        switch (id) {
            case 10:
                logic.setProgress(data);
                break;
            case 11:
                logic.setDuration(data);
        }
    }

    private class SlotCokeOven extends SlotRailcraft {

        public SlotCokeOven(IInventory iinventory, int slotIndex, int posX, int posY) {
            super(iinventory, slotIndex, posX, posY);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return Crafters.cokeOven().getRecipe(stack).isPresent();
        }

    }
}
