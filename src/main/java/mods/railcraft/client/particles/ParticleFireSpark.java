/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.particles;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@SideOnly(Side.CLIENT)
public class ParticleFireSpark extends ParticleBase {

    private final float lavaParticleScale;
    private final Vec3d end;
    private final double maxHorizontalDist;

    public ParticleFireSpark(World world, Vec3d start, Vec3d end) {
        super(world, start, new Vec3d(0, 0, 0));
        this.end = end;

        maxHorizontalDist = getHorizontalDistSq(end);
        calculateVector(maxHorizontalDist);

        multipleParticleScaleBy(0.5f);

        this.particleRed = this.particleGreen = this.particleBlue = 1.0F;
        this.particleScale *= rand.nextFloat() * 2.0F + 0.2F;
        this.lavaParticleScale = particleScale;
        this.particleMaxAge = 2000;
        this.canCollide = false;
        setParticleTextureIndex(49);
    }

    private double getHorizontalDistSq(Vec3d point) {
        Vec3d pos = getPos();
        double xDiff = pos.x - point.x;
        double zDiff = pos.z - point.z;
        return xDiff * xDiff + zDiff * zDiff;
    }

    private void calculateVector(double dist) {
        Vec3d vecParticle = getPos();

        Vec3d vel = end.subtract(vecParticle);
        vel = vel.normalize();

        float velScale = 0.1f;
        this.motionX = vel.x * velScale;
        this.motionY = vel.y * velScale + 0.4 * (dist / maxHorizontalDist);
        this.motionZ = vel.z * velScale;
    }

    @Override
    public int getBrightnessForRender(float par1) {
        int brightness = super.getBrightnessForRender(par1);
        short short1 = 240;
        int j = brightness >> 16 & 255;
        return short1 | j << 16;
    }

//    /**
//     * Gets how bright this entity is.
//     */
//    @Override
//    public float getBrightness(float par1) {
//        return 1.0F;
//    }

    @Override
    public void renderParticle(BufferBuilder world, Entity entity, float par2, float par3, float par4, float par5, float par6, float par7) {
        float f6 = ((float) particleAge + par2) / (float) particleMaxAge;
        this.particleScale = lavaParticleScale * (1.0F - f6 * f6);
        super.renderParticle(world, entity, par2, par3, par4, par5, par6, par7);
    }

    /**
     * Called to update the entity's position/logic.
     */
    @Override
    public void onUpdate() {
        this.prevPosX = posX;
        this.prevPosY = posY;
        this.prevPosZ = posZ;

        if (particleAge >= particleMaxAge) {
            setExpired();
            return;
        }
        this.particleAge++;

        // Called to spawn smoke particles with the entity
        float f = (float) particleAge / (float) particleMaxAge;

        if (rand.nextFloat() > f)
        {
            world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, posX, posY, posZ, motionX, motionY, motionZ, 0);
        }

        double dist = getPos().squareDistanceTo(end);
        if (dist <= 0.1) {
            setExpired();
            return;
        }

        calculateVector(getHorizontalDistSq(end));

        move(motionX, motionY, motionZ);
    }

}
