/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.gui.buttons;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.gui.buttons.IButtonTextureSet;
import mods.railcraft.common.gui.buttons.StandardButtonTextureSets;
import mods.railcraft.common.gui.tooltips.ToolTip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiBetterButton extends GuiButton {

    private static final ResourceLocation TEXTURE = new ResourceLocation(RailcraftConstants.GUI_TEXTURE_FOLDER + "gui_basic.png");
    protected final IButtonTextureSet texture;
    private ToolTip toolTip;

    public GuiBetterButton(int id, int x, int y, String label) {
        this(id, x, y, 200, StandardButtonTextureSets.LARGE_BUTTON, label);
    }

    public GuiBetterButton(int id, int x, int y, int width, String label) {
        this(id, x, y, width, StandardButtonTextureSets.LARGE_BUTTON, label);
    }

    public GuiBetterButton(int id, int x, int y, int width, IButtonTextureSet texture, String label) {
        super(id, x, y, width, texture.getHeight(), label);
        this.texture = texture;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return texture.getHeight();
    }

    public int getTextColor(boolean mouseOver) {
        if (!enabled)
            return 0xffa0a0a0;
        else if (mouseOver)
            return 0xffffa0;
        else
            return 0xe0e0e0;
    }

    public boolean isMouseOverButton(int mouseX, int mouseY) {
        return mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + getHeight();
    }

    protected void bindButtonTextures(Minecraft minecraft) {
        minecraft.renderEngine.bindTexture(TEXTURE);
    }

    @Override
    public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
        if (!visible)
            return;
        FontRenderer fontrenderer = minecraft.fontRenderer;
        bindButtonTextures(minecraft);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int xOffset = texture.getX();
        int yOffset = texture.getY();
        int h = texture.getHeight();
        int w = texture.getWidth();
        boolean mouseOver = isMouseOverButton(mouseX, mouseY);
        int hoverState = getHoverState(mouseOver);
        drawTexturedModalRect(xPosition, yPosition, xOffset, yOffset + hoverState * h, width / 2, h);
        drawTexturedModalRect(xPosition + width / 2, yPosition, xOffset + w - width / 2, yOffset + hoverState * h, width / 2, h);
        mouseDragged(minecraft, mouseX, mouseY);
        drawCenteredString(fontrenderer, displayString, xPosition + width / 2, yPosition + (h - 8) / 2, getTextColor(mouseOver));
    }

    public ToolTip getToolTip() {
        return toolTip;
    }

    public void setToolTip(ToolTip tips) {
        this.toolTip = tips;
    }

}
