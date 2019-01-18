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
import mods.railcraft.common.items.ItemGoggles;
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
public class ParticleChunkLoader extends ParticleBase {
    private final IEffectSource source;

    public ParticleChunkLoader(World world, Vec3d start, IEffectSource source) {
        super(world, start, new Vec3d(0, 0, 0));
        this.source = source;

        calculateVector();

        multipleParticleScaleBy(1.2f);
        float brightness = rand.nextFloat() * 0.6F + 0.4F;
        this.particleRed = this.particleGreen = this.particleBlue = 1.0F * brightness;
        this.particleGreen *= 0.3F;
        this.particleRed *= 0.9F;
        this.particleMaxAge = 250;
        this.canCollide = false;
        this.dimAsAge = true;
        setParticleTextureIndex((int) (Math.random() * 8.0D));
    }

    private void calculateVector() {
        Vec3d endPoint = source.getPosF();
        Vec3d vecParticle = new Vec3d(posX, posY, posZ);

        Vec3d vel = endPoint.subtract(vecParticle);
        vel = vel.normalize();

        float velScale = 0.04f;
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

        if (source.isDead()) {
            setExpired();
            return;
        }

        if (!ClientEffects.INSTANCE.isGoggleAuraActive(ItemGoggles.GoggleAura.WORLDSPIKE)) {
            setExpired();
            return;
        }

        if (particleAge >= particleMaxAge) {
            setExpired();
            return;
        }
        this.particleAge++;

        if (getPos().squareDistanceTo(source.getPosF()) <= 0.5) {
            setExpired();
            return;
        }

        if (source instanceof EffectSourceEntity) {
            calculateVector();
        }

        move(motionX, motionY, motionZ);
    }
}
