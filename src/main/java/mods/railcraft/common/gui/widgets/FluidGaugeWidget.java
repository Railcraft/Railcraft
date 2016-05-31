/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.gui.widgets;

import mods.railcraft.client.gui.GuiContainerRailcraft;
import mods.railcraft.client.render.tools.FluidRenderer;
import mods.railcraft.client.render.tools.OpenGL;
import mods.railcraft.common.fluids.tanks.StandardTank;
import mods.railcraft.common.gui.tooltips.ToolTip;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class FluidGaugeWidget extends Widget {

    public final StandardTank tank;

    public FluidGaugeWidget(StandardTank tank, int x, int y, int u, int v, int w, int h) {
        super(x, y, u, v, w, h);
        this.tank = tank;
    }

    @Override
    public ToolTip getToolTip() {
        return tank.getToolTip();
    }

    //TODO: test render
    @Override
    public void draw(GuiContainerRailcraft gui, int guiX, int guiY, int mouseX, int mouseY) {
        if (tank == null)
            return;
        if (tank.renderData.fluid == null || tank.renderData.amount <= 0)
            return;

        TextureAtlasSprite fluidIcon = FluidRenderer.getFluidTexture(tank.renderData.fluid, FluidRenderer.FlowState.STILL);

        if (fluidIcon == null)
            return;

        float scale = Math.min(tank.renderData.amount, tank.getCapacity()) / (float) tank.getCapacity();

        gui.bindTexture(FluidRenderer.getFluidSheet(tank.renderData.fluid));
        FluidRenderer.setColorForTank(tank);

        for (int col = 0; col < w / 16; col++) {
            for (int row = 0; row <= h / 16; row++) {
                gui.drawTexturedModalRect(guiX + x + col * 16, guiY + y + row * 16 - 1, fluidIcon, 16, 16);
            }
        }

        OpenGL.glColor4f(1, 1, 1, 1);
        gui.bindTexture(gui.texture);

        gui.drawTexturedModalRect(guiX + x, guiY + y - 1, x, y - 1, w, h - (int) Math.floor(h * scale) + 1);
        gui.drawTexturedModalRect(guiX + x, guiY + y, u, v, w, h);
    }

}
