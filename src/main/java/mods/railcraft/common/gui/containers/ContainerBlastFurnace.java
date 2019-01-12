/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.containers;

import mods.railcraft.common.blocks.logic.BlastFurnaceLogic;
import mods.railcraft.common.gui.slots.SlotOutput;
import mods.railcraft.common.gui.slots.SlotStackFilter;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class ContainerBlastFurnace extends ContainerCrafter {

    private final BlastFurnaceLogic logic;
    private int lastBurnTime;
    private int lastItemBurnTime;

    public ContainerBlastFurnace(InventoryPlayer invPlayer, BlastFurnaceLogic logic) {
        super(logic);
        this.logic = logic;
        addSlot(new SlotStackFilter(BlastFurnaceLogic.INPUT_FILTER, logic, BlastFurnaceLogic.SLOT_INPUT, 56, 17));
        addSlot(new SlotStackFilter(BlastFurnaceLogic.FUEL_FILTER, logic, BlastFurnaceLogic.SLOT_FUEL, 56, 53));
        addSlot(new SlotOutput(logic, BlastFurnaceLogic.SLOT_OUTPUT, 116, 21));
        addSlot(new SlotOutput(logic, BlastFurnaceLogic.SLOT_SLAG, 116, 53));

        addPlayerSlots(invPlayer);
    }

    @Override
    public void addListener(IContainerListener player) {
        super.addListener(player);
        player.sendWindowProperty(this, 0, logic.burnTime);
        player.sendWindowProperty(this, 1, logic.currentItemBurnTime);
    }

    /**
     * Updates crafting matrix; called from onCraftMatrixChanged. Args: none
     */
    @Override
    public void sendUpdateToClient() {
        super.sendUpdateToClient();

        for (IContainerListener listener : listeners) {
            if (lastBurnTime != logic.burnTime)
                listener.sendWindowProperty(this, 0, logic.burnTime);

            if (lastItemBurnTime != logic.currentItemBurnTime)
                listener.sendWindowProperty(this, 1, logic.currentItemBurnTime);
        }

        lastBurnTime = logic.burnTime;
        lastItemBurnTime = logic.currentItemBurnTime;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data) {
        switch (id) {
            case 0:
                logic.burnTime = data;
                break;
            case 1:
                logic.currentItemBurnTime = data;
        }
    }

}
