/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.interfaces;

import net.minecraft.util.EnumFacing;
import org.jetbrains.annotations.Nullable;

/**
 * Created by CovertJaguar on 9/8/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface ITileRotate extends ITile {
    EnumFacing getFacing();

    default void setFacing(EnumFacing facing) {
    }

    default boolean canRotate() {
        EnumFacing[] rotations = getValidRotations();
        return rotations != null && rotations.length > 1;
    }

    default boolean rotateBlock(EnumFacing axis) {
        if (!canRotate()) return false;
        if (getFacing() == axis)
            setFacing(axis.getOpposite());
        else
            setFacing(axis);
        markBlockForUpdate();
        return true;
    }

    default @Nullable EnumFacing[] getValidRotations() {
        return EnumFacing.VALUES;
    }
}
