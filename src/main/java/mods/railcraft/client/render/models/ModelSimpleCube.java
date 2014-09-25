/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render.models;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ModelSimpleCube extends ModelTextured {

    public ModelSimpleCube() {
        super("cube");
        renderer.setTextureSize(64, 32);
        renderer.addBox(-8F, -8F, -8F, 16, 16, 16);
        renderer.rotationPointX = 8F;
        renderer.rotationPointY = 8F;
        renderer.rotationPointZ = 8F;
    }
}
