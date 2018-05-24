/*
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.gui.containers;

import mods.railcraft.common.blocks.multi.TileBlastFurnace;
import mods.railcraft.common.gui.slots.SlotStackFilter;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnaceOutput;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class ContainerBlastFurnace extends RailcraftContainer {

    private final TileBlastFurnace furnace;
    private int lastCookTime;
    private int lastBurnTime;
    private int lastItemBurnTime;

    public ContainerBlastFurnace(InventoryPlayer player, TileBlastFurnace tile) {
        super(tile);
        this.furnace = tile;
        addSlot(new SlotStackFilter(TileBlastFurnace.INPUT_FILTER, tile, 0, 56, 17));
        addSlot(new SlotStackFilter(TileBlastFurnace.FUEL_FILTER, tile, 1, 56, 53));
        addSlot(new SlotFurnaceOutput(player.player, tile, 2, 116, 35));

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
        player.sendWindowProperty(this, 0, furnace.getMasterCookTime());
        player.sendWindowProperty(this, 1, furnace.fuelTimeLeft);
        player.sendWindowProperty(this, 2, furnace.currentItemBurnTime);
    }

    /**
     * Updates crafting matrix; called from onCraftMatrixChanged. Args: none
     */
    @Override
    public void sendUpdateToClient() {
        super.sendUpdateToClient();

        for (IContainerListener listener : listeners) {
            if (lastCookTime != furnace.getMasterCookTime())
                listener.sendWindowProperty(this, 0, furnace.getMasterCookTime());

            if (lastBurnTime != furnace.fuelTimeLeft)
                listener.sendWindowProperty(this, 1, furnace.fuelTimeLeft);

            if (lastItemBurnTime != furnace.currentItemBurnTime)
                listener.sendWindowProperty(this, 2, furnace.currentItemBurnTime);
        }

        lastCookTime = furnace.getMasterCookTime();
        lastBurnTime = furnace.fuelTimeLeft;
        lastItemBurnTime = furnace.currentItemBurnTime;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data) {
        if (id == 0)
            furnace.setCookTime(data);

        if (id == 1)
            furnace.fuelTimeLeft = data;

        if (id == 2)
            furnace.currentItemBurnTime = data;
    }

}
