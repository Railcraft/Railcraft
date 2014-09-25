/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.beta;

import mods.railcraft.common.blocks.machine.IEnumMachine;
import static mods.railcraft.common.blocks.machine.beta.TileTankSteelWall.STEEL_TANK;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileTankSteelValve extends TileTankIronValve {

    @Override
    public IEnumMachine getMachineType() {
        return EnumMachineBeta.TANK_STEEL_VALVE;
    }

    @Override
    public MetalTank getTankType() {
        return STEEL_TANK;
    }

    @Override
    public int getCapacityPerBlock() {
        return CAPACITY_PER_BLOCK_STEEL;
    }
}
