/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.gui;

import mods.railcraft.client.gui.buttons.GuiMultiButton;
import net.minecraft.entity.player.InventoryPlayer;
import mods.railcraft.common.blocks.machine.gamma.TileFluidLoader;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.gui.containers.ContainerFluidLoader;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.PacketBuilder;

public class GuiLoaderFluid extends TileGui {

    private final String FILTER_LABEL = LocalizationPlugin.translate("railcraft.gui.filters");
    private final TileFluidLoader tile;
    private GuiMultiButton button;

    public GuiLoaderFluid(InventoryPlayer inv, TileFluidLoader tile) {
        super(tile, new ContainerFluidLoader(inv, tile), RailcraftConstants.GUI_TEXTURE_FOLDER + "gui_fluid_loader.png");
        this.tile = tile;
    }

    @Override
    public void initGui() {
        super.initGui();
        if (tile == null) {
            return;
        }
        buttonList.clear();
        int w = (width - xSize) / 2;
        int h = (height - ySize) / 2;
        button = new GuiMultiButton(0, w + 40, h + 60, 80, tile.getStateController().copy());
        buttonList.add(button);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        int sWidth = fontRendererObj.getStringWidth(tile.getName());
        int sPos = xSize / 2 - sWidth / 2;
        fontRendererObj.drawString(tile.getName(), sPos, 6, 0x404040);
        fontRendererObj.drawString(FILTER_LABEL, 62, 25, 0x404040);
    }

    @Override
    public void onGuiClosed() {
        if (Game.isNotHost(tile.getWorld())) {
            tile.getStateController().setCurrentState(button.getController().getCurrentState());
            PacketBuilder.instance().sendGuiReturnPacket(tile);
        }
    }

}
