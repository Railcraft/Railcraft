/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.interfaces;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.util.EnumFacing;

/**
 * Created by CovertJaguar on 7/5/2017 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface ITileNonSolid {

    default BlockFaceShape getShape(EnumFacing side) {
        return BlockFaceShape.SOLID;
    }
}
