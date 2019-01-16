/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.particles;

import mods.railcraft.common.util.effects.EffectManager.EffectSourceEntity;
import mods.railcraft.common.util.effects.EffectManager.IEffectSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@SideOnly(Side.CLIENT)
public class ParticleHeatTrail extends ParticleBase {

    private static Random colorRand = new Random();
    private final IEffectSource source;

    public ParticleHeatTrail(World world, Vec3d start, long colorSeed, IEffectSource source) {
        super(world, start, new Vec3d(0, 0, 0));
        this.source = source;

        calculateVector();

        multipleParticleScaleBy(0.5f);

        colorRand.setSeed(colorSeed);
        this.particleRed = colorRand.nextFloat() * 0.8F + 0.2F;
        this.particleGreen = colorRand.nextFloat() * 0.8F + 0.2F;
        this.particleBlue = colorRand.nextFloat() * 0.8F + 0.2F;
        float variant = rand.nextFloat() * 0.6F + 0.4F;
        this.particleRed *= variant;
        this.particleGreen *= variant;
        this.particleBlue *= variant;
        this.particleMaxAge = 2000;
        this.canCollide = false;
        this.dimAsAge = true;
        setParticleTextureIndex((int) (Math.random() * 8.0D));
    }

    private void calculateVector() {
        Vec3d endPoint = source.getPosF();
        Vec3d vecParticle = new Vec3d(posX, posY, posZ);

        Vec3d vel = vecParticle.subtract(endPoint);
        vel = vel.normalize();

        float velScale = 0.1f;
        this.motionX = vel.x * velScale;
        this.motionY = vel.y * velScale;
        this.motionZ = vel.z * velScale;
    }

//    /**
//     * Gets how bright this entity is.
//     */
//    @Override
//    public float getBrightness(float par1) {
//        float var2 = super.getBrightness(par1);
//        float var3 = (float) this.particleAge / (float) this.particleMaxAge;
//        var3 = var3 * var3 * var3 * var3;
//        return var2 * (1.0F - var3) + var3;
//    }

    /**
     * Called to update the entity's position/logic.
     */
    @Override
    public void onUpdate() {
        this.prevPosX = posX;
        this.prevPosY = posY;
        this.prevPosZ = posZ;

        if (source.isDead()) {
            setExpired();
            return;
        }

        if (particleAge >= particleMaxAge) {
            setExpired();
            return;
        }
        this.particleAge++;

        if (getPos().squareDistanceTo(source.getPosF()) <= 0.1) {
            setExpired();
            return;
        }

        if (source instanceof EffectSourceEntity) {
            calculateVector();
        }

        move(motionX, motionY, motionZ);
    }

}
