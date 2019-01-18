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
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Created by CovertJaguar on 7/31/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ParticlePumpkin extends ParticleBaseSmoke {
    public static TextureAtlasSprite sprite;
    private final float oScale;

    public ParticlePumpkin(World par1World, Vec3d start) {
        this(par1World, start, new Vec3d(0, 0, 0), 2.5f);
    }

    public ParticlePumpkin(World world, Vec3d start, Vec3d vel, float scale) {
        super(world, start, vel, scale);
        setParticleTexture(sprite);
        this.particleGravity = -0.01F;
        this.particleMaxAge = (int) (16.0D / (Math.random() * 0.8D + 0.2D));
//        this.particleScale = 10.0f;
        oScale = rand.nextFloat() + 2F;
    }

    @Override
    public void renderParticle(BufferBuilder worldRendererIn, Entity entityIn, float partialTicks, float par3, float par4, float par5, float par6, float par7) {
        float age = ((float) particleAge) / (float) particleMaxAge;

        age = MathHelper.clamp(age, 0.0F, 1.0F);

//        setSize(0.2F * age, 0.2F * age);
        this.particleScale = oScale * (float) Math.sin(age * Math.PI);
        super.renderParticle(worldRendererIn, entityIn, partialTicks, par3, par4, par5, par6, par7);
    }

    @Override
    public int getFXLayer() {
        return 1;
    }
}
