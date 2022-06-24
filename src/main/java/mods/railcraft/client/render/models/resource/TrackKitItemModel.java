/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.client.render.models.resource;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import mods.railcraft.api.tracks.TrackKit;
import mods.railcraft.api.tracks.TrackRegistry;
import mods.railcraft.common.blocks.tracks.outfitted.ItemTrackKit;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.common.model.IModelState;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;

/**
 * Created by CovertJaguar on 8/18/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TrackKitItemModel implements IModel {
    private final ItemLayerModel model;

    public TrackKitItemModel(ImmutableList<ResourceLocation> textures) {
        this.model = new ItemLayerModel(textures);
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return model.getDependencies();
    }

    @Override
    public Collection<ResourceLocation> getTextures() {
        return model.getTextures();
    }

    @Override
    public IModelState getDefaultState() {
        return Transforms.getItem();
    }

    @Override
    public ItemLayerModel retexture(ImmutableMap<String, String> textures) {
        return model.retexture(textures);
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        return model.bake(state, format, bakedTextureGetter);
    }

    public enum Loader implements ICustomModelLoader {
        INSTANCE;

        @Override
        public void onResourceManagerReload(IResourceManager resourceManager) {
        }

        @Override
        public boolean accepts(ResourceLocation modelLocation) {
            return Objects.equals(modelLocation.getNamespace(), "railcraft")
                    && modelLocation.getPath().startsWith(ItemTrackKit.MODEL_PREFIX);
        }

        @Override
        public IModel loadModel(ResourceLocation modelLocation) {
            String[] tokens = modelLocation.getPath().split("\\.");
            TrackKit trackKit = TrackRegistry.TRACK_KIT.get(tokens[1]);
            ImmutableList.Builder<ResourceLocation> texBuilder = ImmutableList.builder();

            texBuilder.add(new ResourceLocation(trackKit.getRegistryName().getNamespace(),
                    "items/track_kits/" + trackKit.getRegistryName().getPath()));
            return new TrackKitItemModel(texBuilder.build());
        }
    }
}
