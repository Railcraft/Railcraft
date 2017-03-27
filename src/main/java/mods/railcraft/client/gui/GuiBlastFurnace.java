/*
 * Copyright (c) CovertJaguar, 2011-2017
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.gui;

import mods.railcraft.common.blocks.machine.alpha.TileBlastFurnace;
import mods.railcraft.common.gui.containers.ContainerBlastFurnace;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.text.translation.I18n;

public class GuiBlastFurnace extends TileGui {

    private final TileBlastFurnace tile;

    public GuiBlastFurnace(InventoryPlayer par1InventoryPlayer, TileBlastFurnace tile) {
        super(tile, new ContainerBlastFurnace(par1InventoryPlayer, tile), "textures/gui/container/furnace.png");
        this.tile = tile;
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of
     * the items)
     */
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        fontRendererObj.drawString(I18n.translateToLocal("container.inventory"), 8, ySize - 96 + 2, 4210752);
    }

    /**
     * Draw the background layer for the GuiContainer (everything behind the
     * items)
     */
    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        super.drawGuiContainerBackgroundLayer(par1, par3, par3);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;

        if (tile.clientBurning) {
            int scale = tile.getBurnProgressScaled(12);
            drawTexturedModalRect(x + 56, y + 36 + 12 - scale, 176, 12 - scale, 14, scale + 2);
        }

        int scale = tile.getCookProgressScaled(24);
        drawTexturedModalRect(x + 79, y + 34, 176, 14, scale + 1, 16);
    }
}
