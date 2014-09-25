/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.gui;

import net.minecraft.entity.player.InventoryPlayer;
import mods.railcraft.client.gui.buttons.GuiMultiButton;
import mods.railcraft.common.blocks.detector.TileDetector;
import mods.railcraft.common.blocks.detector.types.DetectorTank;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.gui.containers.ContainerDetectorTank;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.PacketDispatcher;
import mods.railcraft.common.util.network.PacketGuiReturn;

public class GuiDetectorTank extends TileGui {

    private final String label;
    private final TileDetector tile;
    private final DetectorTank detector;
    private GuiMultiButton button;

    public GuiDetectorTank(InventoryPlayer inv, TileDetector tile) {
        super(tile, new ContainerDetectorTank(inv, tile), RailcraftConstants.GUI_TEXTURE_FOLDER + "gui_detector_tank.png");
        this.tile = tile;
        this.detector = (DetectorTank) tile.getDetector();
        xSize = 176;
        ySize = 140;
        
        label = tile.getName();
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
        buttonList.add(button = new GuiMultiButton(0, w + 95, h + 22, 60, detector.getButtonController().copy()));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        int sWidth = fontRendererObj.getStringWidth(label);
        int sPos = xSize / 2 - sWidth / 2;
        fontRendererObj.drawString(label, sPos, 6, 0x404040);
        fontRendererObj.drawString(LocalizationPlugin.translate("railcraft.gui.filter"), 50, 29, 0x404040);
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();

        if (Game.isNotHost(tile.getWorld())) {
            detector.getButtonController().setCurrentState(button.getController().getCurrentState());
            PacketGuiReturn pkt = new PacketGuiReturn(tile);
            PacketDispatcher.sendToServer(pkt);
        }
    }

}
