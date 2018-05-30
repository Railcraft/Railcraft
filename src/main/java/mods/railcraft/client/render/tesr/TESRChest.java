/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.render.tesr;

import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.single.TileChestRailcraft;
import mods.railcraft.common.core.RailcraftConstants;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class TESRChest extends TileEntitySpecialRenderer<TileChestRailcraft> {

    /**
     * The Ender Chest Chest's model.
     */
    private final ModelChest chestModel = new ModelChest();

    private final ResourceLocation texture;

    public TESRChest(RailcraftBlocks type) {
        this.texture = new ResourceLocation(RailcraftConstants.TESR_TEXTURE_FOLDER + type.getBaseTag() + ".png");
    }

    /**
     * Helps to render Ender Chest.
     */
    @Override
    public void render(TileChestRailcraft tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
//        if (tile.hasWorld()) {
//            RenderHelper.enableStandardItemLighting();
//            int i = Minecraft.getMinecraft().world.getCombinedLight(tile.getPos(), 0);
//            int j = i % 65536;
//            int k = i / 65536;
//            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j, (float) k);
//            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
//        }
//
//        if (destroyStage >= 0) {
//            bindTexture(DESTROY_STAGES[destroyStage]);
//            OpenGL.glMatrixMode(GL11.GL_TEXTURE);
//            OpenGL.glPushMatrix();
//            OpenGL.glScalef(4.0F, 4.0F, 1.0F);
//            OpenGL.glTranslatef(0.0625F, 0.0625F, 0.0625F);
//            OpenGL.glMatrixMode(GL11.GL_MODELVIEW);
//        } else {
//            bindTexture(texture);
//        }
//
//        OpenGL.glPushMatrix();
//        OpenGL.glPushAttrib();
//        OpenGL.glEnable(GL12.GL_RESCALE_NORMAL);
//        OpenGL.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
//        OpenGL.glTranslatef((float) x, (float) y + 1.0F, (float) z + 1.0F);
//        OpenGL.glScalef(1.0F, -1.0F, -1.0F);
//        OpenGL.glTranslatef(0.5F, 0.5F, 0.5F);
//        short rotation = 0;
//
//        switch (tile.getFacing()) {
//            case SOUTH:
//                rotation = 180;
//                break;
//            case NORTH:
//                rotation = 0;
//                break;
//            case EAST:
//                rotation = 90;
//                break;
//            case WEST:
//                rotation = -90;
//                break;
//        }
//
//        OpenGL.glRotatef((float) rotation, 0.0F, 1.0F, 0.0F);
//        OpenGL.glTranslatef(-0.5F, -0.5F, -0.5F);
//        float lidAngle = tile.prevLidAngle + (tile.lidAngle - tile.prevLidAngle) * partialTicks;
//        lidAngle = 1.0F - lidAngle;
//        lidAngle = 1.0F - lidAngle * lidAngle * lidAngle;
//        chestModel.chestLid.rotateAngleX = -(lidAngle * (float) Math.PI / 2.0F);
//        chestModel.renderAll();
//        OpenGL.glPopAttrib();
//        OpenGL.glPopMatrix();
//        OpenGL.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
//
//        if (destroyStage >= 0) {
//            OpenGL.glMatrixMode(GL11.GL_TEXTURE);
//            OpenGL.glPopMatrix();
//            OpenGL.glMatrixMode(GL11.GL_MODELVIEW);
//        }

        int i = 0;

        if (tile.hasWorld()) {
            i = tile.getBlockMetadata();
        }

        if (destroyStage >= 0) {
            this.bindTexture(DESTROY_STAGES[destroyStage]);
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.scale(4.0F, 4.0F, 1.0F);
            GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
            GlStateManager.matrixMode(5888);
        } else {
            this.bindTexture(texture);
        }

        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
        GlStateManager.translate((float) x, (float) y + 1.0F, (float) z + 1.0F);
        GlStateManager.scale(1.0F, -1.0F, -1.0F);
        GlStateManager.translate(0.5F, 0.5F, 0.5F);
        int j = 0;

        if (i == 2) {
            j = 180;
        }

        if (i == 3) {
            j = 0;
        }

        if (i == 4) {
            j = 90;
        }

        if (i == 5) {
            j = -90;
        }

        GlStateManager.rotate((float) j, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(-0.5F, -0.5F, -0.5F);
        float f = tile.prevLidAngle + (tile.lidAngle - tile.prevLidAngle) * partialTicks;
        f = 1.0F - f;
        f = 1.0F - f * f * f;
        this.chestModel.chestLid.rotateAngleX = -(f * ((float) Math.PI / 2F));
        this.chestModel.renderAll();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        if (destroyStage >= 0) {
            GlStateManager.matrixMode(5890);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
        }
    }

}
