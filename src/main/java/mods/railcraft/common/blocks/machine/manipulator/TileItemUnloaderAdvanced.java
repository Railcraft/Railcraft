/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.manipulator;

import net.minecraft.util.EnumFacing;

public class TileItemUnloaderAdvanced extends TileItemUnloader {

    @Override
    public ManipulatorVariant getMachineType() {
        return ManipulatorVariant.ITEM_UNLOADER_ADVANCED;
    }

    @Override
    public EnumFacing[] getValidRotations() {
        return EnumFacing.VALUES;
    }
}
