/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.gui;

import mods.railcraft.client.gui.buttons.GuiBetterButton;
import mods.railcraft.client.gui.buttons.GuiToggleButton;
import mods.railcraft.common.blocks.machine.gamma.TileRFUnloader;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.gui.containers.ContainerLoaderRF;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.PacketDispatcher;
import mods.railcraft.common.util.network.PacketGuiReturn;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;
import java.util.List;

public class GuiUnloaderRF extends TileGui {

    private final String button1Label = LocalizationPlugin.translate("railcraft.gui.energy.unloader.wait");
    //    private final String BUTTON1 = "Wait till Empty";
    private TileRFUnloader tile;

    public GuiUnloaderRF(TileRFUnloader tile) {
        super(tile, new ContainerLoaderRF(tile), RailcraftConstants.GUI_TEXTURE_FOLDER + "gui_rf_device.png");
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
        List<GuiBetterButton> buttons = new ArrayList<GuiBetterButton>();
        buttons.add(new GuiToggleButton(0, w + 75, h + 60, 90, button1Label, tile.waitTillEmpty()));
        GuiTools.newButtonRowBookended(buttonList, w + 5, w + xSize - 5, buttons);
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) {
        if (tile == null)
            return;
        if (guibutton.id == 0) {
            tile.setWaitTillEmpty(!tile.waitTillEmpty());
            ((GuiToggleButton) guibutton).active = tile.waitTillEmpty();
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        GuiTools.drawCenteredString(fontRendererObj, tile.getName(), 6);
    }

    @Override
    public void onGuiClosed() {
        if (Game.isNotHost(tile.getWorld())) {
            PacketGuiReturn pkt = new PacketGuiReturn(tile);
            PacketDispatcher.sendToServer(pkt);
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        TileEntity t = tile.getWorld().getTileEntity(tile.getX(), tile.getY(), tile.getZ());
        if (t instanceof TileRFUnloader)
            tile = (TileRFUnloader) t;
        else
            mc.thePlayer.closeScreen();
    }

}
