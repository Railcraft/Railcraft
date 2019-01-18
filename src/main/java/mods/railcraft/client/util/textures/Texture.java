/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.client.util.textures;

import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class Texture extends AbstractTexture {

    protected BufferedImage imageData;

    protected Texture() {
    }

    protected Texture(BufferedImage image) {
        this.imageData = image;
    }

    @SuppressWarnings("RedundantThrows")
    @Override
    public void loadTexture(IResourceManager iResourceManager) throws IOException {
        deleteGlTexture();
        TextureUtil.uploadTextureImageAllocate(getGlTextureId(), imageData, false, false);
    }

    public BufferedImage getImage() {
        return imageData;
    }

}
