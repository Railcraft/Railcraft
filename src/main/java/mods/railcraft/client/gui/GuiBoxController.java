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
import mods.railcraft.api.signals.SignalAspect;
import mods.railcraft.common.blocks.signals.TileBoxController;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.PacketDispatcher;
import mods.railcraft.common.util.network.PacketGuiReturn;

public class GuiBoxController extends GuiBasic {

    private final TileBoxController tile;
    private SignalAspect defaultAspect;
    private SignalAspect poweredAspect;

    public GuiBoxController(TileBoxController t) {
        super(t.getName());
        tile = t;
        defaultAspect = t.defaultAspect;
        poweredAspect = t.poweredAspect;
    }

    @Override
    public void initGui() {
        if (tile == null)
            return;
        buttonList.clear();
        int w = (width - xSize) / 2;
        int h = (height - ySize) / 2;

        buttonList.add(new GuiButton(0, w + 10, h + 25, 30, 20, "<"));
        buttonList.add(new GuiButton(1, w + 135, h + 25, 30, 20, ">"));

        buttonList.add(new GuiButton(2, w + 10, h + 60, 30, 20, "<"));
        buttonList.add(new GuiButton(3, w + 135, h + 60, 30, 20, ">"));
    }

    @Override
    protected void drawExtras(int x, int y, float f) {
        GuiTools.drawCenteredString(fontRendererObj, LocalizationPlugin.translate("railcraft.gui.box.controller.aspect.default"), 25);
        GuiTools.drawCenteredString(fontRendererObj, LocalizationPlugin.translate(defaultAspect.getLocalizationTag()), 35);
        GuiTools.drawCenteredString(fontRendererObj, LocalizationPlugin.translate("railcraft.gui.box.controller.aspect.redstone"), 60);
        GuiTools.drawCenteredString(fontRendererObj, LocalizationPlugin.translate(poweredAspect.getLocalizationTag()), 70);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (tile == null)
            return;
        int normal = defaultAspect.ordinal();
        int redstone = poweredAspect.ordinal();
        switch (button.id) {
            case 0:
                normal--;
                break;
            case 1:
                normal++;
                break;
            case 2:
                redstone--;
                break;
            case 3:
                redstone++;
                break;
        }
        normal = Math.min(normal, SignalAspect.OFF.ordinal());
        normal = Math.max(normal, 0);
        redstone = Math.min(redstone, SignalAspect.OFF.ordinal());
        redstone = Math.max(redstone, 0);
        defaultAspect = SignalAspect.values()[normal];
        poweredAspect = SignalAspect.values()[redstone];
    }

    @Override
    public void onGuiClosed() {
        if (Game.isNotHost(tile.getWorld())) {
            tile.defaultAspect = defaultAspect;
            tile.poweredAspect = poweredAspect;
            PacketGuiReturn pkt = new PacketGuiReturn(tile);
            PacketDispatcher.sendToServer(pkt);
        }
    }

}
