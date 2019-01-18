/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.interfaces;

import mods.railcraft.common.plugins.color.EnumColor;
import net.minecraft.block.state.IBlockState;

/**
 * Implemented by blocks that run that full color palette of sixteen colors.
 *
 * Examples: Reinforced Concrete, Reinforced Glass
 *
 * Created by CovertJaguar on 11/28/2018 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IBlockColored {
    default EnumColor getColor(IBlockState state) {
        return EnumColor.WHITE;
    }

}
