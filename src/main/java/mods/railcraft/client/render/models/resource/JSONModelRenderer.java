/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.client.render.models.resource;

import mods.railcraft.client.render.tools.RenderTools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.*;

/**
 * Created by CovertJaguar on 9/2/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
@SideOnly(Side.CLIENT)
public final class JSONModelRenderer {
    public static final JSONModelRenderer INSTANCE = new JSONModelRenderer();
    private final Set<ResourceLocation> modelLocations = new LinkedHashSet<>();
    private final Map<ResourceLocation, IModel> models = new LinkedHashMap<>();
    private final Map<ResourceLocation, IBakedModel> bakedModels = new LinkedHashMap<>();

    private JSONModelRenderer() {
    }

    @SubscribeEvent
    public void loadModels(TextureStitchEvent.Pre event) {
        final TextureMap map = event.getMap();
        for (ResourceLocation modelLocation : modelLocations) {
            IModel model = ModelManager.getModel(modelLocation);
            models.put(modelLocation, model);
            model.getTextures().forEach(map::registerSprite);
        }
    }

    @SubscribeEvent
    public void bakeModels(TextureStitchEvent.Post event) {
        final TextureMap map = event.getMap();
        for (Map.Entry<ResourceLocation, IModel> model : models.entrySet()) {
            IBakedModel bakedModel = model.getValue().bake(TRSRTransformation.identity(),
                    DefaultVertexFormats.BLOCK,
                    l -> l == null ? RenderTools.getMissingTexture() : map.getAtlasSprite(l.toString()));
            bakedModels.put(model.getKey(), bakedModel);
        }
    }

    public void registerModel(ResourceLocation modelLocation) {
        modelLocations.add(modelLocation);
    }

    public void renderModel(ResourceLocation model) {
        IBakedModel bakedModel = bakedModels.get(model);
        if (bakedModel == null) return;
        Minecraft mc = Minecraft.getMinecraft();
        mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buffer = tess.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
        putQuads(buffer, bakedModel.getQuads(null, null, 1234));
        for (EnumFacing side : EnumFacing.VALUES) {
            putQuads(buffer, bakedModel.getQuads(null, side, 1234));
        }

        tess.draw();
        mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
    }

    private void putQuads(BufferBuilder buffer, List<BakedQuad> quads) {
        for (BakedQuad quad : quads) {
            buffer.addVertexData(quad.getVertexData());
        }
    }
}
