/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui;

import mods.railcraft.client.gui.buttons.GuiBetterButton;
import mods.railcraft.client.render.tools.OpenGL;
import mods.railcraft.common.util.collections.Streams;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.io.IOException;

public abstract class GuiBasic extends GuiScreen {

    protected final int xSize;
    protected final int ySize;
    protected final String label;
    protected final ResourceLocation texture;

    protected GuiBasic(String label) {
        this(label, "gui_basic.png", 176, 88);
    }

    protected GuiBasic(String label, String texture, int x, int y) {
        this.label = label;
        xSize = x;
        ySize = y;
        this.texture = GuiTools.findTexture(texture);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void drawScreen(int x, int y, float f) {
        drawDefaultBackground();
        int w = (width - xSize) / 2;
        int h = (height - ySize) / 2;
        mc.renderEngine.bindTexture(texture);
        OpenGL.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        drawTexturedModalRect(w, h, 0, 0, xSize, ySize);
        OpenGL.glPushMatrix();
        OpenGL.glTranslatef(w, h, 0.0F);
        OpenGL.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderHelper.disableStandardItemLighting();
        OpenGL.glDisable(GL11.GL_LIGHTING);
        OpenGL.glDisable(GL11.GL_DEPTH_TEST);
        GuiTools.drawCenteredString(fontRenderer, label, 6, xSize);
        drawExtras(x, y, f);
        OpenGL.glPopMatrix();
        super.drawScreen(x, y, f);
        OpenGL.glEnable(GL11.GL_DEPTH_TEST);
        OpenGL.glEnable(GL11.GL_LIGHTING);
    }

    protected void drawExtras(int x, int y, float f) {
    }

    @Override
    protected void keyTyped(char c, int i) {
        if (i == 1 || i == mc.gameSettings.keyBindInventory.getKeyCode())
            mc.player.closeScreen();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        if (button instanceof GuiBetterButton)
            ((GuiBetterButton<?>) button).consumeClick();
        buttonList.stream().flatMap(Streams.toType(GuiBetterButton.class)).forEach(GuiBetterButton::updateStatus);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (!mc.player.isEntityAlive() || mc.player.isDead)
            mc.player.closeScreen();
        buttonList.stream().flatMap(Streams.toType(GuiBetterButton.class)).forEach(GuiBetterButton::updateStatus);
    }

}
