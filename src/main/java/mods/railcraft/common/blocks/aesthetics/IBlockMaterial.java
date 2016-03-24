/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.blocks.aesthetics;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public interface IBlockMaterial {

    IBlockState getState();

    Block.SoundType getSound();

    boolean isTransparent();

    float getBlockHardness(World world, BlockPos pos);

    float getExplosionResistance(Entity entity);

    String getRegistryName();

    String getLocalizationSuffix();
}
