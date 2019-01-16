/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui;

import mods.railcraft.client.gui.buttons.GuiButtonSmall;
import mods.railcraft.client.render.tools.OpenGL;
import mods.railcraft.common.blocks.detector.TileDetector;
import mods.railcraft.common.blocks.detector.types.DetectorItem;
import mods.railcraft.common.gui.containers.ContainerDetectorItem;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.PacketDispatcher;
import mods.railcraft.common.util.network.PacketGuiReturn;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class GuiDetectorItem extends GuiTitled {

    private final TileDetector tile;
    private final DetectorItem detector;
    private GuiButton filterLeft;
    private GuiButton filterRight;

    public GuiDetectorItem(InventoryPlayer inv, TileDetector tile) {
        super(tile, new ContainerDetectorItem(inv, tile), "gui_detector_item.png");
        this.tile = tile;
        this.detector = (DetectorItem) tile.getDetector();
        drawInvTitle = false;
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
        if (Game.isClient(tile.getWorld())) {
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
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        GuiTools.drawCenteredString(fontRenderer, detector.getPrimaryMode().toString(), 25);

        if (detector.getPrimaryMode() != DetectorItem.PrimaryMode.FILTERED) {
            Color color = new Color(0, 0, 0, 80);
            int displayY;

            OpenGL.glDisable(GL11.GL_LIGHTING);
            OpenGL.glDisable(GL11.GL_DEPTH_TEST);
            for (int slotNum = 0; slotNum < 9; slotNum++) {
                Slot slot = inventorySlots.inventorySlots.get(slotNum);

                int displayX = slot.xPos;
                displayY = slot.yPos;
                drawGradientRect(displayX, displayY, displayX + 16, displayY + 16, color.getRGB(), color.getRGB());
            }
            OpenGL.glEnable(GL11.GL_LIGHTING);
            OpenGL.glEnable(GL11.GL_DEPTH_TEST);
        } else
            GuiTools.drawCenteredString(fontRenderer, detector.getFilterMode().toString(), 45);
    }

}
