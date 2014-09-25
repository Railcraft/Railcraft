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

public class ModelLocomotiveSteamMagic extends ModelSimple {

    public ModelLocomotiveSteamMagic() {
        super("loco");

        renderer.setTextureSize(128, 64);

        setTextureOffset("loco.wheels", 1, 23);
        setTextureOffset("loco.frame", 1, 1);
        setTextureOffset("loco.boiler", 68, 38);
        setTextureOffset("loco.cab", 81, 8);
        setTextureOffset("loco.cowcatcher", 1, 43);
        setTextureOffset("loco.stack", 47, 43);
        setTextureOffset("loco.dome", 21, 43);
        setTextureOffset("loco.jar1", 28, 52);
        setTextureOffset("loco.jar2", 28, 52);
        setTextureOffset("loco.jar3", 28, 52);
        setTextureOffset("loco.jar4", 28, 52);
        setTextureOffset("loco.pipe1", 45, 53);
        setTextureOffset("loco.pipe2", 45, 53);
        setTextureOffset("loco.pipe3", 45, 53);
        setTextureOffset("loco.pipe4", 45, 53);

        renderer.rotationPointX = 8F;
        renderer.rotationPointY = 8F;
        renderer.rotationPointZ = 8F;

        ModelRenderer loco = renderer;
        loco.addBox("wheels", -20F, -5F, -16F, 23, 2, 16);
        loco.addBox("frame", -21F, -7F, -17F, 25, 2, 18);
        loco.addBox("boiler", -20F, -17F, -14F, 16, 10, 12);
        loco.addBox("cab", -4F, -19F, -16F, 7, 12, 16);
        loco.addBox("cowcatcher", -22F, -8F, -13F, 3, 5, 10);
        loco.addBox("stack", -17F, -22F, -10F, 4, 5, 4);
        loco.addBox("dome", -11F, -19F, -11F, 6, 2, 6);
        loco.addBox("jar1", -10F, -13F, -4F, 4, 6, 4);
        loco.addBox("jar2", -10F, -13F, -16F, 4, 6, 4);
        loco.addBox("jar3", -15F, -13F, -4F, 4, 6, 4);
        loco.addBox("jar4", -15F, -13F, -16F, 4, 6, 4);
        loco.addBox("pipe1", -9F, -16F, -3F, 2, 3, 2);
        loco.addBox("pipe2", -9F, -16F, -15F, 2, 3, 2);
        loco.addBox("pipe3", -14F, -16F, -3F, 2, 3, 2);
        loco.addBox("pipe4", -14F, -16F, -15F, 2, 3, 2);

    }

}
