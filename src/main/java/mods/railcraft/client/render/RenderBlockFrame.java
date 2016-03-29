/* Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar and may only be used with explicit written permission unless otherwise
 * specified on the license page at http://railcraft.info/wiki/info:license. */
package mods.railcraft.client.render;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;

import mods.railcraft.common.blocks.frame.BlockFrame;

/** @author CovertJaguar <http://www.railcraft.info> */
public class RenderBlockFrame extends BlockModelBase {

    public RenderBlockFrame() {
        super(BlockFrame.getBlock());
    }

    @Override
    public IBakedModel bakeItem(ItemStack item) {
        CuboidRenderHelper helper = CuboidRenderHelper.INSTANCE;
        List<BakedQuad> quads = new ArrayList<BakedQuad>();
        IBlockState state = getBlock().getDefaultState();
        helper.resetAll();
        helper.fillTexturesFromBlock(state);
        helper.setBounds(0.999, 0.999, 0.999, 0.001, 0.001, 0.001);
        helper.bakeAllFaces(DefaultVertexFormats.ITEM, quads);
        helper.setBounds(0, 0, 0, 1, 1, 1);
        helper.bakeAllFaces(DefaultVertexFormats.ITEM, quads);
        TextureAtlasSprite particle = CuboidRenderHelper.getParticleTexture(state);
        return new PerspAwareModelBase(DefaultVertexFormats.ITEM, ImmutableList.copyOf(quads), particle, getBlockTransforms());
    }

    @Override
    public IBakedModel handleBlockState(IBlockState state) {
        CuboidRenderHelper helper = CuboidRenderHelper.INSTANCE;
        List<BakedQuad> quads = new ArrayList<BakedQuad>();
        helper.resetAll();
        helper.fillTexturesFromBlock(state);
        helper.setBounds(0.999, 0.999, 0.999, 0.001, 0.001, 0.001);
        helper.bakeAllFaces(DefaultVertexFormats.BLOCK, quads);
        helper.setBounds(0, 0, 0, 1, 1, 1);
        helper.bakeAllFaces(DefaultVertexFormats.BLOCK, quads);
        TextureAtlasSprite particle = CuboidRenderHelper.getParticleTexture(state);
        return new PerspAwareModelBase(DefaultVertexFormats.BLOCK, ImmutableList.copyOf(quads), particle, getBlockTransforms());
    }
}
