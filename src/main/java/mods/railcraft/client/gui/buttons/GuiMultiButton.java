/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.gui.buttons;

import mods.railcraft.client.render.OpenGL;
import mods.railcraft.common.gui.buttons.IButtonTextureSet;
import mods.railcraft.common.gui.buttons.IMultiButtonState;
import mods.railcraft.common.gui.buttons.MultiButtonController;
import mods.railcraft.common.gui.buttons.StandardButtonTextureSets;
import mods.railcraft.common.gui.tooltips.ToolTip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;

import javax.annotation.Nullable;

/**
 * @author CovertJaguar <http://railcraft.info/wiki/info:license>
 */
@SideOnly(Side.CLIENT)
public class GuiMultiButton<T extends IMultiButtonState> extends GuiBetterButton {

    private final MultiButtonController<T> control;
    public boolean canChange = true;

    private GuiMultiButton(int id, int x, int y, int width, MultiButtonController<T> control) {
        super(id, x, y, width, StandardButtonTextureSets.LARGE_BUTTON, "");
        this.control = control;
    }

    public static <T extends IMultiButtonState> GuiMultiButton<T> create(int id, int x, int y, int width, MultiButtonController<T> control) {
        return new GuiMultiButton<T>(id, x, y, width, control);
    }

    @Override
    public int getHeight() {
        return texture.getHeight();
    }

    @Override
    public void drawButton(Minecraft minecraft, int x, int y) {
        if (!visible) {
            return;
        }
        FontRenderer fontrenderer = minecraft.fontRendererObj;
        bindButtonTextures(minecraft);
        OpenGL.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        IMultiButtonState state = control.getButtonState();
        IButtonTextureSet tex = state.getTextureSet();
        int xOffset = tex.getX();
        int yOffset = tex.getY();
        int h = tex.getHeight();
        int w = tex.getWidth();
        boolean flag = x >= xPosition && y >= yPosition && x < xPosition + width && y < yPosition + h;
        int hoverState = getHoverState(flag);
        drawTexturedModalRect(xPosition, yPosition, xOffset, yOffset + hoverState * h, width / 2, h);
        drawTexturedModalRect(xPosition + width / 2, yPosition, xOffset + w - width / 2, yOffset + hoverState * h, width / 2, h);
        mouseDragged(minecraft, x, y);
        displayString = state.getLabel();
        if (!displayString.equals("")) {
            if (!enabled) {
                drawCenteredString(fontrenderer, displayString, xPosition + width / 2, yPosition + (h - 8) / 2, 0xffa0a0a0);
            } else if (flag) {
                drawCenteredString(fontrenderer, displayString, xPosition + width / 2, yPosition + (h - 8) / 2, 0xffffa0);
            } else {
                drawCenteredString(fontrenderer, displayString, xPosition + width / 2, yPosition + (h - 8) / 2, 0xe0e0e0);
            }
        }
    }

    @Override
    public boolean mousePressed(Minecraft minecraft, int mouseX, int mouseY) {
        boolean pressed = super.mousePressed(minecraft, mouseX, mouseY);
        if (canChange && pressed && enabled) {
            if (Mouse.getEventButton() == 0) {
                control.incrementState();
            } else {
                control.decrementState();
            }
        }
        return pressed;
    }

    public MultiButtonController<T> getController() {
        return control;
    }

    @Nullable
    @Override
    public ToolTip getToolTip() {
        ToolTip tip = control.getButtonState().getToolTip();
        if (tip != null) {
            return tip;
        }
        return super.getToolTip();
    }

}
