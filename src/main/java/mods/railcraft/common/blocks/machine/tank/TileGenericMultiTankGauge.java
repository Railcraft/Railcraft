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
import mods.railcraft.common.modules.ModuleAdvancedTanks;
import net.minecraft.nbt.NBTTagCompound;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileGenericMultiTankGauge extends TileTankIronGauge {

	private MetalTank tankType;
	private IEnumMachine gaugeType;


	public TileGenericMultiTankGauge() {
		
	}
	
	public TileGenericMultiTankGauge(MetalTank thisTankType, IEnumMachine thisGaugeType) {
		tankType = thisTankType;
		gaugeType = thisGaugeType;
		markDirty();
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

	@Override
	public void writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);
		data.setString("Machine.Type", gaugeType.getTag());
	}

	@Override
	public void readFromNBT(NBTTagCompound data) {
		gaugeType = ModuleAdvancedTanks.cacheTankType.get(data.getString("Machine.Type"));
		tankType = ModuleAdvancedTanks.cacheTankMaterial.get(data.getString("Machine.Type"));
		super.readFromNBT(data);
	}
	
}
