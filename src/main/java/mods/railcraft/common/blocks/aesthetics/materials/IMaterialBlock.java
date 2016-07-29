/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/

package mods.railcraft.common.blocks.aesthetics.materials;

import mods.railcraft.common.blocks.IRailcraftBlock;
import mods.railcraft.common.util.sounds.IBlockSoundProvider;
import net.minecraft.block.SoundType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by CovertJaguar on 7/13/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IMaterialBlock extends IRailcraftBlock, IBlockSoundProvider {
    String getUnlocalizedName(Materials mat);

    @Override
    default SoundType getSound(World world, BlockPos pos) {
        return MatTools.getSound(world, pos);
    }
}
