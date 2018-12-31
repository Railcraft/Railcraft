/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
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
import net.minecraft.inventory.Slot;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class ContainerBlastFurnace extends RailcraftContainer {

    private final BlastFurnaceLogic logic;
    private int lastCookTime;
    private int lastCookTimeTotal;
    private int lastBurnTime;
    private int lastItemBurnTime;

    public ContainerBlastFurnace(InventoryPlayer player, BlastFurnaceLogic logic) {
        super(logic);
        this.logic = logic;
        addSlot(new SlotStackFilter(BlastFurnaceLogic.INPUT_FILTER, logic, BlastFurnaceLogic.SLOT_INPUT, 56, 17));
        addSlot(new SlotStackFilter(BlastFurnaceLogic.FUEL_FILTER, logic, BlastFurnaceLogic.SLOT_FUEL, 56, 53));
        addSlot(new SlotOutput(logic, BlastFurnaceLogic.SLOT_OUTPUT, 116, 21));
        addSlot(new SlotOutput(logic, BlastFurnaceLogic.SLOT_SLAG, 116, 53));

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlot(new Slot(player, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int i = 0; i < 9; ++i) {
            addSlot(new Slot(player, i, 8 + i * 18, 142));
        }
    }

    @Override
    public void addListener(IContainerListener player) {
        super.addListener(player);
        player.sendWindowProperty(this, 0, logic.getProgress());
        player.sendWindowProperty(this, 1, logic.getDuration());
        player.sendWindowProperty(this, 2, logic.burnTime);
        player.sendWindowProperty(this, 3, logic.currentItemBurnTime);
    }

    /**
     * Updates crafting matrix; called from onCraftMatrixChanged. Args: none
     */
    @Override
    public void sendUpdateToClient() {
        super.sendUpdateToClient();

        for (IContainerListener listener : listeners) {
            if (lastCookTime != logic.getProgress())
                listener.sendWindowProperty(this, 0, logic.getProgress());
            if (lastCookTimeTotal != logic.getDuration())
                listener.sendWindowProperty(this, 1, logic.getDuration());

            if (lastBurnTime != logic.burnTime)
                listener.sendWindowProperty(this, 2, logic.burnTime);

            if (lastItemBurnTime != logic.currentItemBurnTime)
                listener.sendWindowProperty(this, 3, logic.currentItemBurnTime);
        }

        lastCookTime = logic.getProgress();
        lastCookTimeTotal = logic.getDuration();
        lastBurnTime = logic.burnTime;
        lastItemBurnTime = logic.currentItemBurnTime;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data) {
        if (id == 0)
            logic.setProgress(data);

        if (id == 1)
            logic.setDuration(data);

        if (id == 2)
            logic.burnTime = data;

        if (id == 3)
            logic.currentItemBurnTime = data;
    }

}
