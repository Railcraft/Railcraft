/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.gui.containers;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnace;
import mods.railcraft.common.blocks.machine.alpha.TileBlastFurnace;
import mods.railcraft.common.gui.slots.SlotStackFilter;
import mods.railcraft.common.util.network.PacketBuilder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class ContainerBlastFurnace extends RailcraftContainer {

    private TileBlastFurnace furnace;
    private int lastCookTime = 0;
    private int lastBurnTime = 0;
    private int lastItemBurnTime = 0;

    public ContainerBlastFurnace(InventoryPlayer player, TileBlastFurnace tile) {
        super(tile);
        this.furnace = tile;
        this.addSlot(new SlotStackFilter(TileBlastFurnace.INPUT_FILTER, tile, 0, 56, 17));
        this.addSlot(new SlotStackFilter(TileBlastFurnace.FUEL_FILTER, tile, 1, 56, 53));
        this.addSlot(new SlotFurnace(player.player, tile, 2, 116, 35));
        int var3;

        for (var3 = 0; var3 < 3; ++var3) {
            for (int var4 = 0; var4 < 9; ++var4) {
                this.addSlot(new Slot(player, var4 + var3 * 9 + 9, 8 + var4 * 18, 84 + var3 * 18));
            }
        }

        for (var3 = 0; var3 < 9; ++var3) {
            this.addSlot(new Slot(player, var3, 8 + var3 * 18, 142));
        }
    }

    @Override
    public void addCraftingToCrafters(ICrafting player) {
        super.addCraftingToCrafters(player);
        player.sendProgressBarUpdate(this, 0, furnace.getCookTime());
        PacketBuilder.instance().sendGuiIntegerPacket((EntityPlayerMP) player, windowId, 1, furnace.burnTime);
        PacketBuilder.instance().sendGuiIntegerPacket((EntityPlayerMP) player, windowId, 2, furnace.currentItemBurnTime);
    }

    /**
     * Updates crafting matrix; called from onCraftMatrixChanged. Args: none
     */
    @Override
    public void sendUpdateToClient() {
        super.sendUpdateToClient();

        for (int i = 0; i < crafters.size(); ++i) {
            ICrafting player = (ICrafting) crafters.get(i);

            if (lastCookTime != furnace.getCookTime())
                player.sendProgressBarUpdate(this, 0, furnace.getCookTime());

            if (lastBurnTime != furnace.burnTime)
                PacketBuilder.instance().sendGuiIntegerPacket((EntityPlayerMP) player, windowId, 1, furnace.burnTime);

            if (lastItemBurnTime != furnace.currentItemBurnTime)
                PacketBuilder.instance().sendGuiIntegerPacket((EntityPlayerMP) player, windowId, 2, furnace.currentItemBurnTime);
        }

        lastCookTime = furnace.getCookTime();
        lastBurnTime = furnace.burnTime;
        lastItemBurnTime = furnace.currentItemBurnTime;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data) {
        if (id == 0)
            this.furnace.setCookTime(data);

        if (id == 1)
            this.furnace.burnTime = data;

        if (id == 2)
            this.furnace.currentItemBurnTime = data;
    }

}
