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
import mods.railcraft.client.gui.buttons.GuiMultiButton;
import mods.railcraft.common.blocks.signals.TileBoxCapacitor;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.PacketDispatcher;
import mods.railcraft.common.util.network.PacketGuiReturn;

public class GuiBoxCapacitor extends GuiBasic {

    private final TileBoxCapacitor tile;
    private short ticksToPower;
    private GuiMultiButton stateMode;

    public GuiBoxCapacitor(TileBoxCapacitor tile) {
        super(tile.getName());
        this.tile = tile;
        this.ticksToPower = tile.ticksToPower;
    }

    @Override
    public void initGui() {
        if (tile == null) {
            return;
        }
        buttonList.clear();
        int w = (width - xSize) / 2;
        int h = (height - ySize) / 2;

        buttonList.add(new GuiButton(0, w + 13, h + 38, 30, 20, "-10"));
        buttonList.add(new GuiButton(1, w + 53, h + 38, 30, 20, "-1"));
        buttonList.add(new GuiButton(2, w + 93, h + 38, 30, 20, "+1"));
        buttonList.add(new GuiButton(3, w + 133, h + 38, 30, 20, "+10"));
        buttonList.add(stateMode = new GuiMultiButton(4, w + 23, h + 65, 130, tile.getStateModeController().copy()));
    }

    @Override
    protected void drawExtras(int x, int y, float f) {
        GuiTools.drawCenteredString(fontRendererObj, LocalizationPlugin.translate("railcraft.gui.box.capacitor.duration", ticksToPower / 20), 25);
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) {
        short ticks = ticksToPower;
        if (guibutton.id == 0) {
            ticks += -200;
        }
        if (guibutton.id == 1) {
            ticks += -20;
        }
        if (guibutton.id == 2) {
            ticks += 20;
        }
        if (guibutton.id == 3) {
            ticks += 200;
        }
        if (ticks < 0) {
            ticks = 0;
        }
        ticksToPower = ticks;
    }

    @Override
    public void onGuiClosed() {
        if (Game.isNotHost(tile.getWorld())) {
            tile.ticksToPower = ticksToPower;
            tile.getStateModeController().setCurrentState(stateMode.getController().getCurrentState());
            PacketGuiReturn pkt = new PacketGuiReturn(tile);
            PacketDispatcher.sendToServer(pkt);
        }
    }
}
