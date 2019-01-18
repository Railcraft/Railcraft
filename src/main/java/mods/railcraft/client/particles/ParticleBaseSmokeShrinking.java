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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class ParticleBaseSmokeShrinking extends ParticleBaseSmoke {

    private final float originalScale;

    protected ParticleBaseSmokeShrinking(World world, Vec3d start, Vec3d vel, float scale) {
        super(world, start, vel, scale);
        this.originalScale = particleScale;
    }

    @Override
    public void renderParticle(BufferBuilder worldRendererIn, Entity entityIn, float par2, float par3, float par4, float par5, float par6, float par7) {
        float age = ((float) particleAge + par2) / (float) particleMaxAge * 32.0F;

        age = MathHelper.clamp(age, 0.0F, 1.0F);

        this.particleScale = originalScale * age;
        super.renderParticle(worldRendererIn, entityIn, par2, par3, par4, par5, par6, par7);
    }

}
