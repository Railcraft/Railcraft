/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui;

import mods.railcraft.common.blocks.tracks.outfitted.TileTrackOutfitted;
import mods.railcraft.common.blocks.tracks.outfitted.kits.TrackKitDelayedLocking;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.PacketDispatcher;
import mods.railcraft.common.util.network.PacketGuiReturn;
import net.minecraft.client.gui.GuiButton;

public class GuiTrackDelayedLocking extends GuiBasic {
    protected int delay;
    private TrackKitDelayedLocking track;

    public GuiTrackDelayedLocking(TrackKitDelayedLocking t) {
        super(((TileTrackOutfitted) t.getTile()).getName());
        track = t;
        delay = track.getDelay();
    }

    @Override
    public void initGui() {
        buttonList.clear();
        int w = (width - xSize) / 2;
        int h = (height - ySize) / 2;
        buttonList.add(new GuiButton(0, w + 13, h + 50, 30, 20, "-10"));
        buttonList.add(new GuiButton(1, w + 53, h + 50, 30, 20, "-1"));
        buttonList.add(new GuiButton(2, w + 93, h + 50, 30, 20, "+1"));
        buttonList.add(new GuiButton(3, w + 133, h + 50, 30, 20, "+10"));
    }

    @Override
    protected void drawExtras(int x, int y, float f) {
        GuiTools.drawCenteredString(fontRenderer, LocalizationPlugin.translate("gui.railcraft.track.locking.delay.delay", delay), 25);
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) {
        int f = delay;
        if (guibutton.id == 0)
            f += -10;
        if (guibutton.id == 1)
            f += -1;
        if (guibutton.id == 2)
            f += 1;
        if (guibutton.id == 3)
            f += 10;
        if (f < TrackKitDelayedLocking.MIN_DELAY)
            f = TrackKitDelayedLocking.MIN_DELAY;
        if (f > TrackKitDelayedLocking.MAX_DELAY)
            f = TrackKitDelayedLocking.MAX_DELAY;
        delay = f;
    }

    @Override
    public void onGuiClosed() {
        track.setDelay(delay);
        if (Game.isClient(track.theWorldAsserted())) {
            PacketGuiReturn pkt = new PacketGuiReturn(track.getTile());
            PacketDispatcher.sendToServer(pkt);
        }
    }
}
