/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.particles;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class EntitySteamFX extends EntityShrinkingParticleFX {

    public EntitySteamFX(World world, double x, double y, double z, double velX, double velY, double velZ) {
        this(world, x, y, z, velX, velY, velZ, 1.0F);
    }

    public EntitySteamFX(World world, double x, double y, double z, double velX, double velY, double velZ, float scale) {
        super(world, x, y, z, velX, velY, velZ, scale);
        this.particleRed = this.particleGreen = this.particleBlue = (float) (Math.random() * 0.4) + 0.4f;
        this.particleMaxAge = (int) (8.0D / (Math.random() * 0.8D + 0.2D));
        this.particleMaxAge = (int) ((float) this.particleMaxAge * scale);
    }

}
