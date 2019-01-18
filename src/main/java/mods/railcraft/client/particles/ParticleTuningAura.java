/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.particles;

import mods.railcraft.client.util.effects.ClientEffects;
import mods.railcraft.common.util.effects.EffectManager.EffectSourceEntity;
import mods.railcraft.common.util.effects.EffectManager.IEffectSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@SideOnly(Side.CLIENT)
public class ParticleTuningAura extends ParticleBase {
    private final IEffectSource source;
    private final IEffectSource dest;

    public ParticleTuningAura(World world, Vec3d start, IEffectSource source, IEffectSource dest, int colorSeed) {
        super(world, start);
        this.source = source;
        this.dest = dest;

        calculateVector();

        multipleParticleScaleBy(0.5f);

        float c1 = (float) (colorSeed >> 16 & 255) / 255.0F;
        float c2 = (float) (colorSeed >> 8 & 255) / 255.0F;
        float c3 = (float) (colorSeed & 255) / 255.0F;
//
        float variant = rand.nextFloat() * 0.6F + 0.4F;
//        this.particleRed = this.particleGreen = this.particleBlue = 1.0F * variant;
        this.particleRed = c1 * variant;
        this.particleGreen = c2 * variant;
        this.particleBlue = c3 * variant;
        this.particleMaxAge = 2000;
        this.canCollide = false;
        this.dimAsAge = true;
        setParticleTextureIndex((int) (Math.random() * 8.0D));
    }

    private void calculateVector() {
        Vec3d endPoint = dest.getPosF();
        Vec3d vecParticle = new Vec3d(posX, posY, posZ);

        Vec3d vel = endPoint.subtract(vecParticle);
        vel = vel.normalize();

        float velScale = 0.1f;
        this.motionX = vel.x * velScale;
        this.motionY = vel.y * velScale;
        this.motionZ = vel.z * velScale;
    }

    /**
     * Called to update the entity's position/logic.
     */
    @Override
    public void onUpdate() {
        this.prevPosX = posX;
        this.prevPosY = posY;
        this.prevPosZ = posZ;

        if (source.isDead() || dest.isDead()) {
            setExpired();
            return;
        }

        if (!ClientEffects.INSTANCE.isTuningAuraActive()) {
            setExpired();
            return;
        }

        if (particleAge >= particleMaxAge) {
            setExpired();
            return;
        }
        this.particleAge++;

        if (getPos().squareDistanceTo(dest.getPosF()) <= 0.3) {
            setExpired();
            return;
        }

        if (dest instanceof EffectSourceEntity) {
            calculateVector();
        }

        move(motionX, motionY, motionZ);
    }
}
