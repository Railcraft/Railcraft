/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.client.gui;

import mods.railcraft.common.gui.containers.ContainerRCChest;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;

public class GuiRCChest extends GuiTitled {

    /**
     * Window height is calculated with these values; the more rows, the higher
     */
    private final int inventoryRows;

    public GuiRCChest(InventoryPlayer playerInv, IInventory chest) {
        super(chest, new ContainerRCChest(playerInv, chest), "textures/gui/container/generic_54.png");
        this.allowUserInput = false;
        this.inventoryRows = chest.getSizeInventory() / 9;
        this.ySize = 114 + inventoryRows * 18;
    }

    /**
     * Draws the background layer of this container (behind the items).
     */
    @Override
    protected void drawGuiBackground(int x, int y) {
        drawTexturedModalRect(x, y, 0, 0, xSize, inventoryRows * 18 + 17);
        drawTexturedModalRect(x, y + inventoryRows * 18 + 17, 0, 126, xSize, 96);
    }
}