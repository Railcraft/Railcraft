/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import mods.railcraft.client.gui.buttons.GuiToggleButton;
import mods.railcraft.common.blocks.machine.gamma.TileEnergyUnloader;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.gui.containers.ContainerEnergyLoader;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.PacketDispatcher;
import mods.railcraft.common.util.network.PacketGuiReturn;

public class GuiUnloaderEnergy extends TileGui {

    private final String label;
    private final String button1Label = LocalizationPlugin.translate("railcraft.gui.energy.unloader.wait");
//    private final String BUTTON1 = "Wait till Empty";
    private TileEnergyUnloader tile;

    public GuiUnloaderEnergy(InventoryPlayer inv, TileEnergyUnloader tile) {
        super(tile, new ContainerEnergyLoader(inv, tile), RailcraftConstants.GUI_TEXTURE_FOLDER + "gui_energy_loader.png");
        this.tile = tile;
        label = tile.getName();
    }

    @Override
    public void initGui() {
        super.initGui();
        if (tile == null)
            return;
        buttonList.clear();
        int w = (width - xSize) / 2;
        int h = (height - ySize) / 2;
        buttonList.add(new GuiToggleButton(0, w + 75, h + 18, 70, button1Label, tile.waitTillEmpty()));
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
        int sWidth = fontRendererObj.getStringWidth(tile.getName());
        int sPos = xSize / 2 - sWidth / 2;
        fontRendererObj.drawString(label, sPos, 6, 0x404040);

        fontRendererObj.drawString(Integer.toString((int) tile.getEnergy()), 30, 55, 0x404040);

        String capacity = "/" + tile.getCapacity();
        fontRendererObj.drawString(capacity, 28, 65, 0x404040);

        fontRendererObj.drawString(LocalizationPlugin.translate("railcraft.gui.ic2.energy.rate", tile.getTransferRate()), 90, 67, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
        super.drawGuiContainerBackgroundLayer(f, i, j);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        if (tile.getEnergy() > 0) {
            int energy = tile.getEnergyBarScaled(24);
            drawTexturedModalRect(x + 31, y + 34, 176, 14, energy, 17);
        }
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
        if (t instanceof TileEnergyUnloader)
            tile = (TileEnergyUnloader) t;
        else
            mc.thePlayer.closeScreen();
    }

}
