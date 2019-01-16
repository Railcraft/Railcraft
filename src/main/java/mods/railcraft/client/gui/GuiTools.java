/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui;

import mods.railcraft.api.tracks.ITrackKitInstance;
import mods.railcraft.client.gui.buttons.GuiBetterButton;
import mods.railcraft.client.render.tools.OpenGL;
import mods.railcraft.common.blocks.tracks.outfitted.TileTrackOutfitted;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IWorldNameable;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.Collection;
import java.util.List;

public final class GuiTools {

    public static void drawCenteredString(FontRenderer fr, IWorldNameable nameable) {
        drawCenteredString(fr, nameable, 6);
    }

    public static void drawCenteredString(FontRenderer fr, IWorldNameable nameable, int y) {
        ITextComponent name = nameable.getDisplayName();
        if (name != null)
            drawCenteredString(fr, name.getFormattedText(), y);
    }

    public static void drawCenteredString(FontRenderer fr, String s) {
        drawCenteredString(fr, s, 6);
    }

    public static void drawCenteredString(FontRenderer fr, String s, int y) {
        drawCenteredString(fr, s, y, 176);
    }

    public static void drawCenteredString(FontRenderer fr, String s, int y, int guiWidth) {
        drawCenteredString(fr, s, y, guiWidth, 0x404040, false);
    }

    public static void drawCenteredString(FontRenderer fr, String s, int y, int guiWidth, int color, boolean shadow) {
        int sWidth = fr.getStringWidth(s);
        int sPos = guiWidth / 2 - sWidth / 2;
        fr.drawString(s, sPos, y, color, shadow);
    }

    public static void drawStringCenteredAtPos(FontRenderer fr, String s, int x, int y) {
        drawStringCenteredAtPos(fr, s, x, y, 0x404040, false);
    }

    public static void drawStringCenteredAtPos(FontRenderer fr, String s, int x, int y, int color, boolean shadow) {
        int sWidth = fr.getStringWidth(s);
        //noinspection IntegerDivisionInFloatingPointContext
        fr.drawString(s, x - sWidth / 2, y, color, shadow);
    }

    public static void newButtonRowAuto(List<GuiButton> buttonList, int xStart, int xSize, Collection<? extends GuiBetterButton<?>> buttons) {
        int buttonWidth = buttons.stream().mapToInt(GuiBetterButton::getWidth).sum();
        int remaining = xSize - buttonWidth;
        int spacing = remaining / (buttons.size() + 1);
        int pointer = 0;
        for (GuiBetterButton<?> b : buttons) {
            pointer += spacing;
            b.x = xStart + pointer;
            pointer += b.getWidth();
            buttonList.add(b);
        }
    }

    public static void newButtonRowBookended(List<GuiButton> buttonList, int xStart, int xEnd, Collection<? extends GuiBetterButton<?>> buttons) {
        int buttonWidth = buttons.stream().mapToInt(GuiBetterButton::getWidth).sum();
        int remaining = (xEnd - xStart) - buttonWidth;
        int spacing = remaining / (buttons.size() + 1);
        int pointer = 0;
        for (GuiBetterButton<?> b : buttons) {
            pointer += spacing;
            b.x = xStart + pointer;
            pointer += b.getWidth();
            buttonList.add(b);
        }
    }

    public static void newButtonRow(List<GuiButton> buttonList, int xStart, int spacing, Collection<? extends GuiBetterButton<?>> buttons) {
        int pointer = 0;
        for (GuiBetterButton<?> b : buttons) {
            b.x = xStart + pointer;
            pointer += b.getWidth() + spacing;
            buttonList.add(b);
        }
    }

    public static String getDisplayTitle(ITrackKitInstance kitInstance) {
        TileTrackOutfitted tile = kitInstance.getTile();
        return tile.hasCustomName() ? tile.getName() : LocalizationPlugin.translate(tile.getLocalizationTag());
    }

    public static void drawVillager(EntityVillager villager, int x, int y, int scale, float yaw, float pitch) {
        OpenGL.glPushAttrib(GL11.GL_ENABLE_BIT);
        OpenGL.glEnable(GL11.GL_LIGHTING);
        OpenGL.glEnable(GL11.GL_DEPTH_TEST);
        OpenGL.glEnable(GL11.GL_COLOR_MATERIAL);
        OpenGL.glPushMatrix();
        OpenGL.glTranslatef((float) x, (float) y, 50.0F);
        OpenGL.glScalef((float) (-scale), (float) scale, (float) scale);
        OpenGL.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
        OpenGL.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        OpenGL.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        OpenGL.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
        OpenGL.glRotatef(-((float) Math.atan((double) (pitch / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
        villager.renderYawOffset = (float) Math.atan((double) (yaw / 40.0F)) * 20.0F;
        villager.rotationYaw = (float) Math.atan((double) (yaw / 40.0F)) * 40.0F;
        villager.rotationPitch = -((float) Math.atan((double) (pitch / 40.0F))) * 20.0F;
        villager.rotationYawHead = villager.rotationYaw;
        OpenGL.glTranslatef(0.0F, (float) villager.getYOffset(), 0.0F);
        Minecraft.getMinecraft().getRenderManager().playerViewY = 180.0F;
        Minecraft.getMinecraft().getRenderManager().renderEntity(villager, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
        OpenGL.glPopMatrix();
        RenderHelper.disableStandardItemLighting();
        OpenGL.glDisable(GL12.GL_RESCALE_NORMAL);
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        OpenGL.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        OpenGL.glPopAttrib();
    }

    public static ResourceLocation findTexture(String texture) {
        if (!texture.contains("/"))
            texture = RailcraftConstants.GUI_TEXTURE_FOLDER + texture;
        return new ResourceLocation(texture);
    }
}
