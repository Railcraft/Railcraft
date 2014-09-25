/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import org.lwjgl.opengl.GL11;
import mods.railcraft.common.core.RailcraftConstants;
import net.minecraft.util.ResourceLocation;

public abstract class GuiBasic extends GuiScreen {

    protected final int xSize;
    protected final int ySize;
    protected final String label;
    protected final ResourceLocation texture;

    protected GuiBasic(String label) {
        this.label = label;
        xSize = 176;
        ySize = 88;
        texture = new ResourceLocation(RailcraftConstants.GUI_TEXTURE_FOLDER + "gui_basic.png");
    }

    protected GuiBasic(String label, String texture, int x, int y) {
        this(label, new ResourceLocation(texture), x, y);
    }

    protected GuiBasic(String label, ResourceLocation texture, int x, int y) {
        this.label = label;
        xSize = x;
        ySize = y;
        this.texture = texture;
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
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        drawTexturedModalRect(w, h, 0, 0, xSize, ySize);
        GL11.glPushMatrix();
        GL11.glTranslatef(w, h, 0.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GuiTools.drawCenteredString(fontRendererObj, label, 6, xSize);
        drawExtras(x, y, f);
        GL11.glPopMatrix();
        super.drawScreen(x, y, f);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    protected void drawExtras(int x, int y, float f) {
    }

    @Override
    protected void keyTyped(char c, int i) {
        if (i == 1 || i == mc.gameSettings.keyBindInventory.getKeyCode())
            mc.thePlayer.closeScreen();
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (!mc.thePlayer.isEntityAlive() || mc.thePlayer.isDead)
            mc.thePlayer.closeScreen();
    }

}
