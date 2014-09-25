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
public class ModelEngineFrame extends ModelSimple
{

    public ModelEngineFrame()
    {
        super("frame");
        renderer.setTextureSize(128, 128);
        setTextureOffset("frame.boxA", 65, 1);
        setTextureOffset("frame.boxB", 1, 23);
        setTextureOffset("frame.boxC", 1, 45);
        renderer.rotationPointX = 8F;
        renderer.rotationPointY = 8F;
        renderer.rotationPointZ = 8F;
        renderer.addBox("boxA", -6F, -4f, -6F, 12, 4, 12);
        renderer.addBox("boxB", -3F, -4f, -8F, 6, 4, 16);
        renderer.addBox("boxC", -8F, -4f, -3F, 16, 4, 6);
    }
}
