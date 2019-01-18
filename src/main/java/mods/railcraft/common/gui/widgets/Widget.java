/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.widgets;

import mods.railcraft.client.gui.GuiContainerRailcraft;
import mods.railcraft.common.gui.containers.RailcraftContainer;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.inventory.IContainerListener;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
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
    private byte widgetId;

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
        widgetId = (byte) container.getWidgets().indexOf(this);
    }

    public byte getId() {
        return widgetId;
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

    public @Nullable ToolTip getToolTip() {
        return null;
    }

    public boolean hasServerSyncData(IContainerListener listener) {
        return false;
    }

    public void writeServerSyncData(IContainerListener listener, RailcraftOutputStream data) throws IOException {
    }

    public void readServerSyncData(RailcraftInputStream data) throws IOException {
    }
}
