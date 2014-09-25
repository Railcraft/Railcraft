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
import mods.railcraft.common.blocks.RailcraftTileEntity;
import mods.railcraft.common.blocks.machine.gamma.TileLoaderItemBase;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.gui.containers.ContainerItemLoader;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.PacketBuilder;

public class GuiLoaderItem extends TileGui {

    private final String FILTER_LABEL = LocalizationPlugin.translate("railcraft.gui.filters");
    private final String CART_FILTER_LABEL = LocalizationPlugin.translate("railcraft.gui.filters.carts");
    private final String BUFFER_LABEL = LocalizationPlugin.translate("railcraft.gui.item.loader.buffer");
    private GuiMultiButton transferMode;
    private GuiMultiButton redstoneMode;
    private final TileLoaderItemBase tile;

    public GuiLoaderItem(InventoryPlayer inv, TileLoaderItemBase tile) {
        super((RailcraftTileEntity) tile, new ContainerItemLoader(inv, tile), RailcraftConstants.GUI_TEXTURE_FOLDER + "gui_item_loader.png");
        this.tile = tile;
    }

    @Override
    public void initGui() {
        super.initGui();
        if (tile == null)
            return;
        buttonList.clear();
        int w = (width - xSize) / 2;
        int h = (height - ySize) / 2;
        buttonList.add(transferMode = new GuiMultiButton(0, w + 62, h + 45, 52, tile.getTransferModeController().copy()));
        buttonList.add(redstoneMode = new GuiMultiButton(0, w + 62, h + 62, 52, tile.getRedstoneModeController().copy()));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        GuiTools.drawCenteredString(fontRendererObj, tile.getName(), 6);
        fontRendererObj.drawString(FILTER_LABEL, 18, 16, 0x404040);
        fontRendererObj.drawString(CART_FILTER_LABEL, 75, 16, 0x404040);
        fontRendererObj.drawString(BUFFER_LABEL, 126, 16, 0x404040);
    }

    @Override
    public void onGuiClosed() {
        if (Game.isNotHost(tile.getWorld())) {
            tile.getTransferModeController().setCurrentState(transferMode.getController().getCurrentState());
            tile.getRedstoneModeController().setCurrentState(redstoneMode.getController().getCurrentState());
            PacketBuilder.instance().sendGuiReturnPacket(tile);
        }
    }

}
