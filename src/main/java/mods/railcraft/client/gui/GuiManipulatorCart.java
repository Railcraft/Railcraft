/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui;

import mods.railcraft.client.gui.buttons.GuiMultiButton;
import mods.railcraft.common.blocks.machine.manipulator.TileItemManipulator;
import mods.railcraft.common.blocks.machine.manipulator.TileManipulatorCart;
import mods.railcraft.common.gui.containers.ContainerManipulatorCart;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.PacketBuilder;
import net.minecraft.client.gui.GuiButton;

import java.io.IOException;

public class GuiManipulatorCart extends GuiTitled {

    private final String CART_FILTER_LABEL = LocalizationPlugin.translate("gui.railcraft.filters.carts");
    private GuiMultiButton transferMode;
    private GuiMultiButton redstoneMode;
    private final TileManipulatorCart tile;

    public GuiManipulatorCart(TileManipulatorCart tile, ContainerManipulatorCart container, String texture) {
        super(tile, container, texture);
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
        if (tile instanceof TileItemManipulator)
            buttonList.add(transferMode = GuiMultiButton.create(0, w + 73, h + 45, 30, ((TileItemManipulator) tile).getTransferModeController().copy()));
        buttonList.add(redstoneMode = GuiMultiButton.create(0, w + 73, h + 62, 30, tile.redstoneController().copy()));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        if (((ContainerManipulatorCart) container).hasCartFilter)
            fontRenderer.drawString(CART_FILTER_LABEL, 75, 16, 0x404040);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        updateServer();
    }

    private void updateServer() {
        if (Game.isClient(tile.getWorld())) {
            if (tile instanceof TileItemManipulator)
                ((TileItemManipulator) tile).getTransferModeController().setCurrentState(transferMode.getController().getCurrentState());
            tile.redstoneController().setCurrentState(redstoneMode.getController().getCurrentState());
            PacketBuilder.instance().sendGuiReturnPacket(tile);
        }
    }

    @Override
    public void onGuiClosed() {
        updateServer();
    }

}
