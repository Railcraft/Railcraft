/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render.models.carts;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import mods.railcraft.client.render.TexturedQuadAdv;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.TexturedQuad;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ModelMaintanceLampOn extends ModelMaintanceLampOff {

    public ModelMaintanceLampOn() {
        for (Object box : renderer.cubeList) {
            TexturedQuadAdv[] quadsNew = new TexturedQuadAdv[6];
            TexturedQuad[] quadsOld = ObfuscationReflectionHelper.getPrivateValue(ModelBox.class, (ModelBox) box, 1);
            for (int i = 0; i < 6; i++) {
                quadsNew[i] = new TexturedQuadAdv(quadsOld[i].vertexPositions);
                quadsNew[i].setBrightness(210);
                quadsNew[i].setColorRGBA(255, 255, 255, 255);
            }
            ObfuscationReflectionHelper.setPrivateValue(ModelBox.class, (ModelBox) box, quadsNew, 1);
        }
    }
}
