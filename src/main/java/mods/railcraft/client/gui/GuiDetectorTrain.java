/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.gui;

import mods.railcraft.common.blocks.detector.TileDetector;
import mods.railcraft.common.blocks.detector.types.DetectorTrain;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.client.gui.GuiButton;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.PacketDispatcher;
import mods.railcraft.common.util.network.PacketGuiReturn;

public class GuiDetectorTrain extends GuiBasic {

    protected short trainSize = 5;
    private final TileDetector tile;
    private final DetectorTrain detector;

    public GuiDetectorTrain(TileDetector t) {
        super(t.getName());
        this.tile = t;
        this.detector = (DetectorTrain) tile.getDetector();
        if (tile != null)
            trainSize = detector.getTrainSize();
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
        GuiTools.drawCenteredString(fontRendererObj, LocalizationPlugin.translate("railcraft.gui.detector.train.size", trainSize), 25);
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) {
        short f = trainSize;
        if (guibutton.id == 0)
            f += -10;
        if (guibutton.id == 1)
            f += -1;
        if (guibutton.id == 2)
            f += 1;
        if (guibutton.id == 3)
            f += 10;
        if (f < 1)
            f = 1;
        if (f > 100)
            f = 100;
        trainSize = f;
    }

    @Override
    public void onGuiClosed() {
        detector.setTrainSize(trainSize);
        if (Game.isNotHost(tile.getWorld())) {
            PacketGuiReturn pkt = new PacketGuiReturn(tile);
            PacketDispatcher.sendToServer(pkt);
        }
    }

}
