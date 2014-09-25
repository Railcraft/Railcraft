/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render.models.engine;

import mods.railcraft.client.render.models.ModelSimple;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ModelEngineBase extends ModelSimple {

    public ModelEngineBase() {
        super("base");
        renderer.setTextureSize(128, 128);
        renderer.setTextureOffset(1, 1);
        renderer.addBox(-8F, -8F, -8F, 16, 4, 16);
        renderer.rotationPointX = 8F;
        renderer.rotationPointY = 8F;
        renderer.rotationPointZ = 8F;
    }
}
