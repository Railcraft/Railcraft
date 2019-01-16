/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.client.particles;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Created by CovertJaguar on 7/31/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ParticleSpark extends ParticleBase {
    public static TextureAtlasSprite sprite;

    public ParticleSpark(World world, Vec3d start, Vec3d vel) {
        super(world, start, vel);
        setParticleTexture(sprite);
        particleGravity = 1.0F;
        this.particleMaxAge = (int) (8.0D / (Math.random() * 0.8D + 0.2D));
    }

    @Override
    public int getFXLayer() {
        return 1;
    }
}
