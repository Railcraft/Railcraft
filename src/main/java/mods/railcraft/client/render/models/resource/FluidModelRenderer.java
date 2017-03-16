/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.client.render.models.resource;

import mods.railcraft.client.render.tools.RenderTools;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.List;

/**
 * Created by CovertJaguar on 9/2/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
@SideOnly(Side.CLIENT)
public class FluidModelRenderer {
    public static final FluidModelRenderer INSTANCE = new FluidModelRenderer();

    private FluidModelRenderer() {
    }

    @SubscribeEvent
    public void loadTextures(TextureStitchEvent.Pre event) {
        final TextureMap map = event.getMap();
        FluidRegistry.getRegisteredFluids().values().forEach(f -> {
            map.registerSprite(f.getFlowing());
            map.registerSprite(f.getStill());
        });
    }

    /**
     * @param level Ranges from 1 to 16 inclusively, 0 will crash
     */
    public void renderFluid(FluidStack fluidStack, int level) {
        IBakedModel bakedModel = new FluidModel(fluidStack, false).bake(TRSRTransformation.identity(),
                DefaultVertexFormats.BLOCK, RenderTools::getTexture);
        Minecraft mc = Minecraft.getMinecraft();
        mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
        Tessellator tess = Tessellator.getInstance();
        VertexBuffer buffer = tess.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
        IExtendedBlockState state = (IExtendedBlockState) new ExtendedBlockState(Blocks.WATER, new IProperty[]{BlockFluidBase.LEVEL}, BlockFluidBase.FLUID_RENDER_PROPS.toArray(new IUnlistedProperty<?>[0])).getBaseState();
        for (int i = 0; i < 4; i++)
            state = state.withProperty(BlockFluidBase.LEVEL_CORNERS[i], level / 16F);
        state = (IExtendedBlockState) state.withProperty(BlockFluidBase.LEVEL, level - 1);
        state = state.withProperty(BlockFluidBase.FLOW_DIRECTION, -1000F);
        putQuads(buffer, bakedModel.getQuads(state, null, 1234));
        for (EnumFacing side : EnumFacing.VALUES) {
            putQuads(buffer, bakedModel.getQuads(state, side, 1234));
        }

        tess.draw();
        mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
    }

    private void putQuads(VertexBuffer buffer, List<BakedQuad> quads) {
        for (BakedQuad quad : quads) {
            buffer.addVertexData(quad.getVertexData());
        }
    }
}
