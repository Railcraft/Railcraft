/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.gui;

import mods.railcraft.common.blocks.tracks.TileTrack;
import mods.railcraft.common.blocks.tracks.TrackPriming;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.IGuiReturnHandler;
import mods.railcraft.common.util.network.PacketDispatcher;
import mods.railcraft.common.util.network.PacketGuiReturn;
import net.minecraft.client.gui.GuiButton;

public class GuiTrackPriming extends GuiBasic {
    protected short fuse = 80;
    TrackPriming track;

    public GuiTrackPriming(TrackPriming t) {
        super(((TileTrack) t.getTile()).getName());
        track = t;
        fuse = track.getFuse();
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
        GuiTools.drawCenteredString(fontRendererObj, LocalizationPlugin.translate("railcraft.gui.track.priming.fuse", fuse), 25);
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) {
        short f = fuse;
        if (guibutton.id == 0)
            f += -10;
        if (guibutton.id == 1)
            f += -1;
        if (guibutton.id == 2)
            f += 1;
        if (guibutton.id == 3)
            f += 10;
        if (f < TrackPriming.MIN_FUSE)
            f = TrackPriming.MIN_FUSE;
        if (f > TrackPriming.MAX_FUSE)
            f = TrackPriming.MAX_FUSE;
        fuse = f;
    }

    @Override
    public void onGuiClosed() {
        track.setFuse(fuse);
        if (Game.isNotHost(track.getWorld())) {
            PacketGuiReturn pkt = new PacketGuiReturn((IGuiReturnHandler) track.tileEntity);
            PacketDispatcher.sendToServer(pkt);
        }
    }
}
