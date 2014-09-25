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
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class RenderChest extends TileEntitySpecialRenderer implements IInvRenderer {

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
    public void renderChest(TileChestRailcraft tile, double x, double y, double z, float time) {
        int facing = tile.getFacing().ordinal();

        bindTexture(texture);
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glTranslatef((float) x, (float) y + 1.0F, (float) z + 1.0F);
        GL11.glScalef(1.0F, -1.0F, -1.0F);
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
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

        GL11.glRotatef((float) rotation, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        float lidAngle = tile.prevLidAngle + (tile.lidAngle - tile.prevLidAngle) * time;
        lidAngle = 1.0F - lidAngle;
        lidAngle = 1.0F - lidAngle * lidAngle * lidAngle;
        this.chestModel.chestLid.rotateAngleX = -(lidAngle * (float) Math.PI / 2.0F);
        this.chestModel.renderAll();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float time) {
        this.renderChest((TileChestRailcraft) tile, x, y, z, time);
    }

    @Override
    public void renderItem(RenderBlocks renderBlocks, ItemStack item, ItemRenderType renderType) {
        TileEntityRendererDispatcher.instance.renderTileEntityAt(this.itemTile, 0.0D, 0.0D, 0.0D, 0.0F);
    }

}
