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
import mods.railcraft.common.blocks.tracks.TrackEmbarking;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.IGuiReturnHandler;
import mods.railcraft.common.util.network.PacketGuiReturn;
import net.minecraft.client.gui.GuiButton;

public class GuiTrackEmbarking extends GuiBasic {
    protected byte radius = 2;
    TrackEmbarking track;

    public GuiTrackEmbarking(TrackEmbarking t) {
        super(((TileTrack) t.getTile()).getName());
        track = t;
        if (track != null) {
            radius = track.getArea();
        }
    }

    @Override
    public void initGui() {
        buttonList.clear();
        int w = (width - xSize) / 2;
        int h = (height - ySize) / 2;
        buttonList.add(new GuiButton(0, w + 53, h + 50, 30, 20, "-1"));
        buttonList.add(new GuiButton(1, w + 93, h + 50, 30, 20, "+1"));
    }

    @Override
    protected void drawExtras(int x, int y, float f) {
        if (track != null) {
            GuiTools.drawCenteredString(fontRendererObj, LocalizationPlugin.translate("railcraft.gui.track.embarking.radius") + " = " + radius, 25);
        }
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) {
        if (guibutton.id == 0) {
            radius--;
        }
        if (guibutton.id == 1) {
            radius++;
        }

        radius = (byte) Math.max(TrackEmbarking.MIN_AREA, radius);
        radius = (byte) Math.min(TrackEmbarking.MAX_AREA, radius);
    }

    @Override
    public void onGuiClosed() {
        track.setArea(radius);
        if (Game.isNotHost(track.getWorld())) {
            PacketGuiReturn pkt = new PacketGuiReturn((IGuiReturnHandler) track.tileEntity);
            pkt.sendPacket();
        }
    }
}
