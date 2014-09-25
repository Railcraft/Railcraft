/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render.models.carts;

import mods.railcraft.client.render.models.ModelSimple;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ModelMaintanceLampOff extends ModelSimple {

    public ModelMaintanceLampOff() {
        super("maint");
        renderer.setTextureSize(16, 16);
        setTextureOffset("maint.lamp", 0, 1);
        renderer.addBox("lamp", -2, 9, -2, 4, 4, 4);
        renderer.rotationPointX = 8F;
        renderer.rotationPointY = 8F;
        renderer.rotationPointZ = 8F;
    }
}
