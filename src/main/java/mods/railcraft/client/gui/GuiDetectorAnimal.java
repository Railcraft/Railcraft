/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.gui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import mods.railcraft.client.gui.buttons.GuiBetterButton;
import mods.railcraft.client.gui.buttons.GuiToggleButton;
import mods.railcraft.common.blocks.detector.TileDetector;
import mods.railcraft.common.blocks.detector.types.DetectorAnimal;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.PacketDispatcher;
import mods.railcraft.common.util.network.PacketGuiReturn;

public class GuiDetectorAnimal extends GuiBasic {

    private final TileDetector tile;
    private final DetectorAnimal detector;

    protected GuiDetectorAnimal(TileDetector tile) {
        super(tile.getName(), RailcraftConstants.GUI_TEXTURE_FOLDER + "gui_long.png", 256, 88);
        this.tile = tile;
        this.detector = (DetectorAnimal) tile.getDetector();
    }

    @Override
    public void initGui() {
        if (tile == null) {
            return;
        }
        buttonList.clear();
        int w = (width - xSize) / 2;
        int h = (height - ySize) / 2;

        List<GuiBetterButton> row1 = new ArrayList<GuiBetterButton>();
        row1.add(new GuiToggleButton(0, 0, h + 25, 50, "Chicken", detector.chicken));
        row1.add(new GuiToggleButton(1, 0, h + 25, 50, "Cow", detector.cow));
        row1.add(new GuiToggleButton(2, 0, h + 25, 50, "Pig", detector.pig));
        row1.add(new GuiToggleButton(3, 0, h + 25, 50, "Sheep", detector.sheep));
        GuiTools.newButtonRowAuto(buttonList, w, xSize, row1);

        List<GuiBetterButton> row2 = new ArrayList<GuiBetterButton>();
        row2.add(new GuiToggleButton(4, 0, h + 55, 50, "Wolf", detector.wolf));
        row2.add(new GuiToggleButton(5, 0, h + 55, 70, "Mooshroom", detector.mooshroom));
        row2.add(new GuiToggleButton(6, 0, h + 55, 50, "Other", detector.other));
        GuiTools.newButtonRowAuto(buttonList, w, xSize, row2);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (tile == null) {
            return;
        }
        switch (button.id) {
            case 0:
                detector.chicken = !detector.chicken;
                ((GuiToggleButton) button).active = detector.chicken;
                break;
            case 1:
                detector.cow = !detector.cow;
                ((GuiToggleButton) button).active = detector.cow;
                break;
            case 2:
                detector.pig = !detector.pig;
                ((GuiToggleButton) button).active = detector.pig;
                break;
            case 3:
                detector.sheep = !detector.sheep;
                ((GuiToggleButton) button).active = detector.sheep;
                break;
            case 4:
                detector.wolf = !detector.wolf;
                ((GuiToggleButton) button).active = detector.wolf;
                break;
            case 5:
                detector.mooshroom = !detector.mooshroom;
                ((GuiToggleButton) button).active = detector.mooshroom;
                break;
            case 6:
                detector.other = !detector.other;
                ((GuiToggleButton) button).active = detector.other;
                break;
        }
    }

    @Override
    public void onGuiClosed() {
        if (Game.isNotHost(tile.getWorld())) {
            PacketGuiReturn pkt = new PacketGuiReturn(tile);
            PacketDispatcher.sendToServer(pkt);
        }
    }

}
