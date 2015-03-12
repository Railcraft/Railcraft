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
import mods.railcraft.common.blocks.tracks.TrackLauncher;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.IGuiReturnHandler;
import mods.railcraft.common.util.network.PacketGuiReturn;
import net.minecraft.client.gui.GuiButton;

public class GuiTrackLauncher extends GuiBasic {
    protected int force = 25;
    TrackLauncher track;

    public GuiTrackLauncher(TrackLauncher t) {
        super(((TileTrack) t.getTile()).getName());
        track = t;
        if (track != null)
            force = track.getLaunchForce();
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
    protected void actionPerformed(GuiButton guibutton) {
        int f = force;
        if (guibutton.id == 0)
            f += -10;
        if (guibutton.id == 1)
            f += -1;
        if (guibutton.id == 2)
            f += 1;
        if (guibutton.id == 3)
            f += 10;
        if (f < TrackLauncher.MIN_LAUNCH_FORCE)
            f = TrackLauncher.MIN_LAUNCH_FORCE;
        if (f > RailcraftConfig.getLaunchRailMaxForce())
            f = RailcraftConfig.getLaunchRailMaxForce();
        force = f;
    }

    @Override
    public void drawExtras(int x, int y, float f) {
        if (track != null)
            fontRendererObj.drawString(LocalizationPlugin.translate("railcraft.gui.track.launcher.force", force), 61, 25, 0x404040);
    }

    @Override
    public void onGuiClosed() {
        track.setLaunchForce(force);
        if (Game.isNotHost(track.getWorld())) {
            PacketGuiReturn pkt = new PacketGuiReturn((IGuiReturnHandler) track.tileEntity);
            pkt.sendPacket();
        }
    }
}
