/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render.models.locomotives;

import mods.railcraft.client.render.models.ModelSimple;
import net.minecraft.client.model.ModelRenderer;

public class ModelLocomotiveElectric extends ModelSimple {

    public ModelLocomotiveElectric() {
        super("loco");

        renderer.setTextureSize(128, 64);

        setTextureOffset("loco.wheels", 1, 25);
        setTextureOffset("loco.frame", 1, 1);
        setTextureOffset("loco.engine", 67, 37);
        setTextureOffset("loco.sideA", 35, 45);
        setTextureOffset("loco.sideB", 35, 45);
//        setTextureOffset("loco.lightA", 1, 45);
        setTextureOffset("loco.lightB", 1, 55);

        renderer.rotationPointX = 8F;
        renderer.rotationPointY = 8F;
        renderer.rotationPointZ = 8F;

        ModelRenderer loco = renderer;
        loco.addBox("wheels", -20F, -5F, -16F, 23, 2, 16);
        loco.addBox("frame", -21F, -10F, -17F, 25, 5, 18);
        loco.addBox("engine", -15F, -19F, -16F, 13, 9, 16);
        loco.addBox("sideA", -20F, -17F, -13F, 5, 7, 10);
        loco.addBox("sideB", -2F, -17F, -13F, 5, 7, 10);
//        loco.addBox("lightA", -2F, -18F, -10F, 6, 4, 4);
        loco.addBox("lightB", -21F, -18F, -10F, 6, 4, 4);

    }

}
