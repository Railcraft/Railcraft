/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.gui.widgets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.DataInputStream;
import java.io.IOException;
import mods.railcraft.client.gui.GuiContainerRailcraft;
import mods.railcraft.common.gui.containers.RailcraftContainer;
import mods.railcraft.common.gui.tooltips.ToolTip;
import net.minecraft.inventory.ICrafting;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class Widget {

    public final int x;
    public final int y;
    public final int u;
    public final int v;
    public final int w;
    public final int h;
    public boolean hidden;
    protected RailcraftContainer container;

    public Widget(int x, int y, int u, int v, int w, int h) {
        this.x = x;
        this.y = y;
        this.u = u;
        this.v = v;
        this.w = w;
        this.h = h;
    }

    public void addToContainer(RailcraftContainer container) {
        this.container = container;
    }

    @SideOnly(Side.CLIENT)
    public final boolean isMouseOver(int mouseX, int mouseY) {
        return mouseX >= x - 1 && mouseX < x + w + 1 && mouseY >= y - 1 && mouseY < y + h + 1;
    }

    @SideOnly(Side.CLIENT)
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public void draw(GuiContainerRailcraft gui, int guiX, int guiY, int mouseX, int mouseY) {
        gui.drawTexturedModalRect(guiX + x, guiY + y, u, v, w, h);
    }

    @SideOnly(Side.CLIENT)
    public ToolTip getToolTip() {
        return null;
    }

    public void initWidget(ICrafting player) {
    }

    public void updateWidget(ICrafting player) {
    }

    @SideOnly(Side.CLIENT)
    public void handleClientPacketData(DataInputStream data) throws IOException {
    }

}
