/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui;

import mods.railcraft.client.gui.buttons.GuiToggleButton;
import mods.railcraft.common.blocks.machine.manipulator.TileIC2Loader;
import mods.railcraft.common.gui.containers.ContainerManipulatorCartIC2;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.PacketDispatcher;
import mods.railcraft.common.util.network.PacketGuiReturn;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;

public class GuiManipulatorCartIC2Loader extends GuiManipulatorCartIC2 {

    private final String BUTTON1 = LocalizationPlugin.translate("gui.railcraft.energy.loader.empty");
    private final String BUTTON2 = LocalizationPlugin.translate("gui.railcraft.energy.loader.fill");
    private TileIC2Loader tile;
    private boolean waitIfEmpty;
    private boolean waitTillFull;

    public GuiManipulatorCartIC2Loader(InventoryPlayer inv, TileIC2Loader tile) {
        super(tile, new ContainerManipulatorCartIC2(inv, tile), "gui_energy_loader.png");
        this.tile = tile;
        waitIfEmpty = tile.waitIfEmpty();
        waitTillFull = tile.waitTillFull();
    }

    @Override
    public void initGui() {
        super.initGui();
        if (tile == null)
            return;
        buttonList.clear();
        int w = (width - xSize) / 2;
        int h = (height - ySize) / 2;
        buttonList.add(new GuiToggleButton(0, w + 75, h + 18, 70, BUTTON1, waitIfEmpty));
        buttonList.add(new GuiToggleButton(1, w + 75, h + 42, 70, BUTTON2, waitTillFull));
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) {
        if (tile == null)
            return;
        if (guibutton.id == 0) {
            waitIfEmpty = !waitIfEmpty;
            if (!waitIfEmpty)
                waitTillFull = false;
            ((GuiToggleButton) guibutton).active = waitIfEmpty;
            ((GuiToggleButton) buttonList.get(1)).active = waitTillFull;
        }
        if (guibutton.id == 1) {
            waitTillFull = !waitTillFull;
            if (waitTillFull)
                waitIfEmpty = true;
            ((GuiToggleButton) buttonList.get(0)).active = waitIfEmpty;
            ((GuiToggleButton) guibutton).active = waitTillFull;
        }
    }

    @Override
    public void onGuiClosed() {
        if (Game.isClient(tile.getWorld())) {
            tile.setWaitIfEmpty(waitIfEmpty);
            tile.setWaitTillFull(waitTillFull);
            PacketGuiReturn pkt = new PacketGuiReturn(tile);
            PacketDispatcher.sendToServer(pkt);
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        TileEntity t = tile.getWorld().getTileEntity(tile.getPos());
        if (t instanceof TileIC2Loader)
            tile = (TileIC2Loader) t;
        else
            mc.player.closeScreen();
    }

}
