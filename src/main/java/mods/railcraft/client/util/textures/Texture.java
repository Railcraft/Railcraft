/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.util.textures;

import java.awt.image.BufferedImage;
import net.minecraft.client.renderer.texture.AbstractTexture;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public abstract class Texture extends AbstractTexture {

    protected BufferedImage imageData;

    public BufferedImage getImage() {
        return imageData;
    }

}
