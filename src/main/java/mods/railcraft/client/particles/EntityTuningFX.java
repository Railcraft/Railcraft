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
import net.minecraft.client.particle.EntityFX;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import mods.railcraft.common.util.effects.EffectManager;
import mods.railcraft.common.util.effects.EffectManager.EffectSourceEntity;
import mods.railcraft.common.util.effects.EffectManager.IEffectSource;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@SideOnly(Side.CLIENT)
public class EntityTuningFX extends EntityFX {
    private final IEffectSource source;

    public EntityTuningFX(World world, double x, double y, double z, IEffectSource source, int colorSeed) {
        super(world, x, y, z, 0, 0, 0);
        this.source = source;

        calculateVector();

        multipleParticleScaleBy(0.5f);

        float c1 = (float) (colorSeed >> 16 & 255) / 255.0F;
        float c2 = (float) (colorSeed >> 8 & 255) / 255.0F;
        float c3 = (float) (colorSeed & 255) / 255.0F;
//
        float variant = this.rand.nextFloat() * 0.6F + 0.4F;
//        this.particleRed = this.particleGreen = this.particleBlue = 1.0F * variant;
        this.particleRed = c1 * variant;
        this.particleGreen = c2 * variant;
        this.particleBlue = c3 * variant;
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

    //    @Override
//    public void renderParticle(Tessellator par1Tessellator, float par2, float par3, float par4, float par5, float par6, float par7) {
//        float var8 = ((float)this.particleAge + par2) / (float)this.particleMaxAge;
//        var8 = 1.0F - var8;
//        var8 *= var8;
//        var8 = 1.0F - var8;
//        this.particleScale = this.portalParticleScale * var8;
//        super.renderParticle(par1Tessellator, par2, par3, par4, par5, par6, par7);
//    }
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

        if (!EffectManager.instance.isTuningAuraActive()) {
            setDead();
            return;
        }

        if (this.particleAge++ >= this.particleMaxAge) {
            this.setDead();
            return;
        }

        if (getDistanceSq(source.getX(), source.getY(), source.getZ()) <= 0.3) {
            this.setDead();
            return;
        }

        if (source instanceof EffectSourceEntity) {
            calculateVector();
        }

        this.moveEntity(this.motionX, this.motionY, this.motionZ);
    }
}
