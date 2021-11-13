/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.tank;

import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.beta.MetalTank;
import mods.railcraft.common.blocks.machine.beta.TileTankIronValve;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileGenericMultiTankValve extends TileTankIronValve {

    private final MetalTank tankType;
	private final IEnumMachine valveType;

	public TileGenericMultiTankValve(MetalTank thisTankType, IEnumMachine thisValveType) {
		tankType = thisTankType;
		valveType = thisValveType;
	}
	
    @Override
    public IEnumMachine getMachineType() {
        return valveType;
    }

    @Override
    public MetalTank getTankType() {
        return tankType;
    }

    @Override
    public int getCapacityPerBlock() {
    	return valveType.getCapacity();
    }
}
