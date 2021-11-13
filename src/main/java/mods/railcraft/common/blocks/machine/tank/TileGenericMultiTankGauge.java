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
import mods.railcraft.common.blocks.machine.beta.TileTankIronGauge;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileGenericMultiTankGauge extends TileTankIronGauge {

	private final MetalTank tankType;
	private final IEnumMachine gaugeType;
	
	public TileGenericMultiTankGauge(MetalTank thisTankType, IEnumMachine thisGaugeType) {
		tankType = thisTankType;
		gaugeType = thisGaugeType;
	}
	
    @Override
    public IEnumMachine getMachineType() {
        return gaugeType;
    }

    @Override
    public MetalTank getTankType() {
        return tankType;
    }

    @Override
    public int getCapacityPerBlock() {
    	return gaugeType.getCapacity();
    }
}
