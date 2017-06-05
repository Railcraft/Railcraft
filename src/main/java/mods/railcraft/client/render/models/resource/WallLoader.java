/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.client.render.models.resource;

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;

import java.io.IOException;
import java.util.Objects;

/**
 * Created by CovertJaguar on 6/5/2017 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
//TODO: Register in client proxy
public enum WallLoader implements ICustomModelLoader {
    INSTANCE {
        @Override
        public void onResourceManagerReload(IResourceManager resourceManager) {
        }

        @Override
        public boolean accepts(ResourceLocation modelLocation) {
            return Objects.equals(modelLocation.getResourceDomain(), "railcraft")
                    && modelLocation.getResourcePath().contains("wall");
        }

        @Override
        public IModel loadModel(ResourceLocation modelLocation) throws IOException {
            // TODO: double check the resource location
            return new MaterialBlockModel(new ResourceLocation("minecraft:blocks/wall"));
        }
    }
}
