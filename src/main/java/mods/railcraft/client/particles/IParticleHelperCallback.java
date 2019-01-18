/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.particles;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleDigging;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IParticleHelperCallback {

    @SideOnly(Side.CLIENT)
    void addHitEffects(ParticleDigging fx, World world, BlockPos pos, IBlockState state);

    @SideOnly(Side.CLIENT)
    void addDestroyEffects(ParticleDigging fx, World world, BlockPos pos, IBlockState state);
}
