/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.gui;

import cpw.mods.fml.common.registry.VillagerRegistry;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.passive.EntityVillager;
import mods.railcraft.client.gui.buttons.GuiToggleButtonSmall;
import mods.railcraft.common.blocks.detector.TileDetector;
import mods.railcraft.common.blocks.detector.types.DetectorVillager;
import mods.railcraft.common.blocks.detector.types.DetectorVillager.Mode;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.collections.RevolvingList;
import mods.railcraft.common.util.network.PacketDispatcher;
import mods.railcraft.common.util.network.PacketGuiReturn;

public class GuiDetectorVillager extends GuiBasic {

    private final TileDetector tile;
    private final DetectorVillager detector;
    private final EntityVillager villager;
    private Mode mode;
    private GuiToggleButtonSmall any;
    private GuiToggleButtonSmall none;
    private GuiToggleButtonSmall equals;
    private GuiToggleButtonSmall not;
    private final RevolvingList<Integer> professions = new RevolvingList<Integer>();

    public GuiDetectorVillager(TileDetector t) {
        super(t.getName(), RailcraftConstants.GUI_TEXTURE_FOLDER + "gui_detector_villager.png", 176, 105);
        this.tile = t;
        this.detector = (DetectorVillager) tile.getDetector();
        villager = new EntityVillager(tile.getWorld());
        mode = detector.getMode();

        for (int prof = 0; prof < 5; prof++) {
            professions.add(prof);
        }
        professions.addAll(VillagerRegistry.getRegisteredVillagers());

        professions.setCurrent(detector.getProfession());
        villager.setProfession(professions.getCurrent());
    }

    @Override
    public void initGui() {
        if (tile == null) {
            return;
        }
        buttonList.clear();
        int w = (width - xSize) / 2;
        int h = (height - ySize) / 2;

        buttonList.add(new GuiButton(0, w + 10, h + 25, 30, 20, "<"));
        buttonList.add(new GuiButton(1, w + 135, h + 25, 30, 20, ">"));

        buttonList.add(any = new GuiToggleButtonSmall(2, w + 10, h + 55, 45, LocalizationPlugin.translate("railcraft.gui.detector.villager.any"), mode == Mode.ANY));
        buttonList.add(none = new GuiToggleButtonSmall(3, w + 10, h + 75, 45, LocalizationPlugin.translate("railcraft.gui.detector.villager.none"), mode == Mode.NONE));
        buttonList.add(equals = new GuiToggleButtonSmall(4, w + 122, h + 55, 45, LocalizationPlugin.translate("railcraft.gui.detector.villager.equals"), mode == Mode.EQUALS));
        buttonList.add(not = new GuiToggleButtonSmall(5, w + 122, h + 75, 45, LocalizationPlugin.translate("railcraft.gui.detector.villager.not"), mode == Mode.NOT));
    }

    @Override
    protected void drawExtras(int x, int y, float f) {
        int guiLeft = (width - xSize) / 2;
        int guiTop = (height - ySize) / 2;
        GuiTools.drawVillager(villager, 87, 91, 30, (float) (guiLeft + 87) - x, (float) (guiTop + 91 - 50) - y);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (tile == null) {
            return;
        }
        switch (button.id) {
            case 0:
                professions.rotateLeft();
                break;
            case 1:
                professions.rotateRight();
                break;
            case 2:
                mode = Mode.ANY;
                break;
            case 3:
                mode = Mode.NONE;
                break;
            case 4:
                mode = Mode.EQUALS;
                break;
            case 5:
                mode = Mode.NOT;
                break;
        }

        any.active = mode == Mode.ANY;
        none.active = mode == Mode.NONE;
        equals.active = mode == Mode.EQUALS;
        not.active = mode == Mode.NOT;

        villager.setProfession(professions.getCurrent());
    }

    @Override
    public void onGuiClosed() {
        if (Game.isNotHost(tile.getWorld())) {
            detector.setProfession(professions.getCurrent());
            detector.setMode(mode);
            PacketGuiReturn pkt = new PacketGuiReturn(tile);
            PacketDispatcher.sendToServer(pkt);
        }
    }

}
