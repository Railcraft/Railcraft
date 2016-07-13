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

import mods.railcraft.common.core.IRailcraftObject;
import mods.railcraft.common.util.sounds.IBlockSoundProvider;

/**
 * Created by CovertJaguar on 7/13/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IMaterialBlock extends IRailcraftObject, IBlockSoundProvider {
    String getUnlocalizedName(Materials mat);
}
