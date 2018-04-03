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
import mods.railcraft.common.util.network.PacketBuilder;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnaceOutput;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerBlastFurnace extends RailcraftContainer {

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
        int var3;

        for (var3 = 0; var3 < 3; ++var3) {
            for (int var4 = 0; var4 < 9; ++var4) {
                addSlot(new Slot(player, var4 + var3 * 9 + 9, 8 + var4 * 18, 84 + var3 * 18));
            }
        }

        for (var3 = 0; var3 < 9; ++var3) {
            addSlot(new Slot(player, var3, 8 + var3 * 18, 142));
        }
    }

    @Override
    public void addListener(IContainerListener player) {
        super.addListener(player);
        player.sendWindowProperty(this, 0, furnace.getCookTime());
        PacketBuilder.instance().sendGuiIntegerPacket((EntityPlayerMP) player, windowId, 1, furnace.burnTime);
        PacketBuilder.instance().sendGuiIntegerPacket((EntityPlayerMP) player, windowId, 2, furnace.currentItemBurnTime);
    }

    /**
     * Updates crafting matrix; called from onCraftMatrixChanged. Args: none
     */
    @Override
    public void sendUpdateToClient() {
        super.sendUpdateToClient();

        for (IContainerListener listener : listeners) {
            if (lastCookTime != furnace.getCookTime())
                listener.sendWindowProperty(this, 0, furnace.getCookTime());

            if (lastBurnTime != furnace.burnTime)
                PacketBuilder.instance().sendGuiIntegerPacket((EntityPlayerMP) listener, windowId, 1, furnace.burnTime);

            if (lastItemBurnTime != furnace.currentItemBurnTime)
                PacketBuilder.instance().sendGuiIntegerPacket((EntityPlayerMP) listener, windowId, 2, furnace.currentItemBurnTime);
        }

        lastCookTime = furnace.getCookTime();
        lastBurnTime = furnace.burnTime;
        lastItemBurnTime = furnace.currentItemBurnTime;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data) {
        if (id == 0)
            furnace.setCookTime(data);

        if (id == 1)
            furnace.burnTime = data;

        if (id == 2)
            furnace.currentItemBurnTime = data;
    }

}
