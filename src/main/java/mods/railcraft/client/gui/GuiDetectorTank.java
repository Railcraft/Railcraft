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
import mods.railcraft.common.blocks.detector.TileDetector;
import mods.railcraft.common.blocks.detector.types.DetectorTank;
import mods.railcraft.common.gui.containers.ContainerDetectorTank;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.PacketDispatcher;
import mods.railcraft.common.util.network.PacketGuiReturn;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiDetectorTank extends GuiTitled {

    private final TileDetector tile;
    private final DetectorTank detector;
    private GuiMultiButton button;

    public GuiDetectorTank(InventoryPlayer inv, TileDetector tile) {
        super(tile, new ContainerDetectorTank(inv, tile), "gui_detector_tank.png");
        this.tile = tile;
        this.detector = (DetectorTank) tile.getDetector();
        xSize = 176;
        ySize = 140;
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
        buttonList.add(button = GuiMultiButton.create(0, w + 95, h + 22, 60, detector.getButtonController().copy()));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        fontRenderer.drawString(LocalizationPlugin.translate("gui.railcraft.filter"), 50, 29, 0x404040);
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();

        if (Game.isClient(tile.getWorld())) {
            detector.getButtonController().setCurrentState(button.getController().getCurrentState());
            PacketGuiReturn pkt = new PacketGuiReturn(tile);
            PacketDispatcher.sendToServer(pkt);
        }
    }

}
