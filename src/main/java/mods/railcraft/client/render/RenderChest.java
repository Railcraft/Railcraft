/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render;

import mods.railcraft.common.blocks.machine.beta.TileChestRailcraft;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class RenderChest extends TileEntitySpecialRenderer<TileChestRailcraft> implements IInvRenderer {

    /**
     * The Ender Chest Chest's model.
     */
    private final ModelChest chestModel = new ModelChest();
    private final TileChestRailcraft itemTile;

    private final ResourceLocation texture;

    public RenderChest(String texture, TileChestRailcraft itemTile) {
        this.texture = new ResourceLocation(texture);
        this.itemTile = itemTile;
    }

    /**
     * Helps to render Ender Chest.
     */
    @Override
    public void renderTileEntityAt(TileChestRailcraft tile, double x, double y, double z, float partialTicks, int destroyStage) {
        int facing = tile.getFacing().ordinal();

        if (destroyStage >= 0) {
            bindTexture(DESTROY_STAGES[destroyStage]);
            OpenGL.glMatrixMode(OpenGL.GL_TEXTURE);
            OpenGL.glPushMatrix();
            OpenGL.glScalef(4.0F, 4.0F, 1.0F);
            OpenGL.glTranslatef(0.0625F, 0.0625F, 0.0625F);
            OpenGL.glMatrixMode(OpenGL.GL_MODELVIEW);
        } else {
            bindTexture(texture);
        }

        OpenGL.glPushMatrix();
        OpenGL.glPushAttrib();
        OpenGL.glEnable(OpenGL.GL_RESCALE_NORMAL);
        OpenGL.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        OpenGL.glTranslatef((float) x, (float) y + 1.0F, (float) z + 1.0F);
        OpenGL.glScalef(1.0F, -1.0F, -1.0F);
        OpenGL.glTranslatef(0.5F, 0.5F, 0.5F);
        short rotation = 0;

        switch (facing) {
            case 2:
                rotation = 180;
                break;
            case 3:
                rotation = 0;
                break;
            case 4:
                rotation = 90;
                break;
            case 5:
                rotation = -90;
                break;
        }

        OpenGL.glRotatef((float) rotation, 0.0F, 1.0F, 0.0F);
        OpenGL.glTranslatef(-0.5F, -0.5F, -0.5F);
        float lidAngle = tile.prevLidAngle + (tile.lidAngle - tile.prevLidAngle) * partialTicks;
        lidAngle = 1.0F - lidAngle;
        lidAngle = 1.0F - lidAngle * lidAngle * lidAngle;
        chestModel.chestLid.rotateAngleX = -(lidAngle * (float) Math.PI / 2.0F);
        chestModel.renderAll();
        OpenGL.glPopAttrib();
        OpenGL.glPopMatrix();
        OpenGL.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        if (destroyStage >= 0) {
            OpenGL.glMatrixMode(OpenGL.GL_TEXTURE);
            OpenGL.glPopMatrix();
            OpenGL.glMatrixMode(OpenGL.GL_MODELVIEW);
        }
    }

    @Override
    public void renderItem(RenderBlocks renderBlocks, ItemStack item, ItemRenderType renderType) {
        TileEntityRendererDispatcher.instance.renderTileEntityAt(itemTile, 0.0D, 0.0D, 0.0D, 0.0F);
    }

}
