/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.particles;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleSteam extends ParticleBaseSmokeShrinking {

    public ParticleSteam(World world, Vec3d start, Vec3d vel) {
        this(world, start, vel, 1.0F);
    }

    public ParticleSteam(World world, Vec3d start, Vec3d vel, float scale) {
        super(world, start, vel, scale);
        this.particleGravity = ParticleHelper.SMOKE_GRAVITY;
        this.particleRed = this.particleGreen = this.particleBlue = (float) (Math.random() * 0.4) + 0.4f;
        this.particleMaxAge = (int) (8.0D / (Math.random() * 0.8D + 0.2D));
        this.particleMaxAge = (int) ((float) particleMaxAge * scale);
    }

}
