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
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.world.World;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface ParticleHelperCallback {

    @SideOnly(Side.CLIENT)
    void addHitEffects(EntityDiggingFX fx, World world, int x, int y, int z, int meta);

    @SideOnly(Side.CLIENT)
    void addDestroyEffects(EntityDiggingFX fx, World world, int x, int y, int z, int meta);
}
