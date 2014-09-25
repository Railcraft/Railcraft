/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render.models.carts;

import mods.railcraft.client.render.models.ModelTextured;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ModelMaintance extends ModelTextured {

    public ModelMaintance() {
        super("maint");
        renderer.setTextureSize(64, 64);
        setTextureOffset("maint.base", 0, 1);
        setTextureOffset("maint.bracket", 1, 35);
        renderer.addBox("base", -8, -8, -8, 16, 16, 16);
        renderer.addBox("bracket", -3, 8, -3, 6, 1, 6);
        renderer.rotationPointX = 8F;
        renderer.rotationPointY = 8F;
        renderer.rotationPointZ = 8F;
    }
}
