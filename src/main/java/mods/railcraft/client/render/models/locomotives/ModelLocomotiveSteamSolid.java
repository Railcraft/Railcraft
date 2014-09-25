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

public class ModelLocomotiveSteamSolid extends ModelSimple {

    public ModelLocomotiveSteamSolid() {
        super("loco");

        renderer.setTextureSize(128, 64);

        setTextureOffset("loco.wheels", 1, 23);
        setTextureOffset("loco.frame", 1, 1);
        setTextureOffset("loco.boiler", 67, 38);
        setTextureOffset("loco.cab", 81, 8);
        setTextureOffset("loco.cowcatcher", 1, 43);
        setTextureOffset("loco.stack", 49, 43);
        setTextureOffset("loco.dome", 23, 43);

        renderer.rotationPointX = 8F;
        renderer.rotationPointY = 8F;
        renderer.rotationPointZ = 8F;

        ModelRenderer loco = renderer;
        loco.addBox("wheels", -20F, -5F, -16F, 23, 2, 16);
        loco.addBox("frame", -21F, -7F, -17F, 25, 2, 18);
        loco.addBox("boiler", -20F, -18F, -15F, 16, 11, 14);
        loco.addBox("cab", -4F, -19F, -16F, 7, 12, 16);
        loco.addBox("cowcatcher", -22F, -8F, -14F, 3, 5, 12);
        loco.addBox("stack", -17F, -24F, -10F, 4, 6, 4);
        loco.addBox("dome", -11F, -20F, -11F, 6, 2, 6);

    }

}
