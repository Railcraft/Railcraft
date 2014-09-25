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
public class ModelEnginePiston extends ModelSimple
{

    public ModelEnginePiston()
    {
        super("piston");
        renderer.setTextureSize(128, 128);
        setTextureOffset("piston.core", 32, 24);
        setTextureOffset("piston.barA", 32, 24);
        setTextureOffset("piston.barB", 32, 24);
        setTextureOffset("piston.barC", 32, 24);
        setTextureOffset("piston.barD", 32, 24);
        renderer.rotationPointX = 8F;
        renderer.rotationPointY = 8F;
        renderer.rotationPointZ = 8F;
        renderer.addBox("core", -5F, -4, -5F, 10, 2, 10);
        renderer.addBox("barA", -7F, -4, -1F, 1, 2, 2);
        renderer.addBox("barB", -1F, -4, -7F, 2, 2, 1);
        renderer.addBox("barC", 6F, -4, -1F, 1, 2, 2);
        renderer.addBox("barD", -1F, -4, 6F, 2, 2, 1);
    }
}
