/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.particles;

import mods.railcraft.common.plugins.color.EnumColor;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleChimney extends ParticleBaseSmoke {

    public ParticleChimney(World par1World, Vec3d start) {
        this(par1World, start, EnumColor.BLACK);
    }

    public ParticleChimney(World par1World, Vec3d start, EnumColor color) {
        this(par1World, start, new Vec3d(0, 0, 0), 3f, color);
    }

    public ParticleChimney(World par1World, Vec3d start, Vec3d vel, float scale, EnumColor color) {
        super(par1World, start, vel, scale);
        this.particleGravity = ParticleHelper.SMOKE_GRAVITY;
        this.particleRed = MathHelper.clamp_float((float) (Math.random() * 0.1f - 0.05f) + ((color.getHexColor() >> 16) & 0xFF) / 255f, 0f, 1f);
        this.particleGreen = MathHelper.clamp_float((float) (Math.random() * 0.1f - 0.05f) + ((color.getHexColor() >> 8) & 0xFF) / 255f, 0f, 1f);
        this.particleBlue = MathHelper.clamp_float((float) (Math.random() * 0.1f - 0.05f) + ((color.getHexColor()) & 0xFF) / 255f, 0f, 1f);
        this.particleMaxAge = (int) (24.0D / (Math.random() * 0.5D + 0.2D));
        this.particleMaxAge = (int) (particleMaxAge * scale);
    }

}
