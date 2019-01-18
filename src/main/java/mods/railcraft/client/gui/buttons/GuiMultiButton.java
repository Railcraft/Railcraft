/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui.buttons;

import com.google.common.base.Strings;
import mods.railcraft.client.render.tools.OpenGL;
import mods.railcraft.common.gui.buttons.IButtonTextureSet;
import mods.railcraft.common.gui.buttons.IMultiButtonState;
import mods.railcraft.common.gui.buttons.MultiButtonController;
import mods.railcraft.common.gui.buttons.StandardButtonTextureSets;
import mods.railcraft.common.gui.tooltips.ToolTip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.input.Mouse;

/**
 * @author CovertJaguar <http://railcraft.info/wiki/info:license>
 */
@SideOnly(Side.CLIENT)
public final class GuiMultiButton<T extends IMultiButtonState> extends GuiBetterButton<GuiMultiButton<T>> {

    private final MultiButtonController<T> control;
    public boolean canChange = true;

    private GuiMultiButton(int id, int x, int y, int width, MultiButtonController<T> control) {
        super(id, x, y, width, StandardButtonTextureSets.LARGE_BUTTON, "");
        this.control = control;
    }

    public static <T extends IMultiButtonState> GuiMultiButton<T> create(int id, int x, int y, int width, MultiButtonController<T> control) {
        return new GuiMultiButton<>(id, x, y, width, control);
    }

    @Override
    public int getHeight() {
        return texture.getHeight();
    }

    @Override
    public void drawButton(Minecraft minecraft, int x, int y, float partialTicks) {
        if (!visible) {
            return;
        }
        FontRenderer fontrenderer = minecraft.fontRenderer;
        bindButtonTextures(minecraft);
        OpenGL.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        IMultiButtonState state = control.getButtonState();
        IButtonTextureSet tex = state.getTextureSet();
        int xOffset = tex.getX();
        int yOffset = tex.getY();
        int h = tex.getHeight();
        int w = tex.getWidth();
        boolean flag = x >= this.x && y >= this.y && x < this.x + width && y < this.y + h;
        int hoverState = getHoverState(flag);
        drawTexturedModalRect(this.x, this.y, xOffset, yOffset + hoverState * h, width / 2, h);
        drawTexturedModalRect(this.x + width / 2, this.y, xOffset + w - width / 2, yOffset + hoverState * h, width / 2, h);
        mouseDragged(minecraft, x, y);
        displayString = state.getLabel();
        if (!Strings.isNullOrEmpty(displayString)) {
            if (!enabled) {
                drawCenteredString(fontrenderer, displayString, this.x + width / 2, this.y + (h - 8) / 2, 0xffa0a0a0);
            } else if (flag) {
                drawCenteredString(fontrenderer, displayString, this.x + width / 2, this.y + (h - 8) / 2, 0xffffa0);
            } else {
                drawCenteredString(fontrenderer, displayString, this.x + width / 2, this.y + (h - 8) / 2, 0xe0e0e0);
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

    @Override
    public @Nullable ToolTip getToolTip() {
        ToolTip tip = control.getButtonState().getToolTip();
        if (tip != null) {
            return tip;
        }
        return super.getToolTip();
    }

}
