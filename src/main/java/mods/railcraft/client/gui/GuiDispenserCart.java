/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui;

import mods.railcraft.common.blocks.machine.manipulator.TileDispenserCart;
import mods.railcraft.common.gui.containers.ContainerDispenserCart;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiDispenserCart extends GuiTitled {

    private final InventoryPlayer inv;

    public GuiDispenserCart(InventoryPlayer inv, TileDispenserCart tile) {
        super(tile, new ContainerDispenserCart(inv, tile), "gui_cart_slots.png");
        ySize = 140;
        this.inv = inv;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        fontRenderer.drawString(inv.getDisplayName().getUnformattedText(), 8, ySize - 96 + 2, 4210752);
    }
}
