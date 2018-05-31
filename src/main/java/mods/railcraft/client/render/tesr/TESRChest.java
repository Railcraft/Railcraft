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
import mods.railcraft.common.blocks.single.BlockChestMetals;
import mods.railcraft.common.blocks.single.TileChestRailcraft;
import mods.railcraft.common.core.RailcraftConstants;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
        final BlockPos pos = tile.getPos();
        final World world = tile.getWorld();
        final IBlockState blockState = world.getBlockState(pos);
        final int blockX = pos.getX();
        final int blockY = pos.getY();
        final int blockZ = pos.getZ();
        float brightness = 0f;

        switch (blockState.getValue(BlockHorizontal.FACING)) {
            case SOUTH:
                brightness = blockState.getPackedLightmapCoords(world, new BlockPos(blockX, blockY, blockZ + 1));
                break;
            case WEST:
                brightness = blockState.getPackedLightmapCoords(world, new BlockPos(blockX - 1, blockY, blockZ));
                break;
            case NORTH:
                brightness = blockState.getPackedLightmapCoords(world, new BlockPos(blockX, blockY, blockZ - 1));
                break;
            case EAST:
                brightness = blockState.getPackedLightmapCoords(world, new BlockPos(blockX + 1, blockY, blockZ));
                break;
        }

        GlStateManager.enableDepth();
        GlStateManager.depthFunc(515);
        GlStateManager.depthMask(true);

        int i = 0;

        if (tile.hasWorld()) {
            i = tile.getBlockState().getValue(BlockChestMetals.FACING).ordinal();
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

        if (destroyStage < 0) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
        }

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

        // Set lightmap coordinates to the skylight value of the block in front of the cache item model
        float jl = brightness % 65536;
        float kl = brightness / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, jl / 1.0F, kl / 1.0F);
        GlStateManager.color(1f, 1f, 1f, 1f);

        GlStateManager.rotate((float) j, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(-0.5F, -0.5F, -0.5F);
        float f = tile.prevLidAngle + (tile.lidAngle - tile.prevLidAngle) * partialTicks;
        f = 1.0F - f;
        f = 1.0F - f * f * f;
        this.chestModel.chestLid.rotateAngleX = -(f * ((float) Math.PI / 2F));
        this.chestModel.renderAll();
        GlStateManager.disableRescaleNormal();

        // Revert lightmap texture coordinates to world values pre-render tick
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, OpenGlHelper.lastBrightnessX, OpenGlHelper.lastBrightnessY);

        GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        if (destroyStage >= 0) {
            GlStateManager.matrixMode(5890);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
        }
    }
}
