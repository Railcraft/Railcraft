/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.render.tesr;

import mods.railcraft.common.blocks.single.BlockChestRailcraft;
import mods.railcraft.common.blocks.single.TileChestRailcraft;
import mods.railcraft.common.core.RailcraftConstants;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import java.util.Objects;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class TESRChest extends TileEntitySpecialRenderer<TileChestRailcraft> {

    /**
     * The Ender Chest Chest's model.
     */
    private final ModelChest chestModel = new ModelChest();

    private final ResourceLocation texture;

    public TESRChest(BlockChestRailcraft type) {
        this.texture = new ResourceLocation(RailcraftConstants.TESR_TEXTURE_FOLDER + Objects.requireNonNull(type.getRegistryName()).getPath() + ".png");
    }

    /**
     * Helps to render Ender Chest.
     */
    @Override
    public void render(TileChestRailcraft tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        final EnumFacing facing = tile.getFacing();

        if (destroyStage >= 0) {
            bindTexture(DESTROY_STAGES[destroyStage]);
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.scale(4.0F, 4.0F, 1.0F);
            GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
            GlStateManager.matrixMode(5888);
        } else {
            bindTexture(texture);
        }

        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
        GlStateManager.translate((float) x, (float) y + 1.0F, (float) z + 1.0F);
        GlStateManager.scale(1.0F, -1.0F, -1.0F);
        GlStateManager.translate(0.5F, 0.5F, 0.5F);

        int j;
        switch (facing) {
            case NORTH:
                j = 180;
                break;
            case SOUTH:
                j = 0;
                break;
            case WEST:
                j = 90;
                break;
            case EAST:
                j = -90;
                break;
            default:
                j = 0;
                break;
        }

        GlStateManager.rotate((float) j, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(-0.5F, -0.5F, -0.5F);
        float f = tile.prevLidAngle + (tile.lidAngle - tile.prevLidAngle) * partialTicks;
        f = 1.0F - f;
        f = 1.0F - f * f * f;
        chestModel.chestLid.rotateAngleX = -(f * ((float) Math.PI / 2F));
        chestModel.renderAll();
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
