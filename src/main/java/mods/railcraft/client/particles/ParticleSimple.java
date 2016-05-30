/*
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.particles;

import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class ParticleSimple extends Particle {

    public double gravity = 0.004D;

    public ParticleSimple(World par1World, double x, double y, double z) {
        this(par1World, x, y, z, 0, 0, 0, 3f);
    }

    public ParticleSimple(World par1World, double x, double y, double z, double velX, double velY, double velZ, float scale) {
        super(par1World, x, y, z, 0.0D, 0.0D, 0.0D);
        this.motionX *= 0.1;
        this.motionY *= 0.1;
        this.motionZ *= 0.1;
        this.motionX += velX;
        this.motionY += velY;
        this.motionZ += velZ;
        this.particleScale *= 0.75F;
        this.particleScale *= scale;
        this.particleMaxAge = (int) (24.0D / (Math.random() * 0.5D + 0.2D));
        this.particleMaxAge = (int) (particleMaxAge * scale);
        this.noClip = true;
    }

    /**
     * Called to update the entity's position/logic.
     */
    @Override
    public void onUpdate() {
        this.prevPosX = posX;
        this.prevPosY = posY;
        this.prevPosZ = posZ;

        if (particleAge >= particleMaxAge)
            setDead();
        this.particleAge++;

        setParticleTextureIndex(7 - particleAge * 8 / particleMaxAge);
        this.motionY += gravity;
        moveEntity(motionX, motionY, motionZ);

        if (posY == prevPosY) {
            this.motionX *= 1.1D;
            this.motionZ *= 1.1D;
        }

        this.motionX *= 0.96D;
        this.motionY *= 0.96D;
        this.motionZ *= 0.96D;

        if (isCollided) {
            this.motionX *= 0.67D;
            this.motionZ *= 0.67D;
        }
    }

}
