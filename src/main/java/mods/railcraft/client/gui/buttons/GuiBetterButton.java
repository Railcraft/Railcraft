/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui.buttons;

import mods.railcraft.client.gui.GuiTools;
import mods.railcraft.client.render.tools.OpenGL;
import mods.railcraft.common.gui.buttons.IButtonTextureSet;
import mods.railcraft.common.gui.tooltips.ToolTip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

@SideOnly(Side.CLIENT)
@SuppressWarnings("unchecked")
public abstract class GuiBetterButton<T extends GuiBetterButton<T>> extends GuiButton {

    private static final ResourceLocation TEXTURE = GuiTools.findTexture("gui_basic.png");
    protected final IButtonTextureSet texture;
    private ToolTip toolTip;
    private @Nullable Consumer<? super T> clickConsumer;
    private @Nullable Consumer<? super T> statusUpdater;

    protected GuiBetterButton(int id, int x, int y, int width, IButtonTextureSet texture, String label) {
        super(id, x, y, width, texture.getHeight(), label);
        this.texture = texture;
    }

    public void setClickConsumer(Consumer<? super T> clickConsumer) {
        this.clickConsumer = clickConsumer;
    }

    public void consumeClick() {
        if (clickConsumer != null)
            clickConsumer.accept((T) this);
    }

    public void setStatusUpdater(Consumer<? super T> statusUpdater) {
        this.statusUpdater = statusUpdater;
    }

    public void updateStatus() {
        if (statusUpdater != null)
            statusUpdater.accept((T) this);
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
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + getHeight();
    }

    protected void bindButtonTextures(Minecraft minecraft) {
        minecraft.renderEngine.bindTexture(TEXTURE);
    }

    @Override
    public void drawButton(Minecraft minecraft, int mouseX, int mouseY, float partialTick) {
        if (!visible)
            return;
        FontRenderer fontrenderer = minecraft.fontRenderer;
        bindButtonTextures(minecraft);
        OpenGL.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int xOffset = texture.getX();
        int yOffset = texture.getY();
        int h = texture.getHeight();
        int w = texture.getWidth();
        boolean mouseOver = isMouseOverButton(mouseX, mouseY);
        int hoverState = getHoverState(mouseOver);
        drawTexturedModalRect(x, y, xOffset, yOffset + hoverState * h, width / 2, h);
        drawTexturedModalRect(x + width / 2, y, xOffset + w - width / 2, yOffset + hoverState * h, width / 2, h);
        mouseDragged(minecraft, mouseX, mouseY);
        drawCenteredString(fontrenderer, displayString, x + width / 2, y + (h - 8) / 2, getTextColor(mouseOver));
    }

    public @Nullable ToolTip getToolTip() {
        return toolTip;
    }

    public void setToolTip(@Nullable ToolTip tips) {
        this.toolTip = tips;
    }

}
