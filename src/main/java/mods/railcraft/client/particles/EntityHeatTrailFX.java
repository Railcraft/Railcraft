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
import java.util.Random;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import mods.railcraft.common.util.effects.EffectManager.EffectSourceEntity;
import mods.railcraft.common.util.effects.EffectManager.IEffectSource;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
@SideOnly(Side.CLIENT)
public class EntityHeatTrailFX extends EntityFX {

    private static Random colorRand = new Random();
    private final IEffectSource source;

    public EntityHeatTrailFX(World world, double x, double y, double z, long colorSeed, IEffectSource source) {
        super(world, x, y, z, 0, 0, 0);
        this.source = source;

        calculateVector();

        multipleParticleScaleBy(0.5f);

        colorRand.setSeed(colorSeed);
        this.particleRed = colorRand.nextFloat() * 0.8F + 0.2F;
        this.particleGreen = colorRand.nextFloat() * 0.8F + 0.2F;
        this.particleBlue = colorRand.nextFloat() * 0.8F + 0.2F;
        float varient = this.rand.nextFloat() * 0.6F + 0.4F;
        this.particleRed *= varient;
        this.particleGreen *= varient;
        this.particleBlue *= varient;
        this.particleMaxAge = 2000;
        this.noClip = true;
        this.setParticleTextureIndex((int) (Math.random() * 8.0D));
    }

    private void calculateVector() {
        Vec3 endPoint = Vec3.createVectorHelper(source.getX(), source.getY(), source.getZ());
        Vec3 vecParticle = Vec3.createVectorHelper(posX, posY, posZ);

        Vec3 vel = vecParticle.subtract(endPoint);
        vel = vel.normalize();

        float velScale = 0.1f;
        this.motionX = vel.xCoord * velScale;
        this.motionY = vel.yCoord * velScale;
        this.motionZ = vel.zCoord * velScale;
    }

    @Override
    public int getBrightnessForRender(float par1) {
        int var2 = super.getBrightnessForRender(par1);
        float var3 = (float) this.particleAge / (float) this.particleMaxAge;
        var3 *= var3;
        var3 *= var3;
        int var4 = var2 & 255;
        int var5 = var2 >> 16 & 255;
        var5 += (int) (var3 * 15.0F * 16.0F);

        if (var5 > 240) {
            var5 = 240;
        }

        return var4 | var5 << 16;
    }

    /**
     * Gets how bright this entity is.
     */
    @Override
    public float getBrightness(float par1) {
        float var2 = super.getBrightness(par1);
        float var3 = (float) this.particleAge / (float) this.particleMaxAge;
        var3 = var3 * var3 * var3 * var3;
        return var2 * (1.0F - var3) + var3;
    }

    /**
     * Called to update the entity's position/logic.
     */
    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (source.isDead()) {
            setDead();
            return;
        }

        if (this.particleAge++ >= this.particleMaxAge) {
            this.setDead();
            return;
        }

        if (getDistanceSq(source.getX(), source.getY(), source.getZ()) <= 0.1) {
            this.setDead();
            return;
        }

        if (source instanceof EffectSourceEntity) {
            calculateVector();
        }

        this.moveEntity(this.motionX, this.motionY, this.motionZ);
    }

}
