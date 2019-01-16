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
import mods.railcraft.common.blocks.machine.manipulator.TileIC2Unloader;
import mods.railcraft.common.gui.containers.ContainerManipulatorCartIC2;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.PacketDispatcher;
import mods.railcraft.common.util.network.PacketGuiReturn;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;

public class GuiManipulatorCartIC2Unloader extends GuiManipulatorCartIC2 {

    private final String button1Label = LocalizationPlugin.translate("gui.railcraft.energy.unloader.wait");
    //    private final String BUTTON1 = "Wait till Empty";
    private TileIC2Unloader tile;

    public GuiManipulatorCartIC2Unloader(InventoryPlayer inv, TileIC2Unloader tile) {
        super(tile, new ContainerManipulatorCartIC2(inv, tile), "gui_energy_loader.png");
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
    public void onGuiClosed() {
        if (Game.isClient(tile.getWorld())) {
            PacketGuiReturn pkt = new PacketGuiReturn(tile);
            PacketDispatcher.sendToServer(pkt);
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        TileEntity t = tile.getWorld().getTileEntity(tile.getPos());
        if (t instanceof TileIC2Unloader)
            tile = (TileIC2Unloader) t;
        else
            mc.player.closeScreen();
    }

}
