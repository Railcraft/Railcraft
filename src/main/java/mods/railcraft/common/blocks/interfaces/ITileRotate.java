/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.interfaces;

import net.minecraft.util.EnumFacing;

import java.util.Arrays;

/**
 * Created by CovertJaguar on 9/8/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface ITileRotate extends ITile {
    EnumFacing getFacing();

    void setFacing(EnumFacing facing);

    default boolean canRotate(EnumFacing axis) {
        return Arrays.asList(getValidRotations()).contains(axis);
    }

    default boolean rotateBlock(EnumFacing axis) {
        if (!canRotate(axis)) return false;
        if (getFacing() == axis)
            setFacing(axis.getOpposite());
        else
            setFacing(axis);
        markBlockForUpdate();
        return true;
    }

    default EnumFacing[] getValidRotations() {
        return EnumFacing.VALUES;
    }
}
