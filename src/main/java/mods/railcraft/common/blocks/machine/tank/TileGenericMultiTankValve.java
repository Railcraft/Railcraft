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
import mods.railcraft.common.modules.ModuleAdvancedTanks;
import net.minecraft.nbt.NBTTagCompound;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileGenericMultiTankValve extends TileTankIronValve {

    private MetalTank tankType;
	private IEnumMachine valveType;


	public TileGenericMultiTankValve() {
		
	}

	public TileGenericMultiTankValve(MetalTank thisTankType, IEnumMachine thisValveType) {
		tankType = thisTankType;
		valveType = thisValveType;
		markDirty();
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

	@Override
	public void writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);
		if (valveType != null && !data.hasKey("Machine.Type")) {
			data.setString("Machine.Type", valveType.getTag());
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound data) {
		valveType = ModuleAdvancedTanks.cacheTankType.get(data.getString("Machine.Type"));
		tankType = ModuleAdvancedTanks.cacheTankMaterial.get(data.getString("Machine.Type"));
		super.readFromNBT(data);
	}
    
}
