/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.beta;

import mods.railcraft.common.gui.EnumGui;

import javax.annotation.Nullable;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileBoilerTankLow extends TileBoilerTank {

    public TileBoilerTankLow() {
        super();
    }

//    @Override
//    public EnumMachineBeta getMachineType() {
//        return EnumMachineBeta.BOILER_TANK_LOW_PRESSURE;
//    }

    @Nullable
    @Override
    public EnumGui getGui() {
        return EnumGui.TANK;
    }
}
