/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.gui;

import java.awt.Color;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import org.lwjgl.opengl.GL11;
import mods.railcraft.client.gui.buttons.GuiButtonSmall;
import mods.railcraft.common.blocks.detector.TileDetector;
import mods.railcraft.common.blocks.detector.types.DetectorItem;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.gui.containers.ContainerDetectorItem;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.PacketDispatcher;
import mods.railcraft.common.util.network.PacketGuiReturn;

public class GuiDetectorItem extends TileGui {

    private final String label;
    private final TileDetector tile;
    private final DetectorItem detector;
    private GuiButton filterLeft;
    private GuiButton filterRight;

    public GuiDetectorItem(InventoryPlayer inv, TileDetector tile) {
        super(tile, new ContainerDetectorItem(inv, tile), RailcraftConstants.GUI_TEXTURE_FOLDER + "gui_detector_item.png");
        this.tile = tile;
        this.detector = (DetectorItem) tile.getDetector();
        xSize = 176;
        ySize = 166;
        label = tile.getName();
    }

    @Override
    public void initGui() {
        super.initGui();
        if (tile == null)
            return;
        buttonList.clear();
        int w = (width - xSize) / 2;
        int h = (height - ySize) / 2;

        buttonList.add(new GuiButtonSmall(0, w + 10, h + 20, 20, "<"));
        buttonList.add(new GuiButtonSmall(1, w + 146, h + 20, 20, ">"));

        buttonList.add(filterLeft = new GuiButtonSmall(2, w + 10, h + 40, 20, "<"));
        buttonList.add(filterRight = new GuiButtonSmall(3, w + 146, h + 40, 20, ">"));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (tile == null)
            return;
        int primary = detector.getPrimaryMode().ordinal();
        int filter = detector.getFilterMode().ordinal();
        switch (button.id) {
            case 0:
                primary--;
                break;
            case 1:
                primary++;
                break;
            case 2:
                filter--;
                break;
            case 3:
                filter++;
                break;
        }
        if (primary < 0)
            primary = DetectorItem.PrimaryMode.values().length - 1;
        if (primary >= DetectorItem.PrimaryMode.values().length)
            primary = 0;
        detector.setPrimaryMode(DetectorItem.PrimaryMode.values()[primary]);

        if (filter < 0)
            filter = DetectorItem.FilterMode.values().length - 1;
        if (filter >= DetectorItem.FilterMode.values().length)
            filter = 0;
        detector.setFilterMode(DetectorItem.FilterMode.values()[filter]);
        if (Game.isNotHost(tile.getWorld())) {
            PacketGuiReturn pkt = new PacketGuiReturn(tile);
            PacketDispatcher.sendToServer(pkt);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
        filterLeft.visible = detector.getPrimaryMode() == DetectorItem.PrimaryMode.FILTERED;
        filterRight.visible = detector.getPrimaryMode() == DetectorItem.PrimaryMode.FILTERED;

        super.drawGuiContainerBackgroundLayer(f, i, j);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        int sWidth = fontRendererObj.getStringWidth(label);
        int sPos = xSize / 2 - sWidth / 2;
        fontRendererObj.drawString(label, sPos, 6, 0x404040);
        GuiTools.drawCenteredString(fontRendererObj, detector.getPrimaryMode().toString(), 25);

        if (detector.getPrimaryMode() != DetectorItem.PrimaryMode.FILTERED) {
            Color color = new Color(0, 0, 0, 80);
            int displayY;

            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            for (int slotNum = 0; slotNum < 9; slotNum++) {
                Slot slot = (Slot) this.inventorySlots.inventorySlots.get(slotNum);

                int displayX = slot.xDisplayPosition;
                displayY = slot.yDisplayPosition;
                this.drawGradientRect(displayX, displayY, displayX + 16, displayY + 16, color.getRGB(), color.getRGB());
            }
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
        } else
            GuiTools.drawCenteredString(fontRendererObj, detector.getFilterMode().toString(), 45);
    }

}
