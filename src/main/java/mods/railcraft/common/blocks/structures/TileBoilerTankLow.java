/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.structures;

import mods.railcraft.common.gui.EnumGui;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class TileBoilerTankLow extends TileBoilerTank {

    @Override
    public EnumGui getGui() {
        return EnumGui.TANK;
    }
}
